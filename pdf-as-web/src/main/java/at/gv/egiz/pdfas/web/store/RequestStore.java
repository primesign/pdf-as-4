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
