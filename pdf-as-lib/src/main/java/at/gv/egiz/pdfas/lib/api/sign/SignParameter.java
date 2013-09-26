package at.gv.egiz.pdfas.lib.api.sign;

import at.gv.egiz.pdfas.lib.api.DataSink;
import at.gv.egiz.pdfas.lib.api.PdfAsParameter;

public interface SignParameter extends PdfAsParameter {
	
	public String getSignatureProfileId();

	public void setSignatureProfileId(String signatureProfileId);

	public String getSignaturePosition();

	public void setSignaturePosition(String signaturePosition);

	public void setOutput(DataSink output);
	
	public DataSink getOutput();
}
