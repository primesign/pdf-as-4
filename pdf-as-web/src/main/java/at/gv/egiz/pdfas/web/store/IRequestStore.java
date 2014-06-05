package at.gv.egiz.pdfas.web.store;

import at.gv.egiz.pdfas.api.ws.PDFASSignRequest;

public interface IRequestStore {
	public String createNewStoreEntry(PDFASSignRequest request);
	public PDFASSignRequest fetchStoreEntry(String id);
}
