package at.gv.egiz.pdfas.api.ws;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="VerificationResponse")
public class PDFASVerificationResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2581929633991566751L;

	int valueCode;
	int certificateCode;
	byte[] signerCertificate;
	
	@XmlElement(required = true, nillable = false, name="valueCode")
	public int getValueCode() {
		return valueCode;
	}
	public void setValueCode(int valueCode) {
		this.valueCode = valueCode;
	}
	
	@XmlElement(required = true, nillable = false, name="certificateCode")
	public int getCertificateCode() {
		return certificateCode;
	}
	public void setCertificateCode(int certificateCode) {
		this.certificateCode = certificateCode;
	}
	
	@XmlElement(required = false, nillable = false, name="signerCertificate")
	public byte[] getSignerCertificate() {
		return signerCertificate;
	}
	public void setSignerCertificate(byte[] signerCertificate) {
		this.signerCertificate = signerCertificate;
	}
}
