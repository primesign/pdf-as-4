package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.settings.ISettings;

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
	
	static {
		fontStyleMap.put(HELVETICA+SEP+NORMAL, PDType1Font.HELVETICA);
		fontStyleMap.put(HELVETICA+SEP+BOLD, PDType1Font.HELVETICA_BOLD);
		
		fontStyleMap.put(COURIER+SEP+NORMAL, PDType1Font.COURIER);
		fontStyleMap.put(COURIER+SEP+BOLD, PDType1Font.COURIER_BOLD);
		
		fontStyleMap.put(TIMES_ROMAN+SEP+NORMAL, PDType1Font.TIMES_ROMAN);
		fontStyleMap.put(TIMES_ROMAN+SEP+BOLD, PDType1Font.TIMES_BOLD);
		fontStyleMap.put(TIMES_ROMAN+SEP+ITALIC, PDType1Font.TIMES_ITALIC);
	}
	
	public static void showBuildinFonts() {
		Iterator<String> it = fontStyleMap.keySet().iterator();
		logger.info("Available Fonts:");
		while(it.hasNext()) {
			logger.info(it.next());
		}
	}
	
	PDFont font;
	PDFont cachedfont = null;
	float fontSize;
	String fontDesc;
	String ttfFontDesc;
	PDDocument doc;
	ISettings settings;
	
	private PDFont generateTTF(String fonttype, PDDocument doc) throws IOException {
		boolean cacheNow = false;
		if(doc == null) {
			if(this.doc == null) {
				this.doc = new PDDocument();
			}
			doc = this.doc;
		} else {
			cacheNow = true;
		}
		ttfFontDesc = fonttype;
		String fontName = fonttype.replaceFirst("TTF:", "");
		
		logger.debug("Instantiating font.");
        String fontPath = this.settings.getWorkingDirectory()  + File.separator + "fonts" + File.separator + fontName;
        logger.debug("Instantiating \"" + fontPath + "\".");

        if(cacheNow) {
        	cachedfont = PDTrueTypeFont.loadTTF(doc, fontPath);
        	return cachedfont;
        } else {
        	return PDTrueTypeFont.loadTTF(doc, fontPath);
        }
	}
	
	private PDFont generateFont(String fonttype, String fontder) throws IOException {
		if(fonttype.startsWith("TTF:")) {
			// Load TTF Font
			return generateTTF(fonttype, null);
		} else {
			if(fontder == null) {
				fontder = NORMAL;
			}
			
			String fontDesc = fonttype + SEP + fontder;
			PDFont font = fontStyleMap.get(fontDesc);
			if(font == null) {
				showBuildinFonts();
				throw new IOException("Invalid font descriptor");
			}
			return font;
		}
	}
	
	private void setFont(String desc) throws IOException {
		String[] fontArr = desc.split(",");
		
		if(fontArr.length == 3) {
			font = generateFont(fontArr[0], fontArr[2]);
			fontSize = Float.parseFloat(fontArr[1]);
		} else if(fontArr.length == 2 && fontArr[0].startsWith("TTF:")) {
			font = generateFont(fontArr[0], null);
			fontSize = Float.parseFloat(fontArr[1]);
		} else {
			logger.warn("Using default font because: {} is not a valid font descriptor.", desc);
			this.font = defaultFont;
			this.fontSize = defaultFontSize;
		}
		
	}

	public PDFBoxFont(String fontDesc, ISettings settings) throws IOException {
		this.settings = settings;
		this.fontDesc = fontDesc;
		logger.debug("Creating Font: " + fontDesc);
		this.setFont(fontDesc);
	}
	
	public PDFont getFont(PDDocument doc) throws IOException {
		if(cachedfont != null) {
			return cachedfont;
		}
		if(font instanceof PDTrueTypeFont && doc != null) {
			return generateTTF(ttfFontDesc, doc);
		} else {
			return font;
		}
	}
	
	public float getFontSize() {
		return fontSize;
	}
}
