package at.gv.egiz.pdfas.common.exceptions;

import java.io.IOException;

public class PdfAsWrappedIOException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1241649395712516711L;

	public PdfAsWrappedIOException(PdfAsException e) {
		super(e);
	}
	
}
