package at.gv.egiz.pdfas.lib.util;

import iaik.cms.CMSException;
import iaik.cms.SignedData;
import iaik.cms.SignerInfo;
import iaik.x509.X509Certificate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.SignatureException;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsSignatureException;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.lib.impl.verify.VerifyResultImpl;

public class SignatureUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(SignatureUtils.class);

	public static int countSignatures(PDDocument doc, String sigName) {
		int count = 0;
		COSDictionary trailer = doc.getDocument().getTrailer();
		COSDictionary root = (COSDictionary) trailer
				.getDictionaryObject(COSName.ROOT);
		COSDictionary acroForm = (COSDictionary) root
				.getDictionaryObject(COSName.ACRO_FORM);
		COSArray fields = (COSArray) acroForm
				.getDictionaryObject(COSName.FIELDS);
		for (int i = 0; i < fields.size(); i++) {
			COSDictionary field = (COSDictionary) fields.getObject(i);
			String type = field.getNameAsString("FT");
			if ("Sig".equals(type)) {
				String name = field.getString(COSName.T);
				if (name != null) {
					logger.debug("Found Sig: " + name);
					try {
						if (name.startsWith(sigName)) {
							String numberString = name.replace(sigName, "");

							logger.debug("Found Number: " + numberString);

							int SigIDX = Integer.parseInt(numberString);
							if(SigIDX > count) {
								count = SigIDX;
							}
						}
					} catch (Throwable e) {
						logger.info("Found a different Signature, we do not need to count this.");
					}
				}
			}

		}

		count++;
		
		logger.debug("Returning sig number: " + count);
		
		return count;
	}

	
	public static VerifyResult verifySignature(byte[] signature, byte[] input) throws PdfAsSignatureException {
		//List<VerifyResult> results = new ArrayList<VerifyResult>();
		try {
			SignedData signedData = new SignedData(new ByteArrayInputStream(
					signature));

			signedData.setContent(input);
			
			// get the signer infos
			SignerInfo[] signerInfos = signedData.getSignerInfos();
			if (signerInfos.length == 0) {
				throw new PdfAsSignatureException("Invalid Signature (no signer info created!)", null);
			}
			
			if (signerInfos.length != 1) {
				throw new PdfAsSignatureException("Invalid Signature (multiple signer infos found!)", null);
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
					throw new PdfAsSignatureException("error.pdf.sig.08", ex);
				}
				
				return verifyResult;
			//}
		} catch (CMSException e) {
			throw new PdfAsSignatureException("error.pdf.sig.08", e);
		} catch (IOException e) {
			throw new PdfAsSignatureException("error.pdf.sig.08", e);
		}
		
		
	}
}
