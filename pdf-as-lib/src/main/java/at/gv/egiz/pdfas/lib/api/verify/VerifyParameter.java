package at.gv.egiz.pdfas.lib.api.verify;

import at.gv.egiz.pdfas.lib.api.PdfAsParameter;

public interface VerifyParameter extends PdfAsParameter {
	
	public int getWhichSignature();
	
	public void setWhichSignature(int which);
}
