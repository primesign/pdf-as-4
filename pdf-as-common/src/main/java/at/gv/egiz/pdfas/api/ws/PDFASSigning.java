package at.gv.egiz.pdfas.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.RPC)
public interface PDFASSigning {
	@WebMethod(operationName = "sign")
	public byte[] signPDFDokument(@WebParam(name = "pdfDocument")byte[] inputDocument, @WebParam(name = "parameters")PDFASSignParameters parameters);
}
