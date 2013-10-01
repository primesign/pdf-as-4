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

    private final String tmpFilePrefix = "/tmp/";
    private static final String tmpFileSuffix = ".tmppdf";

    private String tmpDir = "tmp";

    private MessageDigest messageDigest = null;

    private List<String> tmpFiles = new ArrayList<String>();
    
    public TempFileHelper(ISettings settings) {
    	initializeMD();
    	
    	String myTmpDir = settings.getValue(TMP_DIR);
    	if(myTmpDir != null) {
    		File myTmpDirFile = new File(myTmpDir);
    		if(!myTmpDirFile.isAbsolute()) {
    			// relatives tmp dir
    			myTmpDirFile = new File(settings.getWorkingDirectory() + File.separator + myTmpDir);
    		} 
    		tmpDir = myTmpDirFile.getAbsolutePath();
    	} else {
    		tmpDir = settings.getWorkingDirectory() + File.separator + TMP_DIR_DEFAULT_VALUE;
    	}
    	
    	logger.info("TempDirHelper for TempDirectory: " + tmpDir);
    	
        createTmpDir();
    }
    
    @Override
    protected void finalize() throws Throwable {
    	this.deleteTmpDir();
    	super.finalize();
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
        String tmpFilename = tmpFilePrefix + getHashedHexString(uuidString) + tmpFileSuffix;
        logger.info("Temporary filename " + tmpFilename);
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
