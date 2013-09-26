package at.gv.egiz.pdfas.lib.impl.configuration;

import at.gv.egiz.pdfas.common.settings.ISettings;

public abstract class SpecificBaseConfiguration {
	
	protected ISettings configuration;

	public SpecificBaseConfiguration(ISettings configuration) {
		this.configuration = configuration;
	}
	
}
