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
package at.gv.egiz.pdfas.lib.impl.verify;

import iaik.x509.X509Certificate;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.verify.SignatureCheck;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;

public class VerifyResultImpl implements VerifyResult {

	private boolean verificationDone;
	private boolean qualifiedCertificate;
	private PdfAsException verificationException;
	private SignatureCheck certificateCheck;
	private SignatureCheck valueCheck;
	private SignatureCheck manifestCheck;
	private byte[] signatureData;
	private X509Certificate signerCertificate;
	
	public boolean isVerificationDone() {
		return verificationDone;
	}
	
	public void setVerificationDone(boolean value) {
		this.verificationDone = value;
	}

	public PdfAsException getVerificationException() {
		return verificationException;
	}
	
	public void setVerificationException(PdfAsException e) {
		verificationException = e;
	}

	public SignatureCheck getCertificateCheck() {
		return certificateCheck;
	}

	public void setCertificateCheck(SignatureCheck certificateCheck) {
		this.certificateCheck=certificateCheck;
	}
	
	public SignatureCheck getValueCheckCode() {
		return valueCheck;
	}
	
	public void setValueCheckCode(SignatureCheck valueCheck) {
		this.valueCheck=valueCheck;
	}

	public SignatureCheck getManifestCheckCode() {
		return manifestCheck;
	}
	
	public void setManifestCheckCode(SignatureCheck manifestCheck) {
		this.manifestCheck=manifestCheck;
	}

	public boolean isQualifiedCertificate() {
		return qualifiedCertificate;
	}
	
	public void setQualifiedCertificate(boolean value) {
		this.qualifiedCertificate = value;
	}

	public X509Certificate getSignerCertificate() {
		return signerCertificate;
	}
	
	public void setSignerCertificate(X509Certificate signerCertificate) {
		this.signerCertificate = signerCertificate;
	}

	public void setSignatureData(byte[] signaturData) {
		this.signatureData = signaturData;
	}
	
	public byte[] getSignatureData() {
		return signatureData;
	}

}
