package at.gv.egiz.sl.util;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.sl.CreateCMSSignatureRequestType;
import at.gv.egiz.sl.CreateCMSSignatureResponseType;
import at.gv.egiz.sl.InfoboxReadRequestType;
import at.gv.egiz.sl.InfoboxReadResponseType;

public interface ISLConnector {

	public InfoboxReadRequestType createInfoboxReadRequest();
	public InfoboxReadResponseType sendInfoboxReadRequest(InfoboxReadRequestType request)  throws PdfAsException;
	public CreateCMSSignatureRequestType createCMSRequest(byte[] signatureData, int[] byteRange);
	public CreateCMSSignatureResponseType sendCMSRequest(CreateCMSSignatureRequestType request) throws PdfAsException;
}
