package at.gv.egiz.pdfas.common.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: afitzek
 * Date: 9/10/13
 * Time: 10:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class PdfAsSettingsException extends PdfAsException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3277787631624822104L;


	public PdfAsSettingsException(String msgId) {
        super(msgId);
    }


    public PdfAsSettingsException(String msgId, Throwable e) {
        super(msgId, e);
    }
}
