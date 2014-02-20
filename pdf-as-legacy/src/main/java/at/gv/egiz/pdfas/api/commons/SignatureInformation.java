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
 *
 * $Id: SignatureHolder.java,v 1.3 2006/10/11 07:57:58 wprinz Exp $
 */
package at.gv.egiz.pdfas.api.commons;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import at.gv.egiz.pdfas.api.analyze.NonTextObjectInfo;
import at.gv.egiz.pdfas.api.io.DataSource;

/**
 * Holds the information of one found signature block, which is the signed data
 * and the corresponding signature information.
 * 
 * @author wprinz
 */
@Deprecated
public interface SignatureInformation
{
  /**
   * Returns the type of this signature (binary/textual).
   * 
   * <p>
   * May be {@link Constants#SIGNATURE_TYPE_BINARY} or
   * {@link Constants#SIGNATURE_TYPE_TEXTUAL}.
   * </p>
   * 
   * @return Returns the type of this signature (binary/textual).
   */
  public String getSignatureType();

  /**
   * Returns the DataSource providing the data that was signed.
   * 
   * <p>
   * Note that this is the signed data as sent to the verification device by
   * PDF-AS. The verification device (e.g. MOA) may perform several other
   * transformations on the data before feeding it to the signature hash
   * function. To get the actual hashed data use the ReturnHashInputData mechanism (which is very slow).
   * </p>
   * 
   * @return Returns the DataSource providing the data that was signed.
   * 
   * @see at.gv.egiz.pdfas.api.verify.VerifyParameters#setReturnHashInputData(boolean)
   * @see at.gv.egiz.pdfas.api.verify.VerifyResult#getHashInputData()
   * 
   */
  public DataSource getSignedData();

  /**
   * Returns the certificate of the signer.
   * 
   * <p>
   * Information like subject name, issuer name or serial number can be
   * retrieved form this certificate.
   * </p>
   * 
   * @return Returns the certificate of the signer.
   */
  public X509Certificate getSignerCertificate();

  /**
   * Returns the signing time, which is the time when the signature was created.
   * 
   * @return Returns the signing time, which is the time when the signature was
   *         created.
   */
  public Date getSigningTime();

  /**
   * Returns additional, internal information about the found signature.
   * 
   * <p>
   * Note that this provides a way for developers to gather core information
   * about the signature. What information is returned strongly depends on the
   * core implementation.
   * </p>
   * 
   * @return Returns additional, internal information about the signature. Null
   *         means that no additional information is available.
   */
  public Object getInternalSignatureInformation();
  
  /**
   * Returns the embedded /TimeStamp value (b64 encoded) from the signature if available.
   * @return 
   */
  public String getTimeStampValue();
  
  /**
   * Returns a list<{@link NonTextObjectInfo}> of non textual objects in the pdf document.
   * Only available for textual signatures. Show this to the user who signed the textual content only! 
   * @return  List<{@link NonTextObjectInfo} or <tt>null</tt> of not available (binary signature)
   */
  public List getNonTextualObjects();
  
  public void setNonTextualObjects(List nonTextualObjects);

  /**
   * Returns <code>true</code> if non textual objects have been found, <code>false</code> if not.
   * @return <code>true</code> if non textual objects have been found, <code>false</code> if not.
   */
  public boolean hasNonTextualObjects();


}
