package at.gv.egiz.pdfas.api.exceptions;

@Deprecated
public class PdfAsWrappedException extends PdfAsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3947240372353864753L;

	public PdfAsWrappedException(Throwable e) {
		super(ErrorCode.WRAPPED_ERROR_CODE, e.getMessage(), e);
	}
	
}
