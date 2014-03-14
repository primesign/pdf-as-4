package at.gv.egiz.pdfas.lib.test.mains;
import iaik.x509.X509Certificate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.impl.stamping.TableFactory;
import at.knowcenter.wag.egov.egiz.table.Entry;
import at.knowcenter.wag.egov.egiz.table.Style;
import at.knowcenter.wag.egov.egiz.table.Table;

public class TestPDFBoxTable {

	private static final Logger logger = LoggerFactory
			.getLogger(TestPDFBoxTable.class);
	
	private static void drawTable(PDPage page, PDPageContentStream contentStream, 
			float x, float y, Table abstractTable) throws IOException {

        final int rows = abstractTable.getRows().size();
        final int cols = abstractTable.getMaxCols();
        float[] colsSizes = abstractTable.getColsRelativeWith();
        int max_cols = abstractTable.getMaxCols();
        if (colsSizes == null)
        {
        	colsSizes = new float[max_cols];
            // set the column ratio for all columns to 1
            for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++)
            {
            	colsSizes[cols_idx] = 1;
            }
        }
        
        logger.info("TOTAL Col: " + abstractTable.getWidth());
        
        float total = 0;
        
        for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++)
        {
        	total += colsSizes[cols_idx];
        }
        
        for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++)
        {
        	colsSizes[cols_idx] = (colsSizes[cols_idx]/total) * abstractTable.getWidth();
        }
        
        for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++)
        {
        	logger.info("Col: " + cols_idx + " : " + colsSizes[cols_idx]);
        }
        
        final float cellMargin=5f;
        final float rowHeight = 12f + 2 * cellMargin;
        final float tableWidth = abstractTable.getWidth();
        final float tableHeight = rowHeight * rows;
        final float colWidth = tableWidth/(float)cols;

        //draw the rows
        float nexty = y ;
        for (int i = 0; i <= rows; i++) {
            contentStream.drawLine(x, nexty, x+tableWidth, nexty);
            nexty-= rowHeight;
        }

        //draw the columns
        float nextx = x;
        for (int i = 0; i <= cols; i++) {
            contentStream.drawLine(nextx, y, nextx, y-tableHeight);
            if(i < colsSizes.length) {
            	nextx += (colsSizes != null) ? colsSizes[i] : colWidth;
            }
        }
        
        float textx = x+cellMargin;
        float texty = y-15;
        for(int i = 0; i < abstractTable.getRows().size(); i++){
        	ArrayList row = (ArrayList) abstractTable.getRows().get(i);
            for(int j = 0 ; j < row.size(); j++) {
            	Entry cell = (Entry) row.get(j);
                String text = cell.toString();
                text = "Hallo";
                COSName name = COSName.getPDFName("ANDI_TAG!");
                contentStream.beginMarkedContentSequence(COSName.ALT, name);
                contentStream.beginText();
                logger.info("Writing: " + textx + " : " + texty + " = " + text);
                contentStream.moveTextPositionByAmount(textx,texty);
                
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
            texty-= rowHeight;
            textx = x+cellMargin;
        }
    }
	
	
	private static void renderTable(Table abstractTable) {
		
		ArrayList rows = abstractTable.getRows();
		Style table_style = abstractTable.getStyle();
        for (int row_idx = 0; row_idx < rows.size(); row_idx++)
        {
            @SuppressWarnings("rawtypes")
			ArrayList row = (ArrayList) rows.get(row_idx);
            logger.info("## Row:" + row_idx + " ## of table:" + abstractTable.getName());
            for (int entry_idx = 0; entry_idx < row.size(); entry_idx++)
            {
                Entry cell = (Entry) row.get(entry_idx);
                // 03.11.2010 changed by exthex - swapped the two params, was probably a bug
                Style inherit_style = Style.doInherit(table_style, cell.getStyle());
                cell.setStyle(inherit_style);
                logger.info(cell.toString());
                /*PdfPCell pdf_cell = renderCell(cell);
                if (cell.getColSpan() > 1)
                {
                    pdf_cell.setColspan(cell.getColSpan());
                }
                if (cell.isNoWrap())
                {
                    pdf_cell.setNoWrap(true);
                }*/
                // System.err.println("valign:" + pdf_cell.getVerticalAlignment() + "
                // halign:" +
                // pdf_cell.getHorizontalAlignment());
                //pdf_table.addCell(pdf_cell);
            }
            //pdf_table.completeRow();
        }
        logger.info("render table:" + abstractTable.getName());
	}
	
	public static void main(String[] args) {
		try {
			PdfAs pdfAs = PdfAsFactory.createPdfAs(new File("/home/afitzek/.pdfas/"));
			ISettings settings  =  (ISettings) pdfAs.getConfiguration();
			SignatureProfileSettings profileSettings = 
					TableFactory.createProfile("SIGNATURBLOCK_DE", settings);
			
			X509Certificate cert = new X509Certificate(new FileInputStream("/home/afitzek/qualified.cer"));
			
			CertificateHolderRequest request = new CertificateHolderRequest(cert);
			
			Table main = TableFactory.createSigTable(profileSettings, "main", settings, request);
		
			main.setWidth(400);
			
			renderTable(main);
			
//			PDStream stream1;
			
			PDDocument document = new PDDocument();
			PDPage page = new PDPage();
			page.setMediaBox(new PDRectangle());
			PDPageContentStream stream = new PDPageContentStream(document, page);
			stream.setFont(PDType1Font.HELVETICA_BOLD , 12);
			drawTable(page, stream, 100, 300, main);
			stream.close();
			
			document.addPage(page);
			
			document.save("/tmp/test.pdf");
			
			/*
			FileOutputStream fos = new FileOutputStream("/tmp/buffer.bin");
			fos.write(page.getContents().getByteArray());
			fos.close();
			*/

		} catch(Throwable e) {
			e.printStackTrace();
		}
	}
}
