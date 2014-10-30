package at.gv.egiz.pdfas.common.utils;

import at.gv.egiz.pdfas.common.settings.ISettings;

public class SettingsUtils {
	public static boolean getBooleanValue(ISettings setting, String key, boolean defaultValue) {
		String theValue = setting.getValue(key);
		if(theValue != null) {
			if(theValue.equals("true")) {
				return true;
			} else if(theValue.equals("false")) {
				return false;
			} else {
				return defaultValue;
			}
		}
		return defaultValue;
	}
}
