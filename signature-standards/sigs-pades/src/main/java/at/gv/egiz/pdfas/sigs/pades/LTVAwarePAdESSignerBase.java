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

import static iaik.cms.SignedDataStream.EXPLICIT;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.ErrorConstants;
import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSignatureException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;
import at.gv.egiz.pdfas.lib.api.sign.DigestInfo;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter.LTVMode;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import at.gv.egiz.pdfas.lib.pki.CertificateVerificationDataService;
import at.gv.egiz.pdfas.lib.pki.spi.CertificateVerificationData;
import at.gv.egiz.pdfas.lib.util.CertificateUtils;
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

public abstract class LTVAwarePAdESSignerBase implements IPlainSigner {

	private Logger log = LoggerFactory.getLogger(LTVAwarePAdESSignerBase.class);

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
				log.info("Unable to handle LTV retrieval for signer certificate with issuer (ski: {}): {}", CertificateUtils.getAuthorityKeyIdentifierHexString(eeCertificate), eeCertificate.getIssuerDN());
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
	public DigestInfo calculateDigest(byte[] dataToBeSigned, SignParameter parameter, RequestedSignature requestedSignature) throws PdfAsException {
		
		try {
			
			DigestInfoImpl digestInfo = new DigestInfoImpl();
			
			SignedData signedData = new SignedData(dataToBeSigned, EXPLICIT);
			signedData.addCertificates(new Certificate[] { requestedSignature.getCertificate() });
			
			signedData.setSecurityProvider(new SecurityProvider() {

				// we do not care about signatures at this stage, we want to get the digest
				
				@Override
				public byte[] calculateSignatureFromHash(AlgorithmID signatureAlgorithm, AlgorithmID digestAlgorithm, PrivateKey privateKey,
						byte[] digest) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
					
					digestInfo.setAlgorithm(digestAlgorithm);
					digestInfo.setValue(digest);
					
					return new byte[] { 0 };
				}

				@Override
				public byte[] calculateSignatureFromSignedAttributes(AlgorithmID signatureAlgorithm, AlgorithmID digestAlgorithm, PrivateKey privateKey,
						byte[] asn1EncodedAttributes) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
				
					digestInfo.setAlgorithm(digestAlgorithm);
					digestInfo.setValue(getHash(digestAlgorithm, asn1EncodedAttributes));
					
					return new byte[] { 0 };
				}

			});

			SignerInfo signerInfo = createSignerInfo(parameter, requestedSignature);
			
			// triggers both digest calculation (of signed attributes) and signature (provide empty signature value)
			signedData.addSignerInfo(signerInfo);
			
			// TODO[PDFAS-114]: Return SignedData (or encoded SignedData together with Digest) without EncapsulatedContentInfo
			
			return digestInfo;

		} catch (NoSuchAlgorithmException e) {
			throw new PdfAsSignatureException("error.pdf.sig.01", e);
		}
		
	}
	
	private class DigestInfoImpl implements DigestInfo {
		
		private AlgorithmID algorithm;
		private byte[] value;
		
		private void setAlgorithm(AlgorithmID algorithm) {
			this.algorithm = algorithm;
		}

		private void setValue(byte[] value) {
			this.value = value;
		}

		@Override
		public AlgorithmID getAlgorithm() {
			return algorithm;
		}

		@Override
		public byte[] getValue() {
			return value;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("DigestInfo [algorithm=");
			builder.append(algorithm);
			if (value != null) {
				// TODO[PDFAS-114]: Do not show both value representations
				builder.append(", value (base64)=");
				builder.append(Base64.getEncoder().encodeToString(value));
				builder.append(", value (base64Url)=");
				builder.append(Base64.getUrlEncoder().encodeToString(value));
			} else {
				builder.append("value=null");
			}
			builder.append("]");
			return builder.toString();
		}
		
		
	}
	
	private SignerInfo createSignerInfo(SignParameter parameter, RequestedSignature requestedSignature) throws PdfAsException {
		
		X509Certificate signingCertificate = requestedSignature.getCertificate();
		
		IssuerAndSerialNumber issuer = new IssuerAndSerialNumber(signingCertificate);
		
		try {
			
			AlgorithmID[] algorithms = CertificateUtils.getAlgorithmIDs(signingCertificate);
			
			// there is no private key since we do not intend to conduct any signatures at this stage
			SignerInfo signerInfo = new SignerInfo(issuer, algorithms[1], algorithms[0], null);
			
			// consider PAdESCompatibility flag from configuration
			if (IConfigurationConstants.TRUE.equalsIgnoreCase(parameter.getConfiguration().getValue(IConfigurationConstants.SIG_PADES_FORCE_FLAG))) {
				setAttributes(signerInfo, signingCertificate);
			} else {
				setAttributes(signerInfo, "application/pdf", signingCertificate, requestedSignature.getStatus().getSigningDate().getTime());
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

	@Override
	public byte[] encodeExternalSignatureValue(byte[] signatureValue, byte[] dataToBeSigned, SignParameter parameter, RequestedSignature requestedSignature) throws PdfAsException {
		
		try {
			
			SignedData signedData = new SignedData(dataToBeSigned, EXPLICIT);
			signedData.addCertificates(new Certificate[] { requestedSignature.getCertificate() });
			
			signedData.setSecurityProvider(new SecurityProvider() {

				// we do not care about signatures at this stage, we want the digest to be calculated
				
				@Override
				public byte[] calculateSignatureFromHash(AlgorithmID signatureAlgorithm, AlgorithmID digestAlgorithm, PrivateKey privateKey,
						byte[] digest) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
					return new byte[] { 0 };
				}

				@Override
				public byte[] calculateSignatureFromSignedAttributes(AlgorithmID signatureAlgorithm, AlgorithmID digestAlgorithm, PrivateKey privateKey,
						byte[] asn1EncodedAttributes) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
				
					return new byte[] { 0 };
				}

			});

			SignerInfo signerInfo = createSignerInfo(parameter, requestedSignature);
			
			// triggers both digest calculation (of signed attributes) and signature (provide empty signature value)
			signedData.addSignerInfo(signerInfo);
			// overwrite signature
			signerInfo.setSignatureValue(signatureValue);
			
			ContentInfo contentInfo = new ContentInfo(signedData);
			
			// return encoded cms signature
			return contentInfo.getEncoded();

		} catch (CMSException | NoSuchAlgorithmException e) {
			throw new PdfAsSignatureException("error.pdf.sig.01", e);
		}
		
	}
	
}
