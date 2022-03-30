package at.gv.egiz.sl20.exceptions;

public class SLCommandoParserException extends SL20Exception {

	private static final long serialVersionUID = 1L;

	
	public SLCommandoParserException() {
		super("sl20.02");
		
	}
	
	public SLCommandoParserException(Throwable e) {
		super("sl20.02", e);
		
	}
}
