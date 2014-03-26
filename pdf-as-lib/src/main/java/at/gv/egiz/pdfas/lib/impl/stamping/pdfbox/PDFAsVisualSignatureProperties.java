package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox;

import iaik.x509.X509Certificate;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDFTemplateBuilder;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDFTemplateCreator;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSigProperties;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSignDesigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.lib.impl.stamping.TableFactory;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;
import at.gv.egiz.pdfas.lib.test.mains.CertificateHolderRequest;
import at.knowcenter.wag.egov.egiz.table.Table;

public class PDFAsVisualSignatureProperties extends PDVisibleSigProperties {

	private static final Logger logger = LoggerFactory.getLogger(PDFAsVisualSignatureProperties.class);
	
	private ISettings settings;

	private PDFBoxTable main;
	private PDFont tableFont;
	
	private PDFAsVisualSignatureDesigner designer;

	public PDFAsVisualSignatureProperties(ISettings settings, PDFObject object) {
		this.settings = settings;
		try {
			SignatureProfileSettings profileSettings = TableFactory
					.createProfile(object.getStatus().getRequestedSignature().getSignatureProfileID(), 
							settings);
			//float width = object.getStatus().getRequestedSignature().getSignaturePosition().getWidth();
			object.getStatus().getRequestedSignature().getCertificate();
			X509Certificate cert = object.getStatus().getRequestedSignature().getCertificate();

			CertificateHolderRequest request = new CertificateHolderRequest(
					cert);

			Table mainTable = TableFactory.createSigTable(profileSettings, "main",
					settings, request);
			
			main = new PDFBoxTable(mainTable, null, 230.f);
			
			//tableFont = PDFont.
			
			//main.setWidth(100);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		try {
			PDDocument origDoc = PDDocument.load(new ByteArrayInputStream(
					object.getStampedDocument()));

			designer = new PDFAsVisualSignatureDesigner(origDoc, 1, this);
			designer.coordinates(100, 100);
			float[] form_rect = new float[] {0,0, main.getWidth() + 2, main.getHeight() + 2};
			logger.info("AP Rect: {} {} {} {}", form_rect[0], form_rect[1], form_rect[2], form_rect[3]);
			designer.formaterRectangleParams(form_rect);
			//this.setPdVisibleSignature(designer);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void buildSignature() throws IOException {
		PDFAsVisualSignatureBuilder builder = new PDFAsVisualSignatureBuilder(this, this.settings);
		PDFAsTemplateCreator creator = new PDFAsTemplateCreator(builder);
		setVisibleSignature(creator.buildPDF(designer));
	}

	public PDFBoxTable getMainTable() {
		return main;
	}
	
	

	
}
