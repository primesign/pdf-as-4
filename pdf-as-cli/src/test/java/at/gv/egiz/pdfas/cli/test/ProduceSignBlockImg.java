package at.gv.egiz.pdfas.cli.test;

import iaik.x509.X509Certificate;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;

import javax.imageio.ImageIO;

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;

public class ProduceSignBlockImg {
	public static void main(String[] args) throws IOException, PDFASError, CertificateException {
		PdfAs pdfAs = PdfAsFactory.createPdfAs(new File("/home/afitzek/.pdfas"));
		
		Configuration cfg = pdfAs.getConfiguration();
		cfg.setValue("sig_obj.SIGNATURBLOCK_SMALL_DE.value.SIG_LABEL", "iVBORw0KGgoAAAANSUhEUgAAAMgAAADIAQAAAACFI5MzAAAA30lEQVR42u2XQRKDMAhFcZVj5KamuWmO4SqUfLQz " +
"tema70xYYZ4Lhp+PKPovZJFFFnkGOcRiU61Hfo00M5GRViT1euQhL8kVx/WQjZB41aSk7cJIoHaTpPN7EEngktHOuX8iiYdbZDZ3IslQeBQ7pHYrk5Fi7UyQ+kttBmLHuIn2yq3qYJK75J7MwbKDExHVbmLjzMczFfm4BB8NJnJNl9Ha1LafDSWUYCqjaruPhYxAZ" +
"Jj4vj1xEJS8J+1J6cg5YFqZ7FWRxNU2HxfsKUzkdIkP5tmGEkfW/9wiizyUvAFFQH2e7NyBBgAAAABJRU5ErkJggg==");
		SignParameter signParameter = PdfAsFactory.createSignParameter(cfg, null, null);
		
		X509Certificate crt = new X509Certificate(new FileInputStream("/home/afitzek/qualified.cer"));
		
		Image img = pdfAs.generateVisibleSignaturePreview(signParameter, crt, 256);
		ImageIO.write((RenderedImage) img, "png", new File("/tmp/block.png"));
	}
}
