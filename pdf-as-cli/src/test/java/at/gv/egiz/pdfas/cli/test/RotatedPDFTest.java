package at.gv.egiz.pdfas.cli.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;

import javax.activation.DataSource;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSource;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.sigs.pades.PAdESSignerKeystore;

public class RotatedPDFTest {

	private static final String[] PDF_SOURCE_FILES = new String[] {
		"/simple_rotated_0.pdf",
		"/rotate_090.pdf",
		"/rotate_180.pdf",
		"/rotate_270.pdf"
		/*,
		"/simple_rotated_90.pdf",
		"/simple_rotated_180.pdf",
		"/simple_rotated_270.pdf"*/
	};

	private static final String[] PDF_TARGET_PREFIX_FILES = new String[] {
		"rot0",
		"rot90",
		"rot180",
		"rot270"
	};
	
	private static final String[] POS_STRINGS = new String[] {
		null,
		"x:0;y:50",
		"x:0;y:50;p:new",
		"x:100;y:300",
		"x:100;y:300;r:90",
		"x:100;y:300;r:180",
		"x:100;y:300;r:270",
		"x:400;y:500",
		"x:500;y:400"
	};
	
	private static final String CFG_DIR = "../pdf-as-lib/src/configuration/";

	private static final String TARGET_DIR = "/home/afitzek/tmp/rotate_test/";
	
	private static final String KS_NAME = "/test.p12";
	private static final String KS_TYPE = "PKCS12";
	private static final String KS_PASS = "123456";
	private static final String KS_KPASS = "123456";
	private static final String KS_ALIAS = "ecc_test";

	private static void signDokument(PdfAs pdfAs, IPlainSigner signer,
			String posString, InputStream inputDokument, OutputStream stream) throws IOException, PDFASError {
		Configuration configuration = pdfAs.getConfiguration();

		DataSource source = new ByteArrayDataSource(
				IOUtils.toByteArray(inputDokument));

		SignParameter signParameter = PdfAsFactory.createSignParameter(
				configuration, source, stream);

		signParameter.setPlainSigner(signer);
		signParameter.setSignaturePosition(posString);
		signParameter.setSignatureProfileId("SIGNATURBLOCK_SMALL_DE");
		
		pdfAs.sign(signParameter);
	}

	public static void main(String[] args) {
		try {
			PdfAs pdfAs = PdfAsFactory.createPdfAs(new File(CFG_DIR));

			KeyStore store = KeyStore.getInstance(KS_TYPE);
			store.load(RotatedPDFTest.class.getResourceAsStream(KS_NAME),
					KS_PASS.toCharArray());

			IPlainSigner ksSigner = new PAdESSignerKeystore(store, KS_ALIAS,
					KS_KPASS);

			if(PDF_SOURCE_FILES.length != PDF_TARGET_PREFIX_FILES.length) {
				throw new Exception("INPUT ARRAY SIZE MISSMATCH!");
			}
			
			for(int fileIdx = 0; fileIdx < PDF_SOURCE_FILES.length; fileIdx++) {
				for(int posIdx = 0; posIdx < POS_STRINGS.length; posIdx++) {
					String inputFile = PDF_SOURCE_FILES[fileIdx];
					String outputFile = TARGET_DIR + PDF_TARGET_PREFIX_FILES[fileIdx]+"_" + POS_STRINGS[posIdx] + ".pdf";
					String posString = POS_STRINGS[posIdx];
					System.out.println("From " + inputFile + " => " + outputFile + "(" + posString + ")");
					FileOutputStream fos = new FileOutputStream(outputFile);
					signDokument(pdfAs, ksSigner, posString, RotatedPDFTest.class.getResourceAsStream(inputFile), fos);
					fos.close();
				}	
			}
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
