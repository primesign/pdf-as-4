package at.gv.egiz.pdfas.lib.impl.status;

public class PDFObject {
	
	private OperationStatus status;
	
	private byte[] originalDocument;
	private byte[] stampedDocument;
	private byte[] signedDocument;

	public PDFObject(OperationStatus operationStatus) {
		this.status = operationStatus;
	}

	public byte[] getOriginalDocument() {
		return originalDocument;
	}

	public void setOriginalDocument(byte[] originalDocument) {
		this.originalDocument = originalDocument;
	}

	public byte[] getStampedDocument() {
		return stampedDocument;
	}

	public void setStampedDocument(byte[] stampedDocument) {
		this.stampedDocument = stampedDocument;
	}

	public byte[] getSignedDocument() {
		return signedDocument;
	}

	public void setSignedDocument(byte[] signedDocument) {
		this.signedDocument = signedDocument;
	}

	public OperationStatus getStatus() {
		return status;
	}

	public void setStatus(OperationStatus status) {
		this.status = status;
	}
}
