package at.gv.egiz.sl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.Arrays;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PDFIOException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.SLPdfAsException;
import at.gv.egiz.pdfas.lib.impl.signing.pdfbox.PADESPDFBOXSigner;
import at.gv.egiz.sl.Base64OptRefContentType;
import at.gv.egiz.sl.CMSDataObjectRequiredMetaType;
import at.gv.egiz.sl.CreateCMSSignatureRequestType;
import at.gv.egiz.sl.CreateCMSSignatureResponseType;
import at.gv.egiz.sl.ErrorResponseType;
import at.gv.egiz.sl.InfoboxReadParamsAssocArrayType;
import at.gv.egiz.sl.InfoboxReadRequestType;
import at.gv.egiz.sl.InfoboxReadResponseType;
import at.gv.egiz.sl.MetaInfoType;
import at.gv.egiz.sl.InfoboxReadParamsAssocArrayType.ReadValue;
import at.gv.egiz.sl.ObjectFactory;

public abstract class BaseSLConnector implements ISLConnector {

	private static final Logger logger = LoggerFactory.getLogger(BaseSLConnector.class);
	
	public static final String SecureSignatureKeypair = "SecureSignatureKeypair";
	
	public static final String PDF_MIME_TYPE = "application/pdf";
	public static final String PDF_MIME_TYPE_DESC = "Adobe PDF-Dateien";
	
	public static final String DETACHED = "detached";
	
	public static final String XMLREQUEST = "XMLRequest";
	
	protected ObjectFactory of = new ObjectFactory();
	
	public InfoboxReadRequestType createInfoboxReadRequest() {
		InfoboxReadRequestType request = new InfoboxReadRequestType();
		request.setInfoboxIdentifier("Certificates");
		InfoboxReadParamsAssocArrayType readData = new InfoboxReadParamsAssocArrayType();
		
		ReadValue readValue = new ReadValue();
		readValue.setKey(SecureSignatureKeypair);

		readData.setReadValue(readValue);
		request.setAssocArrayParameters(readData);
		return request;
	}
	
	public CreateCMSSignatureRequestType createCMSRequest(byte[] signatureData, int[] byteRange) {
		// TODO build byte[] from signatureData and fill 0 bytes in byteRanged
		if(byteRange.length % 2 != 0) {
			// TODO: error
		}
		
		int lastOffset = byteRange[byteRange.length - 2];
		int lastSize = byteRange[byteRange.length - 1];
		
		int dataSize = lastOffset + lastSize;
		
		byte[] data = new byte[dataSize];
		int currentdataOff = 0;
		
		Arrays.fill(data, (byte)0);
		
		for(int i = 0; i < byteRange.length; i = i + 2) {
			int offset = byteRange[i];
			int size = byteRange[i+1];
			
			for(int j = 0; j < size; j++) {
				data[offset + j] = signatureData[currentdataOff];
				currentdataOff++;
			}
			
		}
		
		// == MetaInfoType
		MetaInfoType metaInfoType = new MetaInfoType();
		metaInfoType.setMimeType(PDF_MIME_TYPE);
		metaInfoType.setDescription(PDF_MIME_TYPE_DESC);
		
		// == Base64OptRefContentType
		Base64OptRefContentType base64OptRefContentType = new Base64OptRefContentType();
		base64OptRefContentType.setBase64Content(data);
		
		// == CMSDataObjectRequiredMetaType
		CMSDataObjectRequiredMetaType cmsDataObjectRequiredMetaType = new CMSDataObjectRequiredMetaType();
		cmsDataObjectRequiredMetaType.setMetaInfo(metaInfoType);
		cmsDataObjectRequiredMetaType.setContent(base64OptRefContentType);
		// cmsDataObjectRequiredMetaType.setExcludedByteRange()
		
		// == CreateCMSSignatureRequestType
		CreateCMSSignatureRequestType request = new CreateCMSSignatureRequestType();
		request.setKeyboxIdentifier(SecureSignatureKeypair);
		request.setDataObject(cmsDataObjectRequiredMetaType);
		request.setStructure(DETACHED);
		
		return request;
	}

}
