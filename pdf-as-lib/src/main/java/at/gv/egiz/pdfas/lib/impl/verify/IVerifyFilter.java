package at.gv.egiz.pdfas.lib.impl.verify;

import java.util.List;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;

public interface IVerifyFilter {
	public List<VerifyResult> verify(byte[] contentData, byte[] signatureContent) throws PdfAsException;
	public List<FilterEntry> getFiters();
}
