/*******************************************************************************
 * <copyright> Copyright 2014 by E-Government Innovation Center EGIZ, Graz, Austria </copyright>
 * PDF-AS has been contracted by the E-Government Innovation Center EGIZ, a
 * joint initiative of the Federal Chancellery Austria and Graz University of
 * Technology.
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * http://www.osor.eu/eupl/
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * 
 * This product combines work with different licenses. See the "NOTICE" text
 * file for details on the various modules and licenses.
 * The "NOTICE" text file is part of the distribution. Any derivative works
 * that you distribute must include a readable copy of the "NOTICE" text file.
 ******************************************************************************/
package at.gv.egiz.pdfas.api.ws;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="SignParameters")
public class PDFASSignParameters implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2375108993871456465L;

	@XmlType(name="Connector")
	public enum Connector {
		@XmlEnumValue("jks")
		JKS("jks"),
		@XmlEnumValue("moa")
		MOA("moa"),
		@XmlEnumValue("bku")
		BKU("bku"),
		@XmlEnumValue("mobilebku")
		MOBILEBKU("mobilebku"),
		@XmlEnumValue("onlinebku")
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
	String invokeTarget;
	String invokeErrorUrl;
	String transactionId;
	String profile;
	PDFASPropertyMap preprocessor;
	
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
	
	@XmlElement(required = false, nillable = true, name="invoke-target")
	public String getInvokeTarget() {
		return invokeTarget;
	}
	public void setInvokeTarget(String invokeTarget) {
		this.invokeTarget = invokeTarget;
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

	@XmlElement(required = false, nillable = true, name="preprocessorArguments")
	public PDFASPropertyMap getPreprocessor() {
		return preprocessor;
	}

	public void setPreprocessor(PDFASPropertyMap preprocessor) {
		this.preprocessor = preprocessor;
	}
	
	
}
