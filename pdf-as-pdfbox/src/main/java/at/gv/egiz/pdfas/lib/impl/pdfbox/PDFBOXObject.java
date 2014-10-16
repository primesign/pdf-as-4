package at.gv.egiz.pdfas.lib.impl.pdfbox;

import java.io.IOException;

import javax.activation.DataSource;

import org.apache.pdfbox.pdmodel.PDDocument;

import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;

public class PDFBOXObject extends PDFObject {

	private PDDocument doc;
	
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
	
	public void setOriginalDocument(DataSource originalDocument) throws IOException {
		this.originalDocument = originalDocument;
		if(doc != null) {
			doc.close();
		}
		this.doc = PDDocument.load(this.originalDocument.getInputStream());
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
