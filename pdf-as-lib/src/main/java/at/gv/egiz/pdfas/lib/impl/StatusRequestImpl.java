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
package at.gv.egiz.pdfas.lib.impl;

import java.security.cert.CertificateException;

import at.gv.egiz.pdfas.lib.api.StatusRequest;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import iaik.x509.X509Certificate;

public class StatusRequestImpl implements StatusRequest {

	private boolean needCertificate = false;
	private boolean needSignature = false;
	private boolean isReady = false;
	private X509Certificate certificate;
	private byte[] encodedSignature;
	private byte[] signatureData;
	private int[] byteRange;
	
	private OperationStatus status;
	
	public OperationStatus getStatus() {
		return status;
	}

	public void setStatus(OperationStatus status) {
		this.status = status;
	}

	public void setSignatureData(byte[] signatureData) {
		this.signatureData = signatureData;
	}

	public void setByteRange(int[] byteRange) {
		this.byteRange = byteRange;
	}

	public X509Certificate getCertificate() {
		return this.certificate;
	}

	/**
	 * @deprecated Use {@link #getEncodedSignature()} instead.
	 */
	public byte[] getSignature() {
		return this.encodedSignature;
	}
	
	public byte[] getEncodedSignature() {
		return this.encodedSignature;
	}
	
	public void setNeedSignature(boolean value) {
		this.needSignature = value;
	}
	
	public void setNeedCertificate(boolean value) {
		this.needCertificate = value;
	}
	
	public boolean needCertificate() {
		return needCertificate;
	}

	public boolean needSignature() {
		return needSignature;
	}

	public boolean isReady() {
		return isReady;
	}
	
	public void setIsReady(boolean value) {
		this.isReady = value;
	}

	public byte[] getSignatureData() {
		return signatureData;
	}

	public int[] getSignatureDataByteRange() {
		return byteRange;
	}

	public void setCertificate(byte[] encodedCertificate) throws CertificateException {
		this.certificate = new X509Certificate(encodedCertificate);
	}

	public void setSigature(byte[] signatureValue) {
		this.encodedSignature = signatureValue;
	}

	public SignParameter getSignParameter() {
		return this.status.getSignParamter();
	}
	
	
}
