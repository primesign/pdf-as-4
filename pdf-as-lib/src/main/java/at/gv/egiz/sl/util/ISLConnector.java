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

import java.util.List;

import at.gv.egiz.pdfas.common.exceptions.PDFIOException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.sl.schema.BulkResponseType;
import at.gv.egiz.sl.schema.CreateCMSSignatureResponseType;
import at.gv.egiz.sl.schema.InfoboxReadRequestType;
import at.gv.egiz.sl.schema.InfoboxReadResponseType;

public interface ISLConnector {

	public InfoboxReadRequestType createInfoboxReadRequest(SignParameter parameter);
	public InfoboxReadResponseType sendInfoboxReadRequest(InfoboxReadRequestType request, SignParameter parameter)  throws PdfAsException;
	public RequestPackage createCMSRequest(byte[] signatureData, int[] byteRange, SignParameter parameter) throws PDFIOException;
	public CreateCMSSignatureResponseType sendCMSRequest(RequestPackage pack, SignParameter parameter) throws PdfAsException;
	public BulkResponseType sendBulkRequest(BulkRequestPackage pack, SignParameter parameter) throws PdfAsException;
	public BulkRequestPackage createBulkRequestPackage(List<byte[]> signatureData, List<int[]> byteRange, List<SignParameter> parameter) throws PDFIOException;
}
