package at.gv.egiz.pdfas.web.client;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import at.gv.egiz.pdfas.api.ws.PDFASBulkSignRequest;
import at.gv.egiz.pdfas.api.ws.PDFASBulkSignResponse;
import at.gv.egiz.pdfas.api.ws.PDFASSignParameters;
import at.gv.egiz.pdfas.api.ws.PDFASSignRequest;
import at.gv.egiz.pdfas.api.ws.PDFASSignResponse;
import at.gv.egiz.pdfas.api.ws.PDFASSigning;

public class RemotePDFSigner implements PDFASSigning {

	private URL urlEndpoint;
	private Service service;

	private PDFASSigning proxy;

	public RemotePDFSigner(URL endpoint, boolean useMTOM) {
		this.urlEndpoint = endpoint;
		QName qname = new QName("http://ws.web.pdfas.egiz.gv.at/",
				"PDFASSigningImplService");
		service = Service.create(endpoint, qname);

		proxy = service.getPort(PDFASSigning.class);

		BindingProvider bp = (BindingProvider) proxy;
		SOAPBinding binding = (SOAPBinding) bp.getBinding();
		binding.setMTOMEnabled(useMTOM);
	}

	public PDFASSignResponse signPDFDokument(PDFASSignRequest request) {
		return proxy.signPDFDokument(request);
	}

	public PDFASBulkSignResponse signPDFDokument(PDFASBulkSignRequest request) {
		return proxy.signPDFDokument(request);
	}

}
