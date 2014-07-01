package at.gv.egiz.pdfas.lib.api;

/**
 * Collection of useful configuration key
 */
public interface PdfAsConfigurationConstants {
	/**
	 * Signature object Prefix
	 */
	public static final String SIG_OBJECT_PREFIX = "sig_obj.";
	
	/**
	 * MOA SS Signing Key Identifier
	 */
	public static final String MOA_SS_KEY_IDENTIFIER = "moa.sign.KeyIdentifier";
	
	/**
	 * MOA SS Signing Certificate
	 */
	public static final String MOA_SS_KEY_CERTIFICATE = "moa.sign.Certificate";
	
	
	/**
	 * MOA Trustprofile configuration Key
	 */
	public static final String MOA_TRUSTPROFILE_ID = "moa.verify.TrustProfileID";
	
	
	/**
	 * MOA Verify URL configuration Key
	 */
	public static final String MOA_VERIFY_URL = "moa.verify.url";
	
	
	/**
	 * MOA Sign URL configuration Key
	 */
	public static final String MOA_SIGN_URL = "moa.sign.url";
	
}
