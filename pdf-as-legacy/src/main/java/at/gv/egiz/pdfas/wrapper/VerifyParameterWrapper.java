package at.gv.egiz.pdfas.wrapper;

import at.gv.egiz.pdfas.api.verify.VerifyParameters;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSource;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;

public class VerifyParameterWrapper {

	public static VerifyParameter toNewParameters(VerifyParameters oldParameters, Configuration config) {
		VerifyParameter parameter = PdfAsFactory.createVerifyParameter(config, 
				new ByteArrayDataSource(oldParameters.getDocument().getAsByteArray()));
		
		parameter.setWhichSignature(oldParameters.getSignatureToVerify());
		parameter.setVerificationTime(oldParameters.getVerificationTime());
		return parameter;
	}
	
}
