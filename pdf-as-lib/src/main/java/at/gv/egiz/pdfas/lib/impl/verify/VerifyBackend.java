package at.gv.egiz.pdfas.lib.impl.verify;

import java.util.List;

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;

public interface VerifyBackend {
	public List<VerifyResult> verify(VerifyParameter parameter) throws PDFASError;
}
