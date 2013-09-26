package at.gv.egiz.pdfas.lib.impl.configuration;

import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;

public class PlaceholderConfiguration extends SpecificBaseConfiguration 
		implements IConfigurationConstants {

	public PlaceholderConfiguration(ISettings configuration) {
		super(configuration);
	}

	public boolean isGlobalPlaceholderEnabled() {
		if(configuration.hasValue(PLACEHOLDER_SEARCH_ENABLED)) {
			String value = configuration.getValue(PLACEHOLDER_SEARCH_ENABLED);
			if(value.equalsIgnoreCase(TRUE)) {
				return true;
			}
		}
		return false;
	}
	
}
