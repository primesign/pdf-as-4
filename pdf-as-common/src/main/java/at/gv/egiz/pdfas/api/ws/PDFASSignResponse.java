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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="SignResponse")
public class PDFASSignResponse implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6369697640117556071L;
	
	String requestID;
	String error;
	byte[] signedPDF;
	PDFASVerificationResponse verificationResponse;
	String redirectUrl;
	
	@XmlElement(required = true, nillable = false, name="requestID")
	public String getRequestID() {
		return requestID;
	}
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	
	@XmlElement(required = false, nillable = false, name="signedPDF")
	public byte[] getSignedPDF() {
		return signedPDF;
	}
	public void setSignedPDF(byte[] signedPDF) {
		this.signedPDF = signedPDF;
	}
	
	@XmlElement(required = false, nillable = false, name="verificationResponse")
	public PDFASVerificationResponse getVerificationResponse() {
		return verificationResponse;
	}
	public void setVerificationResponse(PDFASVerificationResponse verificationResponse) {
		this.verificationResponse = verificationResponse;
	}
	
	@XmlElement(required = false, name="error")
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	@XmlElement(required = false, name="redirectUrl")
	public String getRedirectUrl() {
		return redirectUrl;
	}
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
}
