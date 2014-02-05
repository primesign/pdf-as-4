package at.gv.egiz.pdfas.lib.api;

/**
 * Configuration interface
 *
 * This interface is used to configure one PDF-AS run. It contains the configuration values
 * from the configuration file. Use this interface to override properties during runtime.
 */
public interface Configuration {
	
	/**
	 * Gets a specific Value
	 * @param key The configuration key
	 * @return The configured value
	 */
	public String getValue(String key);
	
	/**
	 * Is the configuration key set
	 * @param key The configuration key
	 * @return true | false
	 */
	public boolean hasValue(String key);
	
	/**
	 * Sets or overrides a configuration value
	 * @param key The configuration key
	 * @param value The configuration value
	 */
	public void setValue(String key, String value);
}
