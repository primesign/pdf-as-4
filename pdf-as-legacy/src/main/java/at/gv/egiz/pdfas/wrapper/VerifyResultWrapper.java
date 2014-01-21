package at.gv.egiz.pdfas.wrapper;

import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import at.gv.egiz.pdfas.api.commons.Constants;
import at.gv.egiz.pdfas.api.exceptions.PdfAsException;
import at.gv.egiz.pdfas.api.exceptions.PdfAsWrappedException;
import at.gv.egiz.pdfas.api.io.DataSource;
import at.gv.egiz.pdfas.api.verify.SignatureCheck;
import at.gv.egiz.pdfas.api.verify.VerifyResult;
import at.gv.egiz.pdfas.api.xmldsig.XMLDsigData;

public class VerifyResultWrapper implements VerifyResult {

	private at.gv.egiz.pdfas.lib.api.verify.VerifyResult newResult;
	
	public VerifyResultWrapper(at.gv.egiz.pdfas.lib.api.verify.VerifyResult newResult) {
		this.newResult = newResult; 
	}

	public String getSignatureType() {
		return null;
	}

	public DataSource getSignedData() {
		return new ByteArrayDataSource_OLD(this.newResult.getSignatureData());
	}

	public X509Certificate getSignerCertificate() {
		return this.newResult.getSignerCertificate();
	}

	public Date getSigningTime() {
		return null;
	}

	public Object getInternalSignatureInformation() {
		return null;
	}

	public String getTimeStampValue() {
		return null;
	}

	public void setNonTextualObjects(List nonTextualObjects) {
	}

	public boolean isVerificationDone() {
		return this.newResult.isVerificationDone();
	}

	public PdfAsException getVerificationException() {
		return new PdfAsWrappedException(this.newResult.getVerificationException());
	}

	public SignatureCheck getCertificateCheck() {
		return new SignatureCheckWrapper(this.newResult.getCertificateCheck());
	}

	public SignatureCheck getValueCheckCode() {
		return new SignatureCheckWrapper(this.newResult.getValueCheckCode());
	}

	public SignatureCheck getManifestCheckCode() {
		return new SignatureCheckWrapper(this.newResult.getManifestCheckCode());
	}

	public boolean isQualifiedCertificate() {
		return this.newResult.isQualifiedCertificate();
	}

	public boolean isPublicAuthority() {
		return false;
	}

	public String getPublicAuthorityCode() {
		return null;
	}

	public List getPublicProperties() {
		return null;
	}

	public Date getVerificationTime() {
		return null;
	}

	public String getHashInputData() {
		return null;
	}

	public List getNonTextualObjects() {
		return null;
	}

	public boolean hasNonTextualObjects() {
		return false;
	}

	public XMLDsigData getReconstructedXMLDsig() {
		return null;
	}
	
	
	
}
