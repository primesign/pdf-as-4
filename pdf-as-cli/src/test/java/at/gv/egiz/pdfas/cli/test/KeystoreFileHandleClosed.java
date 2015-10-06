package at.gv.egiz.pdfas.cli.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.sigs.pades.PAdESSignerKeystore;

public class KeystoreFileHandleClosed {

	// -ksf /home/afitzek/devel/pdfas_neu/test.p12 -kst PKCS12 -ksa ecc_test
	// -kskp 123456 -kssp 123456
	private static final String origFile = "/home/afitzek/devel/pdfas_neu/test.p12";
	private static final String keyAlias = "ecc_test";
	private static final String keyStorePassword = "123456";
	private static final String keyPassword = "123456";
	private static final String keyStoreType = "PKCS12";

	@Test
	@Ignore
	public void test() throws IOException, PdfAsException, PDFASError {
		
		File origFileFile = new File(origFile);
		File tmpKeyStoreFile = new File("/tmp/test.ks");
		FileUtils.copyFile(origFileFile, tmpKeyStoreFile);
		try {
			PAdESSignerKeystore pAdESSignerKeystore = new PAdESSignerKeystore(
					tmpKeyStoreFile.getCanonicalPath(), keyAlias,
					keyStorePassword, keyPassword, keyStoreType);
		} finally {
			if (tmpKeyStoreFile != null && tmpKeyStoreFile.exists()) {
				if (!FileUtils.deleteQuietly(tmpKeyStoreFile)) {
					fail("Unable to remove temporary keystore file '"
							+ tmpKeyStoreFile.getAbsolutePath() + "'.");
				}
			}
		}
	}

}
