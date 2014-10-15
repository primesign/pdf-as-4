package at.gv.egiz.pdfas.common.exceptions;

public class PdfAsErrorCarrier extends PdfAsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8823547416257994310L;

	public PdfAsErrorCarrier(PDFASError error) {
		super("Carrier", error);
	}
	
}
