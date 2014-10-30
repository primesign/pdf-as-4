package at.gv.egiz.pdfas.common.exceptions;

import java.util.HashMap;
import java.util.Map;

import at.gv.egiz.pdfas.common.messages.ErrorCodeResolver;

/**
 * The Class PDFASError.
 */
public class PDFASError extends Exception implements ErrorConstants {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1233586898708485346L;

	/** The code. */
	private long code;

	private Map<String, String> metaInformations = new HashMap<String, String>();

	/**
	 * Instantiates a new PDFAS error.
	 *
	 * @param code
	 *            the code
	 */
	public PDFASError(long code) {
		super(ErrorCodeResolver.resolveMessage(code));
		this.code = code;
	}

	/**
	 * Instantiates a new PDFAS error.
	 *
	 * @param code
	 *            the code
	 * @param e
	 *            the e
	 */
	public PDFASError(long code, Throwable e) {
		super(ErrorCodeResolver.resolveMessage(code), e);
		this.code = code;
	}

	/**
	 * Instantiates a new PDFAS error.
	 *
	 * @param code
	 *            the code
	 * @param info
	 *            the info
	 * @param e
	 *            the e
	 */
	public PDFASError(long code, String info, Throwable e) {
		super(info, e);
		this.code = code;
	}

	/**
	 * Instantiates a new PDFAS error.
	 *
	 * @param code
	 *            the code
	 * @param info
	 *            the info
	 */
	public PDFASError(long code, String info) {
		super(info);
		this.code = code;
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public long getCode() {
		return code;
	}

	/**
	 * Gets the info.
	 *
	 * @return the info
	 */
	public String getInfo() {
		return this.getMessage();
	}

	/**
	 * Gets the code info.
	 *
	 * @return the code info
	 */
	public String getCodeInfo() {
		return ErrorCodeResolver.resolveMessage(code);
	}

	/**
	 * Gets the meta informations for the Error. This Map
	 * is never null, but no information 
	 *
	 * @return the meta informations
	 */
	public Map<String, String> getProcessInformations() {
		return metaInformations;
	}

	public static String buildInfoString(long code, Object... args) {
		return String.format(ErrorCodeResolver.resolveMessage(code), args);
	}
}
