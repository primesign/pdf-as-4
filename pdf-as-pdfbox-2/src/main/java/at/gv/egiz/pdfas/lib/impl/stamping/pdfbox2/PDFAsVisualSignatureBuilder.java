/*******************************************************************************
 * <copyright> Copyright 2014 by E-Government Innovation Center EGIZ, Graz, Austria </copyright>
 * PDF-AS has been contracted by the E-Government Innovation Center EGIZ, a
 * joint initiative of the Federal Chancellery Austria and Graz University of
 * Technology.
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * http://www.osor.eu/eupl/
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * 
 * This product combines work with different licenses. See the "NOTICE" text
 * file for details on the various modules and licenses.
 * The "NOTICE" text file is part of the distribution. Any derivative works
 * that you distribute must include a readable copy of the "NOTICE" text file.
 ******************************************************************************/
package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox2;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
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
import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.common.utils.ImageUtils;
import at.knowcenter.wag.egov.egiz.table.Entry;

public class PDFAsVisualSignatureBuilder extends PDVisibleSigBuilder implements
		IDGenerator {

	private static final Logger logger = LoggerFactory
			.getLogger(PDFAsVisualSignatureBuilder.class);

	private PDFAsVisualSignatureProperties properties;
	private PDFAsVisualSignatureDesigner designer;
	private ISettings settings;
	public SignatureProfileSettings signatureProfileSettings;
	private PDResources innerFormResources;
	private Map<String, ImageObject> images = new HashMap<>();

	public PDFAsVisualSignatureBuilder(
			PDFAsVisualSignatureProperties properties, ISettings settings,
			PDFAsVisualSignatureDesigner designer,
			SignatureProfileSettings signatureProfileSettings) {
		this.properties = properties;
		this.settings = settings;
		this.designer = designer;
		this.signatureProfileSettings = signatureProfileSettings;
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

	public void createMyPage(PDFAsVisualSignatureDesigner properties) {
		PDPage page = properties.getSignaturePage();
		if (page == null) {
			page = new PDPage();
			page.setMediaBox(new PDRectangle(properties.getPageWidth(),
					properties.getPageHeight()));

		}
		getStructure().setPage(page);
		logger.info("PDF page has been created");
	}

	@Override
	public void createTemplate(PDPage page) throws IOException {
		PDDocument template = new PDDocument();

		template.addPage(page);
		getStructure().setTemplate(template);
	}

	public String createHashedId(String value) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.reset();
			return Hex.encodeHexString(md.digest(value.getBytes("UTF-8")));
		} catch (Throwable e) {
			logger.warn("Failed to generate ID for Image using value", e);
			return value;
		}
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
				Entry cell = row.get(j);
				if (cell.getType() == Entry.TYPE_IMAGE) {
					String img_value = (String) cell.getValue();
					String img_ref = createHashedId(img_value);
					if (!images.containsKey(img_ref)) {
						BufferedImage img = ImageUtils.getImage(img_value,
								settings);

						float width = colsSizes[j];
						float height = table.getRowHeights()[i] + padding * 2;

						float iwidth = (int) Math.floor(width);
						iwidth -= 2 * padding;

						float iheight = (int) Math.floor(height);
						iheight -= 2 * padding;

						float origWidth = img.getWidth();
						float origHeight = img.getHeight();

						if (table.style != null) {
							if (table.style.getImageScaleToFit() != null) {
								iwidth = table.style.getImageScaleToFit()
										.getWidth();
								iheight = table.style.getImageScaleToFit()
										.getHeight();
							}
						}

						float wfactor = iwidth / origWidth;
						float hfactor = iheight / origHeight;
						float scaleFactor = wfactor;
						if (hfactor < wfactor) {
							scaleFactor = hfactor;
						}

						iwidth = (float) Math
								.floor(scaleFactor * origWidth);
						iheight = (float) Math
								.floor(scaleFactor * origHeight);

						logger.debug("Scaling image to: " + iwidth + " x "
								+ iheight);

						if (this.designer.properties
								.getSignatureProfileSettings().isPDFA()) {
							img = ImageUtils.removeAlphaChannel(img);
						} else {
							if (img.getAlphaRaster() == null
									&& img.getColorModel().hasAlpha()) {
								img = ImageUtils.removeAlphaChannel(img);
							}
						}
						// img = ImageUtils.convertRGBAToIndexed(img);

						PDImageXObject pdImage = LosslessFactory.createFromImage(template, img);
						

						ImageObject image = new ImageObject(pdImage, iwidth,
								iheight);
						images.put(img_ref, image);
						innerFormResources.add(pdImage, "Im");
					}
				} else if (cell.getType() == Entry.TYPE_TABLE) {
					PDFBoxTable tbl_value = (PDFBoxTable) cell.getValue();
					readTableResources(tbl_value, template);
				}
			}
		}
	}

	public void createInnerFormStreamPdfAs(PDDocument template, PDDocument origDoc)
			throws PdfAsException {
		try {

			// Hint we have to create all PDXObjectImages before creating the
			// PDPageContentStream
			// only PDFbox developers know why ...
			// if (getStructure().getPage().getResources() != null) {
			// innerFormResources = getStructure().getPage().getResources();
			// } else {
			innerFormResources = new PDResources();
			getStructure().getPage().setResources(innerFormResources);
			// }
			readTableResources(properties.getMainTable(), template);

			PDPageContentStream stream = new PDPageContentStream(template,
					getStructure().getPage());
			// stream.setFont(PDType1Font.COURIER, 5);
			TableDrawUtils.drawTable(getStructure().getPage(), stream, 1, 1,
					designer.getWidth(), designer.getHeight(),
					properties.getMainTable(), template, false,
					innerFormResources, images, settings, this, properties);
			stream.close();
			PDStream innterFormStream = new PDStream(template,getStructure().getPage().getContents());

			getStructure().setInnterFormStream(innterFormStream);
			logger.debug("Stream of another form (inner form - it would be inside holder form) has been created");

		} catch (Throwable e) {
			logger.warn("Failed to create visual signature block", e);
			throw new PdfAsException("Failed to create visual signature block",
					e);
		}
	}

	@Override
	public void injectProcSetArray(PDFormXObject innerForm, PDPage page,
			PDResources innerFormResources, PDResources imageFormResources,
			PDResources holderFormResources, COSArray procSet) {
		innerForm.getResources().getCOSObject()
				.setItem(COSName.PROC_SET, procSet); //
		page.getCOSObject().setItem(COSName.PROC_SET, procSet);
		innerFormResources.getCOSObject()
				.setItem(COSName.PROC_SET, procSet);
		/*
		 * imageFormResources.getCOSDictionary() .setItem(COSName.PROC_SET,
		 * procSet);
		 */
		holderFormResources.getCOSObject().setItem(COSName.PROC_SET,
				procSet);
		logger.debug("inserted ProcSet to PDF");
	}

	public void injectAppearanceStreams(PDStream holderFormStream,
			PDStream innterFormStream, PDStream imageFormStream,
			String imageObjectName, String imageName, String innerFormName,
			PDFAsVisualSignatureDesigner properties) throws IOException {

		double m00 = getStructure().getAffineTransform().getScaleX();
		double m10 = getStructure().getAffineTransform().getShearY();
		double m01 = getStructure().getAffineTransform().getShearX();
		double m11 = getStructure().getAffineTransform().getScaleY();
		double m02 = getStructure().getAffineTransform().getTranslateX();
		double m12 = getStructure().getAffineTransform().getTranslateY();

		String holderFormComment = "q " + m00 + " " + m10 + " " + m01 + " "
				+ m11 + " " + m02 + " " + m12 + " cm /" + innerFormName
				+ " Do Q";

		logger.debug("Holder Form Stream: " + holderFormComment);

		String innerFormComment = new String(getStructure().getInnerFormStream().toByteArray(), "UTF-8");

		appendRawCommands(getStructure().getHolderFormStream()
				.createOutputStream(), holderFormComment.trim().replace("\n", "").replace("\r", ""));
		appendRawCommands(getStructure().getInnerFormStream()
				.createOutputStream(), innerFormComment/*.trim().replace("\n", "").replace("\r", "")*/);
		// appendRawCommands(getStructure().getImageFormStream().createOutputStream(),
		// imgFormComment);
		logger.debug("Injected apereance stream to pdf");

	}

	public void createPage(PDFAsVisualSignatureDesigner properties) {
		PDPage page = new PDPage();
		page.setMediaBox(new PDRectangle(properties.getPageWidth(), properties
				.getPageHeight()));
		page.setRotation(properties.getPageRotation());
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
		logger.debug("PDSignature has been created");
	}

	public void createAcroFormDictionary(PDAcroForm acroForm,
			PDSignatureField signatureField) throws IOException {
		@SuppressWarnings("unchecked")
		List<PDField> acroFormFields = acroForm.getFields();
		COSDictionary acroFormDict = acroForm.getCOSObject();
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
		upSrc.setLocation(properties.getxAxis() + properties.getWidth(),
				properties.getPageHeight() - properties.getyAxis());

		Point2D llSrc = new Point2D.Float();
		llSrc.setLocation(properties.getxAxis(), properties.getPageHeight()
				- properties.getyAxis() - properties.getHeight());

		rect.setUpperRightX((float) upSrc.getX());
		rect.setUpperRightY((float) upSrc.getY());
		rect.setLowerLeftY((float) llSrc.getY());
		rect.setLowerLeftX((float) llSrc.getX());
		logger.debug("orig rectangle of signature has been created: {}",
				rect.toString());

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

		float xPos = properties.getxAxis();
		float yPos = properties.getPageHeight() - properties.getyAxis();
		logger.debug("POS {} x {}", xPos, yPos);
		logger.debug("SIZE {} x {}", properties.getWidth(),
				properties.getHeight());
		// translate according to page! rotation
		int pageRotation = properties.getPageRotation();
		AffineTransform translate = new AffineTransform();
		switch (pageRotation) {
		case 90:
			translate.setToTranslation(
					properties.getPageHeight()
							- (properties.getPageHeight() - properties
									.getyAxis()) - properties.getxAxis()
							+ properties.getHeight(),
					properties.getxAxis()
							+ properties.getHeight()
							- (properties.getPageHeight() - properties
									.getyAxis()));
			break;
		case 180:
			// translate.setToTranslation(properties.getPageWidth() -
			// properties.getxAxis() - properties.getxAxis(),
			// properties.getPageHeight() - properties.getyAxis() +
			// properties.getHeight());
			translate.setToTranslation(
					properties.getPageWidth() - 2 * xPos,
					properties.getPageHeight() - 2
							* (yPos - properties.getHeight()));
			break;
		case 270:
			translate.setToTranslation(-properties.getHeight() + yPos - xPos,
					properties.getPageWidth() - (yPos - properties.getHeight())
							- xPos);
			break;
		}

		translate.transform(upDst, upDst);
		translate.transform(llDst, llDst);

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

	public void createAffineTransform(float[] params) {
		AffineTransform transform = new AffineTransform(params[0], params[1],
				params[2], params[3], params[4], params[5]);
		// transform.rotate(90);
		getStructure().setAffineTransform(transform);
		logger.debug("Matrix has been added");
	}

	public void createSignatureImage(PDDocument template,
			InputStream inputStream) throws IOException {
		PDImageXObject img = JPEGFactory.createFromStream(template, inputStream);
		getStructure().setImage(img);
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

		getStructure().setFormatterRectangle(formrect);
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

		PDFormXObject holderForm = new PDFormXObject(holderFormStream);
		holderForm.setResources(holderFormResources);
		holderForm.setBBox(formrect);
		holderForm.setFormType(1);
		getStructure().setHolderForm(holderForm);
		logger.debug("Holder form has been created");

	}

	public void createAppearanceDictionary(PDFormXObject holderForml,
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
		logger.debug("PDF appeareance Dictionary has been created");

	}

	public void createInnerFormResource() {
		getStructure().setInnerFormResources(innerFormResources);
		logger.debug("Resources of another form (inner form - it would be inside holder form) have been created");
	}

	public void createInnerForm(PDResources innerFormResources,
			PDStream innerFormStream, PDRectangle formrect) {
		PDFormXObject innerForm = new PDFormXObject(innerFormStream);
		innerForm.setResources(innerFormResources);
		innerForm.setBBox(formrect);
		innerForm.setFormType(1);
		getStructure().setInnerForm(innerForm);
		logger.debug("Another form (inner form - it would be inside holder form) has been created");

	}

	public void insertInnerFormToHolerResources(PDFormXObject innerForm,
			PDResources holderFormResources) {
		COSName name = holderFormResources.add(innerForm, "FRM");//TODO: pdfbox2 - is this right?
		getStructure().setInnerFormName(name);
		logger.debug("Already inserted inner form  inside holder form");
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
			PDRectangle formrect, AffineTransform affineTransform, PDFormXObject img)
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
		PDFormXObject imageForm = new PDFormXObject(imageFormStream);
		imageForm.setBBox(formrect);
		imageForm.setMatrix(affineTransform);
		imageForm.setResources(imageFormResources);
		imageForm.setFormType(1);
		/*
		 * imageForm.getResources().addFont(font);
		 * imageForm.getResources().setFonts(fonts);
		 */

		imageFormResources.getCOSObject().setDirect(true);
		COSName imageFormName = innerFormResource.add(imageForm, "n");//TODO: pdfbox2 - is this right?
		COSName imageName = imageFormResources.add(img, "img");
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

		COSDictionary widgetDict = signatureField.getWidgets().get(0).getCOSObject();//TODO: pdfbox2 - is this right was getWidget before?
		widgetDict.setNeedToBeUpdated(true);
		widgetDict.setItem(COSName.DR, holderFormResources.getCOSObject());

		getStructure().setWidgetDictionary(widgetDict);
		logger.debug("WidgetDictionary has been created");
	}

	public void closeTemplate(PDDocument template) throws IOException {
		template.close();
		this.getStructure().getTemplate().close();
	}

	public void removeCidSet(PDDocument document) throws IOException {

		PDDocumentCatalog catalog = document.getDocumentCatalog();
		COSName cidSet = COSName.getPDFName("CIDSet");

		Iterator<PDPage> pdPageIterator = catalog.getPages().iterator();
			while(pdPageIterator.hasNext()) {

                PDPage page = pdPageIterator.next();

                Iterator<COSName> cosNameIterator = page.getResources().getFontNames().iterator();
                while (cosNameIterator.hasNext()) {
                    COSName fontName = cosNameIterator.next();
                    PDFont pdFont = page.getResources().getFont(fontName);

                    if (pdFont instanceof PDType0Font) {
                        PDType0Font typedFont = (PDType0Font) pdFont;

                        if (typedFont.getDescendantFont() != null) {
                            if (typedFont.getDescendantFont().getFontDescriptor() != null) {
                                typedFont.getDescendantFont().getFontDescriptor().getCOSObject().removeItem(cidSet);
                            }
                        }
                    }
                }
            }
	}

}
