package at.gv.egiz.pdfas.lib.api.verify;

public interface SignatureCheck {
	/**
	 * Returns the response code of the check.
	 * 
	 * @return Returns the response code of the check.
	 */
	public int getCode();

	/**
	 * Returns the textual response message of the check (corresponding to the
	 * code).
	 * 
	 * @return Returns the textual response message of the check (corresponding
	 *         to the code).
	 */
	public String getMessage();
}
