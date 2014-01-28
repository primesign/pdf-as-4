package at.gv.egiz.pdfas.web.helper;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.exception.PdfAsWebException;

public class RemotePDFFetcher {

	public static byte[] fetchPdfFile(String pdfURL) throws PdfAsWebException {
		URL url;
		try {
			url = new URL(pdfURL);
		} catch (MalformedURLException e) {
			throw new PdfAsWebException("Not a valid URL!", e);
		}
		if (WebConfiguration.isProvidePdfURLinWhitelist(url.toExternalForm())) {
			if (url.getProtocol().equals("http")
					|| url.getProtocol().equals("https")) {

				try {
					InputStream is = url.openStream();
					return StreamUtils.inputStreamToByteArray(is);
				} catch (Exception e) {
					throw new PdfAsWebException(
							"Failed to fetch pdf document!", e);
				}
			} else {
				throw new PdfAsWebException(
						"Failed to fetch pdf document protocol "
								+ url.getProtocol() + " is not supported");
			}
		} else {
			throw new PdfAsWebException(
					"Failed to fetch pdf document " + url.toExternalForm() + " is not allowed");
		}
	}
}
