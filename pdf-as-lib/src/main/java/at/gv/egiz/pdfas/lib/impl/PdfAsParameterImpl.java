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
package at.gv.egiz.pdfas.lib.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataSource;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.utils.CheckSignatureBlockParameters;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;
import at.gv.egiz.pdfas.lib.api.PdfAsParameter;

public class PdfAsParameterImpl implements PdfAsParameter {
protected Configuration configuration;
	
	protected DataSource dataSource;
	protected String transactionId;
	protected Map<String, String> preProcessorProps;
	protected Map<String, String> dynamicSignatureBlockArgumentsMap;
	public PdfAsParameterImpl(Configuration configuration, 
			DataSource dataSource) {
		this.configuration = configuration;
		this.dataSource = dataSource;
		this.transactionId = null;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String id) {
		this.transactionId = id;
	}

	@Override
	public Map<String, String> getPreprocessorArguments() {
		return preProcessorProps;
	}

	@Override
	public void setPreprocessorArguments(Map<String, String> map) {
		this.preProcessorProps = map;
	}

	@Override
	public void setDynamicSignatureBlockArguments(Map<String, String> map) throws PdfAsException {
		if(map == null)
			map = new HashMap<String, String>();
		Map<String, String> tmpMap = Collections.unmodifiableMap(map);
		String keyRegex = configuration.getValue(IConfigurationConstants.SIG_BLOCK_PARAMETER_KEY_REGEX);
		String valueRegex = configuration.getValue(IConfigurationConstants.SIG_BLOCK_PARAMETER_VALUE_REGEX);
		if( CheckSignatureBlockParameters.checkSignatureBlockParameterMapIsValid(tmpMap, keyRegex, valueRegex) == true) {
			this.dynamicSignatureBlockArgumentsMap = tmpMap;
		}else{
			throw new PdfAsException("error.invalid.signature.parameter.01");
		}

	}

	@Override
	public Map<String, String> getDynamicSignatureBlockArguments() {
		return this.dynamicSignatureBlockArgumentsMap;
	}
}
