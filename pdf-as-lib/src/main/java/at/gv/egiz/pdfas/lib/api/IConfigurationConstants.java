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
package at.gv.egiz.pdfas.lib.api;

/**
 * Collection of useful configuration key
 */
public interface IConfigurationConstants {

	public static final String TRUE = "true";
	public static final String FALSE = "false";
	
	public static final String SIG_OBJECT = "sig_obj";
	public static final String TYPE = "type";
	public static final String TABLE = "table";
	public static final String MAIN = "main";
	public static final String ISVISIBLE = "isvisible";
	public static final String POS = "pos";
	public static final String DEFAULT = "default";
	public static final String SEPERATOR = ".";

	
	public static final String LEGACY_POSITIONING = ".legacy.pos";
	public static final String LEGACY_40_POSITIONING = ".legacy40.pos";
	public static final String MIN_WIDTH = "minWidth";

	public static final String PLACEHOLDER_WEB_ID = "placeholder_web_id";
	public static final String PLACEHOLDER_ID = "placeholder_id";
	public static final String PLACEHOLDER_MODE = "placeholder_mode";
	
	public static final String PLACEHOLDER_SEARCH_ENABLED = "enable_placeholder_search";
	public static final String DEFAULT_SIGNATURE_PROFILE = SIG_OBJECT + SEPERATOR + TYPE + SEPERATOR + DEFAULT;

	public static final String CONFIG_BKU_URL = "bku.sign.url";

	/**
	 * MOA SS Signing Key Identifier
	 */
	public static final String MOA_SS_KEY_IDENTIFIER = "moa.sign.KeyIdentifier";
	
	/**
	 * MOA SS Signing Certificate
	 */
	public static final String MOA_SS_KEY_CERTIFICATE = "moa.sign.Certificate";
	
	
	/**
	 * MOA Trustprofile configuration Key
	 */
	public static final String MOA_TRUSTPROFILE_ID = "moa.verify.TrustProfileID";
	
	public static final String MOA_SIGN_KEY_ID = MOA_SS_KEY_IDENTIFIER;
	public static final String MOA_SIGN_CERTIFICATE = MOA_SS_KEY_CERTIFICATE;
	
	/**
	 * MOA Verify URL configuration Key
	 */
	public static final String MOA_VERIFY_URL = "moa.verify.url";
	
	
	/**
	 * MOA Sign URL configuration Key
	 */
	public static final String MOA_SIGN_URL = "moa.sign.url";
	
	public static final String KEEP_INVALID_SIGNATURE = "report.invalidSign";
	
	public static final String MOC_SIGN_URL = "moc.sign.url";
	public static final String MOBILE_SIGN_URL = "mobile.sign.url";
	public static final String SL20_SIGN_URL = "sl20.sign.url";
	
	public static final String REGISTER_PROVIDER = "registerSecurityProvider";
	
	public static final String SL_REQUEST_TYPE = "sl.request.type";
	public static final String SL_REQUEST_TYPE_BASE64 = "b64";
	public static final String SL_REQUEST_TYPE_UPLOAD = "upload";
	
	/**
	 * Signature object Prefix
	 */
	public static final String SIG_RESERVED_SIZE = "signatureSize";

	/**
	 * Visual Signature placement
	 */
	public static final String BG_COLOR_DETECTION = "sigblock.placement.bgcolor.detection.enabled";
	public static final String SIG_PLACEMENT_DEBUG_OUTPUT = "sigblock.placement.debug.file";

	/**
	 * PADES Constants
	 */
	public static final String SIG_PADES_FORCE_FLAG= SIG_OBJECT + SEPERATOR+"PAdESCompatibility";
	public static final String SIG_PADES_INTELL_FLAG = SIG_OBJECT + SEPERATOR+"CheckPAdESCompatibility";

}
