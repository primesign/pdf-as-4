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
package at.gv.egiz.pdfas.common.utils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.settings.IProfileConstants;
import at.gv.egiz.pdfas.common.settings.ISettings;


public class TempFileHelper implements IProfileConstants {

    private static final Logger logger = LoggerFactory.getLogger(TempFileHelper.class);

    //private final String tmpFilePrefix = "/tmp/";
    private static final String tmpFileSuffix = ".tmppdf";

    private String tmpDir = "tmp";

    private MessageDigest messageDigest = null;

    private List<String> tmpFiles = new ArrayList<String>();
    
    private boolean needsDeletion = false;
    
    public TempFileHelper(ISettings settings) {
    	initializeMD();
    	
    	String myTmpDir = settings.getValue(TMP_DIR);
    	if(myTmpDir != null) {
    		File myTmpDirFile = new File(myTmpDir);
    		if(!myTmpDirFile.isAbsolute()) {
    			// relatives tmp dir
    			myTmpDirFile = new File(settings.getWorkingDirectory() + File.separator + myTmpDir);
    		} 
    		tmpDir = myTmpDirFile.getAbsolutePath() + File.separator;
    	} else {
    		String uuidString = UUID.randomUUID().toString();
            logger.debug("Generated UUID "  + uuidString);
    		tmpDir = settings.getWorkingDirectory() + File.separator + TMP_DIR_DEFAULT_VALUE + File.separator + getHashedHexString(uuidString) + File.separator;
    		needsDeletion = true;
    	}
    	
    	logger.debug("TempDirHelper for TempDirectory: " + tmpDir);
    	
        createTmpDir();
    }
    
    @Override
    protected void finalize() throws Throwable {
    	this.deleteTmpDir();
    	super.finalize();
    }

    public void clear() {
    	this.deleteTmpDir();
    }
    
    private void deleteTmpDir() {
        try {
            File tmpdir = new File(tmpDir);
            tmpdir.delete();
        } catch (Throwable e) {
            logger.error("Failed to delete temporary directory: " + tmpDir, e);
        }
    }
    
    private void createTmpDir() {
        try {
            File tmpdir = new File(tmpDir);
            tmpdir.mkdirs();
            if(needsDeletion) {
            	tmpdir.deleteOnExit();
            }
        } catch (Throwable e) {
            logger.error("Failed to create temporary directory: " + tmpDir, e);
        }
    }

    private void initializeMD() {
        try {
            messageDigest = MessageDigest.getInstance("SHA1");
            return;
        } catch (NoSuchAlgorithmException e) {
            logger.warn("SHA1 not available", e);
        }
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            return;
        } catch (NoSuchAlgorithmException e) {
            logger.warn("MD5 not available", e);
        }
        throw new RuntimeException("Need at least SHA1 or MD5 Message Digest, none available!");
    }


    public void setTemporaryDirectory(String directory) {
        tmpDir = directory;
        createTmpDir();
    }

    public String getHashedHexString(String str) {
        byte[] digest = messageDigest.digest(str.getBytes());
        return StringUtils.bytesToHexString(digest);
    }

    public String getStaticFilename() {
        String uuidString = UUID.randomUUID().toString();
        logger.debug("Generated UUID "  + uuidString);
        String tmpFilename = tmpDir + getHashedHexString(uuidString) + tmpFileSuffix;
        logger.debug("Temporary filename " + tmpFilename);
        tmpFiles.add(tmpFilename);
        return tmpFilename;
    }

    public void deleteFile(String filename) {
        try {
            File tmpFile = new File(filename);
            if(tmpFile.exists()) {
                tmpFile.delete();
                tmpFiles.remove(filename);
            }
        } catch (Throwable e) {
            logger.error("Failed to delete temporary file: " + filename, e);
        }
    }
}
