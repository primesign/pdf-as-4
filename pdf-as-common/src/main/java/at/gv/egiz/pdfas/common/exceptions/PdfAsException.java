package at.gv.egiz.pdfas.common.exceptions;

import at.gv.egiz.pdfas.common.messages.MessageResolver;

public class PdfAsException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 933244024555676174L;

	public PdfAsException() {
        super();
    }

    public PdfAsException(String msgId) {
        super(msgId);
    }

    public PdfAsException(String msgId, Throwable e) {
        super(msgId, e);
    }

    @Override
    public String getMessage() {
        return localizeMessage(super.getMessage());
    }

    @Override
    public String getLocalizedMessage() {
        return localizeMessage(super.getMessage());
    }

    protected String localizeMessage(String msgId) {
        return MessageResolver.resolveMessage(msgId);
    }
}
