package at.gv.egiz.pdfas.common.exceptions;

public interface ErrorConstants {
	String STATUS_INFO_SIGDEVICE = "SigDevice";
	String STATUS_INFO_SIGDEVICEVERSION = "SigDeviceVersion";
	String STATUS_INFO_INVALIDSIG = "InvalidSignature";

	// Code below 10000 are reserved for SL Error Codes

	long ERROR_GENERIC = 10000;
	long ERROR_NO_INPUT = 10001;
	long ERROR_NO_BACKEND = 10002;

	// Signature Errors
	long ERROR_SIG_FAILED_OPEN_KS = 11002;
	long ERROR_SIG_INVALID_STATUS = 11004;

	long ERROR_SIG_INVALID_BKU_SIG = 11008;
	long ERROR_SIG_INVALID_PROFILE = 11009;

	long ERROR_SIG_CERTIFICATE_MISSMATCH = 11019;

	/**
	 * Error indicating that no LTV data was retrieved (e.g. when no validation data provider is available supporting
	 * the signer's certificate CA) while signature parameters require LTV.
	 */
	long ERROR_SIG_PADESLTV_NO_DATA = 11100;

	/**
	 * There was at least one validation provider capable of retrieving required data, but there was an error retrieving
	 * that data (e.g. connection issues).
	 */
	long ERROR_SIG_PADESLTV_RETRIEVING_REQUIRED_DATA = 11101;

	/**
	 * There was an internal error adding the LTV data to the document (e.g. errors encoding certificates).
	 */
	long ERROR_SIG_PADESLTV_INTERNAL_ADDING_DATA_TO_PDF = 11102;
	
	/**
	 * There was an I/O error adding (writing) LTV related data to the document.
	 */
	long ERROR_SIG_PADESLTV_IO_ADDING_DATA_TO_PDF = 11103;

	// Verification Errors


	// Configuration Errors:
	long ERROR_SET_INVALID_SETTINGS_OBJ = 13001;
	long ERROR_INVALID_CERTIFICATE = 13002;
	long ERROR_INVALID_PLACEHOLDER_MODE = 13003;

	//Validation Errors
	long ERROR_NO_CONF_VALIDATION_BACKEND = 14001;
}
