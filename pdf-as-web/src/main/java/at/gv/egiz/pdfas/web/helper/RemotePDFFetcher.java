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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.exception.PdfAsWebException;
import iaik.utils.URLDecoder;

public class RemotePDFFetcher {

	private static final Logger logger = LoggerFactory.getLogger(RemotePDFFetcher.class);

	public static String[] extractSensitiveInformationFromURL(String pdfURL) throws IOException {
		if (pdfURL.contains("@")) {
			String lowerURL = pdfURL.toLowerCase();
			int startIndex = 0;
			int atIndex = pdfURL.indexOf("@");

			startIndex = lowerURL.indexOf("https://");

			if (startIndex >= 0) {
				startIndex = startIndex + "https://".length();
			} else {
				startIndex = lowerURL.indexOf("http://");
				if (startIndex >= 0) {
					startIndex = startIndex + "http://".length();
				}
			}

			if (startIndex < 0) {
				throw new MalformedURLException("Username/Password Part found, but no scheme found");
			}

			if (atIndex < 0) {
				throw new MalformedURLException("@ Part found, but index not found");
			}

			String usernamePasswordPart = pdfURL.substring(startIndex, atIndex);
			
			pdfURL = pdfURL.substring(0, startIndex) + pdfURL.substring(atIndex + 1);
			
			logger.debug("Modified URL: {}", pdfURL);
			
			String[] usernamePassword = usernamePasswordPart.split(":");
			
			if(usernamePassword.length == 2) {
				return new String[] { pdfURL, URLDecoder.decode(usernamePassword[0]), 
						URLDecoder.decode(usernamePassword[1]) };
			} else {
				throw new MalformedURLException("Wrong or empty username/password part");
			}
		} else {
			return new String[] { pdfURL };
		}
	}
	
	public static byte[] fetchPdfFile(String pdfURL) throws PdfAsWebException {
		URL url;
		String[] fetchInfos;
		try {
			fetchInfos = extractSensitiveInformationFromURL(pdfURL);
			url = new URL(fetchInfos[0]);
		} catch (MalformedURLException e) {
			logger.warn("Not a valid URL!", e);
			throw new PdfAsWebException("Not a valid URL!", e);
		} catch (IOException e) {
			logger.warn("Not a valid URL!", e);
			throw new PdfAsWebException("Not a valid URL!", e);
		}
		if (WebConfiguration.isProvidePdfURLinWhitelist(url.toExternalForm())) {
			if (url.getProtocol().equals("http") || url.getProtocol().equals("https")) {
				URLConnection uc = null;
				InputStream is = null;
				try {
					uc = url.openConnection();
					
					if(fetchInfos.length == 3) {
					    String userpass = fetchInfos[1] + ":" + fetchInfos[2];
					    String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
					    uc.setRequestProperty("Authorization", basicAuth);
					}
					
					is = uc.getInputStream();
					return StreamUtils.inputStreamToByteArray(is);
				} catch (Exception e) {
					logger.warn("Failed to fetch pdf document!", e);
					throw new PdfAsWebException("Failed to fetch pdf document!", e);
				} finally {
					IOUtils.closeQuietly(is);
				}
			} else {
				throw new PdfAsWebException(
						"Failed to fetch pdf document protocol " + url.getProtocol() + " is not supported");
			}
		} else {
			throw new PdfAsWebException("Failed to fetch pdf document " + url.toExternalForm() + " is not allowed");
		}
	}
}
