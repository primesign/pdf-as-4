package at.gv.egiz.pdfas.sigs.pkcs7detached;

import iaik.cms.SignedData;
import iaik.cms.SignerInfo;
import iaik.x509.X509Certificate;

import java.io.ByteArrayInputStream;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.lib.impl.verify.FilterEntry;
import at.gv.egiz.pdfas.lib.impl.verify.IVerifyFilter;
import at.gv.egiz.pdfas.lib.impl.verify.VerifyResultImpl;

public class PKCS7DetachedVerifier implements IVerifyFilter {

	private static final Logger logger = LoggerFactory.getLogger(PKCS7DetachedVerifier.class);
	
	public List<VerifyResult> verify(byte[] contentData, byte[] signatureContent)
			throws PdfAsException {
		try {
			List<VerifyResult> result = new ArrayList<VerifyResult>();
			SignedData signedData = new SignedData(new ByteArrayInputStream(
					signatureContent));
			signedData.setContent(contentData);

			// get the signer infos
			SignerInfo[] signerInfos = signedData.getSignerInfos();
			// verify the signatures
			for (int i = 0; i < signerInfos.length; i++) {
				VerifyResultImpl verifyResult = new VerifyResultImpl();
				try {
					
					// verify the signature for SignerInfo at index i
					X509Certificate signer_cert = signedData.verify(i);
					// if the signature is OK the certificate of the
					// signer is returned
					logger.info("Signature OK from signer: "
							+ signer_cert.getSubjectDN());
					verifyResult.setSignerCertificate(signer_cert);
				} catch (SignatureException ex) {
					// if the signature is not OK a SignatureException
					// is thrown
					logger.info("Signature ERROR from signer: "
							+ signedData.getCertificate(
									signerInfos[i].getSignerIdentifier())
									.getSubjectDN());
					
					verifyResult.setSignerCertificate(
							signedData.getCertificate(signerInfos[i].getSignerIdentifier()));
				}
				result.add(verifyResult);
			}

			return result;
		} catch (Throwable e) {
			throw new PdfAsException("Verify failed", e);
		}
	}

	public List<FilterEntry> getFiters() {
		List<FilterEntry> result = new ArrayList<FilterEntry>();
		result.add(new FilterEntry(PDSignature.FILTER_ADOBE_PPKLITE, PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED));
		return result;
	}

}
