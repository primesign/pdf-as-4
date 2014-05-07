package at.gv.egiz.pdfas.api.ws;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="BulkSignResponse")
public class PDFASBulkSignResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4218977934947700835L;
	
	List<PDFASSignResponse> signResponses;

	@XmlElement(required = true, nillable = false, name="signResponses")
	public List<PDFASSignResponse> getSignResponses() {
		return signResponses;
	}

	public void setSignResponses(List<PDFASSignResponse> signResponses) {
		this.signResponses = signResponses;
	}
}
