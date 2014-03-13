package at.gv.egiz.pdfas.lib.test.mains;
import iaik.x509.X509Certificate;
import at.gv.egiz.pdfas.lib.impl.status.ICertificateProvider;


public class CertificateHolderRequest implements ICertificateProvider {

	private X509Certificate cert;
	
	public CertificateHolderRequest(X509Certificate cert) {
		this.cert = cert;
	}

	public X509Certificate getCertificate() {
		return cert;
	}

}
