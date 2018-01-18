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

public class GlobalConfiguration extends SpecificBaseConfiguration 
	implements IConfigurationConstants {
	
	public GlobalConfiguration(ISettings configuration) {
		super(configuration);
	}

	public String getDefaultSignatureProfile() {
		if(this.configuration.hasValue(DEFAULT_SIGNATURE_PROFILE)) {
			return this.configuration.getValue(DEFAULT_SIGNATURE_PROFILE);
		}
		return null;
	}

	public String getDefaultProtection() {
		if(this.configuration.hasValue(DEFAULT_CONFIG_PROTECT_PDF)) {
			return this.configuration.getValue(DEFAULT_CONFIG_PROTECT_PDF);
		}
		return null;
	}

	public String getDefaultCopyProtection() {
		if(this.configuration.hasValue(DEFAULT_CONFIG_PROTECT_COPY_PDF)) {
			return this.configuration.getValue(DEFAULT_CONFIG_PROTECT_COPY_PDF);
		}
		return null;
	}

	public String getDefaultExtractProtection() {
		if(this.configuration.hasValue(DEFAULT_CONFIG_PROTECT_EXTRACT_PDF)) {
			return this.configuration.getValue(DEFAULT_CONFIG_PROTECT_EXTRACT_PDF);
		}
		return null;
	}


}
