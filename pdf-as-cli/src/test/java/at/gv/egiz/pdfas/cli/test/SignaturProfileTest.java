package at.gv.egiz.pdfas.cli.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sun.misc.IOUtils;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSink;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSource;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.DataSource;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.sign.SignResult;
import at.gv.egiz.pdfas.sigs.pades.PAdESSignerKeystore;

public class SignaturProfileTest {

	public static final String sourcePDFA = "/home/afitzek/tmp/pdfProblem/PDFA/TestGhostscriptPdfA.pdf";
	public static final String sourcePDF = "/home/afitzek/simple.pdf";
	public static final String targetFolder = "/home/afitzek/tmp/sigres/";

	public static final String KS_FILE = "/home/afitzek/devel/pdfas_neu/test.p12";
	public static final String KS_ALIAS = "ecc_test";
	public static final String KS_TYPE = "PKCS12";
	public static final String KS_PASS = "123456";
	public static final String KS_KEY_PASS = "123456";

	/*
	 * -p SIGNATURBLOCK_SMALL_DE -c ks -m sign -ksf
	 * /home/afitzek/devel/pdfas_neu/test.p12 -kst PKCS12 -ksa ecc_test -kskp
	 * 123456 -kssp 123456 /home/afitzek/simple2.pdf
	 */

	public static void main(String[] args) {
		String user_home = System.getProperty("user.home");
		String pdfas_dir = user_home + File.separator + ".pdfas";
		PdfAs pdfas = PdfAsFactory.createPdfAs(new File(pdfas_dir));
		try {
			Configuration config = pdfas.getConfiguration();
			ISettings settings = (ISettings) config;
			List<String> signatureProfiles = new ArrayList<String>();
			
			List<String> signaturePDFAProfiles = new ArrayList<String>();
			
			Iterator<String> itKeys = settings.getFirstLevelKeys(
					"sig_obj.types.").iterator();
			while (itKeys.hasNext()) {
				String key = itKeys.next();
				String profile = key.substring("sig_obj.types.".length());
				System.out.println("[" + profile + "]: "
						+ settings.getValue(key));
				if (settings.getValue(key).equals("on")) {
					signatureProfiles.add(profile);
					if(profile.contains("PDFA")) {
						signaturePDFAProfiles.add(profile);
					}
				}
			}

			byte[] input = IOUtils.readFully(new FileInputStream(sourcePDF),
					-1, true);

			IPlainSigner signer = new PAdESSignerKeystore(KS_FILE, KS_ALIAS, KS_PASS, KS_KEY_PASS, KS_TYPE);
			
			Iterator<String> itProfiles = signatureProfiles.iterator();
			while (itProfiles.hasNext()) {
				String profile = itProfiles.next();
				System.out.println("Testing " + profile);

				DataSource source = new ByteArrayDataSource(input);
				ByteArrayDataSink sink = new ByteArrayDataSink();
				
				SignParameter signParameter = PdfAsFactory.createSignParameter(
						config, source);
				
				signParameter.setPlainSigner(signer);
				signParameter.setOutput(sink);
				signParameter.setSignatureProfileId(profile);
				
				SignResult result = pdfas.sign(signParameter);
				
				FileOutputStream fos = new FileOutputStream(targetFolder + profile + ".pdf");
				fos.write(sink.getData());
				fos.close();
			}
			
			byte[] inputPDFA = IOUtils.readFully(new FileInputStream(sourcePDFA),
					-1, true);
			
			Iterator<String> itPDFAProfiles = signaturePDFAProfiles.iterator();
			while (itPDFAProfiles.hasNext()) {
				String profile = itPDFAProfiles.next();
				System.out.println("Testing " + profile);

				DataSource source = new ByteArrayDataSource(inputPDFA);
				ByteArrayDataSink sink = new ByteArrayDataSink();
				
				SignParameter signParameter = PdfAsFactory.createSignParameter(
						config, source);
				
				signParameter.setPlainSigner(signer);
				signParameter.setOutput(sink);
				signParameter.setSignatureProfileId(profile);
				
				SignResult result = pdfas.sign(signParameter);
				
				FileOutputStream fos = new FileOutputStream(targetFolder + "PDFA_" + profile + ".pdf");
				fos.write(sink.getData());
				fos.close();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
