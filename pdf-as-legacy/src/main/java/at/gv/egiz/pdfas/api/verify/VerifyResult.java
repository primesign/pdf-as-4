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
package at.gv.egiz.pdfas.api.verify;

import java.util.Date;
import java.util.List;

import at.gv.egiz.pdfas.api.PdfAs;
import at.gv.egiz.pdfas.api.analyze.NonTextObjectInfo;
import at.gv.egiz.pdfas.api.commons.SignatureInformation;
import at.gv.egiz.pdfas.api.exceptions.PdfAsException;
import at.gv.egiz.pdfas.api.xmldsig.XMLDsigData;

/**
 * Encapsulates the data of a verification of one signature.
 * 
 * @author wprinz
 */
public interface VerifyResult extends SignatureInformation
{
   /**
    * Returns if the verification was possible or could not even be startet. see {@link #getVerificationException()} for details.
    * @return
    */
   public boolean isVerificationDone();
   
   /**
    * Returns a verification exception if any. Shows that the verification could not be started. See {@link #isVerificationDone()}.
    * @return
    */
   public PdfAsException getVerificationException();
   
  /**
   * Returns the result of the certificate check.
   * 
   * @return Returns the result of the certificate check.
   */
  public SignatureCheck getCertificateCheck();

  /**
   * Returns the result of the value (and hash) check.
   * 
   * @return Returns the result of the value (and hash) check.
   */
  public SignatureCheck getValueCheckCode();

  /**
   * Returns the result of the manifest check.
   * 
   * @return Returns the result of the manifest check.
   */
  public SignatureCheck getManifestCheckCode();

  /**
   * Returns true, if the signer's certificate is a qualified certificate.
   * 
   * @return Returns true, if the signer's certificate is a qualified
   *         certificate.
   */
  public boolean isQualifiedCertificate();
  
  /**
   * Returns {@code true} if public authority is indicated.
   * @return {@code true} if public authority.
   */
  public boolean isPublicAuthority();
  
  /**
   * Returns the public authority code or {@code null}.
   * @return The public authority code or {@code null}.
   */
  public String getPublicAuthorityCode();

  /**
   * Returns a list of Strings each stating one public property of the
   * certificate.
   * 
   * <p>
   * Such public properties are certificate extensions each being assigned an
   * own OID. For example the public property "Verwaltungseigenschaft" has the
   * OID "1.2.40.0.10.1.1.1".
   * </p>
   * 
   * @return Returns the list of Strings representing the public properties of
   *         this certificate, if any.
   */
  public List getPublicProperties();

  /**
   * Returns the verification time, which is the time when the signature was
   * verified.
   * 
   * <p>
   * Note that this is actually the Date passed to the verify methods over
   * {@link VerifyParameters#setVerificationTime(Date)} or
   * {@link VerifyAfterAnalysisParameters#setVerificationTime(Date)}. The
   * signature devices don't respond the actual verification time so there is no
   * guarantee that the set verification time was actually used as time of
   * verification. Please consult the device's documentation for more
   * information.
   * </p>
   * <p>
   * If the verification device does not return a verification time and no
   * verification time was set in the
   * {@link VerifyParameters#setVerificationTime(Date)} or
   * {@link VerifyAfterAnalysisParameters#setVerificationTime(Date)}, the time
   * returned by this method will be equal to the signing time (
   * {@link SignatureInformation#getSigningTime()}).
   * </p>
   * 
   * @return Returns the verification time, which is the time when the signature
   *         was verified.
   */
  public Date getVerificationTime();

  /**
   * Returns the hash input data as returned by MOA as Base64-encoded String.
   * 
   * <p>
   * This will only return a value other than null if the corresponding
   * {@link VerifyParameters} has been set to true.
   * </p>
   * <p>
   * Note that the HashInputData does not necessarily have to be exactly the
   * same as the signed data return by the
   * {@link SignatureInformation#getSignedData()} method.
   * </p>
   * 
   * @return Returns the base64 encoded hash input data as returned by MOA.
   * 
   * @see SignatureInformation#getSignedData()
   */
  public String getHashInputData();
  
  /**
   * Returns a list<{@link NonTextObjectInfo}> of non textual objects in the pdf document.
   * Only available for textual signatures. Show this to the user who signed the textual content only! 
   * @return  List<{@link NonTextObjectInfo} or <tt>null</tt> of not available (binary signature)
   */
  public List getNonTextualObjects();
  

  /**
   * Returns <code>true</code> if non textual objects have been found, <code>false</code> if not.
   * @return <code>true</code> if non textual objects have been found, <code>false</code> if not.
   */
  public boolean hasNonTextualObjects();

  /**
   * Get the reconstructed xmldsig XML data. The reconstruction is done during the verification process.
   * 
   * @see PdfAs#reconstructXMLDSIG(at.gv.egiz.pdfas.api.xmldsig.ReconstructXMLDsigParameters)
   * @see PdfAs#reconstructXMLDSIG(at.gv.egiz.pdfas.api.xmldsig.ReconstructXMLDsigAfterAnalysisParameters)
   * @return
   */
  public XMLDsigData getReconstructedXMLDsig();

}
