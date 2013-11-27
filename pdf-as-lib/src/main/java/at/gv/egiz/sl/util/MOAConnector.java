package at.gv.egiz.sl.util;

import iaik.x509.X509Certificate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;

import javax.activation.DataHandler;

import at.gv.egiz.moa.ByteArrayDataSource;
import at.gv.egiz.moa.SignatureCreationServiceStub;
import at.gv.egiz.moa.SignatureCreationServiceStub.CMSContentBaseType;
import at.gv.egiz.moa.SignatureCreationServiceStub.CreateCMSSignatureRequest;
import at.gv.egiz.moa.SignatureCreationServiceStub.CreateCMSSignatureResponse;
import at.gv.egiz.moa.SignatureCreationServiceStub.DataObjectInfo_type1;
import at.gv.egiz.moa.SignatureCreationServiceStub.DataObject_type1;
import at.gv.egiz.moa.SignatureCreationServiceStub.KeyIdentifierType;
import at.gv.egiz.moa.SignatureCreationServiceStub.SingleSignatureInfo_type1;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.lib.api.Configuration;

public class MOAConnector implements ISignatureConnector {

	public static final String MOA_SIGN_URL = "moa.sign.url";
	public static final String MOA_SIGN_KEY_ID = "moa.sign.KeyIdentifier";
	public static final String MOA_SIGN_CERTIFICATE = "moa.sign.Certificate";
	
	private X509Certificate certificate;
	private String moaEndpoint;
	private String keyIdentifier;

	public MOAConnector(Configuration config)
			throws CertificateException, FileNotFoundException, IOException {
		this.certificate = new X509Certificate(new FileInputStream(new File(config.getValue(MOA_SIGN_CERTIFICATE))));
		this.moaEndpoint = config.getValue(MOA_SIGN_URL);
		this.keyIdentifier = config.getValue(MOA_SIGN_KEY_ID);
	}

	public X509Certificate getCertificate() throws PdfAsException {
		return this.certificate;
	}

	public byte[] sign(byte[] input, int[] byteRange) throws PdfAsException {
		try {
			SignatureCreationServiceStub signatureCreationService = new SignatureCreationServiceStub(
					this.moaEndpoint);

			CreateCMSSignatureRequest createCMSSignatureRequest = new CreateCMSSignatureRequest();
			SingleSignatureInfo_type1 singleSignature = new SingleSignatureInfo_type1();
			DataObjectInfo_type1 dataObjectType = new DataObjectInfo_type1();
			singleSignature.setDataObjectInfo(dataObjectType);
			DataObject_type1 dataObject = new DataObject_type1();
			dataObjectType.setDataObject(dataObject);
			CMSContentBaseType cmsContent = new CMSContentBaseType();
			cmsContent.setBase64Content(new DataHandler(
					new ByteArrayDataSource(input, "application/pdf")));
			dataObject.setContent(cmsContent);

			createCMSSignatureRequest
					.setSingleSignatureInfo(new SingleSignatureInfo_type1[] { singleSignature });
			KeyIdentifierType keyId = new KeyIdentifierType();
			keyId.setKeyIdentifierType(this.keyIdentifier);
			createCMSSignatureRequest.setKeyIdentifier(keyId);

			CreateCMSSignatureResponse response = signatureCreationService
					.createCMSSignature(createCMSSignatureRequest);

			InputStream is = response.getCreateCMSSignatureResponse()
					.getCreateCMSSignatureResponseTypeChoice()[0]
					.getCMSSignature().getInputStream();
			
			byte[] signature = StreamUtils.inputStreamToByteArray(is);
			
			return signature;
		} catch (Exception e) {
			throw new PdfAsException(e.getMessage());
		}
	}
}
