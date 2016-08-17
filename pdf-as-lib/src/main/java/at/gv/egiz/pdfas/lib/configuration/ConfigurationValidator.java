package at.gv.egiz.pdfas.lib.configuration;

import at.gv.egiz.pdfas.common.exceptions.PdfAsSettingsValidationException;
import at.gv.egiz.pdfas.common.settings.ISettings;

public interface ConfigurationValidator {

	void validate(ISettings settings) throws PdfAsSettingsValidationException;

	boolean usedAsDefault();

	String getName();


}
