package at.gv.egiz.pdfas.lib.impl.configuration;

import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;

public class SignatureProfileConfiguration extends SpecificBaseConfiguration 
	implements IConfigurationConstants {
	
	protected String profileID;

	public SignatureProfileConfiguration(ISettings configuration, 
			String profileID) {
		super(configuration);
		this.profileID = profileID;
	}

	public boolean isVisualSignature() {
		String key = SIG_OBJECT + SEPERATOR + profileID + SEPERATOR + TABLE + SEPERATOR + MAIN;
		return this.configuration.hasPrefix(key);
	}
	
	public String getDefaultPositioning() {
		String key = SIG_OBJECT + SEPERATOR + profileID + SEPERATOR + TABLE + SEPERATOR + POS;
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
