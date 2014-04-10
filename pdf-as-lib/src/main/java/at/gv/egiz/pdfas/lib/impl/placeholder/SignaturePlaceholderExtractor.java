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
/**
 * <copyright> Copyright 2006 by Know-Center, Graz, Austria </copyright>
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
 */
package at.gv.egiz.pdfas.lib.impl.placeholder;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.exceptions.WrappedIOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.PDFOperator;
import org.apache.pdfbox.util.PDFStreamEngine;
import org.apache.pdfbox.util.ResourceLoader;

import at.gv.egiz.pdfas.common.exceptions.PDFIOException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PlaceholderExtractionException;
import at.knowcenter.wag.egov.egiz.pdf.TablePos;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

//////

/**
 * Extract all relevant information from a placeholder image.
 *
 * @author exthex
 *
 */
public class SignaturePlaceholderExtractor extends PDFStreamEngine {
	/**
	 * The log.
	 */
	private static Log log = LogFactory
			.getLog(SignaturePlaceholderExtractor.class);

	public static final String QR_PLACEHOLDER_IDENTIFIER = "PDF-AS-POS";
	public static final int PLACEHOLDER_MATCH_MODE_STRICT = 0;
	public static final int PLACEHOLDER_MATCH_MODE_MODERATE = 1;
	public static final int PLACEHOLDER_MATCH_MODE_LENIENT = 2;

	private List<SignaturePlaceholderData> placeholders = new Vector<SignaturePlaceholderData>();
	private int currentPage = 0;

	private SignaturePlaceholderExtractor(String placeholderId,
			int placeholderMatchMode) throws IOException {
		super(ResourceLoader.loadProperties(
				"placeholder/pdfbox-reader.properties", true));
	}

	/**
	 * Search the document for placeholder images and possibly included
	 * additional info.<br/>
	 * Searches only for the first placeholder page after page from top.
	 *
	 * @param inputStream
	 * @return all available info from the first found placeholder.
	 * @throws PDFDocumentException
	 *             if the document could not be read.
	 * @throws PlaceholderExtractionException
	 *             if STRICT matching mode was requested and no suitable
	 *             placeholder could be found.
	 */
	public static SignaturePlaceholderData extract(PDDocument doc,
			String placeholderId, int matchMode) throws PdfAsException {
		SignaturePlaceholderContext.setSignaturePlaceholderData(null);

		SignaturePlaceholderExtractor extractor;
		try {
			extractor = new SignaturePlaceholderExtractor(placeholderId,
					matchMode);
		} catch (IOException e2) {
			throw new PDFIOException("error.pdf.io.04", e2);
		}
		List<?> pages = doc.getDocumentCatalog().getAllPages();
		Iterator<?> iter = pages.iterator();
		int pageNr = 0;
		while (iter.hasNext()) {
			pageNr++;
			PDPage page = (PDPage) iter.next();
			try {
				extractor.setCurrentPage(pageNr);
				extractor.processStream(page, page.findResources(), page
						.getContents().getStream());
				SignaturePlaceholderData ret = matchPlaceholderPage(
						extractor.placeholders, placeholderId, matchMode);
				if (ret != null) {
					SignaturePlaceholderContext
							.setSignaturePlaceholderData(ret);
					return ret;
				}
			} catch (IOException e1) {
				throw new PDFIOException("error.pdf.io.04", e1);
			}

		}
		if (extractor.placeholders.size() > 0) {
			SignaturePlaceholderData ret = matchPlaceholderDocument(
					extractor.placeholders, placeholderId, matchMode);
			SignaturePlaceholderContext.setSignaturePlaceholderData(ret);
			return ret;
		}
		// no placeholders found, apply strict mode if set
		if (matchMode == PLACEHOLDER_MATCH_MODE_STRICT) {
			throw new PlaceholderExtractionException("error.pdf.stamp.09");
		}

		return null;
	}

	private static SignaturePlaceholderData matchPlaceholderDocument(
			List<SignaturePlaceholderData> placeholders, String placeholderId,
			int matchMode) throws PlaceholderExtractionException {

		if (matchMode == PLACEHOLDER_MATCH_MODE_STRICT)
			throw new PlaceholderExtractionException("error.pdf.stamp.09");

		if (placeholders.size() == 0)
			return null;

		for (int i = 0; i < placeholders.size(); i++) {
			SignaturePlaceholderData spd = placeholders.get(i);
			if (spd.getId() == null)
				return spd;
		}

		if (matchMode == PLACEHOLDER_MATCH_MODE_LENIENT)
			return placeholders.get(0);

		return null;
	}

	private static SignaturePlaceholderData matchPlaceholderPage(
			List<SignaturePlaceholderData> placeholders, String placeholderId,
			int matchMode) {
		if (placeholders.size() == 0)
			return null;
		for (int i = 0; i < placeholders.size(); i++) {
			SignaturePlaceholderData data = placeholders.get(i);
			if (placeholderId != null && placeholderId.equals(data.getId()))
				return data;
			if (placeholderId == null && data.getId() == null)
				return data;
		}
		return null;
	}

	private void setCurrentPage(int pageNr) {
		this.currentPage = pageNr;
	}

	@Override
	protected void processOperator(PDFOperator operator, List<COSBase> arguments)
			throws IOException {
		String operation = operator.getOperation();
		if (operation.equals("Do")) {
			COSName objectName = (COSName) arguments.get(0);
			Map<?, ?> xobjects = getResources().getXObjects();
			PDXObject xobject = (PDXObject) xobjects.get(objectName.getName());
			if (xobject instanceof PDXObjectImage) {
				try {
					PDXObjectImage image = (PDXObjectImage) xobject;
					SignaturePlaceholderData data = checkImage(image);
					if (data != null) {
						PDPage page = getCurrentPage();
						Matrix ctm = getGraphicsState()
								.getCurrentTransformationMatrix();
						double rotationInRadians = (page.findRotation() * Math.PI) / 180;

						AffineTransform rotation = new AffineTransform();
						rotation.setToRotation(rotationInRadians);
						AffineTransform rotationInverse = rotation
								.createInverse();
						Matrix rotationInverseMatrix = new Matrix();
						rotationInverseMatrix
								.setFromAffineTransform(rotationInverse);
						Matrix rotationMatrix = new Matrix();
						rotationMatrix.setFromAffineTransform(rotation);

						Matrix unrotatedCTM = ctm
								.multiply(rotationInverseMatrix);

						float x = unrotatedCTM.getXPosition();
						float y = unrotatedCTM.getYPosition()
								+ unrotatedCTM.getYScale();
						float w = unrotatedCTM.getXScale();

						String posString = "p:" + currentPage + ";x:" + x
								+ ";y:" + y + ";w:" + w;
						try {
							data.setTablePos(new TablePos(posString));
							data.setPlaceholderName(objectName.getName());
							placeholders.add(data);
						} catch (PdfAsException e) {
							throw new WrappedIOException(e);
						}
					}
				} catch (NoninvertibleTransformException e) {
					throw new WrappedIOException(e);
				}
			}
		} else {
			super.processOperator(operator, arguments);
		}
	}

	/**
	 * Checks an image if it is a placeholder for a signature.
	 *
	 * @param image
	 * @return
	 * @throws IOException
	 */
	private SignaturePlaceholderData checkImage(PDXObjectImage image)
			throws IOException {
		BufferedImage bimg = image.getRGBImage();
		if (bimg == null) {
			String type = image.getSuffix();
			if (type != null) {
				type = type.toUpperCase() + " images";
			} else {
				type = "Image type";
			}
			log.info("Unable to extract image for QRCode analysis. "
					+ type
					+ " not supported. Add additional JAI Image filters to your classpath. Refer to https://jai.dev.java.net. Skipping image.");
			return null;
		}
		if (bimg.getHeight() < 10 || bimg.getWidth() < 10) {
			log.debug("Image too small for QRCode. Skipping image.");
			return null;
		}

		LuminanceSource source = new BufferedImageLuminanceSource(bimg);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		Result result;
		long before = System.currentTimeMillis();
		try {
			Hashtable<DecodeHintType, Vector<BarcodeFormat>> hints = new Hashtable<DecodeHintType, Vector<BarcodeFormat>>();
			Vector<BarcodeFormat> formats = new Vector<BarcodeFormat>();
			formats.add(BarcodeFormat.QR_CODE);
			hints.put(DecodeHintType.POSSIBLE_FORMATS, formats);
			result = new MultiFormatReader().decode(bitmap, hints);

			String text = result.getText();
			String profile = null;
			String type = null;
			String sigKey = null;
			String id = null;
			if (text != null) {
				if (text.startsWith(QR_PLACEHOLDER_IDENTIFIER)) {
					String[] data = text.split(";");
					if (data.length > 1) {
						for (int i = 1; i < data.length; i++) {
							String kvPair = data[i];
							String[] kv = kvPair.split("=");
							if (kv.length != 2) {
								log.debug("Invalid parameter in placeholder data: "
										+ kvPair);
							} else {
								if (kv[0]
										.equalsIgnoreCase(SignaturePlaceholderData.ID_KEY)) {
									id = kv[1];
								} else if (kv[0]
										.equalsIgnoreCase(SignaturePlaceholderData.PROFILE_KEY)) {
									profile = kv[1];
								} else if (kv[0]
										.equalsIgnoreCase(SignaturePlaceholderData.SIG_KEY_KEY)) {
									sigKey = kv[1];
								} else if (kv[0]
										.equalsIgnoreCase(SignaturePlaceholderData.TYPE_KEY)) {
									type = kv[1];
								}
							}
						}
					}
					return new SignaturePlaceholderData(profile, type, sigKey,
							id);
				} else {
					log.warn("QR-Code found but does not start with \""
							+ QR_PLACEHOLDER_IDENTIFIER
							+ "\". Ignoring QR placeholder.");
				}
			}
		} catch (ReaderException re) {
			if (log.isDebugEnabled()) {
				log.debug("Could not decode - not a placeholder. needed: "
						+ (System.currentTimeMillis() - before));
			}
			if (!(re instanceof NotFoundException)) {
				if (log.isInfoEnabled()) {
					log.info("Failed to decode image", re);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			if (log.isInfoEnabled()) {
				log.info("Failed to decode image. Probably a zxing bug", e);
			}
		}
		return null;
	}

}
