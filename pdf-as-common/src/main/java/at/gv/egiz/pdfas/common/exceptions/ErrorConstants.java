package at.gv.egiz.pdfas.common.exceptions;

public interface ErrorConstants {
	// Code below 10000 are reserved for SL Error Codes
	
	public static final long ERROR_GENERIC = 10000;
	public static final long ERROR_NO_INPUT = 10001;
	
	// Signature Errors
	public static final long ERROR_SIG_INVALID_STATUS = 11004;
	public static final long ERROR_SIG_INVALID_BKU_SIG = 11008;
	public static final long ERROR_SIG_INVALID_PROFILE = 11009;

	public static final long ERROR_SIG_CERTIFICATE_MISSMATCH = 11019;
	
	// Verification Errors
	
	// Configuration Errors:
	public static final long ERROR_SET_INVALID_SETTINGS_OBJ = 13001;
}
