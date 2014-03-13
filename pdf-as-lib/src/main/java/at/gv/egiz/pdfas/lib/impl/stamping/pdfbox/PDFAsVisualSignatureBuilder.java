package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectForm;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDFTemplateStructure;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSigBuilder;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSignDesigner;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
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
	public void createProcSetArray() {
		COSArray procSetArr = new COSArray();
		procSetArr.add(COSName.getPDFName("PDF"));
		procSetArr.add(COSName.getPDFName("Text"));
		getStructure().setProcSet(procSetArr);
		logger.info("ProcSet array has been created");
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
	public void injectProcSetArray(PDXObjectForm innerForm, PDPage page,
			PDResources innerFormResources, PDResources imageFormResources,
			PDResources holderFormResources, COSArray procSet) {
		innerForm.getResources().getCOSDictionary()
				.setItem(COSName.PROC_SET, procSet); //
		page.getCOSDictionary().setItem(COSName.PROC_SET, procSet);
		innerFormResources.getCOSDictionary()
				.setItem(COSName.PROC_SET, procSet);
		/*imageFormResources.getCOSDictionary()
				.setItem(COSName.PROC_SET, procSet);*/
		holderFormResources.getCOSDictionary().setItem(COSName.PROC_SET,
				procSet);
		logger.info("inserted ProcSet to PDF");
	}

	public void injectAppearanceStreams(PDStream holderFormStream,
			PDStream innterFormStream, PDStream imageFormStream,
			String imageObjectName, String imageName, String innerFormName,
			PDFAsVisualSignatureDesigner properties) throws IOException {

		// 100 means that document width is 100% via the rectangle. if rectangle
		// is 500px, images 100% is 500px.
		// String imgFormComment = "q "+imageWidthSize+ " 0 0 50 0 0 cm /" +
		// imageName + " Do Q\n" + builder.toString();
		/*String imgFormComment = "q " + 100 + " 0 0 50 0 0 cm /" + imageName
				+ " Do Q\n";*/
		String holderFormComment = "q 1 0 0 1 0 0 cm /" + innerFormName
				+ " Do Q \n";
		// String innerFormComment = "q 1 0 0 1 0 0 cm /" + imageObjectName +
		// " Do Q\n";
		String innerFormComment = getStructure().getInnterFormStream()
				.getInputStreamAsString();

		logger.info("Inner Form Stream: " + innerFormComment);

		// appendRawCommands(getStructure().getInnterFormStream().createOutputStream(),
		// getStructure().getInnterFormStream().getInputStreamAsString());

		appendRawCommands(getStructure().getHolderFormStream()
				.createOutputStream(), holderFormComment);
		appendRawCommands(getStructure().getInnterFormStream()
				.createOutputStream(), innerFormComment);
		// appendRawCommands(getStructure().getImageFormStream().createOutputStream(),
		// imgFormComment);
		logger.info("Injected apereance stream to pdf");

	}
	
	public void createPage(PDFAsVisualSignatureDesigner properties)
    {
        PDPage page = new PDPage();
        page.setMediaBox(new PDRectangle(properties.getPageWidth(), properties.getPageHeight()));
        getStructure().setPage(page);
        logger.info("PDF page has been created");
    }

    public void createAcroForm(PDDocument template)
    {
        PDAcroForm theAcroForm = new PDAcroForm(template);
        template.getDocumentCatalog().setAcroForm(theAcroForm);
        getStructure().setAcroForm(theAcroForm);
        logger.info("Acro form page has been created");

    }

    public void createSignatureField(PDAcroForm acroForm) throws IOException
    {
        PDSignatureField sf = new PDSignatureField(acroForm);
        getStructure().setSignatureField(sf);
        logger.info("Signature field has been created");
    }

    public void createSignature(PDSignatureField pdSignatureField, PDPage page, String signatureName)
            throws IOException
    {
        PDSignature pdSignature = new PDSignature();
        pdSignatureField.setSignature(pdSignature);
        pdSignatureField.getWidget().setPage(page);
        page.getAnnotations().add(pdSignatureField.getWidget());
        pdSignature.setName(signatureName);
        pdSignature.setByteRange(new int[] { 0, 0, 0, 0 });
        pdSignature.setContents(new byte[4096]);
        getStructure().setPdSignature(pdSignature);
        logger.info("PDSignatur has been created");
    }

    public void createAcroFormDictionary(PDAcroForm acroForm, PDSignatureField signatureField) throws IOException
    {
        @SuppressWarnings("unchecked")
        List<PDField> acroFormFields = acroForm.getFields();
        COSDictionary acroFormDict = acroForm.getDictionary();
        acroFormDict.setDirect(true);
        acroFormDict.setInt(COSName.SIG_FLAGS, 3);
        acroFormFields.add(signatureField);
        acroFormDict.setString(COSName.DA, "/sylfaen 0 Tf 0 g");
        getStructure().setAcroFormFields(acroFormFields);
        getStructure().setAcroFormDictionary(acroFormDict);
        logger.info("AcroForm dictionary has been created");
    }

    public void createSignatureRectangle(PDSignatureField signatureField, PDFAsVisualSignatureDesigner properties)
            throws IOException
    {

        PDRectangle rect = new PDRectangle();
        rect.setUpperRightX(properties.getxAxis() + properties.getWidth());
        rect.setUpperRightY(properties.getPageHeight() - properties.getyAxis());
        rect.setLowerLeftY(properties.getPageHeight() - properties.getyAxis() - properties.getHeight());
        rect.setLowerLeftX(properties.getxAxis());
        signatureField.getWidget().setRectangle(rect);
        getStructure().setSignatureRectangle(rect);
        logger.info("rectangle of signature has been created");
    }

    public void createAffineTransform(byte[] params)
    {
        AffineTransform transform = new AffineTransform(params[0], params[1], params[2], params[3], params[4],
                params[5]);
        getStructure().setAffineTransform(transform);
        logger.info("Matrix has been added");
    }

    public void createSignatureImage(PDDocument template, InputStream inputStream) throws IOException
    {
        PDJpeg img = new PDJpeg(template, inputStream);
        getStructure().setJpedImage(img);
        logger.info("Visible Signature Image has been created");
        // pdfStructure.setTemplate(template);
        inputStream.close();

    }

    public void createFormaterRectangle(byte[] params)
    {

        PDRectangle formrect = new PDRectangle();
        formrect.setUpperRightX(params[0]);
        formrect.setUpperRightY(params[1]);
        formrect.setLowerLeftX(params[2]);
        formrect.setLowerLeftY(params[3]);

        getStructure().setFormaterRectangle(formrect);
        logger.info("Formater rectangle has been created");

    }

    public void createHolderFormStream(PDDocument template)
    {
        PDStream holderForm = new PDStream(template);
        getStructure().setHolderFormStream(holderForm);
        logger.info("Holder form Stream has been created");
    }

    public void createHolderFormResources()
    {
        PDResources holderFormResources = new PDResources();
        getStructure().setHolderFormResources(holderFormResources);
        logger.info("Holder form resources have been created");

    }

    public void createHolderForm(PDResources holderFormResources, PDStream holderFormStream, PDRectangle formrect)
    {

        PDXObjectForm holderForm = new PDXObjectForm(holderFormStream);
        holderForm.setResources(holderFormResources);
        holderForm.setBBox(formrect);
        holderForm.setFormType(1);
        getStructure().setHolderForm(holderForm);
        logger.info("Holder form has been created");

    }

    public void createAppearanceDictionary(PDXObjectForm holderForml, PDSignatureField signatureField)
            throws IOException
    {

        PDAppearanceDictionary appearance = new PDAppearanceDictionary();
        appearance.getCOSObject().setDirect(true);

        PDAppearanceStream appearanceStream = new PDAppearanceStream(holderForml.getCOSStream());

        appearance.setNormalAppearance(appearanceStream);
        signatureField.getWidget().setAppearance(appearance);

        getStructure().setAppearanceDictionary(appearance);
        logger.info("PDF appereance Dictionary has been created");

    }

    public void createInnerFormResource()
    {
        PDResources innerFormResources = new PDResources();
        getStructure().setInnerFormResources(innerFormResources);
        logger.info("Resources of another form (inner form - it would be inside holder form) have been created");
    }

    public void createInnerForm(PDResources innerFormResources, PDStream innerFormStream, PDRectangle formrect)
    {
        PDXObjectForm innerForm = new PDXObjectForm(innerFormStream);
        innerForm.setResources(innerFormResources);
        innerForm.setBBox(formrect);
        innerForm.setFormType(1);
        getStructure().setInnerForm(innerForm);
        logger.info("Another form (inner form - it would be inside holder form) have been created");

    }

    public void insertInnerFormToHolerResources(PDXObjectForm innerForm, PDResources holderFormResources)
    {
        String name = holderFormResources.addXObject(innerForm, "FRM");
        getStructure().setInnerFormName(name);
        logger.info("Alerady inserted inner form  inside holder form");
    }

    public void createImageFormStream(PDDocument template)
    {
        PDStream imageFormStream = new PDStream(template);
        getStructure().setImageFormStream(imageFormStream);
        logger.info("Created image form Stream");

    }

    public void createImageFormResources()
    {
        PDResources imageFormResources = new PDResources();
        getStructure().setImageFormResources(imageFormResources);
        logger.info("Created image form Resources");
    }

    public void createImageForm(PDResources imageFormResources, PDResources innerFormResource,
            PDStream imageFormStream, PDRectangle formrect, AffineTransform affineTransform, PDJpeg img)
            throws IOException
    {

        /*
         * if you need text on the visible signature 
         * 
         * PDFont font = PDTrueTypeFont.loadTTF(this.pdfStructure.getTemplate(), new File("D:\\arial.ttf")); 
         * font.setFontEncoding(new WinAnsiEncoding());
         * 
         * Map<String, PDFont> fonts = new HashMap<String, PDFont>(); fonts.put("arial", font);
         */
        PDXObjectForm imageForm = new PDXObjectForm(imageFormStream);
        imageForm.setBBox(formrect);
        imageForm.setMatrix(affineTransform);
        imageForm.setResources(imageFormResources);
        imageForm.setFormType(1);
        /*
         * imageForm.getResources().addFont(font); 
         * imageForm.getResources().setFonts(fonts);
         */

        imageFormResources.getCOSObject().setDirect(true);
        String imageFormName = innerFormResource.addXObject(imageForm, "n");
        String imageName = imageFormResources.addXObject(img, "img");
        this.getStructure().setImageForm(imageForm);
        this.getStructure().setImageFormName(imageFormName);
        this.getStructure().setImageName(imageName);
        logger.info("Created image form");
    }

    public void appendRawCommands(OutputStream os, String commands) throws IOException
    {
        os.write(commands.getBytes("UTF-8"));
        os.close();
    }

    public void createVisualSignature(PDDocument template)
    {
        this.getStructure().setVisualSignature(template.getDocument());
        logger.info("Visible signature has been created");

    }

    public void createWidgetDictionary(PDSignatureField signatureField, PDResources holderFormResources)
            throws IOException
    {

        COSDictionary widgetDict = signatureField.getWidget().getDictionary();
        widgetDict.setNeedToBeUpdate(true);
        widgetDict.setItem(COSName.DR, holderFormResources.getCOSObject());

        getStructure().setWidgetDictionary(widgetDict);
        logger.info("WidgetDictionary has been crated");
    }

    public void closeTemplate(PDDocument template) throws IOException
    {
        template.close();
        this.getStructure().getTemplate().close();
    }
	
}
