package at.gv.egiz.pdfas.lib.impl.configuration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.ErrorConstants;
import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.backend.PDFASBackend;
import at.gv.egiz.pdfas.lib.configuration.ConfigurationValidator;

public class ConfigValidatorLoader implements ErrorConstants{
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory
			.getLogger(ConfigValidatorLoader.class);
	
	/** The configuration verifier loader. */
	private static ServiceLoader<ConfigurationValidator> configurationValidator = ServiceLoader.load(ConfigurationValidator.class);
	
	/** The available verification backends. */
	private static Map<String, ConfigurationValidator> availableValidators = new HashMap<String, ConfigurationValidator>();
	
	/** The Constant VALIDATOR_CONFIG. */
	public static final String VALIDATOR_CONFIG = "runtime.config.validator";
	
	/** The default validator. */
	private static ConfigurationValidator defaultValidator = null; 
	
	static {
		logger.debug("building Configuration Validator");
		
		Iterator<ConfigurationValidator> backendIterator = configurationValidator.iterator();
		
		while(backendIterator.hasNext()) {
			ConfigurationValidator backend = backendIterator.next();
			logger.debug("Loading " + backend.getName() + " [" + backend.getClass().getName() + "]");
			availableValidators.put(backend.getName(), backend);
			logger.debug("PDF-Backend added " + backend.getName());
			if(backend.usedAsDefault()) {
				defaultValidator = backend;
				logger.debug("PDF-Backend added as default " + backend.getName());
			}
		}
		
		logger.debug("Configuration Validator Backends constructed");
		
		if(defaultValidator != null) {
			logger.debug("Default backend is " + defaultValidator.getName());
		}
	}
	
	/**
	 * Gets the Configuration valdiator.
	 *
	 * @param configuration the configuration
	 * @return the validator backend
	 * @throws PDFASError the PDFAS error
	 */
	public static ConfigurationValidator getConfigurationValidator(Configuration configuration) throws PDFASError {
		String backendName = configuration.getValue(VALIDATOR_CONFIG);
		return getConfigurationValidator(backendName);
	}
	
	/**
	 * Gets the Validator by name.
	 *
	 * @param name the name
	 * @return the validator backend
	 * @throws PDFASError the PDFAS error
	 */
	public static ConfigurationValidator getConfigurationValidator(String name) throws PDFASError {
		if(name != null) {
			if(availableValidators.containsKey(name)) {
				return availableValidators.get(name);
			}
			throw new PDFASError(ERROR_NO_CONF_VALIDATION_BACKEND);
		}
		
		if(defaultValidator != null) {
			return defaultValidator;
		}
		
		throw new PDFASError(ERROR_NO_CONF_VALIDATION_BACKEND);
	}
	
	/**
	 * Gets the Configuration Validator.
	 *
	 * @return the validator backend
	 * @throws PDFASError the PDFAS error
	 */
	public static ConfigurationValidator getConfigurationValidator() throws PDFASError {
		if(defaultValidator != null) {
			return defaultValidator;
		}
		
		throw new PDFASError(ERROR_NO_CONF_VALIDATION_BACKEND);
	}
	
	public static Map<String, ConfigurationValidator> getAvailableValidators(){
			return availableValidators;
	}
}
