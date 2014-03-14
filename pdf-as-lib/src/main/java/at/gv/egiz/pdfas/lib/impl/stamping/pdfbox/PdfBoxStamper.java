package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox;

import java.io.IOException;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFStamper;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFVisualObject;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;
import at.knowcenter.wag.egov.egiz.pdf.PositioningInstruction;
import at.knowcenter.wag.egov.egiz.table.Table;

public class PdfBoxStamper implements IPDFStamper {

//	private static final Logger logger = LoggerFactory.getLogger(PdfBoxStamper.class);

//	private PDFTemplateBuilder pdfBuilder;

	public PdfBoxStamper() {
//		this.pdfBuilder = new PDVisibleSigBuilder();
	}
	
	public IPDFVisualObject createVisualPDFObject(PDFObject pdf, Table table) throws IOException {
		return new PdfBoxVisualObject(table, pdf.getStatus().getSettings());
	}

	public byte[] writeVisualObject(IPDFVisualObject visualObject,
			PositioningInstruction positioningInstruction, byte[] pdfData,
			String placeholderName) throws PdfAsException {
		return null;
	}

	public void setSettings(ISettings settings) {
		// TODO Auto-generated method stub
		
	}

}
