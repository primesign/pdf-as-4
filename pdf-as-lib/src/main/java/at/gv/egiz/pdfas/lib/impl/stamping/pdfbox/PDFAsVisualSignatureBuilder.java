package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSigBuilder;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSignDesigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.lib.test.mains.TestPDFBoxTable;
import at.knowcenter.wag.egov.egiz.table.Entry;
import at.knowcenter.wag.egov.egiz.table.Table;

public class PDFAsVisualSignatureBuilder extends PDVisibleSigBuilder {

	private static final Logger logger = LoggerFactory
			.getLogger(TestPDFBoxTable.class);

	private static void drawTable(PDPage page,
			PDPageContentStream contentStream, float x, float y,
			Table abstractTable) throws IOException {

		final int rows = abstractTable.getRows().size();
		final int cols = abstractTable.getMaxCols();
		float[] colsSizes = abstractTable.getColsRelativeWith();
		int max_cols = abstractTable.getMaxCols();
		if (colsSizes == null) {
			colsSizes = new float[max_cols];
			// set the column ratio for all columns to 1
			for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++) {
				colsSizes[cols_idx] = 1;
			}
		}

		logger.info("TOTAL Col: " + abstractTable.getWidth());

		float total = 0;

		for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++) {
			total += colsSizes[cols_idx];
		}

		for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++) {
			colsSizes[cols_idx] = (colsSizes[cols_idx] / total)
					* abstractTable.getWidth();
		}

		for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++) {
			logger.info("Col: " + cols_idx + " : " + colsSizes[cols_idx]);
		}

		final float cellMargin = 5f;
		final float rowHeight = 12f + 2 * cellMargin;
		final float tableWidth = abstractTable.getWidth();
		final float tableHeight = rowHeight * rows;
		final float colWidth = tableWidth / (float) cols;

		// draw the rows
		float nexty = y;
		for (int i = 0; i <= rows; i++) {
			contentStream.drawLine(x, nexty, x + tableWidth, nexty);
			nexty -= rowHeight;
		}

		// draw the columns
		float nextx = x;
		for (int i = 0; i <= cols; i++) {
			contentStream.drawLine(nextx, y, nextx, y - tableHeight);
			if (i < colsSizes.length) {
				nextx += (colsSizes != null) ? colsSizes[i] : colWidth;
			}
		}

		float textx = x + cellMargin;
		float texty = y - 15;
		for (int i = 0; i < abstractTable.getRows().size(); i++) {
			ArrayList row = (ArrayList) abstractTable.getRows().get(i);
			for (int j = 0; j < row.size(); j++) {
				Entry cell = (Entry) row.get(j);
				String text = cell.toString();
				text = "Hallo";
				COSName name = COSName.getPDFName("ANDI_TAG!");
				contentStream.beginMarkedContentSequence(COSName.ALT, name);
				contentStream.beginText();
				logger.info("Writing: " + textx + " : " + texty + " = " + text);
				contentStream.moveTextPositionByAmount(textx, texty);

				if (text.contains("\n")) {
					String[] lines = text.split("\n");
					contentStream.appendRawCommands(10 + " TL\n");
					for (int k = 0; k < lines.length; k++) {
						contentStream.drawString(lines[k]);
						if (k < lines.length - 1) {
							contentStream.appendRawCommands("T*\n");
						}
					}
				} else {
					contentStream.drawString(text);
				}
				contentStream.endText();
				contentStream.endMarkedContentSequence();
				textx += (colsSizes != null) ? colsSizes[j] : colWidth;
			}
			texty -= rowHeight;
			textx = x + cellMargin;
		}
	}

	private PDFAsVisualSignatureProperties properties;

	public PDFAsVisualSignatureBuilder(PDFAsVisualSignatureProperties properties) {
		this.properties = properties;
	}

	@Override
	public void createTemplate(PDPage page) throws IOException {
		PDDocument template = new PDDocument();

		template.addPage(page);
		getStructure().setTemplate(template);
	}

	@Override
	public void createInnerFormStream(PDDocument template) {
		try {
			PDPageContentStream stream = new PDPageContentStream(template,
					getStructure().getPage());
			stream.setFont(PDType1Font.HELVETICA_BOLD, 12);
			drawTable(getStructure().getPage(), stream, 0, 0,
					properties.getMainTable());
			stream.close();
			PDStream innterFormStream = getStructure().getPage().getContents();
			getStructure().setInnterFormStream(innterFormStream);
			logger.info("Strean of another form (inner form - it would be inside holder form) has been created");

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	   @Override
	    public void injectAppearanceStreams(PDStream holderFormStream, PDStream innterFormStream, PDStream imageFormStream,
	            String imageObjectName, String imageName, String innerFormName, PDVisibleSignDesigner properties)
	            throws IOException
	    {

	        // 100 means that document width is 100% via the rectangle. if rectangle
	        // is 500px, images 100% is 500px.
	        // String imgFormComment = "q "+imageWidthSize+ " 0 0 50 0 0 cm /" +
	        // imageName + " Do Q\n" + builder.toString();
	        String imgFormComment = "q " + 100 + " 0 0 50 0 0 cm /" + imageName + " Do Q\n";
	        String holderFormComment = "q 1 0 0 1 0 0 cm /" + innerFormName + " Do Q \n";
	        String innerFormComment = "q 1 0 0 1 0 0 cm /" + imageObjectName + " Do Q\n";

	        logger.info("Holder Stream: " + getStructure().getInnterFormStream().getInputStreamAsString());
	        
	        //appendRawCommands(getStructure().getInnterFormStream().createOutputStream(), 
	        //		getStructure().getInnterFormStream().getInputStreamAsString());
	        
	        appendRawCommands(getStructure().getHolderFormStream().createOutputStream(), holderFormComment);
	        appendRawCommands(getStructure().getInnterFormStream().createOutputStream(), getStructure().getInnterFormStream().getInputStreamAsString());
	        appendRawCommands(getStructure().getImageFormStream().createOutputStream(), imgFormComment);
	        logger.info("Injected apereance stream to pdf");

	    }
}
