package at.gv.egiz.pdfas.web.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.exception.PdfAsStoreException;

public class RequestStore {
	private static IRequestStore instance = null;

	private static final Logger logger = LoggerFactory
			.getLogger(RequestStore.class);

	public synchronized static IRequestStore getInstance() throws PdfAsStoreException {
		if (instance == null) {
			try {
				String storeClass = WebConfiguration.getStoreClass();
				logger.info("Using Request Store: " + storeClass);

				Class<?> clazz = Class.forName(storeClass);
				Object store = clazz.newInstance();
				if(store instanceof IRequestStore) {
					instance = (IRequestStore)store;
				} else {
					throw new PdfAsStoreException("Failed to instanciate Request Store from " + storeClass);
				}
			} catch (Throwable e) {
				e.printStackTrace();
				throw new PdfAsStoreException("Failed to instanciate Request Store", e);
			}
		}
		return instance;
	}
}
