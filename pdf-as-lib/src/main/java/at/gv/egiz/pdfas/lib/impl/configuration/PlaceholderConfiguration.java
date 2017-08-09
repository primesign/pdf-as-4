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
import at.gv.egiz.pdfas.common.settings.Profiles;
import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.impl.PdfAsImpl;
import at.gv.egiz.pdfas.lib.impl.PdfAsParameterImpl;
import at.gv.egiz.pdfas.lib.impl.placeholder.SignaturePlaceholderData;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import at.gv.egiz.pdfas.lib.settings.Settings;
import at.gv.egiz.pdfas.lib.util.SignatureUtils;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import com.sun.corba.se.spi.orb.Operation;

import java.security.Signature;
import java.util.Properties;


public class PlaceholderConfiguration extends SpecificBaseConfiguration 
		implements IConfigurationConstants {

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
		String profileMatch = SIG_OBJECT+SEPERATOR+selectedProfileID+SEPERATOR+PLACEHOLDER_SEARCH_ENABLED;
		if (configuration.hasValue(profileMatch)) {
			String value = configuration.getValue(profileMatch);
			if (value.equalsIgnoreCase(TRUE)) {
				return true;
			}
		}
		return false;
	}
}

	

