package at.gv.egiz.pdfas.web.store;

import java.util.HashMap;
import java.util.UUID;

import at.gv.egiz.pdfas.api.ws.PDFASSignRequest;

public class InMemoryRequestStore implements IRequestStore {

	public InMemoryRequestStore() {
	}
	
	private HashMap<String, PDFASSignRequest> store = new HashMap<String, PDFASSignRequest>();
	
	public String createNewStoreEntry(PDFASSignRequest request) {
		UUID id = UUID.randomUUID();
		String sid = id.toString();
		this.store.put(sid, request);
		return sid;
	}

	public PDFASSignRequest fetchStoreEntry(String id) {
		if(store.containsKey(id)) {
			PDFASSignRequest request = store.get(id);
			store.remove(id);
			return request;
		}
		return null;
	}

}
