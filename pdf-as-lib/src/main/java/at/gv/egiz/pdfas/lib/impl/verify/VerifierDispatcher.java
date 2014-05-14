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
package at.gv.egiz.pdfas.lib.impl.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.api.Configuration;

public class VerifierDispatcher {

	private static final Logger logger = LoggerFactory
			.getLogger(VerifierDispatcher.class);

	public static final String[] defaultClasses = new String[] {
			"at.gv.egiz.pdfas.sigs.pkcs7detached.PKCS7DetachedVerifier",
			"at.gv.egiz.pdfas.sigs.pades.PAdESVerifier" };

	public static final String CONF_VERIFIER_LIST = "verifier.classes";
	public static final String CONF_VERIFIER_SEP = ";";

	public static final String CONF_VERIFIER = "default.verifier";

	public Map<String, HashMap<String, IVerifyFilter>> filterMap = new HashMap<String, HashMap<String, IVerifyFilter>>();

	private String[] getClasses(ISettings settings) {
		String confVerifiers = settings.getValue(CONF_VERIFIER_LIST);
		String[] classes;
		if (confVerifiers != null) {
			classes = confVerifiers.split(CONF_VERIFIER_SEP);
		} else {
			classes = defaultClasses;
		}
		
		List<String> filteredClasses = new ArrayList<String>();

		for (int i = 0; i < classes.length; i++) {
			String clsName = classes[i];
			try {
				Class<?> cls = Class.forName(clsName);
				filteredClasses.add(clsName);
			} catch (Throwable e) {
				logger.error("Cannot find Verifier class: " + clsName, e);
			}
		}
		
		String[] clsNames = new String[filteredClasses.size()];
		for (int i = 0; i < filteredClasses.size(); i++) {
			clsNames[i] = filteredClasses.get(i);
		}
		
		dumpVerifierClasses(clsNames);
		
		return clsNames;
	}
	
	private void dumpVerifierClasses(String[] clsNames) {
		for (int i = 0; i < clsNames.length; i++) {
			String clsName = clsNames[i];
			logger.debug("Registering Signature Verifier: " + clsName);
		}
	}

	public VerifierDispatcher(ISettings settings) {
		// TODO: add configuration parameter to set verifier

		//Map<String, String> verifierClasses = settings
		//		.getValuesPrefix(CONF_VERIFIER);
		String[] currentClasses = null;
		//if (verifierClasses == null || verifierClasses.isEmpty()) {
		logger.info("Getting Verifier classes");
		currentClasses = getClasses(settings);
		/*} else {
			currentClasses = new String[verifierClasses.values().size()];
			Iterator<String> classIt = verifierClasses.values().iterator();
			int j = 0;
			while (classIt.hasNext()) {
				currentClasses[j] = classIt.next();
				j++;
			}
		}*/
		try {
			for (int i = 0; i < currentClasses.length; i++) {
				String clsName = currentClasses[i];
				Class<?> cls = Class.forName(clsName);
				Object f = cls.newInstance();
				if (!(f instanceof IVerifyFilter))
					throw new ClassCastException();
				IVerifyFilter filter = (IVerifyFilter) f;
				filter.setConfiguration((Configuration) settings);
				List<FilterEntry> entries = filter.getFiters();
				Iterator<FilterEntry> it = entries.iterator();
				while (it.hasNext()) {
					FilterEntry entry = it.next();
					HashMap<String, IVerifyFilter> filters = filterMap
							.get(entry.getFilter().getName());
					if (filters == null) {
						filters = new HashMap<String, IVerifyFilter>();
						filterMap.put(entry.getFilter().getName(), filters);
					}

					IVerifyFilter oldFilter = filters.get(entry.getSubFilter()
							.getName());

					if (oldFilter != null) {
						throw new PdfAsException("Filter allready registered");
					}

					filters.put(entry.getSubFilter().getName(), filter);
					logger.debug("Registered Filter: " + cls.getName()
							+ " for " + entry.getFilter().getName() + "/"
							+ entry.getSubFilter().getName());
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	public IVerifyFilter getVerifier(String filter, String subfilter) {
		HashMap<String, IVerifyFilter> filters = filterMap.get(filter);
		if (filters == null) {
			return null;
		}

		return filters.get(subfilter);
	}
}
