package at.gv.egiz.pdfas.web.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
	
	public static final String MOA_SS_ENABLED = "moa.enabled";
	
	public static final String KEYSTORE_ENABLED = "ks.enabled";
	public static final String KEYSTORE_FILE = "ks.file";
	public static final String KEYSTORE_TYPE = "ks.type";
	public static final String KEYSTORE_PASS = "ks.pass";
	public static final String KEYSTORE_ALIAS = "ks.key.alias";
	public static final String KEYSTORE_KEY_PASS = "ks.key.pass";
	
	public static final String WHITELIST_ENABLED = "whitelist.enabled";
	public static final String WHITELIST_VALUE_PRE = "whitelist.url.";
	
	private static Properties properties = new Properties();
	
	private static final Logger logger = LoggerFactory
			.getLogger(WebConfiguration.class);
	
	private static List<String> whiteListregEx = new ArrayList<String>();

	public static void configure(String config) {
		
		properties.clear();
		whiteListregEx.clear();
		
		try {
			properties.load(new FileInputStream(config));
		} catch(Exception e) {
			logger.error("Failed to load configuration: " + e.getMessage());
			throw new RuntimeException(e);
		}
		
		if(isWhiteListEnabled()) {
			Iterator<Object> keyIt = properties.keySet().iterator();
			while(keyIt.hasNext()) {
				Object keyObj = keyIt.next();
				if(keyObj != null) {
					String key = keyObj.toString();
					if(key.startsWith(WHITELIST_VALUE_PRE)) {
						String whitelist_expr = properties.getProperty(key);
						if(whitelist_expr != null) {
							whiteListregEx.add(whitelist_expr);
							logger.debug("URL Whitelist: " + whitelist_expr);
						}
					}
				}
			}
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
	
	public static boolean getMOASSEnabled() {
		String value = properties.getProperty(MOA_SS_ENABLED);
		if(value != null) {
			if(value.equals("true")) {
				return true;
			}
		}
		return false;
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
	
	public static boolean isWhiteListEnabled() {
		String value = properties.getProperty(WHITELIST_ENABLED);
		if(value != null) {
			if(value.equals("true")) {
				return true;
			}
		}
		return false;
	}

	public static synchronized boolean isProvidePdfURLinWhitelist(String url) {
		if(isWhiteListEnabled()) {
			
			Iterator<String> patterns = whiteListregEx.iterator();
			while(patterns.hasNext()) {
				String pattern = patterns.next();
				try {
					if(url.matches(pattern)) {
						return true;
					}
				} catch(Throwable e) {
					logger.error("Error in matching regex: " + pattern, e);
				}
			}
			
			return false;
		}
		return true;
	}
}
