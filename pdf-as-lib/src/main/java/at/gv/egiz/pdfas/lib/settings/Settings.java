/*******************************************************************************
 * <copyright> Copyright 2014 by E-Government Innovation Center EGIZ, Graz, Austria </copyright>
 * PDF-AS has been contracted by the E-Government Innovation Center EGIZ, a
 * joint initiative of the Federal Chancellery Austria and Graz University of
 * Technology.
 * <p>
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * http://www.osor.eu/eupl/
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * <p>
 * This product combines work with different licenses. See the "NOTICE" text
 * file for details on the various modules and licenses.
 * The "NOTICE" text file is part of the distribution. Any derivative works
 * that you distribute must include a readable copy of the "NOTICE" text file.
 ******************************************************************************/
package at.gv.egiz.pdfas.lib.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsSettingsException;
import at.gv.egiz.pdfas.common.settings.IProfileConstants;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.common.settings.Profiles;

public class Settings implements ISettings, IProfileConstants {

    private static final Logger logger = LoggerFactory
            .getLogger(Settings.class);

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

    private void loadSettingsRecursive(File workDirectory, File file)
            throws PdfAsSettingsException {

    	String configDir = workDirectory.getAbsolutePath() + File.separator
    			+ CFG_DIR;

    	logger.debug("Loading: " + file.getName());

    	try (InputStream in = new FileInputStream(file)) {

    		Properties tmpProps = new Properties();
            tmpProps.load(in);
            properties.putAll(tmpProps);

            Map<String, String> includes = this.getValuesPrefix(INCLUDE,
                    tmpProps);
            File contextFolder = new File(configDir);
            if (includes != null) {
                Iterator<String> includeIterator = includes.values().iterator();
                while (includeIterator.hasNext()) {
                    contextFolder = new File(configDir);
                    String includeFileName = includeIterator.next();

                    File includeInstruction = new File(contextFolder,
                            includeFileName);
                    contextFolder = includeInstruction.getParentFile();
                    String includeName = includeInstruction.getName();

                    WildcardFileFilter fileFilter = new WildcardFileFilter(
                            includeName, IOCase.SENSITIVE);
                    Collection<File> includeFiles = null;

                    if (contextFolder != null && contextFolder.exists()
                            && contextFolder.isDirectory()) {
                        includeFiles = FileUtils.listFiles(contextFolder,
                                fileFilter, null);
                    }
                    if (includeFiles != null && !includeFiles.isEmpty()) {
                        logger.debug("Including '" + includeFileName + "'.");
                        for (File includeFile : includeFiles) {
                            loadSettingsRecursive(workDirectory, includeFile);
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new PdfAsSettingsException("Failed to read settings!", e);
        }
    }

    private void showAugments(Profiles profiles) {
        if (!profiles.getAugments().isEmpty()) {
            logger.debug("\tAugments for {}", profiles.getName());
            for (int i = 0; i < profiles.getAugments().size(); i++) {
                logger.debug("\t\t{}", profiles.getAugments().get(i).getName());
            }
        }
    }

    private boolean isAugmentsReady(Profiles profiles) {
        Iterator<Profiles> augmentingProfiles = profiles.getAugments().iterator();
        boolean allInitialized = true;
        while (augmentingProfiles.hasNext()) {
            if (!augmentingProfiles.next().isInitialized()) {
                allInitialized = false;
            }
        }
        return allInitialized;
    }

    private boolean isParentReady(Profiles profiles) {
        if (profiles.getParent() != null) {
            return profiles.getParent().isInitialized();
        } else {
            return false;
        }
    }

    private void performAugmentConfiguration(Profiles profiles) {
        Iterator<Profiles> augmentingProfiles = profiles.getAugments().iterator();

        String childBase = "sig_obj."
                + profiles.getName();

        while (augmentingProfiles.hasNext()) {
            Profiles augmentingProfile = augmentingProfiles.next();
            String augmentingBase = "sig_obj." + augmentingProfile.getName();

            Iterator<String> augmentingKeyIt = this.getKeys(
                    augmentingBase + ".").iterator();

            while (augmentingKeyIt.hasNext()) {
                String key = augmentingKeyIt.next();
                String keyToCopy = key.substring(augmentingBase
                        .length());
                //logger.debug("Profile: {} => {}",
                //		key, childBase+keyToCopy);
                String sourceKey = augmentingBase + keyToCopy;
                String targetKey = childBase + keyToCopy;

                if (!this.hasValue(targetKey)) {
                    properties.setProperty(targetKey,
                            this.getValue(sourceKey));
                    //logger.debug("Replaced: {} with Value from {}",
                    //		childBase+keyToCopy, parentBase+keyToCopy);
                } else {
                    //logger.debug("NOT Replaced: {} with Value from {}",
                    //		childBase+keyToCopy, parentBase+keyToCopy);
                }
            }
        }
    }

    private void performParentConfiguration(Profiles profiles) {
        if (profiles.getParent() != null) {
            // If Parent is initialized Copy Properties from Parent
            // to this profile
            String parentBase = "sig_obj." + profiles.getParent().getName();
            String childBase = "sig_obj."
                    + profiles.getName();

            Iterator<String> parentKeyIt = this.getKeys(
                    parentBase + ".").iterator();
            while (parentKeyIt.hasNext()) {
                String key = parentKeyIt.next();
                String keyToCopy = key.substring(parentBase
                        .length());
                //logger.debug("Profile: {} => {}",
                //		key, childBase+keyToCopy);
                String sourceKey = parentBase + keyToCopy;
                String targetKey = childBase + keyToCopy;

                if (!this.hasValue(targetKey)) {
                    properties.setProperty(targetKey,
                            this.getValue(sourceKey));
                    //logger.debug("Replaced: {} with Value from {}",
                    //		childBase+keyToCopy, parentBase+keyToCopy);
                } else {
                    //logger.debug("NOT Replaced: {} with Value from {}",
                    //		childBase+keyToCopy, parentBase+keyToCopy);
                }
            }
        }
    }

    private void buildProfiles() {
        Map<String, Profiles> profiles = new HashMap<String, Profiles>();

        Iterator<String> itKeys = this.getFirstLevelKeys("sig_obj.types.")
                .iterator();
        while (itKeys.hasNext()) {
            String key = itKeys.next();
            String profile = key.substring("sig_obj.types.".length());
            //System.out.println("[" + profile + "]: " + this.getValue(key));
            if (this.getValue(key).equals("on")) {
                Profiles prof = new Profiles(profile);
                profiles.put(profile, prof);
            }
        }

        // Initialize Parent Structure ...
        Iterator<Entry<String, Profiles>> profileIterator = profiles.entrySet()
                .iterator();
        while (profileIterator.hasNext()) {
            Entry<String, Profiles> entry = profileIterator.next();
            entry.getValue().findParent(properties, profiles);
        }

        // Debug Output
        Iterator<Entry<String, Profiles>> profileIteratorDbg = profiles.entrySet()
                .iterator();
        while (profileIteratorDbg.hasNext()) {
            Entry<String, Profiles> entry = profileIteratorDbg.next();
            if (entry.getValue().getParent() == null) {
                logger.debug("Got Profile: [{}] : {}", entry.getKey(), entry.getValue().getName());
                showAugments(entry.getValue());
            } else {
                logger.debug("Got Profile: [{}] : {} (Parent {})", entry.getKey(),
                        entry.getValue().getName(), entry.getValue().getParent().getName());
                showAugments(entry.getValue());
            }
        }

        logger.debug("Configured Settings: {}",
                properties.size());

        // Resolve Parent Structures ...
        while (!profiles.isEmpty()) {
            List<String> removes = new ArrayList<String>();
            Iterator<Entry<String, Profiles>> profileIt = profiles.entrySet()
                    .iterator();
            while (profileIt.hasNext()) {
                Entry<String, Profiles> entry = profileIt.next();

                // Remove all base Profiles ...
                if (entry.getValue().getParent() == null && entry.getValue().getAugments().isEmpty()) {
                    // Has neither parent or augmenting profiles

                    entry.getValue().setInitialized(true);
                    removes.add(entry.getKey());
                } else if (entry.getValue().getParent() == null) {
                    // Has augmenting profiles but no parent

                    // check if all augmenting profiles are initialized if so
                    // add them

                    Profiles profile = entry.getValue();
                    if (this.isAugmentsReady(profile)) {
                        this.performAugmentConfiguration(profile);
                        // Copy done
                        entry.getValue().setInitialized(true);
                        removes.add(entry.getKey());
                    } else {
                        logger.debug("Not all augmenting profiles are ready yet for {}", entry.getValue().getName());
                    }
                } else if (entry.getValue().getAugments().isEmpty()) {

                    // Has parent but no augmenting profiles
                    Profiles profile = entry.getValue();

                    if (this.isParentReady(profile)) {
                        this.performParentConfiguration(profile);
                        // Copy done
                        entry.getValue().setInitialized(true);
                        removes.add(entry.getKey());
                    }
                } else {

                    // Has parent and augmenting profiles

                    Profiles profile = entry.getValue();
                    if (this.isAugmentsReady(profile) && this.isParentReady(profile)) {
                        // order is curcial, augments preceed over parent configuration
                        this.performAugmentConfiguration(profile);
                        this.performParentConfiguration(profile);

                        // Copy done
                        entry.getValue().setInitialized(true);
                        removes.add(entry.getKey());
                    }
                }
            }

            // Remove all Profiles from Remove List

            if (removes.isEmpty() && !profiles.isEmpty()) {
                logger.error("Failed to build inheritant Profiles, running in infinite loop! (aborting ...)");
                logger.error("Profiles that cannot be resolved completly:");
                Iterator<Entry<String, Profiles>> failedProfiles = profiles.entrySet().iterator();
                while (failedProfiles.hasNext()) {
                    Entry<String, Profiles> entry = failedProfiles.next();
                    logger.error("Problem Profile: [{}] : {}", entry.getKey(), entry.getValue().getName());
                }
                return;
            }

            Iterator<String> removeIt = removes.iterator();
            while (removeIt.hasNext()) {
                profiles.remove(removeIt.next());
            }
        }

        logger.debug("Derived Settings: {}",
                properties.size());

    }

    public void debugDumpProfileSettings(String profileName) {
        Iterator<String> keysIterator = this.getKeys("sig_obj." + profileName + ".").iterator();

        logger.debug("Configuration for {}", profileName);
        while(keysIterator.hasNext()) {
            String key = keysIterator.next();
            logger.debug("  {}: {}", key, this.getValue(key));
        }
    }

    public void loadSettings(File workDirectory) throws PdfAsSettingsException {
        // try {
        String configDir = workDirectory.getAbsolutePath() + File.separator
                + CFG_DIR;
        String configFile = configDir + File.separator + CFG_FILE;
        loadSettingsRecursive(workDirectory, new File(configFile));
        buildProfiles();
        /*
         * logger.debug("Loading cfg file: " + configFile);
		 *
		 *
		 * properties.load(new FileInputStream(configFile));
		 *
		 * Map<String, String> includes = this.getValuesPrefix(INCLUDE); File
		 * contextFolder = new File(configDir); if (includes != null) {
		 * Iterator<String> includeIterator = includes.values().iterator();
		 * while (includeIterator.hasNext()) { String includeFileName =
		 * includeIterator.next(); if (includeFileName.contains("*")) {
		 * WildcardFileFilter fileFilter = new WildcardFileFilter(
		 * includeFileName, IOCase.SENSITIVE); Collection<File> includeFiles =
		 * null;
		 *
		 * if (contextFolder != null && contextFolder.exists() &&
		 * contextFolder.isDirectory()) { includeFiles =
		 * FileUtils.listFiles(contextFolder, fileFilter, null); } if
		 * (includeFiles != null && !includeFiles.isEmpty()) {
		 * logger.info("Including '" + includeFileName + "'."); for (File
		 * includeFile : includeFiles) { properties .load(new
		 * FileInputStream(includeFile)); } } } else { String includeFile =
		 * configDir + File.separator + includeFileName;
		 * logger.debug("Loading included cfg file: " + includeFile); try {
		 * properties.load(new FileInputStream(includeFile)); } catch (Throwable
		 * e) { logger.error("Failed to load cfg file " + includeFile, e); } } }
		 * }
		 */
        // logger.debug("Configured Properties:");
		/*
		 * if(logger.isDebugEnabled()) { properties.list(System.out); }
		 */

        // } catch (IOException e) {
        // throw new PdfAsSettingsException("Failed to read settings!", e);
        // }
    }

    public String getValue(String key) {
        return properties.getProperty(key);
    }

    public boolean hasValue(String key) {
        return properties.containsKey(key);
    }

    private Map<String, String> getValuesPrefix(String prefix, Properties props) {
        Iterator<Object> keyIterator = props.keySet().iterator();
        Map<String, String> valueMap = new HashMap<String, String>();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next().toString();


            if (key.startsWith(prefix)) {
                valueMap.put(key, props.getProperty(key));
            }
        }

        if (valueMap.isEmpty()) {
            return null;
        }

        return valueMap;
    }

    public Map<String, String> getValuesPrefix(String prefix) {
        return getValuesPrefix(prefix, properties);
    }

    public Vector<String> getKeys(String prefix) {
        Iterator<Object> keyIterator = properties.keySet().iterator();
        Vector<String> valueMap = new Vector<String>();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next().toString();

            if (key.startsWith(prefix)) {
                valueMap.add(key);
            }
        }
        return valueMap;
    }

    public Vector<String> getFirstLevelKeys(String prefix) {
        String mPrefix = prefix.endsWith(".") ? prefix : prefix + ".";
        Iterator<Object> keyIterator = properties.keySet().iterator();
        Vector<String> valueMap = new Vector<String>();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next().toString();

            if (key.startsWith(prefix)) {
                int keyIdx = key.indexOf('.', mPrefix.length()) > 0 ? key
                        .indexOf('.', mPrefix.length()) : key.length();
                String firstLevels = key.substring(0, keyIdx);
                if (!valueMap.contains(firstLevels)) {
                    valueMap.add(firstLevels);
                }
            }
        }

        if (valueMap.isEmpty()) {
            return null;
        }

        return valueMap;
    }

    public boolean hasPrefix(String prefix) {
        Iterator<Object> keyIterator = properties.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next().toString();

            if (key.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public String getWorkingDirectory() {
        return this.workDirectory.getAbsolutePath();
    }

}
