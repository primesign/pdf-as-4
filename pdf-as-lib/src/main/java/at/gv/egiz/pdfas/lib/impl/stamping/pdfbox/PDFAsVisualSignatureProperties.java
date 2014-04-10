package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;
import at.knowcenter.wag.egov.egiz.pdf.PositioningInstruction;

public class PDFAsVisualSignatureProperties extends PDVisibleSigProperties {

	private static final Logger logger = LoggerFactory.getLogger(PDFAsVisualSignatureProperties.class);
	
	private ISettings settings;

	private PDFBoxTable main;
	
	private PDFAsVisualSignatureDesigner designer;

	public PDFAsVisualSignatureProperties(ISettings settings, PDFObject object, 
			PdfBoxVisualObject visObj, PositioningInstruction pos) {
		this.settings = settings;
		try {
			main = visObj.getTable();
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		try {
			PDDocument origDoc = object.getDocument();

			designer = new PDFAsVisualSignatureDesigner(origDoc, pos.getPage(), this, pos.isMakeNewPage());
			float posy = designer.getPageHeight() - pos.getY();
			designer.coordinates(pos.getX(), posy);
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
