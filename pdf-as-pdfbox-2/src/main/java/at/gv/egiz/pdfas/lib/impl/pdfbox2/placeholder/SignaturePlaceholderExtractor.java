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
import java.util.*;
import java.util.Map.Entry;

import javassist.bytecode.stackmap.TypeData.ClassName;

import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontFactory;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PDFIOException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PlaceholderExtractionException;
import at.gv.egiz.pdfas.lib.impl.placeholder.PlaceholderExtractorConstants;
import at.gv.egiz.pdfas.lib.impl.placeholder.SignaturePlaceholderContext;
import at.gv.egiz.pdfas.lib.impl.placeholder.SignaturePlaceholderData;
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

	private static List<SignaturePlaceholderData> placeholders = new Vector<>();
	private int currentPage = 0;
	private PDDocument doc;
	
	

	private SignaturePlaceholderExtractor(String placeholderId,
			int placeholderMatchMode, PDDocument doc) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		super();
		
		final Properties properties = new Properties();
		properties.load(ClassName.class.getClassLoader().getResourceAsStream("placeholder/pdfbox-reader-2.properties"));
		
		Set<Entry<Object, Object>> entries = properties.entrySet();
		for(Entry<Object, Object> entry:entries){
			String processorClassName = (String)entry.getValue();
			Class<?> klass = Class.forName( processorClassName );
            org.apache.pdfbox.contentstream.operator.OperatorProcessor processor =
                (OperatorProcessor) klass.newInstance();
            
            addOperator( processor );
		}
		this.doc = doc;
	}

	public static List<SignaturePlaceholderData> listPlaceholders() {
		return placeholders;
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
					matchMode, doc);
		} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e2) {
			throw new PDFIOException("error.pdf.io.04", e2);
		}

		int pageNr = 0;
		for(PDPage page : doc.getPages()){
			pageNr++;

			try {
				extractor.setCurrentPage(pageNr);
				if(page.getContents() != null && page.getResources() != null && page.getContentStreams() != null) {
						extractor.processPage(page); //TODO: pdfbox2 - right?
					
				}
				SignaturePlaceholderData ret = matchPlaceholderPage(
						extractor.placeholders, placeholderId, matchMode);
				if (ret != null) {
					SignaturePlaceholderContext
							.setSignaturePlaceholderData(ret);
					return ret;
				}
			} catch (IOException e1) {
				throw new PDFIOException("error.pdf.io.04", e1);
			} catch(Throwable e) {
				throw new PDFIOException("error.pdf.io.04", e);
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
			
			if(currentFirstSpd != null) {
				logger.info("Running Placeholder sorted mode: using id: {}", currentFirstSpd.getId());
				return currentFirstSpd;
			} else {
				logger.info("Running Placeholder sorted mode: no placeholder with id found, fallback to first placeholder");
			}
		}
		
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
		
		if(matchMode == PLACEHOLDER_MATCH_MODE_SORTED)
			return null;
		
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
	protected void processOperator(Operator operator, List<COSBase> arguments)
			throws IOException {
		String operation = operator.getName();
		if (operation.equals("Do")) {
			COSName objectName = (COSName) arguments.get(0);
			PDXObject xobject = (PDXObject) getResources().getXObject(objectName);
			if (xobject instanceof PDImageXObject) {
				try {
					PDImageXObject image = (PDImageXObject) xobject;
					SignaturePlaceholderData data = checkImage(image);
					if (data != null) {
						PDPage page = getCurrentPage();
						Matrix ctm = getGraphicsState()
								.getCurrentTransformationMatrix();
						int pageRotation = page.getRotation();
						pageRotation = pageRotation % 360;
						double rotationInRadians = Math.toRadians(pageRotation);//(page.findRotation() * Math.PI) / 180;

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
						float yPos = unrotatedCTM.getYPosition();
						float yScale = unrotatedCTM.getScaleY();
						float y = yPos + yScale;
						float w = unrotatedCTM.getScaleX();

						logger.debug("Page height: {}", page.getCropBox().getHeight());
						logger.debug("Page width: {}", page.getCropBox().getWidth());
						
						if(pageRotation == 90) {
							y = page.getCropBox().getWidth() - (y * (-1));
						} else if(pageRotation == 180) {
							x = page.getCropBox().getWidth() + x;
							y = page.getCropBox().getHeight() - (y * (-1));
						} else if(pageRotation == 270) {
							x = page.getCropBox().getHeight() + x;
						}
						
						String posString = "p:" + currentPage + ";x:" + x
								+ ";y:" + y + ";w:" + w;

						logger.debug("Found Placeholder at: {}", posString);
						try {
							data.setTablePos(new TablePos(posString));
							data.setPlaceholderName(objectName.getName());
							placeholders.add(data);
						} catch (PdfAsException e) {
							throw new IOException();
						}
					}
				} catch (NoninvertibleTransformException e) {
					throw new IOException(e);
				}
			}
		} else {
			super.processOperator(operator, arguments);
		}
	}

	private  Map<String, PDFont> fonts;
	
	//TODO: pdfbox2 - was override
	public Map<String, PDFont> getFonts() {
		if (fonts == null) {
            // at least an empty map will be returned
            // TODO we should return null instead of an empty map
            fonts = new HashMap<String, PDFont>();
            if(this.getResources() != null && this.getResources().getCOSObject() != null) {
            COSDictionary fontsDictionary = (COSDictionary) this.getResources().getCOSObject().getDictionaryObject(COSName.FONT);
            if (fontsDictionary == null) {
            	// ignore we do not want to set anything, never when creating a signature!!!!!
                //fontsDictionary = new COSDictionary();
                //this.getResources().getCOSDictionary().setItem(COSName.FONT, fontsDictionary);
            }
            else {
                for (COSName fontName : fontsDictionary.keySet()) {
                    COSBase font = fontsDictionary.getDictionaryObject(fontName);
                    // data-000174.pdf contains a font that is a COSArray, looks to be an error in the
                    // PDF, we will just ignore entries that are not dictionaries.
                    if (font instanceof COSDictionary) {
                        PDFont newFont = null;
                        try {
                            newFont = PDFontFactory.createFont((COSDictionary) font);
                        }
                        catch (IOException exception) {
                            logger.error("error while creating a font", exception);
                        }
                        if (newFont != null) {
                            fonts.put(fontName.getName(), newFont);
                        }
                    }
                }
            }
            }
        }
        return fonts;
	}

	/**
	 * Checks an image if it is a placeholder for a signature.
	 *
	 * @param image
	 * @return
	 * @throws IOException
	 */
	private SignaturePlaceholderData checkImage(PDImageXObject image)
			throws IOException {
		BufferedImage bimg = image.getImage();
		if (bimg == null) {
			String type = image.getSuffix();
			if (type != null) {
				type = type.toUpperCase() + " images";
			} else {
				type = "Image type";
			}
			logger.info("Unable to extract image for QRCode analysis. "
					+ type
					+ " not supported. Add additional JAI Image filters to your classpath. Refer to https://jai.dev.java.net. Skipping image.");
			return null;
		}
		if (bimg.getHeight() < 10 || bimg.getWidth() < 10) {
			logger.debug("Image too small for QRCode. Skipping image.");
			return null;
		}

		LuminanceSource source = new BufferedImageLuminanceSource(bimg);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		Result result;
		long before = System.currentTimeMillis();
		try {
			Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
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
								logger.debug("Invalid parameter in placeholder data: "
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
					logger.warn("QR-Code found but does not start with \""
							+ QR_PLACEHOLDER_IDENTIFIER
							+ "\". Ignoring QR placeholder.");
				}
			}
		} catch (ReaderException re) {
			if (logger.isDebugEnabled()) {
				logger.debug("Could not decode - not a placeholder. needed: "
						+ (System.currentTimeMillis() - before));
			}
			if (!(re instanceof NotFoundException)) {
				if (logger.isInfoEnabled()) {
					logger.info("Failed to decode image", re);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			if (logger.isInfoEnabled()) {
				logger.info("Failed to decode image. Probably a zxing bug", e);
			}
		}
		return null;
	}
}
