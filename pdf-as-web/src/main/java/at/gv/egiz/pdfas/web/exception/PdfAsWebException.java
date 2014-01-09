package at.gv.egiz.pdfas.web.exception;

public class PdfAsWebException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4632774270754873043L;

	public PdfAsWebException(String message) {
		super(message);
	}
	
	public PdfAsWebException(String message, Throwable e) {
		super(message, e);
	}
}
