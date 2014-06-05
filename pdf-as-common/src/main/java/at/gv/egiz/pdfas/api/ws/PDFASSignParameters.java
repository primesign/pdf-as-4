package at.gv.egiz.pdfas.api.ws;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="SignParameters")
public class PDFASSignParameters implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2375108993871456465L;

	@XmlType(name="Connector")
	public enum Connector {
		JKS("jks"),
		MOA("moa"),
		BKU("bku"),
		MOBILEBKU("mobilebku"),
		ONLINEBKU("onlinebku");
		
		
		private final String name;       

	    private Connector(String s) {
	        name = s;
	    }

	    public boolean equalsName(String otherName){
	        return (otherName == null)? false:name.equals(otherName);
	    }

	    public String toString(){
	       return name;
	    }
	}
	
	
	Connector connector;
	
	
	String position;
	String invokeUrl;
	String invokeErrorUrl;
	String transactionId;
	String profile;
	
	@XmlElement(required = true, nillable = false, name="connector")
	public Connector getConnector() {
		return connector;
	}
	
	public void setConnector(Connector connector) {
		this.connector = connector;
	}
	
	@XmlElement(required = false, nillable = true, name="position")
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	
	@XmlElement(required = false, nillable = true, name="profile")
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	
	@XmlElement(required = false, nillable = true, name="invoke-url")
	public String getInvokeURL() {
		return invokeUrl;
	}
	public void setInvokeURL(String invokeUrl) {
		this.invokeUrl = invokeUrl;
	}
	
	@XmlElement(required = false, nillable = true, name="invoke-error-url")
	public String getInvokeErrorURL() {
		return invokeErrorUrl;
	}
	public void setInvokeErrorURL(String invokeErrorUrl) {
		this.invokeErrorUrl = invokeErrorUrl;
	}
	
	@XmlElement(required = false, nillable = true, name="transactionId")
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
}
