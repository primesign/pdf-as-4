package at.gv.egiz.pdfas.web.helper;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.web.config.WebConfiguration;

public class ConfigurationOverwrite {

	private static final Logger logger = LoggerFactory
			.getLogger(ConfigurationOverwrite.class);

	public static void overwriteConfiguration(Map<String, String> overwrite,
			Configuration config) {
		if (WebConfiguration.isAllowExtOverwrite() && overwrite != null && config != null) {
			Iterator<Entry<String, String>> entryIt = overwrite.entrySet()
					.iterator();
			while (entryIt.hasNext()) {
				Entry<String, String> entry = entryIt.next();
				if (WebConfiguration.isOverwriteAllowed(entry.getKey())) {
					config.setValue(entry.getKey(), entry.getValue());
				} else {
					logger.warn(
							"External component tried to overwrite cfg {}. This is not in the whitelist!",
							entry.getKey());
				}
			}
		}
	}
}
