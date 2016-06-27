/*******************************************************************************
 * <copyright> Copyright 2014 by E-Government Innovation Center EGIZ, Graz, Austria </copyright>
 * PDF-AS has been contracted by the E-Government Innovation Center EGIZ, a
 * joint initiative of the Federal Chancellery Austria and Graz University of
 * Technology.
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * http://www.osor.eu/eupl/
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * 
 * This product combines work with different licenses. See the "NOTICE" text
 * file for details on the various modules and licenses.
 * The "NOTICE" text file is part of the distribution. Any derivative works
 * that you distribute must include a readable copy of the "NOTICE" text file.
 ******************************************************************************/
package at.gv.egiz.pdfas.web.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;
import at.gv.egiz.pdfas.web.helper.PdfAsHelper;

public class WebConfiguration implements IConfigurationConstants {

	public static final String PUBLIC_URL = "public.url";
	public static final String LOCAL_BKU_ENABLED = "bku.sign.enabled";
	public static final String ONLINE_BKU_ENABLED = "moc.sign.enabled";
	public static final String MOBILE_BKU_ENABLED = "mobile.sign.enabled";
	public static final String LOCAL_BKU_URL = "bku.local.url";
	public static final String ONLINE_BKU_URL = "bku.online.url";
	public static final String MOBILE_BKU_URL = "bku.mobile.url";
	public static final String ERROR_DETAILS = "error.showdetails";
	public static final String PDF_AS_WORK_DIR = "pdfas.dir";
	public static final String STATISTIC_BACKEND_LIST = "statistic.backends";
	public static final String ALLOW_EXT_OVERWRITE = "allow.ext.overwrite";
	
	public static final String ALLOW_EXT_WHITELIST_VALUE_PRE = "ext.overwrite.wl.";
	
	public static final String MOA_SS_ENABLED = "moa.enabled";
	public static final String SOAP_SIGN_ENABLED = "soap.sign.enabled";
	public static final String SOAP_VERIFY_ENABLED = "soap.verify.enabled";
	public static final String RELOAD_PASSWORD = "reload.pwd";
	public static final String RELOAD_ENABLED = "reload.enabled";
	public static final String KEEP_SIGNED_DOCUMENT = "keep.signed";
	public static final String JSON_API_ENABLED = "json.enabled";

	public static final String MOA_LIST = "moal";
	public static final String MOA_URL = "url";
	public static final String MOA_KEYID = "KeyIdentifier";
	public static final String MOA_CERT = "Certificate";
	
	public static final String KEYSTORE_LIST = "ksl";
	public static final String KEYSTORE_DEFAULT = "ks";
	
	public static final String KEYSTORE_ENABLED = "enabled";
	public static final String KEYSTORE_FILE = "file";
	public static final String KEYSTORE_TYPE = "type";
	public static final String KEYSTORE_PASS = "pass";
	public static final String KEYSTORE_ALIAS = "key.alias";
	public static final String KEYSTORE_KEY_PASS = "key.pass";
	
	public static final String KEYSTORE_DEFAULT_ENABLED = KEYSTORE_DEFAULT + "." + KEYSTORE_ENABLED;
	public static final String KEYSTORE_DEFAULT_FILE = KEYSTORE_DEFAULT + "." + KEYSTORE_FILE;
	public static final String KEYSTORE_DEFAULT_TYPE = KEYSTORE_DEFAULT + "." + KEYSTORE_TYPE;
	public static final String KEYSTORE_DEFAULT_PASS = KEYSTORE_DEFAULT + "." + KEYSTORE_PASS;
	public static final String KEYSTORE_DEFAULT_ALIAS = KEYSTORE_DEFAULT + "." + KEYSTORE_ALIAS;
	public static final String KEYSTORE_DEFAULT_KEY_PASS = KEYSTORE_DEFAULT + "." + KEYSTORE_KEY_PASS;

	public static final String WHITELIST_ENABLED = "whitelist.enabled";
	public static final String WHITELIST_VALUE_PRE = "whitelist.url.";

	public static final String REQUEST_STORE = "request.store";
	public static final String REQUEST_STORE_INMEM = "at.gv.egiz.pdfas.web.store.InMemoryRequestStore";
	public static final String REQUEST_STORE_DB = "at.gv.egiz.pdfas.web.store.DBRequestStore";
	public static final String DB_REQUEST_TIMEOUT = "request.db.timeout";
	public static final String HIBERNATE_PREFIX = "hibernate.props.";

	public static final String UPLOAD_FILESIZE_THRESHOLD = "web.upload.filesizeThreshold";
	public static final String UPLOAD_MAX_FILESIZE = "web.upload.filesizeMax";
	public static final String UPLOAD_MAX_REQUESTSIZE = "web.upload.RequestsizeMax";
	
	public static final String PLACEHOLDER_GENERATOR_ENABLED = "qr.placeholder.generator.enabled";
	
	private static final int THRESHOLD_SIZE = 1024 * 1024 * 3; // 3MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB
	
	private static Properties properties = new Properties();
	private static Properties hibernateProps = new Properties();

	private static final Logger logger = LoggerFactory
			.getLogger(WebConfiguration.class);

	private static List<String> whiteListregEx = new ArrayList<String>();
	private static List<String> overwritewhiteListregEx = new ArrayList<String>();
	
	public static void configure(String config) {

		properties.clear();
		whiteListregEx.clear();
		overwritewhiteListregEx.clear();

		try {
			properties.load(new FileInputStream(config));
		} catch (Exception e) {
			logger.error("Failed to load configuration: " + e.getMessage());
			throw new RuntimeException(e);
		}

		if (isWhiteListEnabled()) {
			Iterator<Object> keyIt = properties.keySet().iterator();
			while (keyIt.hasNext()) {
				Object keyObj = keyIt.next();
				if (keyObj != null) {
					String key = keyObj.toString();
					if (key.startsWith(WHITELIST_VALUE_PRE)) {
						String whitelist_expr = properties.getProperty(key);
						if (whitelist_expr != null) {
							whiteListregEx.add(whitelist_expr);
							logger.debug("URL Whitelist: " + whitelist_expr);
						}
					}
				}
			}
		}
		
		if (isAllowExtOverwrite()) {
			Iterator<Object> keyIt = properties.keySet().iterator();
			while (keyIt.hasNext()) {
				Object keyObj = keyIt.next();
				if (keyObj != null) {
					String key = keyObj.toString();
					if (key.startsWith(ALLOW_EXT_WHITELIST_VALUE_PRE)) {
						String whitelist_expr = properties.getProperty(key);
						if (whitelist_expr != null) {
							overwritewhiteListregEx.add(whitelist_expr);
							logger.debug("Overwrite Whitelist: " + whitelist_expr);
						}
					}
				}
			}
		}

		Iterator<Object> keyIt = properties.keySet().iterator();
		while (keyIt.hasNext()) {
			Object keyObj = keyIt.next();
			if (keyObj != null) {
				String key = keyObj.toString();
				if (key.startsWith(HIBERNATE_PREFIX)) {
					String value = properties.getProperty(key);
					if (value != null) {
						String hibKey = key.replace(HIBERNATE_PREFIX, "");
						hibernateProps.put(hibKey, value);
					}
				}
			}
		}

		if (hibernateProps.size() != 0) {
			logger.debug("DB Properties: ");
			Iterator<Object> hibkeyIt = hibernateProps.keySet().iterator();
			while (hibkeyIt.hasNext()) {
				Object keyObj = hibkeyIt.next();
				if (keyObj != null) {
					String key = keyObj.toString();
					String value = hibernateProps.getProperty(key);
					logger.debug("  {}: {}", key, value);
				}
			}
		}

		String pdfASDir = getPdfASDir();
		if (pdfASDir == null) {
			logger.error("Please configure pdf as working directory in the web configuration");
			throw new RuntimeException(
					"Please configure pdf as working directory in the web configuration");
		}

		File f = new File(pdfASDir);
 
		if (!f.exists() || !f.isDirectory()) {
			logger.error("Pdf As working directory does not exists or is not a directory!: "
					+ pdfASDir);
			throw new RuntimeException(
					"Pdf As working directory does not exists or is not a directory!");
		}
	}

	public static String getPublicURL() {
		return properties.getProperty(PUBLIC_URL);
	}

	public static String getLocalBKUURL() {
		if(!getLocalBKUEnabled()) {
			String overwrite = properties.getProperty(CONFIG_BKU_URL);
			if(overwrite == null) {
				overwrite = properties.getProperty(LOCAL_BKU_URL);
				if(overwrite == null) {
					overwrite = PdfAsHelper.getPdfAsConfig().getValue(CONFIG_BKU_URL);
				}
			}
			return overwrite;
		}
		return null;
	}

	public static String getOnlineBKUURL() {
		if(!getOnlineBKUEnabled()) {
			String overwrite = properties.getProperty(MOC_SIGN_URL);
			if(overwrite == null) {
				overwrite = properties.getProperty(ONLINE_BKU_URL);
				if(overwrite == null) {
					overwrite = PdfAsHelper.getPdfAsConfig().getValue(MOC_SIGN_URL);
				}
			}
			return overwrite;
		}
		return null;
	}

	public static String getHandyBKUURL() {
		if(!getMobileBKUEnabled()) {
			String overwrite = properties.getProperty(MOBILE_SIGN_URL);
			if(overwrite == null) {
				overwrite = properties.getProperty(MOBILE_BKU_URL);
				if(overwrite == null) {
					overwrite = PdfAsHelper.getPdfAsConfig().getValue(MOBILE_SIGN_URL);
				}
			}
			return overwrite;
		}
		return null;
	}

	public static String getPdfASDir() {
		return properties.getProperty(PDF_AS_WORK_DIR);
	}

	public static String getKeystoreDefaultFile() {
		return properties.getProperty(KEYSTORE_DEFAULT_FILE);
	}

	public static String getKeystoreDefaultType() {
		return properties.getProperty(KEYSTORE_DEFAULT_TYPE);
	}

	public static String getKeystoreDefaultPass() {
		return properties.getProperty(KEYSTORE_DEFAULT_PASS);
	}

	public static String getKeystoreDefaultAlias() {
		return properties.getProperty(KEYSTORE_DEFAULT_ALIAS);
	}

	public static String getKeystoreDefaultKeyPass() {
		return properties.getProperty(KEYSTORE_DEFAULT_KEY_PASS);
	}
	
	public static boolean isAllowExtOverwrite() {
		String value = properties.getProperty(ALLOW_EXT_OVERWRITE);
		if (value != null) {
			if (value.equals("true")) {
				return true;
			}
		}
		return false;
	}
	
	public static synchronized boolean isOverwriteAllowed(String key) {
		if (isAllowExtOverwrite()) {

			Iterator<String> patterns = overwritewhiteListregEx.iterator();
			while (patterns.hasNext()) {
				String pattern = patterns.next();
				try {
					if (key.matches(pattern)) {
						return true;
					}
				} catch (Throwable e) {
					logger.warn("Error in matching regex: " + pattern, e);
				}
			}

			return false;
		}
		return false;
	}

	public static boolean isJSONAPIEnabled() {
		String value = properties.getProperty(JSON_API_ENABLED);
		if (value != null) {
			if (value.equals("true")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isKeepSignedDocument() {
		String value = properties.getProperty(KEEP_SIGNED_DOCUMENT);
		if (value != null) {
			if (value.equals("true")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isMoaEnabled(String keyIdentifier) {
		String value = properties.getProperty(MOA_LIST + "." + keyIdentifier + ".enabled");
		if (value != null) {
			if (value.equals("true")) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isQRPlaceholderGenerator() {
		String value = properties.getProperty(PLACEHOLDER_GENERATOR_ENABLED);
		if (value != null) {
			if (value.equals("true")) {
				return true;
			}
		}
		return false;
	}
	
	public static String getMoaURL(String keyIdentifier) {
		return properties.getProperty(MOA_LIST + "." + keyIdentifier + "." + MOA_URL);
	}
	
	public static String getMoaKeyID(String keyIdentifier) {
		return properties.getProperty(MOA_LIST + "." + keyIdentifier + "." + MOA_KEYID);
	}
	
	public static String getMoaCertificate(String keyIdentifier) {
		return properties.getProperty(MOA_LIST + "." + keyIdentifier + "." + MOA_CERT);
	}
	
	public static String getKeystoreFile(String keyIdentifier) {
		return properties.getProperty(KEYSTORE_LIST + "." + keyIdentifier + "." + KEYSTORE_FILE);
	}

	public static String getKeystoreType(String keyIdentifier) {
		return properties.getProperty(KEYSTORE_LIST + "." + keyIdentifier + "." + KEYSTORE_TYPE);
	}

	public static String getKeystorePass(String keyIdentifier) {
		return properties.getProperty(KEYSTORE_LIST + "." + keyIdentifier + "." + KEYSTORE_PASS);
	}

	public static String getKeystoreAlias(String keyIdentifier) {
		return properties.getProperty(KEYSTORE_LIST + "." + keyIdentifier + "." + KEYSTORE_ALIAS);
	}

	public static String getKeystoreKeyPass(String keyIdentifier) {
		return properties.getProperty(KEYSTORE_LIST + "." + keyIdentifier + "." + KEYSTORE_KEY_PASS);
	}

	public static List<String> getStatisticBackends() {
		List<String> statisticBackends = new ArrayList<String>();
		String commaList = properties.getProperty(STATISTIC_BACKEND_LIST);
		if(commaList != null) {
			String[] commaLists = commaList.split(",");
			for(int i = 0; i < commaLists.length; i++) {
				statisticBackends.add(commaLists[i].trim());
			}
			return statisticBackends;
		}
		return null;
	}
	
	public static boolean getMOASSEnabled() {
		String value = properties.getProperty(MOA_SS_ENABLED);
		if (value != null) {
			if (value.equals("true")) {
				return true;
			}
		}
		return false;
	}

	public static boolean getKeystoreDefaultEnabled() {
		String value = properties.getProperty(KEYSTORE_DEFAULT_ENABLED);
		if (value != null) {
			if (value.equals("true")) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean getKeystoreEnabled(String keyIdentifier) {
		String value = properties.getProperty(KEYSTORE_LIST + "." + keyIdentifier + "." + KEYSTORE_ENABLED);
		if (value != null) {
			if (value.equals("true")) {
				return true;
			}
		}
		return false;
	}

	public static boolean getLocalBKUEnabled() {
		String value = properties.getProperty(LOCAL_BKU_ENABLED);
		if (value != null) {
			if (value.equals("true")) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean getOnlineBKUEnabled() {
		String value = properties.getProperty(ONLINE_BKU_ENABLED);
		if (value != null) {
			if (value.equals("true")) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean getMobileBKUEnabled() {
		String value = properties.getProperty(MOBILE_BKU_ENABLED);
		if (value != null) {
			if (value.equals("true")) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean getSoapSignEnabled() {
		String value = properties.getProperty(SOAP_SIGN_ENABLED);
		if (value != null) {
			if (value.equals("true")) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean getSoapVerifyEnabled() {
		String value = properties.getProperty(SOAP_VERIFY_ENABLED);
		if (value != null) {
			if (value.equals("true")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isShowErrorDetails() {
		String value = properties.getProperty(ERROR_DETAILS);
		if (value != null) {
			if (value.equals("true")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isWhiteListEnabled() {
		String value = properties.getProperty(WHITELIST_ENABLED);
		if (value != null) {
			if (value.equals("true")) {
				return true;
			}
		}
		return false;
	}

	public static synchronized boolean isProvidePdfURLinWhitelist(String url) {
		if (isWhiteListEnabled()) {

			Iterator<String> patterns = whiteListregEx.iterator();
			while (patterns.hasNext()) {
				String pattern = patterns.next();
				try {
					if (url.matches(pattern)) {
						return true;
					}
				} catch (Throwable e) {
					logger.warn("Error in matching regex: " + pattern, e);
				}
			}

			return false;
		}
		return true;
	}

	public static Properties getHibernateProps() {
		return (Properties) hibernateProps.clone();
	}

	public static int getDBTimeout() {
		String value = properties.getProperty(DB_REQUEST_TIMEOUT);
		int ivalue = 600;
		if (value != null) {
			try {
				ivalue = Integer.parseInt(value);
			} catch(NumberFormatException e) {
				logger.error("DB request Timeout not a number", e);
			}
		}
		return ivalue;
	}
	
	public static String getStoreClass() {
		String cls = properties.getProperty(REQUEST_STORE);

		if (cls != null) {
			return cls;
		}

		return REQUEST_STORE_INMEM;
	}
	
	public static String getReloadPassword() {
		return properties.getProperty(RELOAD_PASSWORD);
	}
	
	public static boolean getReloadEnabled() {
		String value = properties.getProperty(RELOAD_ENABLED);
		if (value != null) {
			if (value.equals("true")) {
				return true;
			}
		}
		return false;
	}
	
	public static int getFilesizeThreshold() {
		String value = properties.getProperty(UPLOAD_FILESIZE_THRESHOLD);
		int ivalue = THRESHOLD_SIZE;
		if (value != null) {
			try {
				ivalue = Integer.parseInt(value);
			} catch(NumberFormatException e) {
				logger.warn(UPLOAD_FILESIZE_THRESHOLD + " not a number", e);
			}
		}
		return ivalue;
	}
	
	public static int getMaxFilesize() {
		String value = properties.getProperty(UPLOAD_MAX_FILESIZE);
		int ivalue = MAX_FILE_SIZE;
		if (value != null) {
			try {
				ivalue = Integer.parseInt(value);
			} catch(NumberFormatException e) {
				logger.warn(UPLOAD_MAX_FILESIZE + " not a number", e);
			}
		}
		return ivalue;
	}
	
	public static int getMaxRequestsize() {
		String value = properties.getProperty(UPLOAD_MAX_REQUESTSIZE);
		int ivalue = MAX_REQUEST_SIZE;
		if (value != null) {
			try {
				ivalue = Integer.parseInt(value);
			} catch(NumberFormatException e) {
				logger.warn(UPLOAD_MAX_REQUESTSIZE + " not a number", e);
			}
		}
		return ivalue;
	}
}
