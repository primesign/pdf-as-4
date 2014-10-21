package at.gv.egiz.pdfas.lib.test.mains;

import iaik.x509.X509Certificate;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;

public class PDFToImage {

	private static final String PDF_FILE = "/home/afitzek/simple.pdf";
	private static final String IMG_FILE = "/home/afitzek/simple.png";

	public static final String targetFolder = "/home/afitzek/tmp/sigres/";

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
					if (profile.contains("PDFA")) {
						signaturePDFAProfiles.add(profile);
					}
				}
			}

			Iterator<String> itProfiles = signatureProfiles.iterator();
			while (itProfiles.hasNext()) {
				String profile = itProfiles.next();
				System.out.println("Testing " + profile);

				X509Certificate cert = new X509Certificate(new FileInputStream(
						"/home/afitzek/qualified.cer"));

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				SignParameter parameter = PdfAsFactory.createSignParameter(
						config, null, baos);
				parameter.setSignatureProfileId(profile);
				Image img = pdfas.generateVisibleSignaturePreview(parameter,
						cert, 128);
				if (img != null) {
					ImageIO.write((RenderedImage) img, "png", new File(
							targetFolder + profile + ".png"));
				}
			}

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {

		}
	}

}
