package at.gv.egiz.pdfas.common.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: afitzek
 * Date: 8/28/13
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class PDFIOException extends PdfAsException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 461085759398146094L;


	public PDFIOException(String msgId) {
        super(msgId);
    }


    public PDFIOException(String msgId, Throwable e) {
        super(msgId, e);
    }
}
