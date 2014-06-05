package at.gv.egiz.pdfas.web.exception;

public class PdfAsStoreException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6704586769888839023L;

	public PdfAsStoreException(String message) {
		super(message);
	}
	
	public PdfAsStoreException(String message, Throwable e) {
		super(message, e);
	}
}
