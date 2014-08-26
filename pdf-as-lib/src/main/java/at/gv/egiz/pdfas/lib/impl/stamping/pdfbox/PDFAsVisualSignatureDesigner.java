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
package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class PDFAsVisualSignatureDesigner {

//	private static final Logger logger = LoggerFactory.getLogger(PDFAsVisualSignatureDesigner.class);

	private Float sigImgWidth;
	private Float sigImgHeight;
	private float xAxis;
	private float yAxis;
	private float pageHeight;
	private float pageWidth;
	private InputStream imgageStream;
	private String signatureFieldName = "sig"; // default
	private float[] formaterRectangleParams = { 0, 0, 100, 50 }; // default
	//private float[] AffineTransformParams = { 0, 1, -1, 0, 0, 0 }; // default
	private float[] AffineTransformParams = { 1, 0, 0, 1, 0, 0 }; // default
	private float imageSizeInPercents;
	private PDDocument document = null;
	private int page = 0;
	private boolean newpage = false;
	PDFAsVisualSignatureProperties properties;

	/**
	 * 
	 * @param doc
	 *            - Already created PDDocument of your PDF document
	 * @param imageStream
	 * @param page
	 * @throws IOException
	 *             - If we can't read, flush, or can't close stream
	 */
	public PDFAsVisualSignatureDesigner(PDDocument doc, int page,
			PDFAsVisualSignatureProperties properties, boolean newpage) throws IOException {
		this.properties = properties;
		calculatePageSize(doc, page, newpage);
		document = doc;
		this.page = page;
		this.newpage = newpage;
	}

	/**
	 * Each page of document can be different sizes.
	 * 
	 * @param document
	 * @param page
	 */
	private void calculatePageSize(PDDocument document, int page, boolean newpage) {

		if (page < 1) {
			throw new IllegalArgumentException("First page of pdf is 1, not "
					+ page);
		}
		
		List<?> pages = document.getDocumentCatalog().getAllPages();
		if(newpage) {
			PDPage lastPage = (PDPage) pages.get(pages.size()-1);
			PDRectangle mediaBox = lastPage.findMediaBox();
			this.pageHeight(mediaBox.getHeight());
			this.pageWidth = mediaBox.getWidth();
		} else {
			PDPage firstPage = (PDPage) pages.get(page - 1);
			PDRectangle mediaBox = firstPage.findMediaBox();
			this.pageHeight(mediaBox.getHeight());
			this.pageWidth = mediaBox.getWidth();
		}
		float x = this.pageWidth;
		float y = 0;
		this.pageWidth = this.pageWidth + y;
		float tPercent = (100 * y / (x + y));
		this.imageSizeInPercents = 100 - tPercent;
	}

	/**
	 * 
	 * @param path
	 *            of image location
	 * @return image Stream
	 * @throws IOException
	 */
	public PDFAsVisualSignatureDesigner signatureImage(String path)
			throws IOException {
		InputStream fin = new FileInputStream(path);
		return signatureImageStream(fin);
	}

	/**
	 * zoom signature image with some percent.
	 * 
	 * @param percent
	 *            - x % increase image with x percent.
	 * @return Visible Signature Configuration Object
	 */
	public PDFAsVisualSignatureDesigner zoom(float percent) {
		sigImgHeight = sigImgHeight + (sigImgHeight * percent) / 100;
		sigImgWidth = sigImgWidth + (sigImgWidth * percent) / 100;
		return this;
	}

	/**
	 * 
	 * @param xAxis
	 *            - x coordinate
	 * @param yAxis
	 *            - y coordinate
	 * @return Visible Signature Configuration Object
	 */
	public PDFAsVisualSignatureDesigner coordinates(float x, float y) {
		xAxis(x);
		yAxis(y);
		return this;
	}

	/**
	 * 
	 * @return xAxis - gets x coordinates
	 */
	public float getxAxis() {
		return xAxis;
	}

	/**
	 * 
	 * @param xAxis
	 *            - x coordinate
	 * @return Visible Signature Configuration Object
	 */
	public PDFAsVisualSignatureDesigner xAxis(float xAxis) {
		this.xAxis = xAxis;
		return this;
	}

	/**
	 * 
	 * @return yAxis
	 */
	public float getyAxis() {
		return yAxis;
	}

	/**
	 * 
	 * @param yAxis
	 * @return Visible Signature Configuration Object
	 */
	public PDFAsVisualSignatureDesigner yAxis(float yAxis) {
		this.yAxis = yAxis;
		return this;
	}

	/**
	 * 
	 * @return signature image width
	 */
	public float getWidth() {
		return this.properties.getMainTable().getWidth();
	}

	/**
	 * 
	 * @param sets
	 *            signature image width
	 * @return Visible Signature Configuration Object
	 */
	public PDFAsVisualSignatureDesigner width(float signatureImgWidth) {
		this.sigImgWidth = signatureImgWidth;
		return this;
	}

	/**
	 * 
	 * @return signature image height
	 */
	public float getHeight() {
		return this.properties.getMainTable().getHeight();
	}

	/**
	 * 
	 * @param set
	 *            signature image Height
	 * @return Visible Signature Configuration Object
	 */
	public PDFAsVisualSignatureDesigner height(float signatureImgHeight) {
		this.sigImgHeight = signatureImgHeight;
		return this;
	}

	/**
	 * 
	 * @return template height
	 */
	protected float getTemplateHeight() {
		return getPageHeight();
	}

	/**
	 * 
	 * @param templateHeight
	 * @return Visible Signature Configuration Object
	 */
	private PDFAsVisualSignatureDesigner pageHeight(float templateHeight) {
		this.pageHeight = templateHeight;
		return this;
	}

	/**
	 * 
	 * @return signature field name
	 */
	public String getSignatureFieldName() {
		return signatureFieldName;
	}

	/**
	 * 
	 * @param signatureFieldName
	 * @return Visible Signature Configuration Object
	 */
	public PDFAsVisualSignatureDesigner signatureFieldName(
			String signatureFieldName) {
		this.signatureFieldName = signatureFieldName;
		return this;
	}

	/**
	 * 
	 * @return image Stream
	 */
	public InputStream getImageStream() {
		return imgageStream;
	}

	/**
	 * 
	 * @param imgageStream
	 *            - stream of your visible signature image
	 * @return Visible Signature Configuration Object
	 * @throws IOException
	 *             - If we can't read, flush, or close stream of image
	 */
	private PDFAsVisualSignatureDesigner signatureImageStream(
			InputStream imageStream) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = imageStream.read(buffer)) > -1) {
			baos.write(buffer, 0, len);
		}
		baos.flush();
		baos.close();

		byte[] byteArray = baos.toByteArray();
		byte[] byteArraySecond = byteArray.clone();

		InputStream inputForBufferedImage = new ByteArrayInputStream(byteArray);
		InputStream revertInputStream = new ByteArrayInputStream(
				byteArraySecond);

		if (sigImgHeight == null || sigImgWidth == null) {
			calcualteImageSize(inputForBufferedImage);
		}

		this.imgageStream = revertInputStream;

		return this;
	}

	/**
	 * calculates image width and height. sported formats: all
	 * 
	 * @param fis
	 *            - input stream of image
	 * @throws IOException
	 *             - if can't read input stream
	 */
	private void calcualteImageSize(InputStream fis) throws IOException {

		BufferedImage bimg = ImageIO.read(fis);
		int width = bimg.getWidth();
		int height = bimg.getHeight();

		sigImgHeight = (float) height;
		sigImgWidth = (float) width;

	}

	/**
	 * 
	 * @return Affine Transform parameters of for PDF Matrix
	 */
	public float[] getAffineTransformParams() {
		return AffineTransformParams;
	}

	/**
	 * 
	 * @param affineTransformParams
	 * @return Visible Signature Configuration Object
	 */
	public PDFAsVisualSignatureDesigner affineTransformParams(
			float[] affineTransformParams) {
		AffineTransformParams = affineTransformParams;
		return this;
	}

	/**
	 * 
	 * @return formatter PDRectanle parameters
	 */
	public float[] getFormaterRectangleParams() {
		return formaterRectangleParams;
	}

	/**
	 * sets formatter PDRectangle;
	 * 
	 * @param formaterRectangleParams
	 * @return Visible Signature Configuration Object
	 */
	public PDFAsVisualSignatureDesigner formaterRectangleParams(
			float[] formaterRectangleParams) {
		this.formaterRectangleParams = formaterRectangleParams;
		return this;
	}

	/**
	 * 
	 * @return page width
	 */
	public float getPageWidth() {
		return pageWidth;
	}
	
	public PDPage getSignaturePage() {
		if (page < 1) {
			throw new IllegalArgumentException("First page of pdf is 1, not "
					+ page);
		}
		PDPage pdPage = null;
		List<?> pages = document.getDocumentCatalog().getAllPages();
		if(newpage) {
			pdPage = new PDPage();
		} else {
			pdPage = (PDPage) pages.get(page - 1);
		}
		
		return pdPage;
	}

	/**
	 * 
	 * @param sets
	 *            pageWidth
	 * @return Visible Signature Configuration Object
	 */
	public PDFAsVisualSignatureDesigner pageWidth(float pageWidth) {
		this.pageWidth = pageWidth;
		return this;
	}

	/**
	 * 
	 * @return page height
	 */
	public float getPageHeight() {
		return pageHeight;
	}

	/**
	 * get image size in percents
	 * 
	 * @return
	 */
	public float getImageSizeInPercents() {
		return imageSizeInPercents;
	}

	/**
	 * 
	 * @param imageSizeInPercents
	 */
	public void imageSizeInPercents(float imageSizeInPercents) {
		this.imageSizeInPercents = imageSizeInPercents;
	}

	/**
	 * returns visible signature text
	 * 
	 * @return
	 */
	public String getSignatureText() {
		throw new UnsupportedOperationException(
				"That method is not yet implemented");
	}

	/**
	 * 
	 * @param signatureText
	 *            - adds the text on visible signature
	 * @return
	 */
	public PDFAsVisualSignatureDesigner signatureText(String signatureText) {
		throw new UnsupportedOperationException(
				"That method is not yet implemented");
	}

	public float getRotation() {
		return this.properties.getRotation();
	}
	
}
