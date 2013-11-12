package at.gv.egiz.pdfas.common.exceptions;


public class SLPdfAsException extends PdfAsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1261346424827136327L;

	private int code;
	private String info;
	
	public SLPdfAsException(int code, String info) {
        super();
        this.code = code;
        this.info = info;
    }
	
	
	protected String localizeMessage(String msgId) {
        return String.format("%d : %s", code, info);
    }
}
