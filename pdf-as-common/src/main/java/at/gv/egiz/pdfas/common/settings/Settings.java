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

import at.gv.egiz.pdfas.common.exceptions.PdfAsSettingsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Settings implements ISettings, IProfileConstants{

    private static final Logger logger = LoggerFactory.getLogger(Settings.class);

    protected Properties properties = new Properties();

    protected File workDirectory;
    
    public Settings(File workDirectory) {
        try {
        	this.workDirectory = workDirectory;
            loadSettings(workDirectory);
        } catch (PdfAsSettingsException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void loadSettings(File workDirectory) throws PdfAsSettingsException {
        try {
        	
        	String configDir = workDirectory.getAbsolutePath() + File.separator + CFG_DIR;
        	String configFile = configDir + File.separator + CFG_FILE;
        	logger.debug("Loading cfg file: " + configFile);
            properties.load(new FileInputStream(configFile));
            
            Map<String, String> includes = this.getValuesPrefix(INCLUDE);
            
            if(includes != null) {
            	Iterator<String> includeIterator = includes.values().iterator();
            	while(includeIterator.hasNext()) {
            		String includeFile = configDir + File.separator + includeIterator.next();
            		logger.debug("Loading included cfg file: " + includeFile);
            		try {
            			properties.load(new FileInputStream(includeFile));
            		} catch(Throwable e) {
            			logger.error("Failed to load cfg file " + includeFile, e);
            		}
            	}
            }
            
            logger.debug("Configured Properties:");
            /*if(logger.isDebugEnabled()) {
            	properties.list(System.out);
            }*/
            
        } catch (IOException e) {
            throw new PdfAsSettingsException("Failed to read settings!", e);
        }
    }

    public String getValue(String key) {
        return properties.getProperty(key);
    }

    public boolean hasValue(String key) {
        return properties.containsKey(key);
    }

    public Map<String, String> getValuesPrefix(String prefix) {
        Iterator<Object> keyIterator = properties.keySet().iterator();
        Map<String, String> valueMap = new HashMap<String, String>();
        while(keyIterator.hasNext()) {
            String key = keyIterator.next().toString();

            if(key.startsWith(prefix)) {
                valueMap.put(key, properties.getProperty(key));
            }
        }

        if(valueMap.isEmpty()) {
            return null;
        }

        return valueMap;
    }

    public Vector<String> getFirstLevelKeys(String prefix) {
        String mPrefix = prefix.endsWith(".")?prefix:prefix+".";
        Iterator<Object> keyIterator = properties.keySet().iterator();
        Vector<String> valueMap = new Vector<String>();
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
		Iterator<Object> keyIterator = properties.keySet().iterator();
        while(keyIterator.hasNext()) {
            String key = keyIterator.next().toString();

            if(key.startsWith(prefix)) {
                return true;
            }
        }
        return false;
	}

	public String getWorkingDirectory() {
		return this.workDirectory.getAbsolutePath();
	}

}
