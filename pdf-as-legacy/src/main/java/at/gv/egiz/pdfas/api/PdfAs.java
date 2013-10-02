/**
 * <copyright> Copyright 2006 by Know-Center, Graz, Austria </copyright>
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
 */
package at.gv.egiz.pdfas.api;

import java.util.List;

import at.gv.egiz.pdfas.api.analyze.AnalyzeParameters;
import at.gv.egiz.pdfas.api.analyze.AnalyzeResult;
import at.gv.egiz.pdfas.api.commons.DynamicSignatureLifetimeEnum;
import at.gv.egiz.pdfas.api.commons.DynamicSignatureProfile;
import at.gv.egiz.pdfas.api.commons.SignatureProfile;
import at.gv.egiz.pdfas.api.exceptions.PdfAsException;
import at.gv.egiz.pdfas.api.sign.SignParameters;
import at.gv.egiz.pdfas.api.sign.SignResult;
import at.gv.egiz.pdfas.api.sign.SignatureDetailInformation;
import at.gv.egiz.pdfas.api.verify.VerifyAfterAnalysisParameters;
import at.gv.egiz.pdfas.api.verify.VerifyAfterReconstructXMLDsigParameters;
import at.gv.egiz.pdfas.api.verify.VerifyParameters;
import at.gv.egiz.pdfas.api.verify.VerifyResult;
import at.gv.egiz.pdfas.api.verify.VerifyResults;
import at.gv.egiz.pdfas.api.xmldsig.ReconstructXMLDsigAfterAnalysisParameters;
import at.gv.egiz.pdfas.api.xmldsig.ReconstructXMLDsigParameters;
import at.gv.egiz.pdfas.api.xmldsig.ReconstructXMLDsigResult;

/**
 * The PDF-AS API main interface.
 * 
 * <p>
 * Create an Object implementing this interface using the proper factory.
 * </p>
 * 
 * @author wprinz
 * @author exthex
 */
public interface PdfAs
{
// 23.11.2010 changed by exthex - added:
// reconstructXMLDSIG(ReconstructXMLDsigParameters reconstructXMLDsigParameters)
// reconstructXMLDSIG(ReconstructXMLDsigAfterAnalysisParameters reconstructXMLDsigParameters)
// verify(VerifyAfterReconstructXMLDsigParameters verifyAfterReconstructXMLDsigParameters)

// 16.12.2010 changed by exthex - added:
// prepareSign(SignParameters signParameters)
// sign(SignParameters signParameters, SignatureDetailInformation signatureDetailInformation)
// finishSign(SignParameters signParameters, SignatureDetailInformation signatureDetailInformation)

  /**
   * Signs a PDF document using PDF-AS.
   * 
   * @param signParameters
   *          The sign parameters.
   * @return Returns the signed document plus additional information.
   * @throws PdfAsException
   *           Thrown, if an error occurs.
   * 
   * @see SignParameters
   * @see SignResult
   */
  public SignResult sign(SignParameters signParameters) throws PdfAsException;

  /**
   * Signs a PDF document using PDF-AS.<br/>
   * This uses the {@link SignatorInformation} which was obtained by a call to {@link PdfAs#prepareSign(SignParameters)}
   * 
   * @param signParameters
   *          The sign parameters.
   * @param signatureDetailInformation
   *          The signature information which was previously obtained by a call to {@link PdfAs#prepareSign(SignParameters)}
   * @return Returns the signed document plus additional information.
   * @throws PdfAsException
   *           Thrown, if an error occurs.
   * 
   * @see SignParameters
   * @see SignResult
   */
  public SignResult sign(SignParameters signParameters, SignatureDetailInformation signatureDetailInformation) throws PdfAsException;

  /**
   * Verifies a document with (potentially multiple) PDF-AS signatures.
   * 
   * @param verifyParameters
   *          The verify parameters.
   * @return Returns the verification results.
   * @throws PdfAsException
   *           Thrown, if an error occurs.
   * 
   * @see VerifyParameters
   * @see VerifyResults
   * @see VerifyResult
   */
  public VerifyResults verify(VerifyParameters verifyParameters) throws PdfAsException;

  /**
   * Analyzes a document for signatures and returns a verify-able list of such.
   * 
   * @param analyzeParameters
   *          The analyzation parameters.
   * @return Returns a list of verify-able signatures that were found in the
   *         document.
   * @throws PdfAsException
   *           Thrown on error.
   * 
   * @see AnalyzeParameters
   * @see AnalyzeResult
   * @see {@link #verify(AnalyzeResult)}
   */
  public AnalyzeResult analyze(AnalyzeParameters analyzeParameters) throws PdfAsException;

  /**
   * Reconstruct the <xmldsig:Signature> from the given parameters.
   * 
   * @param reconstructXMLDsigParameters
   *         The data from which to reconstruct the xmldsig
   * @return a list of xmldsigs, one for each signature in the document
   * @throws PdfAsException if the reconstruction fails
   */
  public ReconstructXMLDsigResult reconstructXMLDSIG(ReconstructXMLDsigParameters reconstructXMLDsigParameters) throws PdfAsException;

  /**
   * Reconstruct the <xmldsig:Signature> from the given parameters.
   * 
   * @param reconstructXMLDsigParameters
   *         The data from which to reconstruct the xmldsigs
   * @return a list of xmldsigs, one for each signature in the document
   * @throws PdfAsException
   */
  public ReconstructXMLDsigResult reconstructXMLDSIG(ReconstructXMLDsigAfterAnalysisParameters reconstructXMLDsigParameters) throws PdfAsException;
  
  /**
   * Verifies a list of signatures that have been analyzed previously.
   * 
   * @param verifyAfterAnalysisParameters The parameters.
   * 
   * @return Returns the verification results.
   * @throws PdfAsException
   *           Thrown on error.
   * 
   * @see AnalyzeResult
   * @see VerifyAfterAnalysisParameters
   * @see VerifyResults
   * @see VerifyResult
   * @see {@link #analyze(AnalyzeParameters)}
   */
  public VerifyResults verify(VerifyAfterAnalysisParameters verifyAfterAnalysisParameters) throws PdfAsException;

  /**
   * Verifies a list of signatures that have been analyzed previously and the xmldsigs have been reconstructed.
   * 
   * @param verifyAfterReconstructXMLDsigParameters
   *           The parameters.
   * @return the verification results.
   * @throws PdfAsException
   *           Thrown on error.
   */
  public VerifyResults verify(VerifyAfterReconstructXMLDsigParameters verifyAfterReconstructXMLDsigParameters) throws PdfAsException;
  
  /**
   * Reloads the configuration from the work directory.
   * 
   * @throws PdfAsException
   *           Thrown, if an error occurs.
   */
  public void reloadConfig() throws PdfAsException;

  /**
   * Returns the list of information objects about activated profiles available in the
   * configuration.
   * 
   * <p>
   * Note: Currently the profile information consists of the profile Id and the
   * MOA Key Id only.
   * </p>
   * <p>
   * Note: In near future the profile management will be moved out of the config
   * file into an API class representation of the profiles which may render this
   * (and related) methods obsolete.
   * </p>
   * 
   * @return Returns the list of {@link SignatureProfile} objects with
   *         information about active profiles available in the configuration.
   * @throws PdfAsException
   *           Thrown on error.
   * 
   * @see SignatureProfile
   */
  public List getProfileInformation() throws PdfAsException;

  /**
   * Create a signature profile dynamically. You have do apply() it for usage. See {@link SignatureProfile}.
   * @param parentProfile a parent profile id to inherit all properties
   * @param mode lifetime mode
   * @return the created signature profile to work with.
   */
  public DynamicSignatureProfile createDynamicSignatureProfile(String parentProfile, DynamicSignatureLifetimeEnum mode);

  /**
   * Create a signature profile dynamically. You have to provide a unique name and have do apply() it for usage. See {@link SignatureProfile}.
   * It is recommended to use {@link #createDynamicSignatureProfile(String, DynamicSignatureLifetimeEnum)} that generates 
   * a unique name on its own.
   * @see DynamicSignatureProfile
   * @param parentProfile a parent profile id to inherit all properties
   * @param myUniqueName a unique name for the profile
   * @param mode lifetime mode
   * @return the created signature profile to work with.
   */
  public DynamicSignatureProfile createDynamicSignatureProfile(String myUniqueName, String parentProfile, DynamicSignatureLifetimeEnum mode);
  
  /**
   * Create a signature profile dynamically. You have fill it with properties and apply() it for usage. See {@link SignatureProfile}.
   * <br> 
   * It is recommended to use {@link #createDynamicSignatureProfile(String, DynamicSignatureLifetimeEnum)} that inherits from an 
   * existing profile saving you a lot of work.
   * @param mode lifetime mode
   * @return the created signature profile to work with.
   * @see DynamicSignatureProfile
   */
  public DynamicSignatureProfile createEmptyDynamicSignatureProfile(DynamicSignatureLifetimeEnum mode);
  
  /**
   * Create a signature profile dynamically. You have fill it with properties and apply() it for usage. See {@link SignatureProfile}.
   * <br> 
   * It is recommended to use {@link #createDynamicSignatureProfile(String, DynamicSignatureLifetimeEnum)} that inherits from an 
   * existing profile saving you a lot of work.
   * @param myUniqueName a unique name for the profile
   * @param mode lifetime mode
   * @return the created signature profile to work with.
   */
  public DynamicSignatureProfile createEmptyDynamicSignatureProfile(String myUniqueName, DynamicSignatureLifetimeEnum mode);

  /**
   * Loads an existing dynamic signature profile by its name. Profiles are saved when they are applied 
   * and it has {@link DynamicSignatureLifetimeEnum#MANUAL}
   * @param profileName
   * @return the signature profile or <code>null</code> if not found.
   * @see DynamicSignatureProfile
   */
  public DynamicSignatureProfile loadDynamicSignatureProfile(String profileName);
  
  /**
   * Prepares the signature of the given PDF document. The table for the signature data is placed but not filled.<br/>
   * Usually used for preview.
   * 
   * @param signParameters
   *          The sign parameters.
   * @return Only the {@link SignatureDetailInformation#getSignaturePosition()}, {@link SignatureDetailInformation#getNonTextualObjects()}, {@link SignatureDetailInformation#getSignatureData()} are filled.
   * @throws PdfAsException if something goes wrong during the process
   */
  public SignatureDetailInformation prepareSign(SignParameters signParameters) throws PdfAsException;
  
  /**
   * Finish the signature process. The PDF is filled with the signature data.<br/>
   * Usually used if some steps like the actual signing are to be performed externally.
   * 
   * @param signParameters
   *          The sign parameters.
   * @param signatureDetailInformation
   *          The signature detail information.
   * @return
   * @throws PdfAsException
   */
  public SignResult finishSign(SignParameters signParameters, SignatureDetailInformation signatureDetailInformation) throws PdfAsException;

}
