package at.gv.egiz.pdfas.wrapper;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import at.gv.egiz.pdfas.api.io.DataSource;
import at.gv.egiz.pdfas.api.sign.SignatureDetailInformation;
import at.gv.egiz.pdfas.api.sign.pos.SignaturePosition;

public class SignatureDetailInformationWrapper implements SignatureDetailInformation {

	private SignParameterWrapper wrapper;
	
	public DataSource getSignatureData() {
		// TODO
		return null;
	}

	public SignaturePosition getSignaturePosition() {
		// TODO Auto-generated method stub
		return null;
	}

	public List getNonTextualObjects() {
		return null;
	}

	public Date getSignDate() {
		return null;
	}

	public String getIssuer() {
		return null;
	}

	public Map getIssuerDNMap() {
		return null;
	}

	public String getSubjectName() {
		return null;
	}

	public String getSerialNumber() {
		return null;
	}

	public String getSigAlgorithm() {
		return null;
	}

	public String getSigID() {
		return null;
	}

	public String getSigKZ() {
		return null;
	}

	public String getSignatureValue() {
		return null;
	}

	public String getSigTimeStamp() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map getSubjectDNMap() {
		return null;
	}

	public X509Certificate getX509Certificate() {
		return null;
	}

	public boolean isTextual() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isBinary() {
		return true;
	}

}
