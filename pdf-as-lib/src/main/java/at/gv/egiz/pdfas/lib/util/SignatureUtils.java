package at.gv.egiz.pdfas.lib.util;

import iaik.asn1.CodingException;
import iaik.asn1.ObjectID;
import iaik.asn1.structures.Attribute;
import iaik.cms.CMSException;
import iaik.cms.SignedData;
import iaik.cms.SignerInfo;
import iaik.smime.ess.ESSCertID;
import iaik.smime.ess.ESSCertIDv2;
import iaik.smime.ess.SigningCertificate;
import iaik.smime.ess.SigningCertificateV2;
import iaik.x509.X509Certificate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.ErrorConstants;
import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSignatureException;
import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.lib.impl.verify.VerifyResultImpl;

public class SignatureUtils implements ErrorConstants {

	private static final Logger logger = LoggerFactory
			.getLogger(SignatureUtils.class);

	public static VerifyResult verifySignature(byte[] signature, byte[] input)
			throws PDFASError {
		// List<VerifyResult> results = new ArrayList<VerifyResult>();
		try {
			SignedData signedData = new SignedData(new ByteArrayInputStream(
					signature));

			signedData.setContent(input);

			// get the signer infos
			SignerInfo[] signerInfos = signedData.getSignerInfos();
			if (signerInfos.length == 0) {
				logger.warn("Invalid signature (no signer information)");
				throw new PDFASError(ERROR_SIG_INVALID_BKU_SIG);
			}

			if (signerInfos.length != 1) {
				logger.warn("Invalid signature (multiple signer information)");
				throw new PDFASError(ERROR_SIG_INVALID_BKU_SIG);
			}
			// verify the signatures
			// for (int i = 0; i < signerInfos.length; i++) {
			VerifyResultImpl verifyResult = new VerifyResultImpl();
			// results.add(verifyResult);
			try {
				logger.debug("Signature Algo: {}, Digest {}",
						signedData.getSignerInfos()[0].getSignatureAlgorithm(),
						signedData.getSignerInfos()[0].getDigestAlgorithm());
				// verify the signature for SignerInfo at index i
				X509Certificate signer_cert = signedData.verify(0);

				// Must include Signing Certificate!
				Attribute signedCertificate = signerInfos[0]
						.getSignedAttribute(ObjectID.signingCertificate);

				if (signedCertificate == null) {
					signedCertificate = signerInfos[0]
							.getSignedAttribute(ObjectID.signingCertificateV2);
					if (signedCertificate == null) {
						logger.warn("Signature ERROR missing signed Signing Certificate: ");

						throw new PDFASError(ERROR_SIG_INVALID_BKU_SIG);
					} else {
						// Validate signingCertificate2
						try {
							SigningCertificateV2 signingCert = (SigningCertificateV2)signedCertificate.getAttributeValue();
							
							if (signingCert.isSignerCertificate(signer_cert)) {
								// OK
								logger.debug("Found and verified SigningCertificateV2");
							} else {
								logger.error("Signature ERROR certificate missmatch, misbehaving Signature Backend?");

								throw new PDFASError(ERROR_SIG_INVALID_BKU_SIG);
							}
						} catch (Throwable e) {
							logger.error("Signature ERROR wrong encoding for ESSCertIDv2, misbehaving Signature Backend?");

							throw new PDFASError(ERROR_SIG_INVALID_BKU_SIG, e);
						} 
					}
				} else {
					// Validate signingCertificate
					try {
						SigningCertificate signingCert = (SigningCertificate)signedCertificate.getAttributeValue();
						if (signingCert.isSignerCertificate(signer_cert)) {
							// OK
							logger.debug("Found and verified SigningCertificate");
						} else {
							logger.warn("Signature ERROR certificate missmatch, misbehaving Signature Backend?");

							throw new PDFASError(ERROR_SIG_INVALID_BKU_SIG);
						}
					} catch (Throwable e) {
						logger.error("Signature ERROR wrong encoding for ESSCertIDv2, misbehaving Signature Backend?");

						throw new PDFASError(ERROR_SIG_INVALID_BKU_SIG, e);
					}
				}

				// if the signature is OK the certificate of the
				// signer is returned
				logger.debug("Signature OK");
				verifyResult.setSignerCertificate(signer_cert);

			} catch (SignatureException ex) {
				// if the signature is not OK a SignatureException
				// is thrown
				logger.warn(
						"Signature ERROR from signer: "
								+ signedData.getCertificate(
										signerInfos[0].getSignerIdentifier())
										.getSubjectDN(), ex);

				verifyResult.setSignerCertificate(signedData
						.getCertificate(signerInfos[0].getSignerIdentifier()));
				throw new PDFASError(ERROR_SIG_INVALID_BKU_SIG, ex);
			}

			return verifyResult;
			// }
		} catch (CMSException e) {
			throw new PDFASError(ERROR_SIG_INVALID_BKU_SIG, e);
		} catch (IOException e) {
			throw new PDFASError(ERROR_SIG_INVALID_BKU_SIG, e);
		}

	}
}
