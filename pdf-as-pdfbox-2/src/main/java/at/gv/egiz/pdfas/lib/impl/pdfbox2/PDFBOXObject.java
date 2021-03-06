package at.gv.egiz.pdfas.lib.impl.pdfbox2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataSource;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;

import at.gv.egiz.pdfas.lib.impl.stamping.pdfbox2.PDFAsFontCache;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;

public class PDFBOXObject extends PDFObject {

	private PDDocument doc;
	
	private Map<String, PDFont> fontCache = new HashMap<String, PDFont>();
	
	private PDFAsFontCache sigBlockFontCache = new PDFAsFontCache();
	
	public PDFAsFontCache getSigBlockFontCache() {
		return sigBlockFontCache;
	}

	public void setSigBlockFontCache(PDFAsFontCache sigBlockFontCache) {
		this.sigBlockFontCache = sigBlockFontCache;
	}

	public PDFBOXObject(OperationStatus operationStatus) {
		super(operationStatus);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if(doc != null) {
			doc.close();
		}
	}
	
	public void close() {
		if(doc != null) {
			try {
				doc.close();
				//System.gc();
			} catch(Throwable e) {
				// ignore!
			}
			doc = null;
		}
	}

	private MemoryUsageSetting getMemoryUsageSettings() {
		// TODO: allow fine tuning of memory usage (divided main memory vs file memory)
		return MemoryUsageSetting.setupMainMemoryOnly();
	}

	public void setOriginalDocument(DataSource originalDocument) throws IOException {
		this.originalDocument = originalDocument;
		if(doc != null) {
			doc.close();
		}
		synchronized(PDDocument.class) {
			this.doc = PDDocument.load(this.originalDocument.getInputStream());
		}
		if(this.doc != null) {
			this.doc.getDocument().setWarnMissingClose(false);
		}
	}
	
	public PDDocument getDocument() {
		return this.doc;
	}

	@Override
	public String getPDFVersion() {
		return String.valueOf(getDocument().getDocument().getVersion());
	}

}
