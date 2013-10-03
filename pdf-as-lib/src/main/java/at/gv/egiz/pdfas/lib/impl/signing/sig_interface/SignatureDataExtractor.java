package at.gv.egiz.pdfas.lib.impl.signing.sig_interface;

import iaik.x509.X509Certificate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import org.apache.pdfbox.exceptions.SignatureException;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;

import at.gv.egiz.pdfas.common.utils.StreamUtils;

public class SignatureDataExtractor implements PDFASSignatureInterface {

	protected X509Certificate certificate;
	protected byte[] signatureData;
	
	protected String pdfSubFilter;
	protected String pdfFilter;
	protected PDSignature signature;
	protected int[] byteRange;
	protected Calendar date;
	
	public SignatureDataExtractor(X509Certificate certificate, 
			String filter, String subfilter, Calendar date) {
		this.certificate = certificate;
		this.pdfFilter = filter;
		this.pdfSubFilter = subfilter;
		this.date = date;
	}
	
	public X509Certificate getCertificate() {
		return certificate;
	}

	public String getPDFSubFilter() {
		return this.pdfSubFilter;
	}

	public String getPDFFilter() {
		return this.pdfFilter;
	}

	public byte[] getSignatureData() {
		return this.signatureData;
	}

	public byte[] sign(InputStream content) throws SignatureException,
			IOException {
		signatureData = StreamUtils.inputStreamToByteArray(content);
		byteRange = this.signature.getByteRange();
		return new byte[] { 0 };
	}

	public void setPDSignature(PDSignature signature) {
		this.signature = signature;
	}

	public int[] getByteRange() {
		return byteRange;
	}

	public Calendar getSigningDate() {
		return this.date;
	}
	
	
	
}
