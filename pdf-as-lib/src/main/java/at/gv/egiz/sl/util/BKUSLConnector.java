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
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.ErrorConstants;
import at.gv.egiz.pdfas.common.exceptions.PDFIOException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsWrappedIOException;
import at.gv.egiz.pdfas.common.exceptions.SLPdfAsException;
import at.gv.egiz.pdfas.common.utils.PDFUtils;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.impl.BKUHeaderHolder;
import at.gv.egiz.sl.schema.CreateCMSSignatureResponseType;
import at.gv.egiz.sl.schema.ErrorResponseType;
import at.gv.egiz.sl.schema.InfoboxReadRequestType;
import at.gv.egiz.sl.schema.InfoboxReadResponseType;

public class BKUSLConnector extends BaseSLConnector {

	private static final Logger logger = LoggerFactory
			.getLogger(BKUSLConnector.class);

	public static final String SIGNATURE_DEVICE = "BKU";

	public static final String PATTERN_ERROR_CODE = "<.*:?ErrorCode>\\s*([0-9]+)\\s*</.*:?ErrorCode>";
	public static final String PATTERN_ERROR_INFO = "<.*:?Info>\\s*(.*)\\s*</.*:?Info>";

	private String bkuUrl;

	public BKUSLConnector(Configuration config) {
		this.bkuUrl = config.getValue(CONFIG_BKU_URL);
	}

	private CloseableHttpClient buildHttpClient() {
		HttpClientBuilder builder = HttpClientBuilder.create();
		return builder.build();
	}

	public static SLPdfAsException generateLegacySLException(String xmlResponse) {
		if (xmlResponse != null) {
			if (xmlResponse.contains("ErrorResponse")) {
				int errorCode = -1;
				String errorInfo = null;
				// Probably an ErrorResponse
				Pattern patternErrorCode = Pattern.compile(PATTERN_ERROR_CODE,
						Pattern.CASE_INSENSITIVE);
				Matcher matcherErrorCode = patternErrorCode
						.matcher(xmlResponse);
				if (matcherErrorCode.find()) {
					if (matcherErrorCode.groupCount() == 1) {
						String errorCodeString = matcherErrorCode.group(1);
						try {
							errorCode = Integer.parseInt(errorCodeString);
						} catch (NumberFormatException e) {
							// Ignore
							logger.trace(
									"Failed to convert ErrorCode [{}] into number",
									errorCodeString);
						}
					}
				}

				if (errorCode > 0) {

					Pattern patternErrorInfo = Pattern.compile(
							PATTERN_ERROR_INFO, Pattern.CASE_INSENSITIVE);
					Matcher matcherErrorInfo = patternErrorInfo
							.matcher(xmlResponse);

					if (matcherErrorInfo.find()) {
						if (matcherErrorInfo.groupCount() == 1) {
							errorInfo = matcherErrorInfo.group(1);
						}
					}
					
					return new SLPdfAsException(errorCode, errorInfo);
				}
			}
		}
		return null;
	}

	private String performHttpRequestToBKU(String xmlRequest,
			RequestPackage pack, SignParameter parameter)
			throws ClientProtocolException, IOException, IllegalStateException {
		CloseableHttpClient client = null;
		try {
			client = buildHttpClient();
			HttpPost post = new HttpPost(this.bkuUrl);

			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder
					.create();
			entityBuilder.setCharset(Charset.forName("UTF-8"));
			entityBuilder.addTextBody(XMLREQUEST, xmlRequest,
					ContentType.TEXT_XML.withCharset(Charset.forName("UTF-8")));

			if (parameter != null) {
				String transactionId = parameter.getTransactionId();
				if (transactionId != null) {
					entityBuilder.addTextBody("TransactionId_", transactionId);
				}
			}

			if (pack != null && pack.getSignatureData() != null) {
				entityBuilder.addBinaryBody("fileupload", PDFUtils
						.blackOutSignature(pack.getSignatureData(),
								pack.getByteRange()));
			}
			post.setEntity(entityBuilder.build());

			HttpResponse response = client.execute(post);
			logger.debug("Response Code : "
					+ response.getStatusLine().getStatusCode());

			if (parameter instanceof BKUHeaderHolder) {
				BKUHeaderHolder holder = (BKUHeaderHolder) parameter;
				Header[] headers = response.getAllHeaders();

				if (headers != null) {
					for (int i = 0; i < headers.length; i++) {
						BKUHeader hdr = new BKUHeader(headers[i].getName(),
								headers[i].getValue());
						logger.debug("Response Header : {}", hdr.toString());
						holder.getProcessInfo().add(hdr);
					}
				}

				BKUHeader hdr = new BKUHeader(
						ErrorConstants.STATUS_INFO_SIGDEVICE, SIGNATURE_DEVICE);
				logger.debug("Response Header : {}", hdr.toString());
				holder.getProcessInfo().add(hdr);
			}

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
			response = null;
			rd = null;

			logger.trace(result.toString());
			return result.toString();
		} catch (PDFIOException e) {
			throw new PdfAsWrappedIOException(e);
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	public InfoboxReadResponseType sendInfoboxReadRequest(
			InfoboxReadRequestType request, SignParameter parameter)
			throws PdfAsException {
		JAXBElement<?> element = null;
		String slRequest;
		String slResponse = null;
		try {
			slRequest = SLMarschaller.marshalToString(of
					.createInfoboxReadRequest(request));
			logger.trace(slRequest);

			slResponse = performHttpRequestToBKU(slRequest, null,
					parameter);

			element = (JAXBElement<?>) SLMarschaller
					.unmarshalFromString(slResponse);

		} catch (JAXBException e) {
			
			SLPdfAsException slError = generateLegacySLException(slResponse);
			if(slError != null) {
				throw slError;
			}
			
			throw new PDFIOException("error.pdf.io.03", e);
		} catch (ClientProtocolException e) {
			
			SLPdfAsException slError = generateLegacySLException(slResponse);
			if(slError != null) {
				throw slError;
			}
			
			throw new PDFIOException("error.pdf.io.03", e);
		} catch (IOException e) {
			
			SLPdfAsException slError = generateLegacySLException(slResponse);
			if(slError != null) {
				throw slError;
			}
			
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

	public CreateCMSSignatureResponseType sendCMSRequest(RequestPackage pack,
			SignParameter parameter) throws PdfAsException {
		JAXBElement<?> element = null;
		String slRequest;
		String slResponse = null;
		try {
			slRequest = SLMarschaller.marshalToString(of
					.createCreateCMSSignatureRequest(pack.getRequestType()));
			logger.debug(slRequest);

			slResponse = performHttpRequestToBKU(slRequest, pack,
					parameter);

			element = (JAXBElement<?>) SLMarschaller
					.unmarshalFromString(slResponse);
		} catch (JAXBException e) {
			SLPdfAsException slError = generateLegacySLException(slResponse);
			if(slError != null) {
				throw slError;
			}
			throw new PDFIOException("error.pdf.io.03", e);
		} catch (ClientProtocolException e) {
			SLPdfAsException slError = generateLegacySLException(slResponse);
			if(slError != null) {
				throw slError;
			}
			throw new PDFIOException("error.pdf.io.03", e);
		} catch (IOException e) {
			SLPdfAsException slError = generateLegacySLException(slResponse);
			if(slError != null) {
				throw slError;
			}
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
