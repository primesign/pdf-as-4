package at.gv.egiz.pdfas.web.ws;

import java.io.Serializable;

public class PDFASSignParameters implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2375108993871456465L;

	public enum Connector {
		JKS,
		MOA
	}
	
	Connector connector;
	String position;
	String profile;
	
	
	public Connector getConnector() {
		return connector;
	}
	public void setConnector(Connector connector) {
		this.connector = connector;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	
	
	
}
