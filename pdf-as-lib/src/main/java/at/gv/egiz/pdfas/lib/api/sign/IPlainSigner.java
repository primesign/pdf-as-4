package at.gv.egiz.pdfas.lib.api.sign;

import iaik.x509.X509Certificate;

import java.io.IOException;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.exceptions.SignatureException;

public interface IPlainSigner {
	public X509Certificate getCertificate();
    public byte[] sign(byte[] input) throws SignatureException, IOException;
    public String getPDFSubFilter();
    public String getPDFFilter();
}
