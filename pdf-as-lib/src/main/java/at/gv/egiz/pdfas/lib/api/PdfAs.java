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
package at.gv.egiz.pdfas.lib.api;

import java.awt.Image;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.annotation.Nonnull;

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.sign.ExternalSignatureContext;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.sign.SignResult;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;

public interface PdfAs {	
	/**
	 * Signs a PDF document using PDF-AS.
	 * 
	 * @param parameter
	 * @return
	 */
	SignResult sign(SignParameter parameter) throws PDFASError;
	
	/**
	 * Verifies a document with (potentially multiple) PDF-AS signatures.
	 *  
	 * @param parameter The verification parameter
	 * @return A list of verification Results
	 */
	List<VerifyResult> verify(VerifyParameter parameter) throws PDFASError;
	
	/**
	 * Gets a copy of the PDF-AS configuration, to allow the application to 
	 * override configuration parameters at runtime.
	 * 
	 * @return A private copy of the pdf as configuration
	 */
	Configuration getConfiguration();
	
	/**
	 * Starts a signature process
	 * 
	 * After the process has to be startet the status request has to be services by the user application
	 * 
	 * @param parameter The sign parameter
	 * @return A status request
	 * @throws PdfAsException
	 */
	StatusRequest startSign(SignParameter parameter) throws PDFASError;
	
	/**
	 * Continues an ongoing signature process 
	 * 
	 * @param statusRequest The current status
	 * @return A status request
	 * @throws PdfAsException
	 */
	StatusRequest process(StatusRequest statusRequest) throws PDFASError;
	
	/**
	 * Finishes a signature process
	 * 
	 * @param statusRequest The current status
	 * @return A signature result
	 * @throws PdfAsException
	 */
	SignResult    finishSign(StatusRequest statusRequest) throws PDFASError;
	
	/**
	 * Generates a Image of the visual signatur block as Preview
	 * 
	 * @param parameter The signing Parameter
	 * @param cert The certificate to use to build the signature block
	 * @param resolution the resolution in dpi (dots per inch) (default is 72)
	 * @return
	 * @throws PdfAsException
	 */
	Image generateVisibleSignaturePreview(SignParameter parameter, X509Certificate cert, int resolution) throws PDFASError;
	
	// TODO[PDFAS-114]: Add javadoc
	// TODO[PDFAS-114]: Add note about which fields are relevant from signParameter to javadoc for startExternalSignature

	// ctx will be updated in terms of: digestAlgorithmOid, digestValue, preparedDocument, signatureAlgorithmOid, signatureByteRange, signatureObject, signingTime
	void startExternalSignature(@Nonnull SignParameter signParameter, @Nonnull X509Certificate signingCertificate, @Nonnull ExternalSignatureContext ctx) throws PDFASError;
	
	// TODO[PDFAS-114]: Add note about which fields are relevant from signParameter to javadoc for finishExternalSignature

	SignResult finishExternalSignature(@Nonnull SignParameter signParameter, @Nonnull byte[] signatureValue, @Nonnull ExternalSignatureContext ctx) throws PDFASError;
	
}
