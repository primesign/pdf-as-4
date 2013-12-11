package at.gv.egiz.sl.util;

import iaik.x509.X509Certificate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;

import javax.activation.DataHandler;

import org.apache.axis2.databinding.types.Token;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.moa.ByteArrayDataSource;
import at.gv.egiz.moa.SignatureCreationServiceStub;
import at.gv.egiz.moa.SignatureCreationServiceStub.CMSContentBaseType;
import at.gv.egiz.moa.SignatureCreationServiceStub.CreateCMSSignatureRequest;
import at.gv.egiz.moa.SignatureCreationServiceStub.CreateCMSSignatureResponse;
import at.gv.egiz.moa.SignatureCreationServiceStub.CreateSignatureInfo_type0;
import at.gv.egiz.moa.SignatureCreationServiceStub.DataObjectInfo_type1;
import at.gv.egiz.moa.SignatureCreationServiceStub.DataObject_type1;
import at.gv.egiz.moa.SignatureCreationServiceStub.KeyIdentifierType;
import at.gv.egiz.moa.SignatureCreationServiceStub.MetaInfoType;
import at.gv.egiz.moa.SignatureCreationServiceStub.MimeTypeType;
import at.gv.egiz.moa.SignatureCreationServiceStub.SingleSignatureInfo_type1;
import at.gv.egiz.moa.SignatureCreationServiceStub.Structure_type1;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.lib.api.Configuration;

public class MOAConnector implements ISignatureConnector {

	private static final Logger logger = LoggerFactory
			.getLogger(MOAConnector.class);

	public static final String MOA_SIGN_URL = "moa.sign.url";
	public static final String MOA_SIGN_KEY_ID = "moa.sign.KeyIdentifier";
	public static final String MOA_SIGN_CERTIFICATE = "moa.sign.Certificate";

	public static final String KEY_ID_PATTERN = "##KEYID##";
	public static final String CONTENT_PATTERN = "##CONTENT##";

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
		this.certificate = new X509Certificate(new FileInputStream(new File(
				config.getValue(MOA_SIGN_CERTIFICATE))));
		this.moaEndpoint = config.getValue(MOA_SIGN_URL);
		this.keyIdentifier = config.getValue(MOA_SIGN_KEY_ID);
	}

	public X509Certificate getCertificate() throws PdfAsException {
		return this.certificate;
	}

	private CloseableHttpClient buildHttpClient() {
		HttpClientBuilder builder = HttpClientBuilder.create();
		return builder.build();
	}

	public byte[] sign(byte[] input, int[] byteRange) throws PdfAsException {
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

				//SOAPAction: "urn:CreateCMSSignatureAction"
			post.setHeader("SOAPAction", "urn:CreateCMSSignatureAction");
			
			EntityBuilder entityBuilder = EntityBuilder.create();
			
			entityBuilder.setContentType(ContentType.TEXT_XML);
			entityBuilder.setContentEncoding("UTF-8");
			entityBuilder.setText(request);
			
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
			return new byte[] {};
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return new byte[] {};
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
