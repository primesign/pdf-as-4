package at.gv.egiz.pdfas.common.exceptions;

import at.gv.egiz.pdfas.common.messages.MessageResolver;

public class PdfAsValidationException extends PdfAsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2428540014894153122L;

	private String parameter;
	
	public PdfAsValidationException(String msgId, String parameter) {
		super(msgId);
		this.parameter = parameter;
	}
	
	@Override
	protected String localizeMessage(String msgId) {
		if(parameter != null) {
			return String.format(MessageResolver.resolveMessage(msgId), parameter);
		} 
		return MessageResolver.resolveMessage(msgId);
    }
}
