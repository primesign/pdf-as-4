package at.gv.egiz.pdfas.web.helper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class UrlParameterExtractor {

	public static Map<String, String> splitQuery(URL url)
			throws UnsupportedEncodingException {
		Map<String, String> query_pairs = new LinkedHashMap<String, String>();
		String query = url.getQuery();
		if (query != null) {
			String[] pairs = query.split("&");
			for (String pair : pairs) {
				int idx = pair.indexOf("=");
				query_pairs.put(
						URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
						URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
			}
		}
		return query_pairs;
	}

	public static String buildParameterFormString(URL url) throws IOException {
		Map<String, String> query_pairs = splitQuery(url);
		Iterator<Entry<String, String>> entryIt = query_pairs.entrySet()
				.iterator();

		if (query_pairs.isEmpty()) {
			return "";
		}
		String genericTemplate = PdfAsHelper.getGenericTemplate();
		StringBuilder sb = new StringBuilder();
		while (entryIt.hasNext()) {
			Entry<String, String> entry = entryIt.next();

			String current = genericTemplate
					.replace("##NAME##", entry.getKey());
			current = current.replace("##VALUE##", entry.getValue());
			sb.append(current);
		}

		return sb.toString();
	}

}
