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

import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PDFIOException;
import at.gv.egiz.pdfas.common.utils.PDFUtils;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.sl.schema.Base64OptRefContentType;
import at.gv.egiz.sl.schema.CMSDataObjectRequiredMetaType;
import at.gv.egiz.sl.schema.CreateCMSSignatureRequestType;
import at.gv.egiz.sl.schema.ExcludedByteRangeType;
import at.gv.egiz.sl.schema.InfoboxReadParamsAssocArrayType;
import at.gv.egiz.sl.schema.InfoboxReadParamsAssocArrayType.ReadValue;
import at.gv.egiz.sl.schema.InfoboxReadRequestType;
import at.gv.egiz.sl.schema.MetaInfoType;
import at.gv.egiz.sl.schema.ObjectFactory;

public abstract class BaseSLConnector implements ISLConnector,
		IConfigurationConstants {

	private static final Logger logger = LoggerFactory
			.getLogger(BaseSLConnector.class);

	public static final String SecureSignatureKeypair = "SecureSignatureKeypair";

	public static final String PDF_MIME_TYPE = "application/pdf";
	public static final String PDF_MIME_TYPE_DESC = "Adobe PDF-File";

	public static final String DETACHED = "detached";

	public static final String XMLREQUEST = "XMLRequest";

	protected ObjectFactory of = new ObjectFactory();

	public InfoboxReadRequestType createInfoboxReadRequest(
			SignParameter parameter) {
		InfoboxReadRequestType request = new InfoboxReadRequestType();
		request.setInfoboxIdentifier("Certificates");
		InfoboxReadParamsAssocArrayType readData = new InfoboxReadParamsAssocArrayType();

		ReadValue readValue = new ReadValue();
		readValue.setKey(SecureSignatureKeypair);

		readData.setReadValue(readValue);
		request.setAssocArrayParameters(readData);
		return request;
	}

	
	public BulkRequestPackage createBulkRequestPackage(List<byte[]> signatureData, List<int[]> byteRange, List<SignParameter> parameter) throws PDFIOException {
		
		BulkRequestPackage bulkRequestPackage = new BulkRequestPackage();
		
		for(int i = 0; i< parameter.size(); i++) {
			bulkRequestPackage.getSignRequestPackages().add(createSignatureRequestPackage(signatureData.get(i), byteRange.get(i), parameter.get(i)));
		}
		
		return bulkRequestPackage;
	}
	
	
	public SignRequestPackage createSignatureRequestPackage(byte[] signatureData, int[] byteRange, SignParameter parameter)
			throws PDFIOException {
		SignRequestPackage signRequestPackage = new SignRequestPackage();
		signRequestPackage.setCmsRequest(createCMSRequest(signatureData, byteRange, parameter));
		signRequestPackage.setDisplayName(parameter.getDataSource().getName());
		return signRequestPackage;
	}

	public RequestPackage createCMSRequest(byte[] signatureData,
			int[] byteRange, SignParameter parameter) throws PDFIOException {
		boolean base64 = true;
		String requestType = parameter.getConfiguration().getValue(
				SL_REQUEST_TYPE);
		if (requestType != null) {
			if (requestType.equals(SL_REQUEST_TYPE_BASE64)) {
				base64 = true;
			} else if (requestType.equals(SL_REQUEST_TYPE_UPLOAD)) {
				base64 = false;
			}
		}
		byte[] data = PDFUtils.blackOutSignature(signatureData, byteRange);
		RequestPackage pack = new RequestPackage();
		int[] exclude_range = PDFUtils.buildExcludeRange(byteRange);
		logger.info("Exclude Byte Range: " + exclude_range[0] + " "
				+ exclude_range[1]);

		// == MetaInfoType
		MetaInfoType metaInfoType = new MetaInfoType();
		metaInfoType.setMimeType(PDF_MIME_TYPE);

		// == Base64OptRefContentType
		Base64OptRefContentType base64OptRefContentType = new Base64OptRefContentType();

		if (base64) {
			base64OptRefContentType.setBase64Content(data);
		} else {
			base64OptRefContentType.setReference("formdata:fileupload");
			pack.setSignatureData(signatureData);
			pack.setByteRange(byteRange);
		}

		// == CMSDataObjectRequiredMetaType
		CMSDataObjectRequiredMetaType cmsDataObjectRequiredMetaType = new CMSDataObjectRequiredMetaType();
		cmsDataObjectRequiredMetaType.setMetaInfo(metaInfoType);
		cmsDataObjectRequiredMetaType.setContent(base64OptRefContentType);
		if (byteRange.length > 0) {
			ExcludedByteRangeType excludeByteRange = new ExcludedByteRangeType();
			excludeByteRange.setFrom(new BigInteger(String
					.valueOf(exclude_range[0])));
			excludeByteRange.setTo(new BigInteger(String
					.valueOf(exclude_range[1])));
			cmsDataObjectRequiredMetaType
					.setExcludedByteRange(excludeByteRange);
		}

		// == CreateCMSSignatureRequestType
		CreateCMSSignatureRequestType request = new CreateCMSSignatureRequestType();
		request.setKeyboxIdentifier(SecureSignatureKeypair);
		request.setDataObject(cmsDataObjectRequiredMetaType);
		request.setStructure(DETACHED);

		pack.setRequestType(request);
		return pack;
	}
}
