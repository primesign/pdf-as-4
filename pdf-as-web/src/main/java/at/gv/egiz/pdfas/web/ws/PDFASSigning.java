package at.gv.egiz.pdfas.web.ws;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.RPC)
public interface PDFASSigning {
	public byte[] signPDFDokument(byte[] inputDocument, PDFASSignParameters parameters);
}
