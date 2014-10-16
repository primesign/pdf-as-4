package at.gv.egiz.pdfas.api.ws;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="PropertyEntry")
public class PDFASPropertyEntry {
	String key;
	String value;
	
	@XmlElement(required = true, nillable = false, name="key")
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	@XmlElement(required = true, nillable = false, name="value")
	public String getValue() {
		return value;
	}
	public void setvalue(String value) {
		this.value = value;
	}
}
