package at.gv.egiz.pdfas.lib.impl.configuration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.common.settings.Settings;
import at.gv.egiz.pdfas.lib.api.Configuration;

public class ConfigurationImpl implements ISettings, Configuration {

	protected Properties overwrittenProperties = new Properties();
	
	public void setValue(String key, String value) {
		overwrittenProperties.setProperty(key, value);
	}

	public String getValue(String key) {
		if(overwrittenProperties.containsKey(key)) {
			return overwrittenProperties.getProperty(key);
		} else {
			return Settings.getInstance().getValue(key);
		}
	}

	public boolean hasValue(String key) {
		if(overwrittenProperties.containsKey(key)) {
			return true;
		} else {
			return Settings.getInstance().hasValue(key);
		}
	}

	public Map<String, String> getValuesPrefix(String prefix) {
		
		Map<String, String> valueMap = null;
		valueMap = Settings.getInstance().getValuesPrefix(prefix);
		if(valueMap == null) {
			valueMap = new HashMap<String, String>();
		}
		
		Iterator<Object> keyIterator = overwrittenProperties.keySet().iterator();
        
        while(keyIterator.hasNext()) {
            String key = keyIterator.next().toString();

            if(key.startsWith(prefix)) {
                valueMap.put(key, overwrittenProperties.getProperty(key));
            }
        }

        if(valueMap.isEmpty()) {
            return null;
        }

        return valueMap;
	}

	public Vector<String> getFirstLevelKeys(String prefix) {
		
		Vector<String> valueMap = Settings.getInstance().getFirstLevelKeys(prefix);
		if(valueMap == null) {
			valueMap = new Vector<String>();
		}
		
		
		String mPrefix = prefix.endsWith(".")?prefix:prefix+".";
        Iterator<Object> keyIterator = overwrittenProperties.keySet().iterator();
        
        while(keyIterator.hasNext()) {
            String key = keyIterator.next().toString();

            if(key.startsWith(prefix)) {
                int keyIdx = key.indexOf('.', mPrefix.length()) > 0 ?  key.indexOf('.', mPrefix.length()) : key.length();
                String firstLevels = key.substring(0, keyIdx);
                if(!valueMap.contains(firstLevels)) {
                    valueMap.add(firstLevels);
                }
            }
        }

        if(valueMap.isEmpty()) {
            return null;
        }

        return valueMap;
	}

	public boolean hasPrefix(String prefix) {
		
		if(Settings.getInstance().hasPrefix(prefix)) {
			return true;
		}
		
		Iterator<Object> keyIterator = overwrittenProperties.keySet().iterator();
        while(keyIterator.hasNext()) {
            String key = keyIterator.next().toString();

            if(key.startsWith(prefix)) {
                return true;
            }
        }
        return false;
	}

	
}
