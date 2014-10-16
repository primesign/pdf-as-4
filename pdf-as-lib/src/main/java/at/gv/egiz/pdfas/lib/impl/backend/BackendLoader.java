package at.gv.egiz.pdfas.lib.impl.backend;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.ErrorConstants;
import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.backend.PDFASBackend;

/**
 * The Class BackendLoader.
 */
public class BackendLoader implements ErrorConstants {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory
			.getLogger(BackendLoader.class);
	
	/** The pdf as backend loader. */
	private static ServiceLoader<PDFASBackend> pdfAsBackendLoader = ServiceLoader.load(PDFASBackend.class);
	
	/** The available backends. */
	private static Map<String, PDFASBackend> availableBackends = new HashMap<String, PDFASBackend>();
	
	/** The Constant BACKEND_CONFIG. */
	public static final String BACKEND_CONFIG = "runtime.backend";
	
	/** The default backend. */
	private static PDFASBackend defaultBackend = null; 
	
	static {
		logger.debug("building PDF-AS Backends");
		
		Iterator<PDFASBackend> backendIterator = pdfAsBackendLoader.iterator();
		
		while(backendIterator.hasNext()) {
			PDFASBackend backend = backendIterator.next();
			logger.debug("Loading " + backend.getName() + " [" + backend.getClass().getName() + "]");
			availableBackends.put(backend.getName(), backend);
			logger.debug("PDF-Backend added " + backend.getName());
			if(backend.usedAsDefault()) {
				defaultBackend = backend;
				logger.debug("PDF-Backend added as default " + backend.getName());
			}
		}
		
		logger.debug("PDF-AS Backends constructed");
		
		if(defaultBackend != null) {
			logger.debug("Default backend is " + defaultBackend.getName());
		}
	}
	
	/**
	 * Gets the PDFAS backend.
	 *
	 * @param configuration the configuration
	 * @return the PDFAS backend
	 * @throws PDFASError the PDFAS error
	 */
	public static PDFASBackend getPDFASBackend(Configuration configuration) throws PDFASError {
		String backendName = configuration.getValue(BACKEND_CONFIG);
		return getPDFASBackend(backendName);
	}
	
	/**
	 * Gets the PDFAS backend.
	 *
	 * @param name the name
	 * @return the PDFAS backend
	 * @throws PDFASError the PDFAS error
	 */
	public static PDFASBackend getPDFASBackend(String name) throws PDFASError {
		if(name != null) {
			if(availableBackends.containsKey(name)) {
				return availableBackends.get(name);
			}
			throw new PDFASError(ERROR_NO_BACKEND);
		}
		
		if(defaultBackend != null) {
			return defaultBackend;
		}
		
		throw new PDFASError(ERROR_NO_BACKEND);
	}
	
	/**
	 * Gets the PDFAS backend.
	 *
	 * @return the PDFAS backend
	 * @throws PDFASError the PDFAS error
	 */
	public static PDFASBackend getPDFASBackend() throws PDFASError {
		if(defaultBackend != null) {
			return defaultBackend;
		}
		
		throw new PDFASError(ERROR_NO_BACKEND);
	}
}
