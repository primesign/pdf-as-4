/*******************************************************************************
 * <copyright> Copyright 2014 by E-Government Innovation Center EGIZ, Graz, Austria </copyright>
 * PDF-AS has been contracted by the E-Government Innovation Center EGIZ, a
 * joint initiative of the Federal Chancellery Austria and Graz University of
 * Technology.
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

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStore.Entry;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.ErrorConstants;
import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSignatureException;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import at.gv.egiz.pdfas.lib.util.CertificateUtils;
import at.gv.egiz.pdfas.lib.util.SignatureUtils;
import iaik.asn1.ASN1Object;
import iaik.asn1.CodingException;
import iaik.asn1.ObjectID;
import iaik.asn1.SEQUENCE;
import iaik.asn1.UTF8String;
import iaik.asn1.structures.AlgorithmID;
import iaik.asn1.structures.Attribute;
import iaik.asn1.structures.ChoiceOfTime;
import iaik.cms.ContentInfo;
import iaik.cms.IssuerAndSerialNumber;
import iaik.cms.SecurityProvider;
import iaik.cms.SignedData;
import iaik.cms.SignerInfo;
import iaik.smime.ess.ESSCertID;
import iaik.smime.ess.ESSCertIDv2;
import iaik.x509.X509Certificate;

public class PAdESSignerKeystore extends PAdESSignerBase implements PAdESConstants {

	private static final Logger logger = LoggerFactory
			.getLogger(PAdESSignerKeystore.class);

	private static final String fallBackProvider = "SunJSSE";

	public static final String SIGNATURE_DEVICE = "JKS";

	private PrivateKey privKey;
	private X509Certificate cert;

	private void readKeyStore(KeyStore ks, String alias, String keypassword)  throws Throwable {
		if (keypassword == null) {
			throw new PdfAsException("error.pdf.sig.16");
		}
		PasswordProtection pwdProt = new PasswordProtection(
				keypassword.toCharArray());

		logger.info("Opening Alias: [" + alias + "]");

		Entry entry = ks.getEntry(alias, pwdProt);

		if (!(entry instanceof PrivateKeyEntry)) {
			throw new PdfAsException("error.pdf.sig.18");
		}

		PrivateKeyEntry privateEntry = (PrivateKeyEntry) entry;

		privKey = privateEntry.getPrivateKey();

		if (privKey == null) {
			throw new PdfAsException("error.pdf.sig.13");
		}

		Certificate c = privateEntry.getCertificate();

		if (c == null) {
			if (privateEntry.getCertificateChain() != null) {
				if (privateEntry.getCertificateChain().length > 0) {
					c = privateEntry.getCertificateChain()[0];
				}
			}
		}

		if (c == null) {
			throw new PdfAsException("error.pdf.sig.17");
		}

		cert = new X509Certificate(c.getEncoded());
	}

	private KeyStore buildKeyStoreFromFile(String file, String kspassword,
			String type, String provider)  throws Throwable {
		String viusalProvider = (provider == null ? "IAIK" : provider);
		logger.trace("Opening Keystore: " + file + " with [" + viusalProvider
				+ "]");

		KeyStore ks = null;
		if (provider == null) {
			ks = KeyStore.getInstance(type);
		} else {
			ks = KeyStore.getInstance(type, provider);
		}

		if (ks == null) {
			throw new PdfAsException("error.pdf.sig.14");
		}
		if (kspassword == null) {
			throw new PdfAsException("error.pdf.sig.15");
		}
		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
			ks.load(is, kspassword.toCharArray());
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return ks;
	}

	private void loadKeystore(String file, String alias, String kspassword,
			String keypassword, String type, String provider) throws Throwable {

		KeyStore ks = buildKeyStoreFromFile(file, kspassword, type, provider);

		readKeyStore(ks, alias, keypassword);
	}

	public PAdESSignerKeystore(KeyStore ks, String alias,
			String keypassword) throws PDFASError {
		try {
			readKeyStore(ks, alias, keypassword);
		} catch (Throwable e) {
			throw new PDFASError(PDFASError.ERROR_SIG_FAILED_OPEN_KS, e);
		}
	}

	public PAdESSignerKeystore(String file, String alias, String kspassword,
			String keypassword, String type) throws PDFASError {
		try {
			// Load keystore with default security provider (IAIK)
			loadKeystore(file, alias, kspassword, keypassword, type, null);
		} catch (Throwable e) {
			try {
				// IAIK Provider seems to have problem with some PKCS12 files..
				loadKeystore(file, alias, kspassword, keypassword, type,
						fallBackProvider);
				logger.warn("Failed to open Keystore with IAIK provider!");
			} catch (Throwable e1) {
				logger.error("Keystore IAIK provider error: ", e);
				logger.error("Keystore " + fallBackProvider
						+ " provider error: ", e1);
				throw new PDFASError(PDFASError.ERROR_SIG_FAILED_OPEN_KS, e1);
			}
		}
	}

	public PAdESSignerKeystore(PrivateKey privKey, java.security.cert.Certificate cert) throws PDFASError {
		if(cert == null) {
			logger.error("PAdESSignerKeystore provided certificate is NULL");
			throw new NullPointerException();
		}

		if(cert instanceof X509Certificate) {
			this.cert = (X509Certificate)cert;
		} else {
			try {
				this.cert = new X509Certificate(cert.getEncoded());
			} catch (CertificateEncodingException e) {
				throw new PDFASError(PDFASError.ERROR_INVALID_CERTIFICATE, e);
			} catch (CertificateException e) {
				throw new PDFASError(PDFASError.ERROR_INVALID_CERTIFICATE, e);
			}
		}

		this.privKey = privKey;
	}

	public X509Certificate getCertificate(SignParameter parameter) {
		return cert;
	}

	private void setMimeTypeAttrib(List<Attribute> attributes, String mimeType) {
		String oidStr = "0.4.0.1733.2.1";
		String name = "mime-type";
		ObjectID mimeTypeOID = new ObjectID(oidStr, name);

		Attribute mimeTypeAtt = new Attribute(mimeTypeOID,
				new ASN1Object[] { new UTF8String(mimeType) });
		attributes.add(mimeTypeAtt);
	}

	private void setContentTypeAttrib(List<Attribute> attributes) {
		Attribute contentType = new Attribute(ObjectID.contentType,
				new ASN1Object[] { ObjectID.cms_data });
		attributes.add(contentType);
	}

	private void setSigningCertificateAttrib(List<Attribute> attributes,
			X509Certificate signingCertificate) throws CertificateException,
			NoSuchAlgorithmException, CodingException {
		ObjectID id;
		ASN1Object value = new SEQUENCE();
		AlgorithmID[] algorithms = CertificateUtils
				.getAlgorithmIDs(signingCertificate);
		if (algorithms[1].equals(AlgorithmID.sha1)) {
			id = ObjectID.signingCertificate;
			value.addComponent(new ESSCertID(signingCertificate, true)
					.toASN1Object());
		} else {
			id = ObjectID.signingCertificateV2;
			value.addComponent(new ESSCertIDv2(algorithms[1],
					signingCertificate, true).toASN1Object());
		}
		ASN1Object signingCert = new SEQUENCE();
		signingCert.addComponent(value);
		Attribute signingCertificateAttrib = new Attribute(id,
				new ASN1Object[] { signingCert });
		attributes.add(signingCertificateAttrib);
	}

	private void setSigningTimeAttrib(List<Attribute> attributes, Date date) {
		Attribute signingTime = new Attribute(ObjectID.signingTime,
				new ASN1Object[] { new ChoiceOfTime(date).toASN1Object() });
		attributes.add(signingTime);
	}

	private void setAttributes(String mimeType,
			X509Certificate signingCertificate, Date signingTime,
			SignerInfo signerInfo) throws CertificateException,
			NoSuchAlgorithmException, CodingException {
		List<Attribute> attributes = new ArrayList<>();

		setMimeTypeAttrib(attributes, mimeType);
		setContentTypeAttrib(attributes);
		setSigningCertificateAttrib(attributes, signingCertificate);
		setSigningTimeAttrib(attributes, signingTime);
		Attribute[] attributeArray = attributes
				.toArray(new Attribute[attributes.size()]);
		signerInfo.setSignedAttributes(attributeArray);
	}

	private void setAttributes(X509Certificate signingCertificate, SignerInfo signerInfo) throws CertificateException,
			NoSuchAlgorithmException, CodingException {
		List<Attribute> attributes = new ArrayList<>();

		setContentTypeAttrib(attributes);
		setSigningCertificateAttrib(attributes, signingCertificate);
		Attribute[] attributeArray = attributes
				.toArray(new Attribute[attributes.size()]);
		signerInfo.setSignedAttributes(attributeArray);
	}

	/**
	 * Allows overriding classes to provide a custom CMS Security Provider allowing to override default cryptographic
	 * operations.
	 * <p>
	 * If no Security Provider is provided (returning {@code null}, which ist the default of this method)
	 * {@link iaik.cms.IaikProvider IAIK CMS provider} is used.
	 * </p>
	 * <p>
	 * The returned {@link SecurityProvider} may use (override) the following method (among others) in order to extends
	 * support for cryptographic devices like hardware security modules:
	 * </p>
	 * <ul>
	 * <li>{@link SecurityProvider#calculateSignatureFromHash(AlgorithmID, AlgorithmID, PrivateKey, byte[])
	 * calculateSignatureFromHash(...)}: to calculate the signature value from the message hash</li>
	 * </ul>
	 * <p>
	 * Further note about <strong>thread safety</strong>: In case this keystore implementation is concurrently used by
	 * different threads and the caller makes use of this method, a new SecurityProvider should be provided for each
	 * concurrent use (since {@link iaik.cms.SecurityProvider} is not thread-safe as it uses {@link SecureRandom} in
	 * singleton manner) or other synchronization measures should be taken (e.g. synchronizing keystore invocations,
	 * using {@link ThreadLocal ThreadLocal&lt;SecurityProvider&gt;}, ...).
	 * </p>
	 *
	 * @return The Security Provider (may be {@code null}).
	 */
	public SecurityProvider getSecurityProvider() {
		return null;
	}

	public byte[] sign(byte[] input, int[] byteRange, SignParameter parameter,
			RequestedSignature requestedSignature) throws PdfAsException {
		try {
			logger.info("Creating PAdES signature.");

			Map<String, String> metaInformation = requestedSignature.getStatus().getMetaInformations();
			metaInformation.put(ErrorConstants.STATUS_INFO_SIGDEVICE, SIGNATURE_DEVICE);
			metaInformation.put(ErrorConstants.STATUS_INFO_SIGDEVICEVERSION, PdfAsFactory.getVersion());

			IssuerAndSerialNumber issuer = new IssuerAndSerialNumber(cert);

			AlgorithmID[] algorithms = CertificateUtils.getAlgorithmIDs(cert);

			SignerInfo signer1 = new SignerInfo(issuer, algorithms[1],
					algorithms[0], privKey);

			SignedData signedData = new SignedData(input, SignedData.EXPLICIT);
			signedData.setSecurityProvider(getSecurityProvider());
			signedData.addCertificates(new Certificate[] { cert });

			//Check PAdES Flag
			if (IConfigurationConstants.TRUE.equalsIgnoreCase(parameter.getConfiguration().getValue(IConfigurationConstants.SIG_PADES_FORCE_FLAG))) {
				setAttributes(cert, signer1);
			} else {
				setAttributes("application/pdf", cert,  requestedSignature.getStatus().getSigningDate().getTime(), signer1);
			}

			signedData.addSignerInfo(signer1);
			ContentInfo contentInfo = new ContentInfo(signedData);
			byte[] signature = contentInfo.getEncoded();

			SignatureUtils.verifySignature(signature, input);

			return signature;
			
		} catch (NoSuchAlgorithmException | iaik.cms.CMSException | CertificateException | CodingException | PDFASError e) {
			throw new PdfAsSignatureException("error.pdf.sig.01", e);
		}
	}

}
