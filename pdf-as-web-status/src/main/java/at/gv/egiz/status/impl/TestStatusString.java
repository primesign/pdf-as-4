package at.gv.egiz.status.impl;

import at.gv.egiz.status.TestStatus;

// TODO: Auto-generated Javadoc
/**
 * The Class TestStatusString.
 */
public class TestStatusString {
	
	/**
	 * Gets the string.
	 *
	 * @param status the status
	 * @return the string
	 */
	public static String getString(TestStatus status) {
		switch (status) {
		case OK:
			return "OK";
		case FAILED:
			return "FAILED";
		case INDETERMINATE:
			return "INDETERMINATE";
		default:
			return "UNKNOWN";
		}
	}
}
