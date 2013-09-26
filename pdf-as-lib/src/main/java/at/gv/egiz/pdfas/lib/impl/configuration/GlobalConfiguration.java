package at.gv.egiz.pdfas.lib.impl.configuration;

import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.api.Configuration;
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
	
}
