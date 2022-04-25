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
package at.gv.egiz.pdfas.lib.impl.signing;

import java.awt.Image;
import java.util.Calendar;

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import iaik.x509.X509Certificate;

public interface IPdfSigner {

	PDFASSignatureInterface buildSignaturInterface(IPlainSigner signer,
			SignParameter parameters, RequestedSignature requestedSignature);

	PDFASSignatureExtractor buildBlindSignaturInterface(
			X509Certificate certificate, String filter, String subfilter,
			Calendar date);

	PDFObject buildPDFObject(OperationStatus operationStatus);

	void checkPDFPermissions(PDFObject object) throws PdfAsException;
	
	void signPDF(PDFObject pdfObject, RequestedSignature requestedSignature,
			PDFASSignatureInterface signer) throws PdfAsException;
	
	byte[] rewritePlainSignature(byte[] plainSignature);
	
	Image generateVisibleSignaturePreview(SignParameter parameter,
			java.security.cert.X509Certificate cert, int resolution, OperationStatus status, 
			RequestedSignature requestedSignature) throws PDFASError;

	
}
