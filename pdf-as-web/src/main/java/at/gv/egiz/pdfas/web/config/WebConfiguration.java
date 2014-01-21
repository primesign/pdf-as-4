package at.gv.egiz.pdfas.web.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebConfiguration {

	
	public static final String PUBLIC_URL = "public.url";
	public static final String LOCAL_BKU_URL = "bku.local.url";
	public static final String ONLINE_BKU_URL = "bku.online.url";
	public static final String MOBILE_BKU_URL = "bku.mobile.url";
	public static final String ERROR_DETAILS = "error.showdetails";
	public static final String PDF_AS_WORK_DIR = "pdfas.dir";
	
	public static final String KEYSTORE_ENABLED = "ks.enabled";
	public static final String KEYSTORE_FILE = "ks.file";
	public static final String KEYSTORE_TYPE = "ks.type";
	public static final String KEYSTORE_PASS = "ks.pass";
	public static final String KEYSTORE_ALIAS = "ks.key.alias";
	public static final String KEYSTORE_KEY_PASS = "ks.key.pass";
	
	private static Properties properties = new Properties();
	
	private static final Logger logger = LoggerFactory
			.getLogger(WebConfiguration.class);
	
	public static void configure(String config) {
		try {
			properties.load(new FileInputStream(config));
		} catch(Exception e) {
			logger.error("Failed to load configuration: " + e.getMessage());
			throw new RuntimeException(e);
		}
		
		String pdfASDir = getPdfASDir();
		if(pdfASDir == null) {
			logger.error("Please configure pdf as working directory in the web configuration");
			throw new RuntimeException("Please configure pdf as working directory in the web configuration");
		}
		
		File f = new File(pdfASDir);
		
		if(!f.exists() || !f.isDirectory()) {
			logger.error("Pdf As working directory does not exists or is not a directory!: " + pdfASDir);
			throw new RuntimeException("Pdf As working directory does not exists or is not a directory!");
		}
	}
	
	public static String getPublicURL() {
		return properties.getProperty(PUBLIC_URL);
	}

	public static String getLocalBKUURL() {
		return properties.getProperty(LOCAL_BKU_URL);
	}

	public static String getOnlineBKUURL() {
		return properties.getProperty(ONLINE_BKU_URL);
	}

	public static String getHandyBKUURL() {
		return properties.getProperty(MOBILE_BKU_URL);
	}
	
	public static String getPdfASDir() {
		return properties.getProperty(PDF_AS_WORK_DIR);
	}
	
	public static String getKeystoreFile() {
		return properties.getProperty(KEYSTORE_FILE);
	}
	public static String getKeystoreType() {
		return properties.getProperty(KEYSTORE_TYPE);
	}
	public static String getKeystorePass() {
		return properties.getProperty(KEYSTORE_PASS);
	}
	public static String getKeystoreAlias() {
		return properties.getProperty(KEYSTORE_ALIAS);
	}
	public static String getKeystoreKeyPass() {
		return properties.getProperty(KEYSTORE_KEY_PASS);
	}
	
	
	public static boolean getKeystoreEnabled() {
		String value = properties.getProperty(KEYSTORE_ENABLED);
		if(value != null) {
			if(value.equals("true")) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isShowErrorDetails() {
		String value = properties.getProperty(ERROR_DETAILS);
		if(value != null) {
			if(value.equals("true")) {
				return true;
			}
		}
		return false;
	}

}
