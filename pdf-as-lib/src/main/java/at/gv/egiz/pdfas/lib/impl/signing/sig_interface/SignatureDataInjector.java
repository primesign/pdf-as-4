package at.gv.egiz.pdfas.lib.impl.signing.sig_interface;

import iaik.x509.X509Certificate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import org.apache.pdfbox.exceptions.SignatureException;

import at.gv.egiz.pdfas.common.utils.StreamUtils;

public class SignatureDataInjector extends SignatureDataExtractor {

	protected byte[]  signature;
	protected byte[]  oldSignatureData;
	
	public SignatureDataInjector(X509Certificate certificate, String filter,
			String subfilter, Calendar date, byte[] signature, byte[] signatureData) {
		super(certificate, filter, subfilter, date);
		this.signature = signature;
		this.oldSignatureData = signatureData;
	}

	@Override
	public byte[] sign(InputStream content) throws SignatureException,
			IOException {
		byte[] signatureData = StreamUtils.inputStreamToByteArray(content);
		
		FileOutputStream fos2 = new FileOutputStream("/home/afitzek/devel/pdfas_neu/sign2.pdf");
		fos2.write(signatureData);
		fos2.close();
		
		if(signatureData.length != this.oldSignatureData.length) {
			throw new SignatureException("Signature Data missmatch!");
		}
		
		for(int i = 0; i < signatureData.length; i++) {
			if(signatureData[i] != this.oldSignatureData[i]) {
				throw new SignatureException("Signature Data missmatch! " + i + " " + signatureData[i] + " vs " + this.oldSignatureData[i]);
			}
		}
		
		return signature;
	}

}
