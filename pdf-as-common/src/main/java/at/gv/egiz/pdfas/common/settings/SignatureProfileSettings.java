package at.gv.egiz.pdfas.common.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SignatureProfileSettings implements IProfileConstants {

	private static final Logger logger = LoggerFactory
			.getLogger(SignatureProfileSettings.class);

	private Map<String, SignatureProfileEntry> profileInformations = new HashMap<String, SignatureProfileEntry>();

	private Map<String, String> profileSettings = new HashMap<String, String>();

	private String profileID;

	private ISettings configuration;

	public SignatureProfileSettings(String profileID, ISettings configuration) {
		this.profileID = profileID;
		String profilePrefix = SIG_OBJ + profileID + KEY_SEPARATOR;
		String keysPrefix = profilePrefix + PROFILE_KEY;
		String valuesPrefix = profilePrefix + PROFILE_VALUE;
		String tablePrefix = profilePrefix + TABLE;
		this.configuration = configuration;

		logger.debug("Reading Profile: " + profileID);
		logger.debug("Keys Prefix: " + keysPrefix);
		logger.debug("Values Prefix: " + valuesPrefix);
		logger.debug("Table Prefix: " + tablePrefix);

		Map<String, String> keys = configuration.getValuesPrefix(keysPrefix);
		Map<String, String> values = configuration
				.getValuesPrefix(valuesPrefix);

		if (keys != null) {
			Iterator<String> keyIterator = keys.keySet().iterator();

			while (keyIterator.hasNext()) {
				String key = keyIterator.next();
				key = key.substring(key.lastIndexOf('.') + 1);
				String valueKey = keys.get(keysPrefix + KEY_SEPARATOR + key);

				String valueValue = values.get(valuesPrefix + KEY_SEPARATOR
						+ key);

				// Lookup default values
				if(valueKey == null) {
					valueKey = DefaultSignatureProfileSettings.getDefaultKeyCaption(key);
				}
				
				if(valueValue == null) {
					valueValue = DefaultSignatureProfileSettings.getDefaultKeyValue(key);
				}
				
				SignatureProfileEntry entry = new SignatureProfileEntry();
				entry.setKey(key);
				entry.setCaption(valueKey);
				entry.setValue(valueValue);
				profileInformations.put(key, entry);
				logger.debug("   " + entry.toString());
			}
		}

		if (values != null) {
			// Find entries where only values exists
			Iterator<String> valuesIterator = values.keySet().iterator();

			while (valuesIterator.hasNext()) {
				String key = valuesIterator.next();
				key = key.substring(key.lastIndexOf('.') + 1);

				String valueValue = values.get(valuesPrefix + KEY_SEPARATOR
						+ key);

				// Lookup default values			
				if(valueValue == null) {
					valueValue = DefaultSignatureProfileSettings.getDefaultKeyValue(key);
				}
				
				SignatureProfileEntry entry = profileInformations.get(key);
				if (entry == null) {
					entry = new SignatureProfileEntry();
					entry.setKey(key);
					entry.setCaption(null);
					entry.setValue(valueValue);
					profileInformations.put(key, entry);
				}

				logger.debug("   " + entry.toString());
			}
		}

		Map<String, String> others = configuration
				.getValuesPrefix(profilePrefix);

		Iterator<String> otherIterator = others.keySet().iterator();

		while (otherIterator.hasNext()) {
			String key = otherIterator.next();

			logger.trace("Checking key " + key);
			if (key.startsWith(keysPrefix) || key.startsWith(valuesPrefix)
					|| key.startsWith(tablePrefix)) {
				continue;
			}

			String value = others.get(key);
			key = key.substring(key.lastIndexOf('.') + 1);

			profileSettings.put(key, others.get(value));

			logger.debug("   Settings: " + key + " : " + value);
		}
		
		
		Iterator<SignatureProfileEntry> dumpIterator = 
				profileInformations.values().iterator();
		
		logger.debug("Settings for profile {}", profileID);
		while(dumpIterator.hasNext()) {
			SignatureProfileEntry entry = dumpIterator.next();
			logger.debug("  " + entry.toString());
		}
	}

	public String getCaption(String key) {
		SignatureProfileEntry entry = profileInformations.get(key);
		if (entry != null) {
			return entry.getCaption();
		}
		return null;
	}

	protected String getDefaultValue(String key) {
		String profilePrefix = SIG_OBJ + profileID + KEY_SEPARATOR;
		logger.debug("Searching default value for: " + key);
		if (key.startsWith(profilePrefix)) {
			key = key.substring(profilePrefix.length());
		}
		key = "default." + key;
		logger.debug("Searching default value for: " + key);
		return this.configuration.getValue(key);
	}

	public String getValue(String key) {
		logger.debug("Searching: " + key);
		SignatureProfileEntry entry = profileInformations.get(key);
		if (entry != null) {
			String value = entry.getValue();

			if (value == null) {
				return getDefaultValue(key);
			}

			return value;
		}
		return getDefaultValue(key);
	}

	public String getProfileID() {
		return profileID;
	}

	public String getSigningReason() {
		return this.getValue(SIGNING_REASON);
	}
}
