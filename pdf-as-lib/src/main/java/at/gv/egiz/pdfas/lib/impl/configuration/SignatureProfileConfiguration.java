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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;

public class SignatureProfileConfiguration extends SpecificBaseConfiguration 
	implements IConfigurationConstants {
	
	private static final Logger logger = LoggerFactory.getLogger(SignatureProfileConfiguration.class);
	
	protected String profileID;

	public SignatureProfileConfiguration(ISettings configuration, 
			String profileID) {
		super(configuration);
		this.profileID = profileID;
	}

	public float getMinWidth() {
		String key = SIG_OBJECT + SEPERATOR + profileID + SEPERATOR + MIN_WIDTH;
		
		String minWidthValue = this.configuration.getValue(key);
		
		float result = Float.MAX_VALUE;
		
		if(minWidthValue != null) {
			try {
				result = Float.parseFloat(minWidthValue);
				logger.debug("Got min width for profile {}: {}", profileID, result);
			} catch(NumberFormatException e) {
				logger.warn("Configuration Entry: {} should be a float number", key);
			}
		}
		
		return result;
	}
	
	public boolean isVisualSignature() {
		String key = SIG_OBJECT + SEPERATOR + profileID + SEPERATOR + TABLE + SEPERATOR + MAIN;
		
		String isVisibleKey = SIG_OBJECT + SEPERATOR + profileID + SEPERATOR + ISVISIBLE;
		
		String isVisibleValue = this.configuration.getValue(isVisibleKey);
		
		boolean isVisible = true;
		
		if(isVisibleValue != null) {
			if(isVisibleValue.equals(FALSE)) {
				isVisible = false;
			}
		}
		
		return this.configuration.hasPrefix(key) && isVisible;
	}
	
	public String getDefaultPositioning() {
		String key = SIG_OBJECT + SEPERATOR + profileID + SEPERATOR + POS;
		return this.configuration.getValue(key);
	}
	
	public boolean getLegacy32Positioning() {
		String key = SIG_OBJECT + SEPERATOR + profileID + LEGACY_POSITIONING;
		String value = this.configuration.getValue(key);
		if(value != null) {
			if(value.equalsIgnoreCase(TRUE)) {
				return true;
			}
		}
		return false;
	}
}
