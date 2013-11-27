package at.gv.egiz.sl.util;

import iaik.x509.X509Certificate;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;

public interface ISignatureConnector {
	public X509Certificate getCertificate() throws PdfAsException;
    public byte[] sign(byte[] input, int[] byteRange) throws PdfAsException;
}
