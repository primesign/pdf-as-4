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

import java.util.Map;

import javax.activation.DataSource;

public interface PdfAsParameter {

	/**
	 * Gets the configuration associated with the parameter
	 * @return
	 */
	public Configuration getConfiguration() ;

	/**
	 * Sets the configuration associated with the parameter
	 * @param configuration
	 */
	public void setConfiguration(Configuration configuration);

	/**
	 * Gets the data source of the parameter
	 * @return
	 */
	public DataSource getDataSource();

	/**
	 * Sets the data source of the parameter
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource);
	
	/**
	 * Gets the transaction id.
	 *
	 * @return the transaction id
	 */
	public String getTransactionId();
	
	/**
	 * Sets the transaction id.
	 *
	 * @param id the new transaction id
	 */
	public void setTransactionId(String id);
	
	/**
	 * Gets the preprocessor arguments.
	 *
	 * @return the preprocessor arguments
	 */
	public Map<String, String> getPreprocessorArguments();
	
	/**
	 * Sets the preprocessor arguments.
	 *
	 * @param map the map
	 */
	public void setPreprocessorArguments(Map<String, String> map);
}
