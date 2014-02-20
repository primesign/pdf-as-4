/*******************************************************************************
 * <copyright> Copyright 2014 by E-Government Innovation Center EGIZ, Graz, Austria </copyright>
 * PDF-AS has been contracted by the E-Government Innovation Center EGIZ, a
 * joint initiative of the Federal Chancellery Austria and Graz University of
 * Technology.
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * http://www.osor.eu/eupl/
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * 
 * This product combines work with different licenses. See the "NOTICE" text
 * file for details on the various modules and licenses.
 * The "NOTICE" text file is part of the distribution. Any derivative works
 * that you distribute must include a readable copy of the "NOTICE" text file.
 ******************************************************************************/
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
