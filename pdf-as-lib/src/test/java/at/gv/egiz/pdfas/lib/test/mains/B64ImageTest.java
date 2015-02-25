package at.gv.egiz.pdfas.lib.test.mains;

import java.io.File;
import java.io.FileInputStream;

import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSource;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;

public class B64ImageTest {

	public static final String keyStoreFile = "/home/afitzek/devel/pdfas_neu/test.p12";
	public static final String keyStoreType = "PKCS12";
    public static final String keyStorePass = "123456";
    //public static final String keyAlias = "pdf";
    public static final String keyAlias = "ecc_test";
    public static final String keyPass = "123456";
	
	public static void main(String[] args) {
		String user_home = System.getProperty("user.home");
		String pdfas_dir = user_home + File.separator + ".pdfas";
		PdfAs pdfas = PdfAsFactory.createPdfAs(new File(pdfas_dir));
		try {
			Configuration config = pdfas.getConfiguration();
			ISettings settings = (ISettings) config;
			

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {

		}
	}

}
