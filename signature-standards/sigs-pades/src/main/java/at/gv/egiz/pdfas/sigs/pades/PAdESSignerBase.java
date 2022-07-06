/*******************************************************************************
 * <copyright> Copyright 2017 by PrimeSign GmbH, Graz, Austria </copyright>
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * http://www.osor.eu/eupl/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * This product combines work with different licenses. See the "NOTICE" text
 * file for details on the various modules and licenses.
 * The "NOTICE" text file is part of the distribution. Any derivative works
 * that you distribute must include a readable copy of the "NOTICE" text file.
 ******************************************************************************/
package at.gv.egiz.pdfas.sigs.pades;

import static at.gv.egiz.pdfas.sigs.pades.PAdESConstants.FILTER_ADOBE_PPKLITE;
import static at.gv.egiz.pdfas.sigs.pades.PAdESConstants.SUBFILTER_ETSI_CADES_DETACHED;
import static iaik.cms.SignedDataStream.EXPLICIT;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.ErrorConstants;
import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSignatureException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.api.sign.ExternalSignatureInfo;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter.LTVMode;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import at.gv.egiz.pdfas.lib.pki.CertificateVerificationDataService;
import at.gv.egiz.pdfas.lib.pki.spi.CertificateVerificationData;
import at.gv.egiz.pdfas.lib.util.CertificateUtils;
import iaik.asn1.ASN1;
import iaik.asn1.ASN1Object;
import iaik.asn1.CodingException;
import iaik.asn1.ObjectID;
import iaik.asn1.SEQUENCE;
import iaik.asn1.UTF8String;
import iaik.asn1.structures.AlgorithmID;
import iaik.asn1.structures.Attribute;
import iaik.asn1.structures.ChoiceOfTime;
import iaik.cms.CMSException;
import iaik.cms.ContentInfo;
import iaik.cms.IssuerAndSerialNumber;
import iaik.cms.SecurityProvider;
import iaik.cms.SignedData;
import iaik.cms.SignerInfo;
import iaik.smime.ess.ESSCertID;
import iaik.smime.ess.ESSCertIDv2;
import iaik.x509.X509Certificate;

public abstract class PAdESSignerBase implements IPlainSigner {

	private Logger log = LoggerFactory.getLogger(PAdESSignerBase.class);

	@Override
	public CertificateVerificationData getCertificateVerificationData(RequestedSignature requestedSignature) throws PDFASError {

		// LTV mode controls if and how retrieval/embedding LTV data will be done
		LTVMode ltvMode = requestedSignature.getStatus().getSignParamter().getLTVMode();
		log.trace("LTV mode: {}", ltvMode);

		if (ltvMode == LTVMode.NONE) {
			return null;
		}

		final X509Certificate eeCertificate = requestedSignature.getCertificate();
		if (eeCertificate == null) {
			throw new IllegalStateException("Retrieving certificate verification data required retrieval of the certificate beforehand.");
		}

		try {

			// fetch PDF-AS settings to be provided to verification data service/validation providers
			ISettings settings = requestedSignature.getStatus().getSettings();

			// fetch/create service in order to see if we can handle the signer's CA
			CertificateVerificationDataService ltvVerificationInfoService = CertificateVerificationDataService.getInstance();
			if (ltvVerificationInfoService.canHandle(eeCertificate, settings)) {
				
				// yes, we can
				log.debug("Retrieving LTV verification info.");
				return ltvVerificationInfoService.getCertificateVerificationData(eeCertificate, settings);
				
			} else {
				log.info("Unable to handle LTV retrieval for signer certificate with issuer (ski: {}): {}", CertificateUtils.getAuthorityKeyIdentifierHexString(eeCertificate).orElseGet(() -> null), eeCertificate.getIssuerDN());
			}

		} catch (Exception e) {
			
			// error retrieving LTV data, LTV mode controls how errors are handled
			final String message = "Unable to retrieve LTV related data.";
			if (ltvMode == LTVMode.REQUIRED) {
				throw new PDFASError(ErrorConstants.ERROR_SIG_PADESLTV_RETRIEVING_REQUIRED_DATA, message, e);
			}
			log.warn(message, e);
			
		}

		return null;

	}

	@Override
	public ExternalSignatureInfo determineExternalSignatureInfo(byte[] dataToBeSigned, X509Certificate signingCertificate, Date signingTime, boolean enforceETSIPAdES) throws PdfAsException {
		
		try {
			
			ExternalSignatureInfoImpl externalSignatureInfo = new ExternalSignatureInfoImpl();
			
			SignedData signedData = new SignedData(dataToBeSigned, EXPLICIT);
			signedData.addCertificates(new Certificate[] { signingCertificate });
			
			signedData.setSecurityProvider(new SecurityProvider() {

				// we do not care about signatures at this stage, we want to get the digest
				
				@Override
				public byte[] calculateSignatureFromHash(AlgorithmID signatureAlgorithm, AlgorithmID digestAlgorithm, PrivateKey privateKey,
						byte[] digest) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
					
					externalSignatureInfo.setDigestAlgorithm(digestAlgorithm);
					externalSignatureInfo.setDigestValue(digest);
					externalSignatureInfo.setSignatureAlgorithm(signatureAlgorithm);
					
					return new byte[] { 0 };
				}

				@Override
				public byte[] calculateSignatureFromSignedAttributes(AlgorithmID signatureAlgorithm, AlgorithmID digestAlgorithm, PrivateKey privateKey,
						byte[] asn1EncodedAttributes) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
				
					externalSignatureInfo.setDigestAlgorithm(digestAlgorithm);
					externalSignatureInfo.setDigestValue(getHash(digestAlgorithm, asn1EncodedAttributes));
					externalSignatureInfo.setSignatureAlgorithm(signatureAlgorithm);
					
					return new byte[] { 0 };
				}

			});

			SignerInfo signerInfo = createSignerInfo(signingCertificate, signingTime, enforceETSIPAdES);
			
			// triggers both digest calculation (of signed attributes) and signature (provide empty signature value)
			signedData.addSignerInfo(signerInfo);
			
			externalSignatureInfo.setSignatureObject(new ContentInfo(signedData).getEncoded());
			
			externalSignatureInfo.validate();
			
			if (log.isTraceEnabled()) {
				log.trace("Resulting external signature info: {}", externalSignatureInfo);
			}
			
			return externalSignatureInfo;

		} catch (NoSuchAlgorithmException | CMSException e) {
			throw new PdfAsSignatureException("error.pdf.sig.19", e);
		}
		
	}
	
	private class ExternalSignatureInfoImpl implements ExternalSignatureInfo {
		
		private AlgorithmID digestAlgorithm;
		private AlgorithmID signatureAlgorithm;
		private byte[] digestValue;
		private byte[] signatureObject;

		@Override
		public AlgorithmID getDigestAlgorithm() {
			return digestAlgorithm;
		}

		@Override
		public AlgorithmID getSignatureAlgorithm() {
			return signatureAlgorithm;
		}

		@Override
		public byte[] getDigestValue() {
			return digestValue;
		}

		@Override
		public byte[] getSignatureObject() {
			return signatureObject;
		}

		private void setDigestAlgorithm(AlgorithmID digestAlgorithm) {
			this.digestAlgorithm = digestAlgorithm;
		}

		private void setSignatureAlgorithm(AlgorithmID signatureAlgorithm) {
			this.signatureAlgorithm = signatureAlgorithm;
		}

		private void setDigestValue(byte[] digestValue) {
			this.digestValue = digestValue;
		}

		private void setSignatureObject(byte[] signatureObject) {
			this.signatureObject = signatureObject;
		}

		void validate() {
			if (digestAlgorithm == null) {
				throw new IllegalStateException("Digest algorithm required.");
			}
			if (digestValue == null) {
				throw new IllegalStateException("Digest value required.");
			}
			if (signatureAlgorithm == null) {
				throw new IllegalStateException("Signature algorithm required.");
			}
			if (signatureObject == null) {
				throw new IllegalStateException("Signature object required.");
			}
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("ExternalSignatureInfoImpl [");
			builder.append("digestAlgorithm=").append(digestAlgorithm != null ? digestAlgorithm.getName() : null);
			builder.append(", digestValue=").append(digestValue != null ? Hex.encodeHexString(digestValue) : null);
			builder.append(", signatureAlgorithm=").append(signatureAlgorithm != null ? signatureAlgorithm.getName() : null);
			builder.append(", signatureObject=").append(signatureObject != null ? "<set>" : null);
			builder.append("]");
			return builder.toString();
		}

	}
	
	private SignerInfo createSignerInfo(X509Certificate signingCertificate, Date signingTime, boolean enforceETSIPAdES) throws PdfAsException {
		
		IssuerAndSerialNumber issuer = new IssuerAndSerialNumber(signingCertificate);
		
		try {
			
			AlgorithmID[] algorithms = CertificateUtils.getAlgorithmIDs(signingCertificate);
			
			AlgorithmID signatureAlgorith = algorithms[0];
			AlgorithmID digestAlgorithm = algorithms[1];
			
			// there is no private key since we do not intend to conduct any signatures at this stage
			SignerInfo signerInfo = new SignerInfo(issuer, digestAlgorithm, signatureAlgorith, null);
			
			// consider PAdESCompatibility flag from configuration
			if (enforceETSIPAdES) {
				setAttributes(signerInfo, signingCertificate);
			} else {
				setAttributes(signerInfo, "application/pdf", signingCertificate, signingTime);
			}
			
			return signerInfo;
			
		} catch (NoSuchAlgorithmException | CertificateException | CodingException e) {
			throw new PdfAsSignatureException("error.pdf.sig.01", e);
		}
		
	}
	
	private void setAttributes(SignerInfo signerInfo, String mimeType, X509Certificate signingCertificate, Date signingTime) throws CertificateException, NoSuchAlgorithmException, CodingException {
		List<Attribute> attributes = new ArrayList<>();
		setMimeTypeAttrib(attributes, mimeType);
		setContentTypeAttrib(attributes);
		setSigningCertificateAttrib(attributes, signingCertificate);
		setSigningTimeAttrib(attributes, signingTime);
		Attribute[] attributeArray = attributes.toArray(new Attribute[attributes.size()]);
		signerInfo.setSignedAttributes(attributeArray);
	}

	private void setAttributes(SignerInfo signerInfo, X509Certificate signingCertificate) throws CertificateException, NoSuchAlgorithmException, CodingException {
		List<Attribute> attributes = new ArrayList<>();
		setContentTypeAttrib(attributes);
		setSigningCertificateAttrib(attributes, signingCertificate);
		Attribute[] attributeArray = attributes.toArray(new Attribute[attributes.size()]);
		signerInfo.setSignedAttributes(attributeArray);
	}

	private void setMimeTypeAttrib(List<Attribute> attributes, String mimeType) {
		String oidStr = "0.4.0.1733.2.1";
		String name = "mime-type";
		ObjectID mimeTypeOID = new ObjectID(oidStr, name);
		Attribute mimeTypeAtt = new Attribute(mimeTypeOID, new ASN1Object[] { new UTF8String(mimeType) });
		attributes.add(mimeTypeAtt);
	}

	private void setContentTypeAttrib(List<Attribute> attributes) {
		Attribute contentType = new Attribute(ObjectID.contentType, new ASN1Object[] { ObjectID.cms_data });
		attributes.add(contentType);
	}

	private void setSigningCertificateAttrib(List<Attribute> attributes, X509Certificate signingCertificate)
			throws CertificateException, NoSuchAlgorithmException, CodingException {
		ObjectID id;
		ASN1Object value = new SEQUENCE();
		AlgorithmID[] algorithms = CertificateUtils.getAlgorithmIDs(signingCertificate);
		if (algorithms[1].equals(AlgorithmID.sha1)) {
			id = ObjectID.signingCertificate;
			value.addComponent(new ESSCertID(signingCertificate, true).toASN1Object());
		} else {
			id = ObjectID.signingCertificateV2;
			value.addComponent(new ESSCertIDv2(algorithms[1], signingCertificate, true).toASN1Object());
		}
		ASN1Object signingCert = new SEQUENCE();
		signingCert.addComponent(value);
		Attribute signingCertificateAttrib = new Attribute(id, new ASN1Object[] { signingCert });
		attributes.add(signingCertificateAttrib);
	}

	private void setSigningTimeAttrib(List<Attribute> attributes, Date date) {
		Attribute signingTime = new Attribute(ObjectID.signingTime, new ASN1Object[] { new ChoiceOfTime(date).toASN1Object() });
		attributes.add(signingTime);
	}

	/**
	 * Inserts the provided (plain) externalSignatureValue to the provided signature object.
	 */
	@Override
	public byte[] applyPlainExternalSignatureValue(byte[] externalSignatureValue, byte[] signatureObject) throws PdfAsException {
		
		// PAdESSignerBase implementation takes existing signatureObject (= ASN.1 data (without signature value)) and inserts externalSignatureValue (= plain signature value).
		Objects.requireNonNull(signatureObject, "'signatureObject' (ASN.1 signature without signature value) required.");

		try {
			
			ASN1 asn1 = new ASN1(signatureObject);
			ASN1Object asn1Object = asn1.toASN1Object();
			ContentInfo contentInfo = new ContentInfo(asn1Object);
			if (!ObjectID.cms_signedData.equals(contentInfo.getContentType())) {
				throw new IllegalArgumentException("Expected that 'signatureObject' reflects cms ContentInfo with SignedData content.");
			}
			SignedData signedData = (SignedData) contentInfo.getContent();
			SignerInfo[] signerInfos = signedData.getSignerInfos();
			if (signerInfos == null || signerInfos.length != 1) {
				throw new IllegalArgumentException("Expected that 'signatureObject' reflects cms ContentInfo with SignedData content with exactly one single SignerInfo.");
			}
			SignerInfo signerInfo = signerInfos[0];
			
			// insert signature value
			signerInfo.setSignatureValue(externalSignatureValue);
			
			if (log.isTraceEnabled()) {
				log.trace("Updated ContentInfo with signature value:\n{}", contentInfo.toString(true));
			}
			
			// return encoded cms signature
			return contentInfo.getEncoded();

		} catch (CMSException | CodingException | IllegalArgumentException e) {
			throw new PdfAsSignatureException("error.pdf.sig.20", e);
		}
		
	}
	
	@Override
	public String getPDFSubFilter() {
		return SUBFILTER_ETSI_CADES_DETACHED;
	}

	@Override
	public String getPDFFilter() {
		return FILTER_ADOBE_PPKLITE;
	}

}
