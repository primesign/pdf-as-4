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
package at.gv.egiz.pdfas.lib.impl.configuration;

import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PlaceholderConfiguration extends SpecificBaseConfiguration 
		implements IConfigurationConstants {

	private static final Logger logger = LoggerFactory.getLogger(PlaceholderConfiguration.class);

	public PlaceholderConfiguration(ISettings configuration) {
		super(configuration);
	}

	public boolean isGlobalPlaceholderEnabled() {
		if (configuration.hasValue(PLACEHOLDER_SEARCH_ENABLED)) {
			String value = configuration.getValue(PLACEHOLDER_SEARCH_ENABLED);
			if (value.equalsIgnoreCase(TRUE)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Match selected Profile for Placeholder
	 * Enables to activate placeholder search/match for different profiles
	 * @return
	 */
	public boolean isProfileConfigurationEnabled(String selectedProfileID)
	{
		logger.info("SelectedProfileID in ProfileConfEnabled: "+selectedProfileID);
		String profileMatch = SIG_OBJECT+SEPERATOR+selectedProfileID+SEPERATOR+PLACEHOLDER_SEARCH_ENABLED;
		if (configuration.hasValue(profileMatch)) {
			String value = configuration.getValue(profileMatch);
			if (value.equalsIgnoreCase(TRUE)) {
				logger.info("Configuration has Value: "+value);
				return true;
			}
		}
		return false;
	}
}

	

