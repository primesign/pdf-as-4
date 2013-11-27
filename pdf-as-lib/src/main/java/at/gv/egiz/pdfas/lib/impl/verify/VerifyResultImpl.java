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
