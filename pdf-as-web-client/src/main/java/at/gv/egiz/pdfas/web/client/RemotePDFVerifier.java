package at.gv.egiz.pdfas.web.client;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import at.gv.egiz.pdfas.api.ws.PDFASVerification;
import at.gv.egiz.pdfas.api.ws.PDFASVerifyRequest;
import at.gv.egiz.pdfas.api.ws.PDFASVerifyResponse;

public class RemotePDFVerifier implements PDFASVerification {
	
	private Service service;
	
	private PDFASVerification proxy;
	
	public RemotePDFVerifier(URL endpoint, boolean useMTOM) {
		QName qname = new QName("http://ws.web.pdfas.egiz.gv.at/",
				"PDFASVerificationImplService");
		service = Service.create(endpoint, qname);

		proxy = service.getPort(PDFASVerification.class);

		BindingProvider bp = (BindingProvider) proxy;
		SOAPBinding binding = (SOAPBinding) bp.getBinding();
		binding.setMTOMEnabled(useMTOM);
	}

	public PDFASVerifyResponse verifyPDFDokument(PDFASVerifyRequest request) {
		return proxy.verifyPDFDokument(request);
	}
}
