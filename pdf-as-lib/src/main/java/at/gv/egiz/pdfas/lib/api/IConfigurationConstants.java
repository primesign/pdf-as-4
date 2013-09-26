package at.gv.egiz.pdfas.lib.api;

public interface IConfigurationConstants {

	public static final String TRUE = "true";
	
	public static final String SIG_OBJECT = "sig_obj";
	public static final String TYPE = "type";
	public static final String TABLE = "type";
	public static final String MAIN = "main";
	public static final String DEFAULT = "default";
	public static final String SEPERATOR = ".";
	
	public static final String PLACEHOLDER_SEARCH_ENABLED = "enable_placeholder_search";
	public static final String DEFAULT_SIGNATURE_PROFILE = SIG_OBJECT + SEPERATOR + TYPE + SEPERATOR + DEFAULT;
}
