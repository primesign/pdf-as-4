package at.gv.egiz.sl20.exceptions;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;

public class SL20Exception extends PdfAsException {

	private static final long serialVersionUID = 1L;

	public SL20Exception(String messageId) {
		super(messageId);

	}
	
	public SL20Exception(String messageId, Throwable wrapped) {
		super(messageId, wrapped);

	}

}
