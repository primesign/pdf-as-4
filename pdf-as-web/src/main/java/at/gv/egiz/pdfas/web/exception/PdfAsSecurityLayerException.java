package at.gv.egiz.pdfas.web.exception;

public class PdfAsSecurityLayerException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4632774270754873043L;

	public PdfAsSecurityLayerException(String info, int errorcode) {
		super("SecurityLayer Error: [" + errorcode + "] " + info);
	}
	
}
