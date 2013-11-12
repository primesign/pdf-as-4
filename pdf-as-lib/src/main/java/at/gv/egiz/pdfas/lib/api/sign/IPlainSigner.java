package at.gv.egiz.pdfas.lib.api.sign;

import iaik.x509.X509Certificate;

import java.io.IOException;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.exceptions.SignatureException;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;

public interface IPlainSigner {
	public X509Certificate getCertificate() throws PdfAsException;
    public byte[] sign(byte[] input, int[] byteRange) throws PdfAsException;
    public String getPDFSubFilter();
    public String getPDFFilter();
}
