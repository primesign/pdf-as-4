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
package at.gv.egiz.sl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PDFIOException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.SLPdfAsException;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.sl.CreateCMSSignatureRequestType;
import at.gv.egiz.sl.CreateCMSSignatureResponseType;
import at.gv.egiz.sl.ErrorResponseType;
import at.gv.egiz.sl.InfoboxReadRequestType;
import at.gv.egiz.sl.InfoboxReadResponseType;

public class BKUSLConnector extends BaseSLConnector {

	private static final Logger logger = LoggerFactory
			.getLogger(BKUSLConnector.class);

	public static final String CONFIG_BKU_URL = "bku.sign.url";

	private String bkuUrl;

	public BKUSLConnector(Configuration config) {
		this.bkuUrl = config.getValue(CONFIG_BKU_URL);
	}

	private CloseableHttpClient buildHttpClient() {
		HttpClientBuilder builder = HttpClientBuilder.create();
		return builder.build();
	}

	private String performHttpRequestToBKU(String xmlRequest)
			throws ClientProtocolException, IOException, IllegalStateException {
		CloseableHttpClient client = null;
		try {
			client = buildHttpClient();
			HttpPost post = new HttpPost(this.bkuUrl);

			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder
					.create();
			entityBuilder.addTextBody(XMLREQUEST, xmlRequest,
					ContentType.TEXT_XML);

			post.setEntity(entityBuilder.build());

			HttpResponse response = client.execute(post);
			logger.debug("Response Code : "
					+ response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			logger.trace(result.toString());
			return result.toString();
		} finally {
			if(client != null) {
				client.close();
			}
		}
	}

	public InfoboxReadResponseType sendInfoboxReadRequest(
			InfoboxReadRequestType request) throws PdfAsException {
		JAXBElement<?> element = null;
		String slRequest;
		try {
			slRequest = SLMarschaller.marshalToString(of
					.createInfoboxReadRequest(request));
			logger.trace(slRequest);

			String slResponse = performHttpRequestToBKU(slRequest);

			element = (JAXBElement<?>) SLMarschaller
					.unmarshalFromString(slResponse);

		} catch (JAXBException e) {
			throw new PDFIOException("error.pdf.io.03", e);
		} catch (ClientProtocolException e) {
			throw new PDFIOException("error.pdf.io.03", e);
		} catch (IOException e) {
			throw new PDFIOException("error.pdf.io.03", e);
		}

		if (element == null) {
			throw new PDFIOException("error.pdf.io.04");
		}

		if (element.getValue() instanceof InfoboxReadResponseType) {
			InfoboxReadResponseType infoboxReadResponseType = (InfoboxReadResponseType) element
					.getValue();
			return infoboxReadResponseType;
		} else if (element.getValue() instanceof ErrorResponseType) {
			ErrorResponseType errorResponseType = (ErrorResponseType) element
					.getValue();
			throw new SLPdfAsException(errorResponseType.getErrorCode(),
					errorResponseType.getInfo());
		}
		throw new PdfAsException("error.pdf.io.03");
	}

	public CreateCMSSignatureResponseType sendCMSRequest(
			CreateCMSSignatureRequestType request) throws PdfAsException {
		JAXBElement<?> element = null;
		String slRequest;
		try {
			slRequest = SLMarschaller.marshalToString(of
					.createCreateCMSSignatureRequest(request));
			logger.debug(slRequest);

			String slResponse = performHttpRequestToBKU(slRequest);

			element = (JAXBElement<?>) SLMarschaller
					.unmarshalFromString(slResponse);
		} catch (JAXBException e) {
			throw new PDFIOException("error.pdf.io.03", e);
		} catch (ClientProtocolException e) {
			throw new PDFIOException("error.pdf.io.03", e);
		} catch (IOException e) {
			throw new PDFIOException("error.pdf.io.03", e);
		}

		if (element == null) {
			throw new PDFIOException("error.pdf.io.05");
		}

		if (element.getValue() instanceof CreateCMSSignatureResponseType) {
			CreateCMSSignatureResponseType createCMSSignatureResponseType = (CreateCMSSignatureResponseType) element
					.getValue();
			logger.debug(createCMSSignatureResponseType.toString());
			return createCMSSignatureResponseType;
		} else if (element.getValue() instanceof ErrorResponseType) {
			ErrorResponseType errorResponseType = (ErrorResponseType) element
					.getValue();
			throw new SLPdfAsException(errorResponseType.getErrorCode(),
					errorResponseType.getInfo());
		}
		throw new PdfAsException("error.pdf.io.03");

	}
}
