package at.gv.egiz.pdfas.lib.impl.verify;

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

	public static final String CONF_VERIFIER = "default.verifier";

	public Map<String, HashMap<String, IVerifyFilter>> filterMap = new HashMap<String, HashMap<String, IVerifyFilter>>();

	public VerifierDispatcher(ISettings settings) {
		// TODO: add configuration parameter to set verifier

		Map<String, String> verifierClasses = settings
				.getValuesPrefix(CONF_VERIFIER);
		String[] currentClasses = null;
		if (verifierClasses == null || verifierClasses.isEmpty()) {
			logger.info("No verifier configured using default");
			currentClasses = defaultClasses;
		} else {
			currentClasses = new String[verifierClasses.values().size()];
			Iterator<String> classIt = verifierClasses.values().iterator();
			int j = 0;
			while (classIt.hasNext()) {
				currentClasses[j] = classIt.next();
				j++;
			}
		}
		try {
			for (int i = 0; i < currentClasses.length; i++) {
				String clsName = currentClasses[i];
				Class<? extends IVerifyFilter> cls = (Class<? extends IVerifyFilter>) Class
						.forName(clsName);
				IVerifyFilter filter = cls.newInstance();
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
