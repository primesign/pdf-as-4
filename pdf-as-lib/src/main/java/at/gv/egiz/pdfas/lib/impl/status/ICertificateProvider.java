package at.gv.egiz.pdfas.lib.impl.status;

import iaik.x509.X509Certificate;

public interface ICertificateProvider {
	public X509Certificate getCertificate();
}
