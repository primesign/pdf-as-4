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
import iaik.cms.ContentInfo;

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
	
	/**
	 * <p><strong>Starts an external signature process</strong> (step 1 of 2-steps):</p>
	 * <ul>
	 * <li>prepares the document to be signed adding
	 * <ul>
	 * <li>(visual) signature appearance</li>
	 * <li>empty pdf signature</li>
	 * <li>LTV validation data</li>
	 * </ul>
	 * </li>
	 * <li>populates the provided {@link ExternalSignatureContext} with
	 * <ul>
	 * <li>digest value and algorithm</li>
	 * <li>signature algorithm (derived from the provided signer certificate)</li>
	 * <li>the prepared - still unsigned - document</li>
	 * <li>the byte range to be signed</li>
	 * <li>the signing time</li>
	 * <li>the signature object (e.g. ASN.1 CMS ContentInfo) without the signature value</li>
	 * <li>the signing certificate</li>
	 * <li>the applied signature position</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * <p><strong>Requires</strong></p>
	 * <ul>
	 * <li>document to be signed</li>
	 * <li>signer certificate</li>
	 * <li>(empty) {@link ExternalSignatureContext}<br>
	 * optional: {@link ExternalSignatureContext} with already populated
	 * {@link ExternalSignatureContext#getPreparedDocument() datasource for the prepared document} (will be used instead of
	 * in-memory DataSource)</li>
	 * </ul>
	 * <p><strong>Provides</strong></p>
	 * <ul>
	 * <li>populated {@link ExternalSignatureContext}</li>
	 * </ul>
	 * <p><strong>Sample code for using the API</strong></p>
	 * 
	 * <pre>
	 * File pdfasConfigFolder = ...
	 * PdfAs pdfasApi = PdfAsFactory.createPdfAs(pdfasConfigFolder)
	 * 
	 * File signedDocumentFile = ...
	 * DataSource unsignedDocumentDataSource = ...
	 * 
	 * X509Certificate signingCertificate = ...
	 * PrivateKey signingKey = ...
	 * 
	 * SignResult signResult;
	 * try (OutputStream out = new FileOutputStream(signedDocumentFile)) {
	 * 
	 *    SignParameter signParameter = PdfAsFactory.createSignParameter(pdfasApi.getConfiguration(), unsignedDocumentDataSource, out);
	 *    signParameter.setPlainSigner(new PAdESExternalSigner());
	 *    // optionally with further settings like signature profile, external SigningTimeSource, LTV etc.
	 *    signParameter.setFoo(...)
	 * 
	 *    ExternalSignatureContext ctx = new ExternalSignatureContext();
	 * 
	 *    pdfasApi.startExternalSignature(signParameter, signingCertificate, ctx);
	 *    
	 *    // ** create external signature (sample code snippet for ECDSA) **
	 *       // assuming ECDSA (production code should evaluate ctx.getSignatureAlgorithmOid())
	 *       Signature signature = Signature.getInstance("NONEwithECDSA");
	 *       signature.initSign(signingKey);
	 *       signature.update(ctx.getDigestValue());
	 *       byte[] externalSignatureValue = signature.sign();
	 * 
	 *    signResult = pdfasApi.finishExternalSignature(signParameter, externalSignatureValue, ctx);
	 * 
	 * }
	 * </pre>
	 * 
	 * @param signParameter      The pdfas api signature parameter. (required; must not be {@code null})
	 * @param signingCertificate The signing certificate. (required; must not be {@code null})
	 * @param ctx                The external signature context. (required; must not be {@code null} but may be empty)
	 * @throws PDFASError Thrown in case of error.
	 * @see #startExternalSignature(SignParameter, X509Certificate)
	 * @see #finishExternalSignature(SignParameter, byte[], ExternalSignatureContext)
	 */
	void startExternalSignature(@Nonnull SignParameter signParameter, @Nonnull X509Certificate signingCertificate, @Nonnull ExternalSignatureContext ctx) throws PDFASError;
	
	/**
	 * <p><strong>Starts an external signature process</strong> (step 1 of 2-steps):</p>
	 * <ul>
	 * <li>prepares the document to be signed adding
	 * <ul>
	 * <li>(visual) signature appearance</li>
	 * <li>empty pdf signature</li>
	 * <li>LTV validation data</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <p><strong>Requires</strong></p>
	 * <ul>
	 * <li>document to be signed</li>
	 * <li>signer certificate</li>
	 * </ul>
	 * <p><strong>Provides</strong></p>
	 * <ul>
	 * <li>{@link ExternalSignatureContext} populated with
	 * <ul>
	 * <li>digest value and algorithm</li>
	 * <li>signature algorithm (derived from the provided signer certificate)</li>
	 * <li>the prepared - still unsigned - document</li>
	 * <li>the byte range to be signed</li>
	 * <li>the signing time</li>
	 * <li>the signature object (e.g. ASN.1 CMS ContentInfo) without the signature value</li>
	 * <li>the signing certificate</li>
	 * <li>the applied signature position</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <p><strong>Sample code for using the API</strong></p>
	 * <pre>
	 * File pdfasConfigFolder = ...
	 * PdfAs pdfasApi = PdfAsFactory.createPdfAs(pdfasConfigFolder)
	 * 
	 * File signedDocumentFile = ...
	 * DataSource unsignedDocumentDataSource = ...
	 * 
	 * X509Certificate signingCertificate = ...
	 * PrivateKey signingKey = ...
	 * 
	 * SignResult signResult;
	 * try (OutputStream out = new FileOutputStream(signedDocumentFile)) {
	 * 
	 *    SignParameter signParameter = PdfAsFactory.createSignParameter(pdfasApi.getConfiguration(), unsignedDocumentDataSource, out);
	 *    signParameter.setPlainSigner(new PAdESExternalSigner());
	 *    // optionally with further settings like signature profile, external SigningTimeSource, LTV etc.
	 *    signParameter.setFoo(...)
	 * 
	 *    ExternalSignatureContext ctx = pdfasApi.startExternalSignature(signParameter, signingCertificate);
	 *    
	 *    // ** create external signature (sample code snippet for ECDSA) **
	 *       // assuming ECDSA (production code should evaluate ctx.getSignatureAlgorithmOid())
	 *       Signature signature = Signature.getInstance("NONEwithECDSA");
	 *       signature.initSign(signingKey);
	 *       signature.update(ctx.getDigestValue());
	 *       byte[] externalSignatureValue = signature.sign();
	 * 
	 *    signResult = pdfasApi.finishExternalSignature(signParameter, externalSignatureValue, ctx);
	 * 
	 * }
	 * </pre>
	 * 
	 * @param signParameter      The pdfas api signature parameter. (required; must not be {@code null})
	 * @param signingCertificate The signing certificate. (required; must not be {@code null})
	 * @throws PDFASError Thrown in case of error.
	 * @return The populated external signature context. (never {@code null})
	 * @see #startExternalSignature(SignParameter, X509Certificate, ExternalSignatureContext)
	 * @see #finishExternalSignature(SignParameter, byte[], ExternalSignatureContext)
	 */
	@Nonnull
	default ExternalSignatureContext startExternalSignature(@Nonnull SignParameter signParameter, @Nonnull X509Certificate signingCertificate) throws PDFASError {
		ExternalSignatureContext externalSignatureContext = new ExternalSignatureContext();
		startExternalSignature(signParameter, signingCertificate, externalSignatureContext);
		return externalSignatureContext;
	}
	
	/**
	 * <p><strong>Completes an external signature process</strong> (step 2 of 2-steps):</p>
	 * <ul>
	 * <li>incorporates the signature value into the signature object (provided with the external context) (e.g. incorporating the signature value into an encoded ASN.1 {@link ContentInfo})</li>
	 * <li>performs cryptographical validation of the signature object (using the prepared document from the external signature context as digest input)</li>
	 * <li>compares the signer certificate from the external signature context with the signer certificate from the signature object</li>
	 * <li>incorporates the signature object as pdf signature into the prepared document resulting in the final signed document</li>
	 * </ul>
	 * <p><strong>Requires</strong></p>
	 * <ul>
	 * <li>plain signature value</li>
	 * <li>{@link ExternalSignatureContext} populated with
	 * <ul>
	 * <li>digest value and algorithm</li>
	 * <li>signature algorithm (derived from the provided signer certificate)</li>
	 * <li>the prepared - still unsigned - document</li>
	 * <li>the byte range to be signed</li>
	 * <li>the signature object (e.g. ASN.1 CMS ContentInfo) without the signature value</li>
	 * <li>the signing certificate</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <p><strong>Provides</strong></p>
	 * <ul>
	 * <li>{@link SignResult}</li>
	 * </ul>
	 * <p><strong>Sample code for using the API</strong></p>
	 * 
	 * <pre>
	 * File pdfasConfigFolder = ...
	 * PdfAs pdfasApi = PdfAsFactory.createPdfAs(pdfasConfigFolder)
	 * 
	 * File signedDocumentFile = ...
	 * DataSource unsignedDocumentDataSource = ...
	 * 
	 * X509Certificate signingCertificate = ...
	 * PrivateKey signingKey = ...
	 * 
	 * SignResult signResult;
	 * try (OutputStream out = new FileOutputStream(signedDocumentFile)) {
	 * 
	 *    SignParameter signParameter = PdfAsFactory.createSignParameter(pdfasApi.getConfiguration(), unsignedDocumentDataSource, out);
	 *    signParameter.setPlainSigner(new PAdESExternalSigner());
	 *    // optionally with further settings like signature profile, external SigningTimeSource, LTV etc.
	 *    signParameter.setFoo(...)
	 * 
	 *    ExternalSignatureContext ctx = pdfasApi.startExternalSignature(signParameter, signingCertificate);
	 *    
	 *    // ** create external signature (sample code snippet for ECDSA) **
	 *       // assuming ECDSA (production code should evaluate ctx.getSignatureAlgorithmOid())
	 *       Signature signature = Signature.getInstance("NONEwithECDSA");
	 *       signature.initSign(signingKey);
	 *       signature.update(ctx.getDigestValue());
	 *       byte[] externalSignatureValue = signature.sign();
	 * 
	 *    signResult = pdfasApi.finishExternalSignature(signParameter, externalSignatureValue, ctx);
	 * 
	 * }
	 * </pre>
	 * 
	 * @param signParameter  The pdfas api signature parameter. (required; must not be {@code null})
	 * @param signatureValue The signature value (provided by an external signature device). (required; must not be
	 *                       {@code null})
	 * @param ctx            The external signature context. (required; must not be {@code null} but may be empty)
	 * @return The signature result. (never {@code null})
	 * @throws PDFASError Thrown in case of error.
	 * @see #startExternalSignature(SignParameter, X509Certificate)
	 * @see #startExternalSignature(SignParameter, X509Certificate, ExternalSignatureContext)
	 */
	SignResult finishExternalSignature(@Nonnull SignParameter signParameter, @Nonnull byte[] signatureValue, @Nonnull ExternalSignatureContext ctx) throws PDFASError;
	
}
