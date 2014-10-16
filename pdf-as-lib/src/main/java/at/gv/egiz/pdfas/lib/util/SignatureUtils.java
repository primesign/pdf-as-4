package at.gv.egiz.pdfas.lib.util;

import iaik.cms.CMSException;
import iaik.cms.SignedData;
import iaik.cms.SignerInfo;
import iaik.x509.X509Certificate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.SignatureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.ErrorConstants;
import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.lib.impl.verify.VerifyResultImpl;

public class SignatureUtils implements ErrorConstants {

	private static final Logger logger = LoggerFactory
			.getLogger(SignatureUtils.class);
	
	public static VerifyResult verifySignature(byte[] signature, byte[] input) throws PDFASError {
		//List<VerifyResult> results = new ArrayList<VerifyResult>();
		try {
			SignedData signedData = new SignedData(new ByteArrayInputStream(
					signature));

			signedData.setContent(input);
			
			// get the signer infos
			SignerInfo[] signerInfos = signedData.getSignerInfos();
			if (signerInfos.length == 0) {
				logger.error("Invalid signature (no signer information)");
				throw new PDFASError(ERROR_SIG_INVALID_BKU_SIG);
			}
			
			if (signerInfos.length != 1) {
				logger.error("Invalid signature (multiple signer information)");
				throw new PDFASError(ERROR_SIG_INVALID_BKU_SIG);
			}
			// verify the signatures
			//for (int i = 0; i < signerInfos.length; i++) {
				VerifyResultImpl verifyResult = new VerifyResultImpl();
				//results.add(verifyResult);
				try {
					logger.info("Signature Algo: {}, Digest {}", signedData
							.getSignerInfos()[0].getSignatureAlgorithm(),
							signedData.getSignerInfos()[0].getDigestAlgorithm());
					// verify the signature for SignerInfo at index i
					X509Certificate signer_cert = signedData.verify(0);
					// if the signature is OK the certificate of the
					// signer is returned
					logger.info("Signature OK from signer: "
							+ signer_cert.getSubjectDN());
					verifyResult.setSignerCertificate(signer_cert);

				} catch (SignatureException ex) {
					// if the signature is not OK a SignatureException
					// is thrown
					logger.error(
							"Signature ERROR from signer: "
									+ signedData.getCertificate(
											signerInfos[0]
													.getSignerIdentifier())
											.getSubjectDN(), ex);

					verifyResult.setSignerCertificate(signedData
							.getCertificate(signerInfos[0]
									.getSignerIdentifier()));
					throw new PDFASError(ERROR_SIG_INVALID_BKU_SIG, ex);
				}
				
				return verifyResult;
			//}
		} catch (CMSException e) {
			throw new PDFASError(ERROR_SIG_INVALID_BKU_SIG, e);
		} catch (IOException e) {
			throw new PDFASError(ERROR_SIG_INVALID_BKU_SIG, e);
		}
		
		
	}
}
