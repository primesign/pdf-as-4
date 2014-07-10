package at.gv.egiz.pdfas.lib.impl.verify;

import java.util.Date;
import java.util.List;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;

public interface IVerifier {
	public List<VerifyResult> verify(byte[] signature,
			byte[] signatureContent, Date verificationTime) throws PdfAsException;
	
	public void setConfiguration(Configuration config);
}
