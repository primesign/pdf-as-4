package at.gv.egiz.pdfas.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.RPC)
public interface PDFASSigning {
	@WebMethod(operationName = "sign")
	@WebResult(name="signedPDF")
	public byte[] signPDFDokument(@WebParam(name = "pdfDocument")byte[] inputDocument, @WebParam(name = "parameters")PDFASSignParameters parameters);
	
	@WebMethod(operationName = "signSingle")
	@WebResult(name="signResponse")
	public PDFASSignResponse signPDFDokument(@WebParam(name = "signRequest") PDFASSignRequest request);
	
	@WebMethod(operationName = "signBulk")
	@WebResult(name="bulkResponse")
	public PDFASBulkSignResponse signPDFDokument(@WebParam(name = "signBulkRequest") PDFASBulkSignRequest request);
}
