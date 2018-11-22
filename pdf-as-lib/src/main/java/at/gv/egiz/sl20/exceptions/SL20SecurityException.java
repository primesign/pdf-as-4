package at.gv.egiz.sl20.exceptions;

public class SL20SecurityException extends SL20Exception {

	private static final long serialVersionUID = 3281385988027147449L;
	
	public SL20SecurityException() {
		super("sl20.05");
	}
	
	public SL20SecurityException(Throwable wrapped) {
		super("sl20.05", wrapped);

	}

	public SL20SecurityException(String string) {
		super(string);
	}

}
