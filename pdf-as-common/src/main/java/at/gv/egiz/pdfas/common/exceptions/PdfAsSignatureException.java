package at.gv.egiz.pdfas.common.exceptions;

public class PdfAsSignatureException extends PdfAsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7708377284196351907L;
	
	public PdfAsSignatureException(String msgId, Throwable e) {
        super(msgId, e);
    }
}
