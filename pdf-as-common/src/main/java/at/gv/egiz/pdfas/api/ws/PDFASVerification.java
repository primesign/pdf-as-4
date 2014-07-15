package at.gv.egiz.pdfas.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.RPC)
public interface PDFASVerification {
	@WebMethod(operationName = "verify")
	@WebResult(name="verifyResponse")
	public PDFASVerifyResponse verifyPDFDokument(@WebParam(name = "verifyRequest") PDFASVerifyRequest request);
	
}
