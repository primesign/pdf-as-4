package at.gv.egiz.pdfas.common.exceptions;

import at.gv.egiz.pdfas.common.messages.MessageResolver;

public class PdfAsMOAException extends PdfAsException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -217112433494784615L;
	
	private String faultCode;
	private String faultString;
	private String errorResponse;
	private String errorCode;
	
	public PdfAsMOAException(String faultCode, String faultString, String errorResponse, String errorCode) {
		super("error.pdf.io.06");
		this.faultCode = faultCode;
		this.faultString = faultString;
		this.errorResponse = errorResponse;
		this.errorCode = errorCode;
	}
	
	@Override
	protected String localizeMessage(String msgId) {
        return String.format(MessageResolver.resolveMessage(msgId), errorResponse, errorCode, faultCode, faultString);
    }
}
