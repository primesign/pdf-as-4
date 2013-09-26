package at.gv.egiz.pdfas.lib.api.sign;

import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.IDataSource;
import at.gv.egiz.pdfas.lib.api.PdfAsParameter;

public class SignParameter extends PdfAsParameter {

	protected String signatureProfileId = null;
	protected String signaturePosition = null;
	
	public SignParameter(Configuration configuration, 
			IDataSource dataSource) {
		super(configuration, dataSource);
	}

	// ========================================================================
	
	public String getSignatureProfileId() {
		return signatureProfileId;
	}

	public void setSignatureProfileId(String signatureProfileId) {
		this.signatureProfileId = signatureProfileId;
	}

	public String getSignaturePosition() {
		return signaturePosition;
	}

	public void setSignaturePosition(String signaturePosition) {
		this.signaturePosition = signaturePosition;
	}
	
	
	
}
