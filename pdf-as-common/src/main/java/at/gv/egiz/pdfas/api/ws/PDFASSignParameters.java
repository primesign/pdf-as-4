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
		JKS,
		MOA
	}
	
	
	Connector connector;
	
	
	String position;
	
	
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
	
	
	
}
