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
package at.gv.egiz.pdfas.lib.impl.pdfbox2.placeholder;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import at.gv.egiz.pdfas.common.exceptions.PDFIOException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PlaceholderExtractionException;
import at.gv.egiz.pdfas.lib.impl.placeholder.PlaceholderExtractorConstants;
import at.gv.egiz.pdfas.lib.impl.placeholder.SignaturePlaceholderContext;
import at.gv.egiz.pdfas.lib.impl.placeholder.SignaturePlaceholderData;
import at.knowcenter.wag.egov.egiz.pdf.TablePos;

/**
 * Extract all relevant information from a placeholder image.
 *
 * @author exthex
 *
 */
public class SignaturePlaceholderExtractor extends PDFStreamEngine implements PlaceholderExtractorConstants{
	/**
	 * The log.
	 */
	private static Logger logger = LoggerFactory
			.getLogger(SignaturePlaceholderExtractor.class);

	private List<SignaturePlaceholderData> placeholders = new ArrayList<>();
	private int currentPage = 0;
	private final Predicate<PDRectangle> predicate;
	private PDDocument doc;

	private SignaturePlaceholderExtractor(PDDocument doc) {
		this(doc, pdfImageRectangle -> true);
	}

	private SignaturePlaceholderExtractor(PDDocument doc, Predicate<PDRectangle> predicate) {
		
		this.doc = Objects.requireNonNull(doc, "'doc' must not be null.");
		this.predicate = Objects.requireNonNull(predicate, "'predicate' must not be null.");
		
		try {
			final Properties properties = new Properties();
			properties.load(SignaturePlaceholderExtractor.class.getResourceAsStream("pdfbox-reader-2.properties"));
			Set<Entry<Object, Object>> entries = properties.entrySet();
			for (Entry<Object, Object> entry : entries) {
				String processorClassName = StringUtils.trimToNull((String) entry.getValue());
				if (processorClassName != null) {
					Class<?> klass = Class.forName(processorClassName);
					OperatorProcessor processor = (OperatorProcessor) klass.newInstance();
					addOperator(processor);
				}
			}
		} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException("Unable to load and instantiate pdfbox operators.", e);
		}
		
	}

	/**
	 * Extracts all placeholders (with placeholder identifier
	 * {@linkplain at.gv.egiz.pdfas.lib.impl.placeholder.PlaceholderExtractorConstants#QR_PLACEHOLDER_IDENTIFIER
	 * QR_PLACEHOLDER_IDENTIFIER}).
	 * 
	 * @param doc
	 *            The pdfbox document object.
	 * @return A (unmodifiable) list of signature place holders (never {@code null}).
	 * @throws IOException
	 *             Thrown in case of I/O error reading/parsing the pdf document.
	 */
	public static List<SignaturePlaceholderData> extract(PDDocument doc) throws IOException {
		SignaturePlaceholderExtractor extractor = new SignaturePlaceholderExtractor(doc);
		return extractor.extract();
	}
	
	/**
	 * Extracts all placeholders (with placeholder identifier
	 * {@linkplain at.gv.egiz.pdfas.lib.impl.placeholder.PlaceholderExtractorConstants#QR_PLACEHOLDER_IDENTIFIER
	 * QR_PLACEHOLDER_IDENTIFIER}).
	 * 
	 * @param doc
	 *            The pdfbox document object.
	 * @param predicate A predicate allowing filtering of images before being scanned for qr-codes (required; must not be {@code null}).
	 * @return A (unmodifiable) list of signature place holders (never {@code null}).
	 * @throws IOException
	 *             Thrown in case of I/O error reading/parsing the pdf document.
	 */
	public static List<SignaturePlaceholderData> extract(PDDocument doc, Predicate<PDRectangle> predicate) throws IOException {
		SignaturePlaceholderExtractor extractor = new SignaturePlaceholderExtractor(doc, predicate);
		return extractor.extract();
	}
	
	public List<SignaturePlaceholderData> extract() throws IOException {
		
		int pageNr = 0;
		for (PDPage page : doc.getDocumentCatalog().getPages()) {
			setCurrentPage(++pageNr);
			processPage(page);
		}
		
		return ListUtils.unmodifiableList(new ArrayList<>(placeholders));
	}

	/**
	 * Search the document for placeholder images and possibly included
	 * additional info.<br/>
	 * Searches only for the first placeholder page after page from top.
	 * @param doc The parsed pdf document (required; must not be {@code null})
	 * @param placeholderId The identifier of the placeholder (required; must not be {@code null})
	 * @param matchMode The matchmode to be applied.
	 *
	 * @return all available info from the first found placeholder.
	 * @throws PdfAsException if the document could not be read. 
	 * @throws PlaceholderExtractionException
	 *             if STRICT matching mode was requested and no suitable
	 *             placeholder could be found.
	 */
	public static SignaturePlaceholderData extract(PDDocument doc,
			String placeholderId, int matchMode) throws PdfAsException {
		SignaturePlaceholderContext.setSignaturePlaceholderData(null);

		SignaturePlaceholderExtractor extractor = new SignaturePlaceholderExtractor(doc);
		Iterator<PDPage> iter = doc.getDocumentCatalog().getPages().iterator();
		int pageNr = 0;
		while (iter.hasNext()) {
			pageNr++;
			PDPage page = iter.next();
			try {
				extractor.setCurrentPage(pageNr);
				extractor.processPage(page);
				
				SignaturePlaceholderData ret = matchPlaceholderPage(
						extractor.placeholders, placeholderId, matchMode);
				if (ret != null) {
					SignaturePlaceholderContext
							.setSignaturePlaceholderData(ret);
					return ret;
				}
			} catch (IOException e1) {
				throw new PDFIOException("error.pdf.io.04", e1);
			} catch (Exception e) {
				throw new PDFIOException("error.pdf.io.04", e);
			}

		}
		if (!extractor.placeholders.isEmpty()) {
			SignaturePlaceholderData ret = matchPlaceholderDocument(
					extractor.placeholders, matchMode);
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
			List<SignaturePlaceholderData> placeholders, int matchMode) throws PlaceholderExtractionException {

		if (matchMode == PLACEHOLDER_MATCH_MODE_STRICT)
			throw new PlaceholderExtractionException("error.pdf.stamp.09");

		if (placeholders.isEmpty())
			return null;

		if (matchMode == PLACEHOLDER_MATCH_MODE_SORTED) {
			// sort all placeholders by the id string if all ids are null do nothing
			SignaturePlaceholderData currentFirstSpd = null;
			for (int i = 0; i < placeholders.size(); i++) {
				SignaturePlaceholderData spd = placeholders.get(i);
				if (spd.getId() != null) {
					if(currentFirstSpd == null) {
						currentFirstSpd = spd;
						logger.debug("Setting new current ID: {}", 
								currentFirstSpd.getId());
					} else {
						String currentID = currentFirstSpd.getId();
						String testID = spd.getId();
						logger.debug("Testing placeholder current: {} compare to {}", 
								currentID, testID);
						if(testID.compareToIgnoreCase(currentID) < 0) {
							currentFirstSpd = spd;
							logger.debug("Setting new current ID: {}", 
									testID);
						}
					}
				}
			}
			
		for (int i = 0; i < placeholders.size(); i++) {
			SignaturePlaceholderData spd = placeholders.get(i);
			if (spd.getId() == null)
				return spd;
		}

		if (matchMode == PLACEHOLDER_MATCH_MODE_LENIENT)
			return placeholders.get(0);
		}
		return null;
	}


	private static SignaturePlaceholderData matchPlaceholderPage(
			List<SignaturePlaceholderData> placeholders, String placeholderId,
			int matchMode) {
					
		if(matchMode == PLACEHOLDER_MATCH_MODE_SORTED)
			return null;
		
		if (placeholders.isEmpty())
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
	
	/**
	 * This will move the rectangle the given relative amount.
	 *
	 * @param horizontalAmount positive values will move rectangle to the right, negative's to the left.
	 * @param verticalAmount   positive values will move the rectangle up, negative's down.
	 */
	private static void move(PDRectangle rectangle, float horizontalAmount, float verticalAmount) {
		rectangle.setUpperRightX(rectangle.getUpperRightX() + horizontalAmount);
		rectangle.setLowerLeftX(rectangle.getLowerLeftX() + horizontalAmount);
		rectangle.setUpperRightY(rectangle.getUpperRightY() + verticalAmount);
		rectangle.setLowerLeftY(rectangle.getLowerLeftY() + verticalAmount);
	}
    
	@Override
	protected void processOperator(Operator operator, List<COSBase> arguments)
			throws IOException {
		String operation = operator.getName();
		if (operation.equals("Do")) {
			COSName objectName = (COSName) arguments.get(0);
			PDXObject xobject = getResources().getXObject(objectName);
			
			if (xobject instanceof PDImageXObject) {
				
				try {
					
					PDImageXObject image = (PDImageXObject) xobject;
						
					PDPage page = getCurrentPage();
					Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
					
					PDRectangle pdfImageFrame = new PDRectangle(ctm.getScaleX(), ctm.getScaleY());
					move(pdfImageFrame, ctm.getTranslateX(), ctm.getTranslateY());
					
					logger.trace("Processing image '{}' ({} x {} @ {}/{}) on page #{}.", objectName.getName(),
							pdfImageFrame.getWidth(), pdfImageFrame.getHeight(), pdfImageFrame.getLowerLeftX(), pdfImageFrame.getLowerLeftY(),
							currentPage);
					
					// apply predicate (and make sure image has minimum dimension)
					if (predicate.test(pdfImageFrame) && pdfImageFrame.getWidth() >= 10 && pdfImageFrame.getHeight() >= 10) {
						
						logger.debug("Scanning image '{}' ({} x {} @ {}/{}) on page #{} for qrcode.", objectName.getName(),
								pdfImageFrame.getWidth(), pdfImageFrame.getHeight(), pdfImageFrame.getLowerLeftX(), pdfImageFrame.getLowerLeftY(),
								currentPage);
						
						SignaturePlaceholderData data = detectPlaceholder(image);
						if (data != null) {
							
							int pageRotation = page.getRotation();
							
							// prepare reverting page rotation
							AffineTransform rotation = AffineTransform.getRotateInstance(Math.toRadians(pageRotation));
							Matrix rotationInverseMatrix = new Matrix();
							rotationInverseMatrix.setFromAffineTransform(rotation.createInverse());
							
							// modify ctm in order to compensate page rotation
							Matrix unrotatedCTM = ctm.multiply(rotationInverseMatrix);
							
							float w = unrotatedCTM.getScaleX();
							float h = unrotatedCTM.getScaleY();
							
							// x/y denotes top left corner of rectangle while the origin of unrotatedCTM is lower left corner of page
							float x = unrotatedCTM.getTranslateX();
							float y = unrotatedCTM.getTranslateY() + h; 
							
							final PDRectangle pageDimension = page.getCropBox();
							// FIXME: x/y and w/h do not seem to be correct with rotated pages or rotated images
							if (pageRotation == 90) {
								y = pageDimension.getWidth() - (y * (-1));
							} else if (pageRotation == 180) {
								x = pageDimension.getWidth() + x;
								y = pageDimension.getHeight() - (y * (-1));
							} else if (pageRotation == 270) {
								x = pageDimension.getHeight() + x;
							}
							
							String posString = "p:" + currentPage + ";x:" + x + ";y:" + y + ";w:" + w;
							
							logger.debug("Found Placeholder at: {};h:{}", posString, h);
							try {
								data.setTablePos(new TablePos(posString).setHeight(h));
								data.setPlaceholderName(objectName.getName());
								placeholders.add(data);
							} catch (PdfAsException e) {
								throw new IOException(e);
							}
						}
						
					} else {
						logger.debug("Skipping qrcode scanning for image '{}' ({} x {} @ {}/{}) on page #{}.", objectName.getName(),
								pdfImageFrame.getWidth(), pdfImageFrame.getHeight(), pdfImageFrame.getLowerLeftX(), pdfImageFrame.getLowerLeftY(),
								currentPage);
					}

				} catch (NoninvertibleTransformException e) {
					throw new IOException(e);
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
	private static SignaturePlaceholderData detectPlaceholder(PDImageXObject image)
			throws IOException {
		BufferedImage bimg = image.getImage();
		if (bimg == null) {
			String type = image.getSuffix();
			if (type != null) {
				type = type.toUpperCase() + " images";
			} else {
				type = "Image type";
			}
			logger.info("Unable to extract image for QRCode analysis. {} not supported. Add additional JAI Image filters to your classpath. Refer to https://jai.dev.java.net. Skipping image.", type);
			return null;
		}
		
		if (bimg.getHeight() < 10 || bimg.getWidth() < 10) {
			logger.debug("Image too small for QRCode. Skipping image.");
			return null;
		}

		LuminanceSource source = new BufferedImageLuminanceSource(bimg);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		Result result;
		try {
			Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
			List<BarcodeFormat> formats = new ArrayList<>();
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
								logger.debug("Invalid parameter in placeholder data: {}", kvPair);
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
					return new SignaturePlaceholderData(profile, type, sigKey, id);
				} else {
					logger.info("QR-Code found but does not start with \"{}\". Ignoring QR placeholder.", QR_PLACEHOLDER_IDENTIFIER);
				}
			}
		
		} catch (NotFoundException e) {
			// ok: image may not contain qr code
		} catch (Exception re) {
			logger.info("Failed to scan image: {}", String.valueOf(re));
		}
		
		return null;
		
	}

}
