package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectForm;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSigBuilder;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.knowcenter.wag.egov.egiz.table.Entry;
import at.knowcenter.wag.egov.egiz.table.Style;

public class PDFAsVisualSignatureBuilder extends PDVisibleSigBuilder {

	private static final Logger logger = LoggerFactory
			.getLogger(PDFAsVisualSignatureBuilder.class);

	private void drawTable(PDPage page, PDPageContentStream contentStream,
			float x, float y, PDFBoxTable abstractTable, PDDocument doc,
			boolean subtable) throws IOException, PdfAsException {

		final int rows = abstractTable.getRowCount();
		final int cols = abstractTable.getColCount();
		float[] colsSizes = abstractTable.getColsRelativeWith();
		int max_cols = abstractTable.getColCount();
		float padding = abstractTable.getPadding();
		float fontSize = PDFBoxFont.defaultFontSize;
		PDFont textFont = PDFBoxFont.defaultFont;
		if (colsSizes == null) {
			colsSizes = new float[max_cols];
			// set the column ratio for all columns to 1
			for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++) {
				colsSizes[cols_idx] = 1;
			}
		}

		logger.debug("Drawing Table:");
		abstractTable.dumpTable();

		if (abstractTable.getBGColor() != null) {
			contentStream.setNonStrokingColor(abstractTable.getBGColor());
			contentStream.fillRect(x, y, abstractTable.getWidth(),
					abstractTable.getHeight());
			contentStream.setNonStrokingColor(Color.BLACK);
		}
		float total = 0;

		for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++) {
			total += colsSizes[cols_idx];
		}

		for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++) {
			colsSizes[cols_idx] = (colsSizes[cols_idx] / total)
					* abstractTable.getWidth();
		}

		for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++) {
			logger.debug("Col: " + cols_idx + " : " + colsSizes[cols_idx]);
		}

		float border = abstractTable.style.getBorder();
		contentStream.setLineWidth(border);

		float tableHeight = abstractTable.getHeight();
		float tableWidth = abstractTable.getWidth();
		final float colWidth = tableWidth / (float) cols;

		// draw if boarder > 0
		if (border != 0) {

			// draw the rows
			float nexty = y + tableHeight;
			for (int i = 0; i <= rows; i++) {
				logger.debug("ROW LINE: {} {} {} {}", x, nexty, x + tableWidth,
						nexty);
				contentStream.drawLine(x, nexty, x + tableWidth, nexty);
				if (i < abstractTable.getRowHeights().length) {
					nexty -= abstractTable.getRowHeights()[i] + padding * 2;
				}
				if (subtable && i + 1 == abstractTable.getRowHeights().length) {
					nexty -= padding;
				}
			}

			// draw the columns
			float nextx = x;
			float ypos = y;
			float yheight = y + abstractTable.getHeight();
			if (subtable) {
				ypos -= padding;
				yheight = y + abstractTable.getHeight();
			}
			for (int i = 0; i <= cols; i++) {
				if (subtable && i == cols) {
					continue;
				}
				logger.debug("COL LINE: {} {} {} {}", nextx, ypos, nextx,
						yheight);
				contentStream.drawLine(nextx, ypos, nextx, yheight);
				if (i < colsSizes.length) {
					nextx += (colsSizes != null) ? colsSizes[i] : colWidth;
				}
			}
		}

		float textx = x + padding;
		float texty = y + tableHeight;
		for (int i = 0; i < abstractTable.getRowCount(); i++) {
			ArrayList<Entry> row = abstractTable.getRow(i);
			for (int j = 0; j < row.size(); j++) {
				Entry cell = (Entry) row.get(j);
				if (cell.getType() == Entry.TYPE_CAPTION
						|| cell.getType() == Entry.TYPE_VALUE) {

					if (cell.getType() == Entry.TYPE_CAPTION) {
						textFont = abstractTable.getFont().getFont(doc);
						fontSize = abstractTable.getFont().getFontSize();
					} else if (cell.getType() == Entry.TYPE_VALUE) {
						textFont = abstractTable.getValueFont().getFont(doc);
						fontSize = abstractTable.getValueFont().getFontSize();
					}

					String text = (String) cell.getValue();
					float ttexty = texty - padding - fontSize;
					// COSName name = COSName.getPDFName("ANDI_TAG!");
					// contentStream.beginMarkedContentSequence(COSName.ALT,
					// name);
					String fontName = textFont.equals(PDType1Font.COURIER) ? "COURIER"
							: "HELVETICA";

					contentStream.beginText();

					if (innerFormResources.getFonts().containsValue(textFont)) {
						String fontID = getFontID(textFont);
						logger.debug("Using Font: " + fontID);
						contentStream.appendRawCommands("/" + fontID + " "
								+ fontSize + " Tf\n");
					} else {
						contentStream.setFont(textFont, fontSize);
					}
					logger.debug("Writing: " + textx + " : " + ttexty + " = "
							+ text + " as " + cell.getType() + " w " + fontName);
					contentStream.moveTextPositionByAmount(textx, ttexty);

					if (text.contains("\n")) {
						String[] lines = text.split("\n");
						contentStream.appendRawCommands(fontSize + " TL\n");
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
					// contentStream.endMarkedContentSequence();
				} else if (cell.getType() == Entry.TYPE_IMAGE) {
					String img_ref = (String) cell.getValue();
					if (!images.containsKey(img_ref)) {
						logger.error("Image not prepared! : " + img_ref);
						throw new PdfAsException("Image not prepared! : "
								+ img_ref);
					}
					ImageObject image = images.get(img_ref);
					PDXObjectImage pdImage = image.getImage();
					// text = "Row :" + i + "COL: " + j;
					// COSName name = COSName.getPDFName("ANDI_TAG!");
					// contentStream.beginMarkedContentSequence(COSName.ALT,
					// name);

					float imgy = texty;
					if (cell.getStyle().getImageVAlign() != null
							&& cell.getStyle().getImageVAlign()
									.equals(Style.TOP)) {
						imgy = texty - padding - image.getSize();
					} else if (cell.getStyle().getImageVAlign() != null
							&& cell.getStyle().getImageVAlign()
									.equals(Style.BOTTOM)) {
						// Should allready be at bottom ...
						imgy = texty - abstractTable.getRowHeights()[i]
								+ padding;
					} else {
						// default to middle
						imgy = texty - padding
								- abstractTable.getRowHeights()[i] / 2;
						imgy = imgy - image.getSize() / 2;
					}
					logger.debug("Image: " + textx + " : " + imgy);
					contentStream.drawXObject(pdImage, textx, imgy,
							image.getSize(), image.getSize());
					// contentStream.endMarkedContentSequence();

				} else if (cell.getType() == Entry.TYPE_TABLE) {
					float tableY = texty - abstractTable.getRowHeights()[i]
							- padding;
					float tableX = textx;
					// texty = texty - padding;
					tableX = textx - padding;
					PDFBoxTable tbl_value = (PDFBoxTable) cell.getValue();
					logger.debug("Table: " + tableX + " : " + tableY);
					drawTable(page, contentStream, tableX, tableY, tbl_value,
							doc, true);
				}
				textx += (colsSizes != null) ? colsSizes[j] : colWidth;
			}
			// if (i + 1 < abstractTable.getRowHeights().length) {
			logger.debug("Row {} from {} - {} - {} = {}", i, texty,
					abstractTable.getRowHeights()[i], padding * 2, texty
							- (abstractTable.getRowHeights()[i] + padding * 2));
			texty -= abstractTable.getRowHeights()[i] + padding * 2;
			// texty = texty - abstractTable.getRowHeights()[i + 1] - padding
			// * 2;
			// texty = texty - abstractTable.getRowHeights()[i] - padding
			// * 2;
			// }
			textx = x + padding;
		}
	}

	private PDFAsVisualSignatureProperties properties;
	private ISettings settings;
	private PDResources innerFormResources;
	private Map<String, ImageObject> images = new HashMap<String, ImageObject>();

	private String getFontID(PDFont font) {
		Iterator<java.util.Map.Entry<String, PDFont>> it = innerFormResources
				.getFonts().entrySet().iterator();
		while (it.hasNext()) {
			java.util.Map.Entry<String, PDFont> entry = it.next();
			if (entry.getValue().equals(font)) {
				return entry.getKey();
			}
		}
		return "";
	}

	public PDFAsVisualSignatureBuilder(
			PDFAsVisualSignatureProperties properties, ISettings settings) {
		this.properties = properties;
		this.settings = settings;
	}

	@Override
	public void createProcSetArray() {
		COSArray procSetArr = new COSArray();
		procSetArr.add(COSName.getPDFName("PDF"));
		procSetArr.add(COSName.getPDFName("Text"));
		procSetArr.add(COSName.getPDFName("ImageC"));
		procSetArr.add(COSName.getPDFName("ImageB"));
		procSetArr.add(COSName.getPDFName("ImageI"));
		getStructure().setProcSet(procSetArr);
		logger.debug("ProcSet array has been created");
	}

	@Override
	public void createTemplate(PDPage page) throws IOException {
		PDDocument template = new PDDocument();

		template.addPage(page);
		getStructure().setTemplate(template);
	}

	private void readTableResources(PDFBoxTable table, PDDocument template)
			throws PdfAsException, IOException {

		float[] colsSizes = table.getColsRelativeWith();
		int max_cols = table.getColCount();
		float padding = table.getPadding();
		if (colsSizes == null) {
			colsSizes = new float[max_cols];
			// set the column ratio for all columns to 1
			for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++) {
				colsSizes[cols_idx] = 1;
			}
		}

		logger.debug("TOTAL Width: " + table.getWidth());

		float total = 0;

		for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++) {
			total += colsSizes[cols_idx];
		}

		for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++) {
			colsSizes[cols_idx] = (colsSizes[cols_idx] / total)
					* table.getWidth();
		}

		for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++) {
			logger.debug("Col: " + cols_idx + " : " + colsSizes[cols_idx]);
		}

		/*
		 * if(!addedFonts.contains(table.getFont().getFont(null))) { PDFont font
		 * = table.getFont().getFont(template); addedFonts.add(font);
		 * innerFormResources.addFont(font); }
		 * 
		 * if(!addedFonts.contains(table.getValueFont().getFont(null))) { PDFont
		 * font = table.getValueFont().getFont(template); addedFonts.add(font);
		 * innerFormResources.addFont(font); }
		 */

		for (int i = 0; i < table.getRowCount(); i++) {
			ArrayList<Entry> row = table.getRow(i);
			for (int j = 0; j < row.size(); j++) {
				Entry cell = (Entry) row.get(j);
				if (cell.getType() == Entry.TYPE_IMAGE) {
					String img_ref = (String) cell.getValue();
					if (!images.containsKey(img_ref)) {
						File img_file = new File(img_ref);
						if (!img_file.isAbsolute()) {
							logger.debug("Image file declaration is relative. Prepending path of resources directory.");
							logger.debug("Image Location: "
									+ settings.getWorkingDirectory()
									+ File.separator + img_ref);
							img_file = new File(settings.getWorkingDirectory()
									+ File.separator + img_ref);
						} else {
							logger.debug("Image file declaration is absolute. Skipping file relocation.");
						}

						if (!img_file.exists()) {
							logger.debug("Image file \""
									+ img_file.getCanonicalPath()
									+ "\" doesn't exist.");
							throw new PdfAsException("error.pdf.stamp.04");
						}

						BufferedImage img = null;
						try {
							img = ImageIO.read(img_file);
						} catch (IOException e) {
							throw new PdfAsException("error.pdf.stamp.04", e);
						}

						float width = colsSizes[j];

						float size = (int) Math.floor((double) width);
						size -= 2 * padding;
						logger.debug("Scaling image to: " + size);

						if (table.style != null) {
							if (table.style.getImageScaleToFit() != null) {
								size = table.style.getImageScaleToFit()
										.getWidth();
							}
						}

						PDXObjectImage pdImage = new PDJpeg(template, img);
						ImageObject image = new ImageObject(pdImage, size);
						images.put(img_ref, image);
						innerFormResources.addXObject(pdImage, "Im");
					}
				} else if (cell.getType() == Entry.TYPE_TABLE) {
					PDFBoxTable tbl_value = (PDFBoxTable) cell.getValue();
					readTableResources(tbl_value, template);
				}
			}
		}
	}

	@Override
	public void createInnerFormStream(PDDocument template) {
		try {

			// Hint we have to create all PDXObjectImages before creating the
			// PDPageContentStream
			// only PDFbox developers know why ...
			innerFormResources = new PDResources();
			getStructure().getPage().setResources(innerFormResources);
			readTableResources(properties.getMainTable(), template);

			PDPageContentStream stream = new PDPageContentStream(template,
					getStructure().getPage());
			// stream.setFont(PDType1Font.COURIER, 5);

			drawTable(getStructure().getPage(), stream, 1, 1,
					properties.getMainTable(), template, false);
			stream.close();
			PDStream innterFormStream = getStructure().getPage().getContents();
			getStructure().setInnterFormStream(innterFormStream);
			logger.debug("Strean of another form (inner form - it would be inside holder form) has been created");

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
		/*
		 * imageFormResources.getCOSDictionary() .setItem(COSName.PROC_SET,
		 * procSet);
		 */
		holderFormResources.getCOSDictionary().setItem(COSName.PROC_SET,
				procSet);
		logger.debug("inserted ProcSet to PDF");
	}

	public void injectAppearanceStreams(PDStream holderFormStream,
			PDStream innterFormStream, PDStream imageFormStream,
			String imageObjectName, String imageName, String innerFormName,
			PDFAsVisualSignatureDesigner properties) throws IOException {

		// 100 means that document width is 100% via the rectangle. if rectangle
		// is 500px, images 100% is 500px.
		// String imgFormComment = "q "+imageWidthSize+ " 0 0 50 0 0 cm /" +
		// imageName + " Do Q\n" + builder.toString();
		/*
		 * String imgFormComment = "q " + 100 + " 0 0 50 0 0 cm /" + imageName +
		 * " Do Q\n";
		 */
		double m00 = getStructure().getAffineTransform().getScaleX();
		double m10 = getStructure().getAffineTransform().getShearY();
		double m01 = getStructure().getAffineTransform().getShearX();
		double m11 = getStructure().getAffineTransform().getScaleY();
		double m02 = getStructure().getAffineTransform().getTranslateX();
		double m12 = getStructure().getAffineTransform().getTranslateY();

		String holderFormComment = "q " + m00 + " " + m10 + " " + m01 + " "
				+ m11 + " " + m02 + " " + m12 + " cm /" + innerFormName
				+ " Do Q \n";
		// String innerFormComment = "q 1 0 0 1 0 0 cm /" + imageObjectName +
		// " Do Q\n";
		String innerFormComment = getStructure().getInnterFormStream()
				.getInputStreamAsString();

		logger.debug("Inner Form Stream: " + innerFormComment);

		// appendRawCommands(getStructure().getInnterFormStream().createOutputStream(),
		// getStructure().getInnterFormStream().getInputStreamAsString());

		appendRawCommands(getStructure().getHolderFormStream()
				.createOutputStream(), holderFormComment);
		appendRawCommands(getStructure().getInnterFormStream()
				.createOutputStream(), innerFormComment);
		// appendRawCommands(getStructure().getImageFormStream().createOutputStream(),
		// imgFormComment);
		logger.debug("Injected apereance stream to pdf");

	}

	public void createPage(PDFAsVisualSignatureDesigner properties) {
		PDPage page = new PDPage();
		page.setMediaBox(new PDRectangle(properties.getPageWidth(), properties
				.getPageHeight()));
		getStructure().setPage(page);
		logger.debug("PDF page has been created");
	}

	public void createAcroForm(PDDocument template) {
		PDAcroForm theAcroForm = new PDAcroForm(template);
		template.getDocumentCatalog().setAcroForm(theAcroForm);
		getStructure().setAcroForm(theAcroForm);
		logger.debug("Acro form page has been created");

	}

	public void createSignatureField(PDAcroForm acroForm) throws IOException {
		PDSignatureField sf = new PDSignatureField(acroForm);
		getStructure().setSignatureField(sf);
		logger.debug("Signature field has been created");
	}

	public void createSignature(PDSignatureField pdSignatureField, PDPage page,
			String signatureName) throws IOException {
		PDSignature pdSignature = new PDSignature();
		pdSignatureField.setSignature(pdSignature);
		pdSignatureField.getWidget().setPage(page);
		page.getAnnotations().add(pdSignatureField.getWidget());
		pdSignature.setName(signatureName);
		pdSignature.setByteRange(new int[] { 0, 0, 0, 0 });
		pdSignature.setContents(new byte[4096]);
		getStructure().setPdSignature(pdSignature);
		logger.debug("PDSignatur has been created");
	}

	public void createAcroFormDictionary(PDAcroForm acroForm,
			PDSignatureField signatureField) throws IOException {
		@SuppressWarnings("unchecked")
		List<PDField> acroFormFields = acroForm.getFields();
		COSDictionary acroFormDict = acroForm.getDictionary();
		acroFormDict.setDirect(true);
		acroFormDict.setInt(COSName.SIG_FLAGS, 3);
		acroFormFields.add(signatureField);
		acroFormDict.setString(COSName.DA, "/sylfaen 0 Tf 0 g");
		getStructure().setAcroFormFields(acroFormFields);
		getStructure().setAcroFormDictionary(acroFormDict);
		logger.debug("AcroForm dictionary has been created");
	}

	public void createSignatureRectangle(PDSignatureField signatureField,
			PDFAsVisualSignatureDesigner properties, float degrees)
			throws IOException {

		PDRectangle rect = new PDRectangle();

		Point2D upSrc = new Point2D.Float();
		upSrc.setLocation(properties.getxAxis() + properties.getWidth() + 10,
				properties.getPageHeight() - properties.getyAxis());

		Point2D llSrc = new Point2D.Float();
		llSrc.setLocation(properties.getxAxis(), properties.getPageHeight()
				- properties.getyAxis() - properties.getHeight() - 10);
		AffineTransform transform = new AffineTransform();
		transform.setToIdentity();
		if (degrees % 360 != 0) {
			transform.setToRotation(Math.toRadians(degrees), llSrc.getX(),
					llSrc.getY());
		}
		Point2D upDst = new Point2D.Float();
		transform.transform(upSrc, upDst);

		Point2D llDst = new Point2D.Float();
		transform.transform(llSrc, llDst);

		rect.setUpperRightX((float) upDst.getX());
		rect.setUpperRightY((float) upDst.getY());
		rect.setLowerLeftY((float) llDst.getY());
		rect.setLowerLeftX((float) llDst.getX());
		logger.debug("rectangle of signature has been created: {}",
				rect.toString());
		signatureField.getWidget().setRectangle(rect);
		getStructure().setSignatureRectangle(rect);
		logger.debug("rectangle of signature has been created");
	}

	public void createAffineTransform(byte[] params) {
		AffineTransform transform = new AffineTransform(params[0], params[1],
				params[2], params[3], params[4], params[5]);
		// transform.rotate(90);
		getStructure().setAffineTransform(transform);
		logger.debug("Matrix has been added");
	}

	public void createSignatureImage(PDDocument template,
			InputStream inputStream) throws IOException {
		PDJpeg img = new PDJpeg(template, inputStream);
		getStructure().setJpedImage(img);
		logger.debug("Visible Signature Image has been created");
		// pdfStructure.setTemplate(template);
		inputStream.close();

	}

	public void createFormaterRectangle(float[] params) {

		PDRectangle formrect = new PDRectangle();
		float[] translated = new float[4];
		getStructure().getAffineTransform().transform(params, 0, translated, 0,
				2);

		formrect.setUpperRightX(translated[0]);
		formrect.setUpperRightY(translated[1]);
		formrect.setLowerLeftX(translated[2]);
		formrect.setLowerLeftY(translated[3]);

		getStructure().setFormaterRectangle(formrect);
		logger.debug("Formater rectangle has been created");

	}

	public void createHolderFormStream(PDDocument template) {
		PDStream holderForm = new PDStream(template);
		getStructure().setHolderFormStream(holderForm);
		logger.debug("Holder form Stream has been created");
	}

	public void createHolderFormResources() {
		PDResources holderFormResources = new PDResources();
		getStructure().setHolderFormResources(holderFormResources);
		logger.debug("Holder form resources have been created");

	}

	public void createHolderForm(PDResources holderFormResources,
			PDStream holderFormStream, PDRectangle formrect) {

		PDXObjectForm holderForm = new PDXObjectForm(holderFormStream);
		holderForm.setResources(holderFormResources);
		holderForm.setBBox(formrect);
		holderForm.setFormType(1);
		getStructure().setHolderForm(holderForm);
		logger.debug("Holder form has been created");

	}

	public void createAppearanceDictionary(PDXObjectForm holderForml,
			PDSignatureField signatureField, float degrees) throws IOException {

		PDAppearanceDictionary appearance = new PDAppearanceDictionary();
		appearance.getCOSObject().setDirect(true);

		PDAppearanceStream appearanceStream = new PDAppearanceStream(
				holderForml.getCOSStream());
		AffineTransform transform = new AffineTransform();
		transform.setToIdentity();
		transform.rotate(Math.toRadians(degrees));
		appearanceStream.setMatrix(transform);
		appearance.setNormalAppearance(appearanceStream);
		signatureField.getWidget().setAppearance(appearance);

		getStructure().setAppearanceDictionary(appearance);
		logger.debug("PDF appereance Dictionary has been created");

	}

	public void createInnerFormResource() {
		getStructure().setInnerFormResources(innerFormResources);
		logger.debug("Resources of another form (inner form - it would be inside holder form) have been created");
	}

	public void createInnerForm(PDResources innerFormResources,
			PDStream innerFormStream, PDRectangle formrect) {
		PDXObjectForm innerForm = new PDXObjectForm(innerFormStream);
		innerForm.setResources(innerFormResources);
		innerForm.setBBox(formrect);
		innerForm.setFormType(1);
		getStructure().setInnerForm(innerForm);
		logger.debug("Another form (inner form - it would be inside holder form) have been created");

	}

	public void insertInnerFormToHolerResources(PDXObjectForm innerForm,
			PDResources holderFormResources) {
		String name = holderFormResources.addXObject(innerForm, "FRM");
		getStructure().setInnerFormName(name);
		logger.debug("Alerady inserted inner form  inside holder form");
	}

	public void createImageFormStream(PDDocument template) {
		PDStream imageFormStream = new PDStream(template);
		getStructure().setImageFormStream(imageFormStream);
		logger.debug("Created image form Stream");
	}

	public void createImageFormResources() {
		PDResources imageFormResources = new PDResources();
		getStructure().setImageFormResources(imageFormResources);
		logger.debug("Created image form Resources");
	}

	public void createImageForm(PDResources imageFormResources,
			PDResources innerFormResource, PDStream imageFormStream,
			PDRectangle formrect, AffineTransform affineTransform, PDJpeg img)
			throws IOException {

		/*
		 * if you need text on the visible signature
		 * 
		 * PDFont font = PDTrueTypeFont.loadTTF(this.pdfStructure.getTemplate(),
		 * new File("D:\\arial.ttf")); font.setFontEncoding(new
		 * WinAnsiEncoding());
		 * 
		 * Map<String, PDFont> fonts = new HashMap<String, PDFont>();
		 * fonts.put("arial", font);
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
		logger.debug("Created image form");
	}

	public void appendRawCommands(OutputStream os, String commands)
			throws IOException {
		os.write(commands.getBytes("UTF-8"));
		os.close();
	}

	public void createVisualSignature(PDDocument template) {
		this.getStructure().setVisualSignature(template.getDocument());
		logger.debug("Visible signature has been created");

	}

	public void createWidgetDictionary(PDSignatureField signatureField,
			PDResources holderFormResources) throws IOException {

		COSDictionary widgetDict = signatureField.getWidget().getDictionary();
		widgetDict.setNeedToBeUpdate(true);
		widgetDict.setItem(COSName.DR, holderFormResources.getCOSObject());

		getStructure().setWidgetDictionary(widgetDict);
		logger.debug("WidgetDictionary has been crated");
	}

	public void closeTemplate(PDDocument template) throws IOException {
		template.close();
		this.getStructure().getTemplate().close();
	}

}
