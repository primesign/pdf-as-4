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

import iaik.x509.X509Certificate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.security.cert.CertificateException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsMOAException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSignatureException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsWrappedIOException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import at.gv.egiz.pdfas.lib.util.SignatureUtils;

public class MOAConnector implements ISignatureConnector {

	private static final Logger logger = LoggerFactory
			.getLogger(MOAConnector.class);
	
	private static final Logger moalogger = LoggerFactory.getLogger("at.knowcenter.wag.egov.egiz.sig.connectors.MOASSRepsonseLogger");

	public static final String MOA_SIGN_URL = "moa.sign.url";
	public static final String MOA_SIGN_KEY_ID = "moa.sign.KeyIdentifier";
	public static final String MOA_SIGN_CERTIFICATE = "moa.sign.Certificate";

	public static final String KEY_ID_PATTERN = "##KEYID##";
	public static final String CONTENT_PATTERN = "##CONTENT##";

	public static final String FAULTCODE = "faultcode";
	public static final String FAULTSTRING = "faultstring";
	public static final String ERRORRESPONSE = "ErrorResponse";
	public static final String ERRORCODE = "ErrorCode";
	public static final String CMSSIGNATURE = "CMSSignature";

	public static final String CMS_REQUEST = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://reference.e-government.gv.at/namespace/moa/20020822#\">"
			+ "<soapenv:Header/><soapenv:Body><ns:CreateCMSSignatureRequest><ns:KeyIdentifier>"
			+ KEY_ID_PATTERN
			+ "</ns:KeyIdentifier>"
			+ "<ns:SingleSignatureInfo SecurityLayerConformity=\"true\"><ns:DataObjectInfo Structure=\"detached\"><ns:DataObject>"
			+ "<ns:MetaInfo><ns:MimeType>application/pdf</ns:MimeType></ns:MetaInfo><ns:Content>"
			+ "<ns:Base64Content>"
			+ CONTENT_PATTERN
			+ "</ns:Base64Content>"
			+ "</ns:Content></ns:DataObject></ns:DataObjectInfo></ns:SingleSignatureInfo>"
			+ "</ns:CreateCMSSignatureRequest></soapenv:Body></soapenv:Envelope>";

	private X509Certificate certificate;
	private String moaEndpoint;
	private String keyIdentifier;

	public MOAConnector(Configuration config) throws CertificateException,
			FileNotFoundException, IOException {
		if(config.getValue(MOA_SIGN_CERTIFICATE) == null) {
			logger.error(MOA_SIGN_CERTIFICATE + " not configured for MOA connector");
			throw new PdfAsWrappedIOException(new PdfAsException("Please configure: " + MOA_SIGN_CERTIFICATE + " to use MOA connector"));
		}
		
		if(!(config instanceof ISettings)) {
			logger.error("Configuration is no instance of ISettings");
			throw new PdfAsWrappedIOException(new PdfAsException("Configuration is no instance of ISettings"));
		}
		
		ISettings settings = (ISettings)config;
		
		String certificateValue = config.getValue(MOA_SIGN_CERTIFICATE);
		
		File certFile = new File(certificateValue); 
		if(!certFile.isAbsolute()) {
			certificateValue = settings.getWorkingDirectory() + "/" + 
					config.getValue(MOA_SIGN_CERTIFICATE);
			certFile = new File(certificateValue); 
		}
		
		logger.info("Loading certificate: " + certificateValue);
		
		this.certificate = new X509Certificate(new FileInputStream(certFile));
		this.moaEndpoint = config.getValue(MOA_SIGN_URL);
		this.keyIdentifier = config.getValue(MOA_SIGN_KEY_ID);
	}

	public X509Certificate getCertificate(SignParameter parameter) throws PdfAsException {
		return this.certificate;
	}

	private CloseableHttpClient buildHttpClient() {
		HttpClientBuilder builder = HttpClientBuilder.create();
		return builder.build();
	}

	public byte[] sign(byte[] input, int[] byteRange, SignParameter parameter
			, RequestedSignature requestedSignature) throws PdfAsException {
		CloseableHttpClient client = null;
		try {
			client = buildHttpClient();
			HttpPost post = new HttpPost(this.moaEndpoint);

			logger.info("signature with MOA [" + this.keyIdentifier + "] @ "
					+ this.moaEndpoint);

			Base64 base64 = new Base64();
			String content = base64.encodeAsString(input);

			String request = CMS_REQUEST;
			request = request.replace(CONTENT_PATTERN, content.trim());
			request = request
					.replace(KEY_ID_PATTERN, this.keyIdentifier.trim());

			// SOAPAction: "urn:CreateCMSSignatureAction"
			post.setHeader("SOAPAction", "urn:CreateCMSSignatureAction");

			EntityBuilder entityBuilder = EntityBuilder.create();

			entityBuilder.setContentType(ContentType.TEXT_XML);
			entityBuilder.setContentEncoding("UTF-8");
			entityBuilder.setText(request);

			post.setEntity(entityBuilder.build());
			moalogger.debug(">>> " + request);
			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			moalogger.debug("<<< " + result.toString());

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader(
					result.toString())));
			doc.getDocumentElement().normalize();

			if (response.getStatusLine().getStatusCode() != 200) {
				String faultCode = "";
				String faultString = "";
				String errorResponse = "";
				String errorCode = "";
				NodeList nodeList = doc.getElementsByTagName("*");
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						if (node.getNodeName().equals(FAULTCODE)) {
							faultCode = node.getTextContent();
						} else if (node.getNodeName().equals(FAULTSTRING)) {
							faultString = node.getTextContent();
						} else if (node.getNodeName().equals(ERRORCODE)) {
							errorCode = node.getTextContent();
						} else if (node.getNodeName().equals(ERRORRESPONSE)) {
							errorResponse = node.getTextContent();
						}
					}
				}
				throw new PdfAsMOAException(faultCode, faultString,
						errorResponse, errorCode);
			} else {
				String cmsSignature = null;
				NodeList nodeList = doc.getElementsByTagName("*");
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						if (node.getNodeName().equals(CMSSIGNATURE)) {
							cmsSignature = node.getTextContent();
							break;
						}
					}
				}

				if (cmsSignature != null) {
					try {
						byte[] cmsSignatureData = base64.decode(cmsSignature);
						
						VerifyResult verifyResult = SignatureUtils.verifySignature(cmsSignatureData, input);

						if(!StreamUtils.dataCompare(requestedSignature.getCertificate().getFingerprintSHA(),
								verifyResult.getSignerCertificate().getFingerprintSHA())) {
							throw new PdfAsSignatureException("Certificates missmatch!");
						}
						
						return cmsSignatureData;
					} catch(Exception e) {
						throw new PdfAsException("error.pdf.io.07", e);
					}
				} else {
					throw new PdfAsException("error.pdf.io.07");
				}
			}
		} catch (IllegalStateException e) {
			throw new PdfAsException("error.pdf.io.08", e);
		} catch (IOException e) {
			throw new PdfAsException("error.pdf.io.08", e);
		} catch (SAXException e) {
			throw new PdfAsException("error.pdf.io.08", e);
		} catch (ParserConfigurationException e) {
			throw new PdfAsException("error.pdf.io.08", e);
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					logger.warn("Failed to close client", e);
				}
			}
		}
	}
	/*
	 * public byte[] sign(byte[] input, int[] byteRange) throws PdfAsException {
	 * try {
	 * 
	 * SignatureCreationServiceStub signatureCreationService = new
	 * SignatureCreationServiceStub( this.moaEndpoint);
	 * 
	 * CreateCMSSignatureRequest createCMSSignatureRequest = new
	 * CreateCMSSignatureRequest(); KeyIdentifierType keyId = new
	 * KeyIdentifierType(); keyId.setKeyIdentifierType(keyIdentifier);
	 * createCMSSignatureRequest.setKeyIdentifier(keyId);
	 * 
	 * SingleSignatureInfo_type1 singleSignature = new
	 * SingleSignatureInfo_type1(); DataObjectInfo_type1 dataObjectType = new
	 * DataObjectInfo_type1();
	 * 
	 * dataObjectType.setStructure(Structure_type1.detached);
	 * singleSignature.setDataObjectInfo(dataObjectType); DataObject_type1
	 * dataObject = new DataObject_type1(); MetaInfoType metaInfoType = new
	 * MetaInfoType(); MimeTypeType mimeTypeType = new MimeTypeType();
	 * mimeTypeType.setMimeTypeType(new Token("application/pdf"));
	 * metaInfoType.setMimeType(mimeTypeType);
	 * dataObject.setMetaInfo(metaInfoType);
	 * dataObjectType.setDataObject(dataObject); CMSContentBaseType cmsContent =
	 * new CMSContentBaseType(); cmsContent.setBase64Content(new DataHandler(
	 * new ByteArrayDataSource(input, "application/pdf")));
	 * 
	 * dataObject.setContent(cmsContent);
	 * 
	 * createCMSSignatureRequest.addSingleSignatureInfo(singleSignature);
	 * 
	 * CreateCMSSignatureResponse response = signatureCreationService
	 * .createCMSSignature(createCMSSignatureRequest);
	 * 
	 * InputStream is = response.getCreateCMSSignatureResponse()
	 * .getCreateCMSSignatureResponseTypeChoice()[0]
	 * .getCMSSignature().getInputStream();
	 * 
	 * byte[] signature = StreamUtils.inputStreamToByteArray(is);
	 * 
	 * return signature; } catch (Exception e) { throw new
	 * PdfAsException(e.getMessage()); } }
	 */
}
