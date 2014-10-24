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
		
		SignParameter signParameter = PdfAsFactory.createSignParameter(cfg, null, null);
		
		X509Certificate crt = new X509Certificate(new FileInputStream("/home/afitzek/qualified.cer"));
		
		Image img = pdfAs.generateVisibleSignaturePreview(signParameter, crt, 256);
		ImageIO.write((RenderedImage) img, "png", new File("/tmp/block.png"));
	}
}
