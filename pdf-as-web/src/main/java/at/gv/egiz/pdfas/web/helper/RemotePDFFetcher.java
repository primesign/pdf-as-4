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
package at.gv.egiz.pdfas.web.helper;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.exception.PdfAsWebException;

public class RemotePDFFetcher {

	private static final Logger logger = LoggerFactory
			.getLogger(RemotePDFFetcher.class);
	
	public static byte[] fetchPdfFile(String pdfURL) throws PdfAsWebException {
		URL url;
		try {
			url = new URL(pdfURL);
		} catch (MalformedURLException e) {
			logger.error("Not a valid URL!", e);
			throw new PdfAsWebException("Not a valid URL!", e);
		}
		if (WebConfiguration.isProvidePdfURLinWhitelist(url.toExternalForm())) {
			if (url.getProtocol().equals("http")
					|| url.getProtocol().equals("https")) {

				try {
					InputStream is = url.openStream();
					return StreamUtils.inputStreamToByteArray(is);
				} catch (Exception e) {
					logger.error("Failed to fetch pdf document!", e);
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
