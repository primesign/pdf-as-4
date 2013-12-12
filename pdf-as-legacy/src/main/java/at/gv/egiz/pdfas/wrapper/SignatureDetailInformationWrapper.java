package at.gv.egiz.pdfas.wrapper;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import at.gv.egiz.pdfas.api.io.DataSource;
import at.gv.egiz.pdfas.api.sign.SignatureDetailInformation;
import at.gv.egiz.pdfas.api.sign.pos.SignaturePosition;
import at.gv.egiz.pdfas.common.utils.DNUtils;
import at.gv.egiz.pdfas.lib.api.StatusRequest;

public class SignatureDetailInformationWrapper implements
		SignatureDetailInformation {

	public SignParameterWrapper wrapper;
	private StatusRequest status;
	private DataSource dataSource;
	private iaik.x509.X509Certificate certificate;

	public SignatureDetailInformationWrapper(iaik.x509.X509Certificate cert) {
		this.certificate = cert;
	}
	
	public StatusRequest getStatus() {
		return status;
	}

	public void setStatus(StatusRequest status) {
		this.status = status;
	}
	

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getSignatureData() {
		return this.dataSource;
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
		return this.certificate.getIssuerDN().getName();
	}

	public Map getIssuerDNMap() {
		try {
			return DNUtils.dnToMap(getIssuer());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getSubjectName() {
		return this.certificate.getSubjectDN().getName();
	}

	public String getSerialNumber() {
		return this.certificate.getSerialNumber().toString();
	}

	public String getSigAlgorithm() {
		return this.certificate.getSigAlgName();
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
		return null;
	}

	public Map getSubjectDNMap() {
		try {
			return DNUtils.dnToMap(getSubjectName());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public X509Certificate getX509Certificate() {
		return this.certificate;
	}

	public boolean isTextual() {
		return false;
	}

	public boolean isBinary() {
		return true;
	}

}
