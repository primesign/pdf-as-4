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
package at.gv.egiz.pdfas.common.settings;

import java.util.HashMap;
import java.util.Map;

public class DefaultSignatureProfileSettings {

	private static Map<String, SignatureProfileEntry> profileSettings = new HashMap<String, SignatureProfileEntry>();

	public static final String KEY_SIG_SUBJECT = "SIG_SUBJECT";
	public static final String KEY_SIG_SUBJECT_DEFAULT = "Unterzeichner";
	
	public static final String KEY_SIG_ISSUER = "SIG_ISSUER";
	public static final String KEY_SIG_ISSUER_DEFAULT = "Aussteller-Zertifikat";
	
	public static final String KEY_SIG_NUMBER = "SIG_NUMBER";
	public static final String KEY_SIG_NUMBER_DEFAULT = "Serien-Nr.";
	
	public static final String KEY_SIG_KZ = "SIG_KZ";
	public static final String KEY_SIG_KZ_DEFAULT = "Methode";
	
	public static final String KEY_SIG_ID = "SIG_ID";
	public static final String KEY_SIG_ID_DEFAULT = "Parameter";
	
	public static final String KEY_SIG_META = "SIG_META";
	public static final String KEY_SIG_META_DEFAULT = "Pr\u00fcfinformation";
	
	public static final String KEY_SIG_DATE = "SIG_DATE";
	public static final String KEY_SIG_DATE_DEFAULT = "Datum/Zeit-UTC";
	
	public static final String VALUE_SIG_SUBJECT_DEFAULT = "${subject.T != null ? (subject.T + \" \") : \"\"}${subject.CN}";
	
	public static final String VALUE_SIG_ISSUER_DEFAULT = "${issuer.DN}";
	
	public static final String VALUE_SIG_NUMBER_DEFAULT = "${sn}";
	
	public static final String VALUE_SIG_KZ_DEFAULT = "";
	
	public static final String VALUE_SIG_ID_DEFAULT = "";
	
	public static final String VALUE_SIG_META_DEFAULT = "Informationen zur Pr\u00FCfung der elektronischen Signatur finden Sie unter: https://www.signaturpruefung.gv.at\n\nInformationen zur Pr\u00FCfung des Ausdrucks finden Sie unter: https://www.behoerde.gv.at/el_signatur/";
	
	public static final String VALUE_SIG_DATE_DEFAULT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
	static {
		profileSettings.put(KEY_SIG_SUBJECT, new SignatureProfileEntry(KEY_SIG_SUBJECT, KEY_SIG_SUBJECT_DEFAULT, VALUE_SIG_SUBJECT_DEFAULT) );
		profileSettings.put(KEY_SIG_ISSUER, new SignatureProfileEntry(KEY_SIG_ISSUER, KEY_SIG_ISSUER_DEFAULT, VALUE_SIG_ISSUER_DEFAULT) );
		profileSettings.put(KEY_SIG_NUMBER, new SignatureProfileEntry(KEY_SIG_NUMBER, KEY_SIG_NUMBER_DEFAULT, VALUE_SIG_NUMBER_DEFAULT) );
		profileSettings.put(KEY_SIG_KZ, new SignatureProfileEntry(KEY_SIG_KZ, KEY_SIG_KZ_DEFAULT, VALUE_SIG_KZ_DEFAULT) );
		profileSettings.put(KEY_SIG_ID, new SignatureProfileEntry(KEY_SIG_ID, KEY_SIG_ID_DEFAULT, VALUE_SIG_ID_DEFAULT) );
		profileSettings.put(KEY_SIG_META, new SignatureProfileEntry(KEY_SIG_META, KEY_SIG_META_DEFAULT, VALUE_SIG_META_DEFAULT) );
		profileSettings.put(KEY_SIG_DATE, new SignatureProfileEntry(KEY_SIG_DATE, KEY_SIG_DATE_DEFAULT, VALUE_SIG_DATE_DEFAULT) );
	}
	
	public static Map<String, SignatureProfileEntry> getDefaultValues() {
		return profileSettings;
	}
	
	public static String getDefaultKeyValue(String key) {
		SignatureProfileEntry entry = profileSettings.get(key);
		if(entry != null) {
			return entry.getValue();
		}
		return null;
	}
	
	public static String getDefaultKeyCaption(String key) {
		SignatureProfileEntry entry = profileSettings.get(key);
		if(entry != null) {
			return entry.getCaption();
		}
		return null;
	}
	
}
