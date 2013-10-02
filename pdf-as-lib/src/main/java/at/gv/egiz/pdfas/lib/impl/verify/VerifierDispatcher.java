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

public class VerifierDispatcher {
	
	private static final Logger logger = LoggerFactory.getLogger(VerifierDispatcher.class);
	
	public static final String currentClass = "at.gv.egiz.pdfas.sigs.pkcs7detached.PKCS7DetachedVerifier";
	
	public Map<String, HashMap<String, IVerifyFilter>> filterMap = new HashMap<String, HashMap<String, IVerifyFilter>>();
	
	public VerifierDispatcher(ISettings settings) {
		// TODO: read config build verify filter
		try {
			Class<? extends IVerifyFilter> cls = (Class<? extends IVerifyFilter>) Class.forName(currentClass);
			IVerifyFilter fitler = cls.newInstance();
			List<FilterEntry> entries = fitler.getFiters();
			Iterator<FilterEntry> it = entries.iterator();
			while(it.hasNext()) {
				FilterEntry entry = it.next();
				HashMap<String, IVerifyFilter> filters = filterMap.get(entry.getFilter().getName());
				if(filters == null) {
					filters = new HashMap<String, IVerifyFilter>();
					filterMap.put(entry.getFilter().getName(), filters);
				}
				
				IVerifyFilter oldFilter = filters.get(entry.getSubFilter().getName());
				
				if(oldFilter != null) {
					throw new PdfAsException("Filter allready registered");
				}
				
				filters.put(entry.getSubFilter().getName(), fitler);
				logger.debug("Registered Filter: " + cls.getName() + " for " + entry.getFilter().getName() + "/" + entry.getSubFilter().getName());
			}
		} catch(Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	public IVerifyFilter getVerifier(String filter, String subfilter) {
		HashMap<String, IVerifyFilter> filters = filterMap.get(filter);
		if(filters == null) {
			return null;
		}
		
		return filters.get(subfilter);
	}
}
