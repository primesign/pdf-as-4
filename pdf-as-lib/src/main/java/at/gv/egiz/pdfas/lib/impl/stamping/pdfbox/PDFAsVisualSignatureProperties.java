package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox;

import iaik.x509.X509Certificate;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDFTemplateBuilder;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDFTemplateCreator;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSigProperties;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSignDesigner;

import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.lib.impl.stamping.TableFactory;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;
import at.gv.egiz.pdfas.lib.test.mains.CertificateHolderRequest;
import at.knowcenter.wag.egov.egiz.table.Table;

public class PDFAsVisualSignatureProperties extends PDVisibleSigProperties {

	private ISettings settings;

	private Table main;

	public PDFAsVisualSignatureProperties(ISettings settings, PDFObject object) {
		this.settings = settings;
		try {
			SignatureProfileSettings profileSettings = TableFactory
					.createProfile("SIGNATURBLOCK_DE", settings);

			X509Certificate cert = new X509Certificate(new FileInputStream(
					"/home/afitzek/qualified.cer"));

			CertificateHolderRequest request = new CertificateHolderRequest(
					cert);

			main = TableFactory.createSigTable(profileSettings, "main",
					settings, request);

			main.setWidth(400);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		try {
			PDDocument origDoc = PDDocument.load(new ByteArrayInputStream(
					object.getStampedDocument()));
			PDVisibleSignDesigner designer = new PDVisibleSignDesigner(origDoc,
					new FileInputStream("/home/afitzek/.pdfas/images/signatur-logo_de.png"), 1);

			this.setPdVisibleSignature(designer);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void buildSignature() throws IOException {
		PDFTemplateBuilder builder = new PDFAsVisualSignatureBuilder(this);
		PDFTemplateCreator creator = new PDFTemplateCreator(builder);
		setVisibleSignature(creator.buildPDF(getPdVisibleSignature()));
	}

	public Table getMainTable() {
		return main;
	}

}
