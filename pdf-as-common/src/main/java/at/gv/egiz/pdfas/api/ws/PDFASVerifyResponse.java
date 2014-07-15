package at.gv.egiz.pdfas.api.ws;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="VerifyResponse")
public class PDFASVerifyResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1984406533559692943L;

	List<PDFASVerifyResult> verifyResults;

	@XmlElement(required = true, nillable = false, name="verifyResults")
	public List<PDFASVerifyResult> getVerifyResults() {
		return verifyResults;
	}

	public void setVerifyResults(List<PDFASVerifyResult> verifyResults) {
		this.verifyResults = verifyResults;
	}
}
