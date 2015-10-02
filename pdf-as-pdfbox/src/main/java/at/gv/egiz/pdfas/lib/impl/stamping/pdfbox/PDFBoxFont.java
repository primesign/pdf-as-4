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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.fontbox.ttf.NameRecord;
import org.apache.fontbox.ttf.NamingTable;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.impl.pdfbox.PDFBOXObject;

public class PDFBoxFont {

	private static final Logger logger = LoggerFactory
			.getLogger(PDFBoxFont.class);

	private static final String HELVETICA = "HELVETICA";
	private static final String COURIER = "COURIER";
	private static final String TIMES_ROMAN = "TIMES_ROMAN";
	private static final String BOLD = "BOLD";
	private static final String NORMAL = "NORMAL";
	private static final String ITALIC = "ITALIC";
	private static final String SEP = ":";

	public static PDFont defaultFont = PDType1Font.HELVETICA;
	public static float defaultFontSize = 8;

	private static Map<String, PDFont> fontStyleMap = new HashMap<String, PDFont>();

	private static Map<String, FontInfoCache> fontInfoCache = new HashMap<String, FontInfoCache>();

	static {
		fontStyleMap.put(HELVETICA + SEP + NORMAL, PDType1Font.HELVETICA);
		fontStyleMap.put(HELVETICA + SEP + BOLD, PDType1Font.HELVETICA_BOLD);

		fontStyleMap.put(COURIER + SEP + NORMAL, PDType1Font.COURIER);
		fontStyleMap.put(COURIER + SEP + BOLD, PDType1Font.COURIER_BOLD);

		fontStyleMap.put(TIMES_ROMAN + SEP + NORMAL, PDType1Font.TIMES_ROMAN);
		fontStyleMap.put(TIMES_ROMAN + SEP + BOLD, PDType1Font.TIMES_BOLD);
		fontStyleMap.put(TIMES_ROMAN + SEP + ITALIC, PDType1Font.TIMES_ITALIC);
	}

	public static void showBuildinFonts() {
		Iterator<String> it = fontStyleMap.keySet().iterator();
		logger.info("Available Fonts:");
		while (it.hasNext()) {
			logger.info(it.next());
		}
	}

	PDFont font;
	PDFont cachedfont = null;
	float fontSize;
	String fontDesc;
	String ttfFontDesc;
	ISettings settings;

	private FontInfoCache getFontInfo(String pathName) {
		synchronized (fontInfoCache) {

			if (fontInfoCache.containsKey(pathName)) {
				return fontInfoCache.get(pathName);
			} else {
				try {
					String fontNameToLoad = null;
					String fontFamilyToLoad = null;
					InputStream ttfData = new FileInputStream(pathName);
					try {
						TrueTypeFont ttf = null;
						TTFParser parser = new TTFParser();
						ttf = parser.parseTTF(ttfData);
						NamingTable naming = ttf.getNaming();
						List<NameRecord> records = naming.getNameRecords();
						for (int i = 0; i < records.size(); i++) {
							NameRecord nr = records.get(i);
							if (nr.getNameId() == NameRecord.NAME_POSTSCRIPT_NAME) {
								fontNameToLoad = nr.getString();
							} else if (nr.getNameId() == NameRecord.NAME_FONT_FAMILY_NAME) {
								fontFamilyToLoad = nr.getString();
							}
						}
					} finally {
						ttfData.close();
					}
					FontInfoCache fontInfo = new FontInfoCache();
					fontInfo.filename = pathName;
					fontInfo.fontFamily = fontFamilyToLoad;
					fontInfo.fontName = fontNameToLoad;
					fontInfo.fontPath = pathName;
					fontInfoCache.put(pathName, fontInfo);
					return fontInfo;
				} catch (Throwable e) {
					logger.warn("Failed to generate FontInfo from file: {}", pathName);
				}
				return null;
			}
		}
	}

	private PDFont findCachedFont(PDFBOXObject pdfObject, FontInfoCache fontInfo) {
		try {
			if(pdfObject.getFontCache().containsKey(fontInfo.fontPath)) {
				return pdfObject.getFontCache().get(fontInfo.fontPath);
			}
			
			List<COSObject> cosObjects = pdfObject.getDocument().getDocument().getObjectsByType(
					COSName.FONT);

			//COSName cosFontName = COSName.getPDFName(fontInfo.fontName);
			//COSName cosFontFamily = COSName.getPDFName(fontInfo.fontFamily);

			Iterator<COSObject> cosObjectIt = cosObjects.iterator();

			while (cosObjectIt.hasNext()) {
				COSObject cosObject = cosObjectIt.next();
				COSDictionary baseObject = (COSDictionary) cosObject
						.getObject();
				if (baseObject instanceof COSDictionary) {
					COSDictionary fontDictionary = (COSDictionary) baseObject;
					COSBase subType = cosObject.getItem(COSName.SUBTYPE);
					COSDictionary fontDescriptor = (COSDictionary)cosObject.getDictionaryObject(COSName.FONT_DESC);
					String fontName = fontDescriptor.getNameAsString(COSName.FONT_NAME);
					String fontFamily = fontDescriptor.getNameAsString(COSName.FONT_FAMILY);
					logger.debug("Checking Font {} - {}", fontFamily, fontName);
					if (COSName.TRUE_TYPE.equals(subType)) {
						if (fontInfo.fontName != null && fontInfo.fontName.equals(fontName) && 
							fontInfo.fontFamily != null && fontInfo.fontFamily.equals(fontFamily)) {
							// Found it! :)
							logger.info("Found Font {}", fontInfo.fontName);
							return new PDTrueTypeFont(fontDictionary);
						} else {
							logger.debug("Font not found: {} is {}",
									fontInfo.fontName, fontName);
						}
					} else {
						logger.debug("Font not a TTF");
					}
				} else {
					logger.debug("Font not a COSDictionary");
				}
			}
		} catch (Throwable e) {
			logger.info("Failed to find existing TTF fonts!", e);
		}
		return null;
	}

	private PDFont generateTTF(String fonttype, PDFBOXObject pdfObject)
			throws IOException {
		/*boolean cacheNow = true;
		if (pdfObject == null) {
			if (this.doc == null) {
				this.doc = new PDDocument();
			}
			doc = this.doc;
		} else {
			cacheNow = true;
		}*/
		ttfFontDesc = fonttype;
		String fontName = fonttype.replaceFirst("TTF:", "");
		String fontPath = this.settings.getWorkingDirectory() + File.separator
				+ "fonts" + File.separator + fontName;
		
		logger.debug("Font from: \"" + fontPath + "\".");

		if(fontStyleMap.containsKey(fontPath)) {
			return fontStyleMap.get(fontPath);
		}
		
		FontInfoCache fontInfo = getFontInfo(fontPath);
		
		if(fontInfo != null) {
		
			PDFont font = findCachedFont(pdfObject, fontInfo);

			if (font != null) {
				return font;
			}
		} 
		
		logger.debug("Instantiating font.");
		
		//if (cacheNow) {
			cachedfont = PDTrueTypeFont.loadTTF(pdfObject.getDocument(), fontPath);
			fontStyleMap.put(fontPath, cachedfont);
			return cachedfont;
		//} else {
		//	return PDTrueTypeFont.loadTTF(doc, fontPath);
		//}

	}

	private PDFont generateFont(String fonttype, String fontder,
			PDFBOXObject pdfObject) throws IOException {
		if (fonttype.startsWith("TTF:")) {
			// Load TTF Font
			return generateTTF(fonttype, pdfObject);
		} else {
			if (fontder == null) {
				fontder = NORMAL;
			}

			String fontDesc = fonttype + SEP + fontder;
			PDFont font = fontStyleMap.get(fontDesc);
			if (font == null) {
				showBuildinFonts();
				throw new IOException("Invalid font descriptor");
			}
			return font;
		}
	}

	private void setFont(String desc, PDFBOXObject pdfObject)
			throws IOException {
		String[] fontArr = desc.split(",");

		if (fontArr.length == 3) {
			font = generateFont(fontArr[0], fontArr[2], pdfObject);
			fontSize = Float.parseFloat(fontArr[1]);
		} else if (fontArr.length == 2 && fontArr[0].startsWith("TTF:")) {
			font = generateFont(fontArr[0], null, pdfObject);
			fontSize = Float.parseFloat(fontArr[1]);
		} else {
			logger.warn(
					"Using default font because: {} is not a valid font descriptor.",
					desc);
			this.font = defaultFont;
			this.fontSize = defaultFontSize;
		}

	}

	public PDFBoxFont(String fontDesc, ISettings settings,
			PDFBOXObject pdfObject) throws IOException {
		this.settings = settings;
		this.fontDesc = fontDesc;
		logger.debug("Creating Font: " + fontDesc);
		this.setFont(fontDesc, pdfObject);
	}

	public PDFont getFont(/*PDFBOXObject pdfObject*/) throws IOException {
		if (cachedfont != null) {
			return cachedfont;
		}
		return font;
		/*
		if (font instanceof PDTrueTypeFont && pdfObject != null) {
			return generateTTF(ttfFontDesc, pdfObject);
		} else {
			return font;
		}*/
	}

	public float getFontSize() {
		return fontSize;
	}
}
