package at.gv.egiz.pdfas.lib.impl.verify;

import iaik.asn1.ObjectID;
import iaik.asn1.structures.AlgorithmID;
import iaik.asn1.structures.Attribute;
import iaik.cms.ContentInfo;
import iaik.cms.SignedData;
import iaik.cms.SignerInfo;
import iaik.smime.ess.SigningCertificate;
import iaik.smime.ess.SigningCertificateV2;
import iaik.x509.X509Certificate;

import java.io.ByteArrayInputStream;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSignatureException;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter.SignatureVerificationLevel;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;

public class IntegrityVerifier implements IVerifier {

	private static final Logger logger = LoggerFactory
			.getLogger(IntegrityVerifier.class);

	public List<VerifyResult> verify(byte[] signature, byte[] signatureContent,
			Date verificationTime) throws PdfAsException {
		try {
			List<VerifyResult> result = new ArrayList<VerifyResult>();

			SignedData signedData = new SignedData(signatureContent,
					new AlgorithmID[] { AlgorithmID.sha256, AlgorithmID.sha1,
							AlgorithmID.ripeMd160, AlgorithmID.ripeMd160_ISO });
			ContentInfo ci = new ContentInfo(
					new ByteArrayInputStream(signature));
			if (!ci.getContentType().equals(ObjectID.cms_signedData)) {
				throw new PdfAsException("error.pdf.verify.01");
			}
			// SignedData signedData = (SignedData)ci.getContent();
			// signedData.setContent(contentData);

			signedData.decode(ci.getContentInputStream());

			// get the signer infos
			SignerInfo[] signerInfos = signedData.getSignerInfos();
			// verify the signatures
			for (int i = 0; i < signerInfos.length; i++) {
				VerifyResultImpl verifyResult = new VerifyResultImpl();
				try {
					// verify the signature for SignerInfo at index i
					X509Certificate signer_cert = signedData.verify(i);

					// Verify signing Certificate 
					Attribute signedCertificate = signerInfos[0]
							.getSignedAttribute(ObjectID.signingCertificate);

					if (signedCertificate == null) {
						signedCertificate = signerInfos[0]
								.getSignedAttribute(ObjectID.signingCertificateV2);
						if (signedCertificate == null) {
							logger.warn("Signature ERROR missing signed Signing Certificate: ");

							throw new SignatureException("Signature ERROR missing signed Signing Certificate");
						} else {
							// Validate signingCertificate2
							try {
								SigningCertificateV2 signingCert = (SigningCertificateV2) signedCertificate
										.getAttributeValue();

								if (signingCert
										.isSignerCertificate(signer_cert)) {
									// OK
									logger.debug("Found and verified SigningCertificateV2");
								} else {
									logger.warn("Signature ERROR certificate missmatch, misbehaving Sign Backend?");

									throw new SignatureException("Signature ERROR certificate missmatch");
								}
							} catch (Throwable e) {
								logger.error("Signature ERROR wrong encoding for ESSCertIDv2, misbehaving Signature Backend?");

								throw new SignatureException("Signature ERROR wrong encoding for ESSCertIDv2");
							}
						}
					} else {
						// Validate signingCertificate
						try {
							SigningCertificate signingCert = (SigningCertificate) signedCertificate
									.getAttributeValue();
							if (signingCert.isSignerCertificate(signer_cert)) {
								// OK
								logger.debug("Found and verified SigningCertificate");
							} else {
								logger.warn("Signature ERROR certificate missmatch");

								throw new SignatureException("Signature ERROR certificate missmatch");
							}
						} catch (Throwable e) {
							logger.error("Signature ERROR wrong encoding for ESSCertIDv2, misbehaving Signature Backend?");

							throw new SignatureException("Signature ERROR wrong encoding for ESSCertIDv2", e);
						}
					}

					logger.debug("Signature Algo: {}, Digest {}", signedData
							.getSignerInfos()[i].getSignatureAlgorithm(),
							signedData.getSignerInfos()[i].getDigestAlgorithm());
					// if the signature is OK the certificate of the
					// signer is returned
					logger.debug("Signature OK from signer: "
							+ signer_cert.getSubjectDN());
					verifyResult.setSignerCertificate(signer_cert);
					verifyResult.setValueCheckCode(new SignatureCheckImpl(0,
							"OK"));
					verifyResult.setManifestCheckCode(new SignatureCheckImpl(
							99, "not checked"));
					verifyResult.setCertificateCheck(new SignatureCheckImpl(99,
							"not checked"));
					verifyResult.setVerificationDone(true);
				} catch (SignatureException ex) {
					// if the signature is not OK a SignatureException
					// is thrown
					logger.warn(
							"Signature ERROR from signer: "
									+ signedData.getCertificate(
											signerInfos[i]
													.getSignerIdentifier())
											.getSubjectDN(), ex);

					verifyResult.setSignerCertificate(signedData
							.getCertificate(signerInfos[i]
									.getSignerIdentifier()));
					verifyResult.setValueCheckCode(new SignatureCheckImpl(1,
							"failed to check signature"));
					verifyResult.setManifestCheckCode(new SignatureCheckImpl(
							99, "not checked"));
					verifyResult.setCertificateCheck(new SignatureCheckImpl(99,
							"not checked"));
					verifyResult.setVerificationDone(false);
					verifyResult
							.setVerificationException(new PdfAsSignatureException(
									"failed to check signature", ex));
				}
				result.add(verifyResult);
			}

			return result;
		} catch (Throwable e) {
			throw new PdfAsException("error.pdf.verify.02", e);
		}
	}

	public void setConfiguration(Configuration config) {

	}

	@Override
	public SignatureVerificationLevel getLevel() {
		return SignatureVerificationLevel.INTEGRITY_ONLY_VERIFICATION;
	}
}
