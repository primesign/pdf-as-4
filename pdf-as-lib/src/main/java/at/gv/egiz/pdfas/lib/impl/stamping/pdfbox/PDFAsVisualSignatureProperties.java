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
	
	private PDFAsVisualSignatureDesigner designer;

	public PDFAsVisualSignatureProperties(ISettings settings, PDFObject object) {
		this.settings = settings;
		try {
			SignatureProfileSettings profileSettings = TableFactory
					.createProfile("SIGNATURBLOCK_DE", settings);

			X509Certificate cert = new X509Certificate(new FileInputStream(
					"/home/andy/certificates/test.crt"));

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

			designer = new PDFAsVisualSignatureDesigner(origDoc, 1);
			
			//this.setPdVisibleSignature(designer);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void buildSignature() throws IOException {
		PDFAsVisualSignatureBuilder builder = new PDFAsVisualSignatureBuilder(this);
		PDFAsTemplateCreator creator = new PDFAsTemplateCreator(builder);
		setVisibleSignature(creator.buildPDF(designer));
	}

	public Table getMainTable() {
		return main;
	}

	
}
