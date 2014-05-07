package at.gv.egiz.pdfas.api.ws;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="BulkSignRequest")
public class PDFASBulkSignRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2335377335418211956L;

	List<PDFASSignRequest> signRequests;

	@XmlElement(required = true, nillable = false, name="signRequests")
	public List<PDFASSignRequest> getSignRequests() {
		return signRequests;
	}

	public void setSignRequests(List<PDFASSignRequest> signRequests) {
		this.signRequests = signRequests;
	}
}
