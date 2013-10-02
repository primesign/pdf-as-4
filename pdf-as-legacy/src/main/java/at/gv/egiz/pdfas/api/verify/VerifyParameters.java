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

import at.gv.egiz.pdfas.api.commons.Constants;
import at.gv.egiz.pdfas.api.io.DataSource;

/**
 * Parameter object that holds the verify parameters.
 * 
 * @author wprinz
 */
public class VerifyParameters
{
  // This would be a perfect point for multiple inheritance in Java.
  // VerifyParameters extends AnalyzeParameters, VerifyAfterAnalysisParameters
  // Then a lot of code could be easily reused in the PdfAsObject's check*Parameters methods.

  /**
   * The document to be verified.
   */
  protected DataSource document = null;

  /**
   * The signature device to perform the actual signature.
   * 
   * <p>
   * May be {@link Constants#SIGNATURE_DEVICE_MOA} or
   * {@link Constants#SIGNATURE_DEVICE_BKU}.
   * </p>
   */
  protected String signatureDevice = Constants.SIGNATURE_DEVICE_MOA;

  /**
   * The mode of operation how the document is analyzed.
   * 
   * <p>
   * May be {@link Constants#VERIFY_MODE_BINARY_ONLY} to check the document for
   * binary signatures only (very fast). Or may be
   * {@link Constants#VERIFY_MODE_SEMI_CONSERVATIVE} to perform a semi
   * conservative (optimized) text and binary verification (slow). Or may be
   * {@link Constants#VERIFY_MODE_FULL_CONSERVATIVE} to perform a full
   * conservative text and binary verification (very slow).
   * </p>
   */
  protected String verifyMode = Constants.VERIFY_MODE_FULL_CONSERVATIVE;

  /**
   * The (zero based) index of the signature to verify.
   * 
   * <p>
   * This allows to verify only one found signature instead of all. {@link Constants#VERIFY_ALL} means to
   * verify all found signatures.
   * </p>
   */
  protected int signatureToVerify = Constants.VERIFY_ALL;

  /**
   * Allows to pass a VerificationTime to the verification device.
   * 
   * <p>
   * Note that the actual usage of this parameter depends on the verification device.
   * </p>
   */
  protected Date verificationTime = null;

  /**
   * Tells the signature device (e.g. MOA) to return the signature hash input
   * data (which is the probably transformed signed data).
   * 
   * <p>
   * Note that this forces MOA to return the potentially large signature data to
   * be returned in the result XML, which may result in very bad performance.
   * </p>
   */
  protected boolean returnHashInputData = false;
    
  protected boolean returnNonTextualObjects = false;
  
  private static ThreadLocal suppressVerifyExceptions = new ThreadLocal();

  
  public VerifyParameters() {
     suppressVerifyExceptions.set(Boolean.FALSE);
  }
  /**
   * @return the document
   */
  public DataSource getDocument()
  {
    return this.document;
  }

  /**
   * @param document
   *          the document to set
   */
  public void setDocument(DataSource document)
  {
    this.document = document;
  }

  /**
   * @return the signatureDevice
   */
  public String getSignatureDevice()
  {
    return this.signatureDevice;
  }

  /**
   * @param signatureDevice
   *          the signatureDevice to set
   */
  public void setSignatureDevice(String signatureDevice)
  {
    this.signatureDevice = signatureDevice;
  }

  /**
   * @return the verifyMode
   */
  public String getVerifyMode()
  {
    return this.verifyMode;
  }

  /**
   * @param verifyMode
   *          the verifyMode to set
   */
  public void setVerifyMode(String verifyMode)
  {
    this.verifyMode = verifyMode;
  }

  /**
   * @return the signatureToVerify
   */
  public int getSignatureToVerify()
  {
    return this.signatureToVerify;
  }

  /**
   * @param signatureToVerify
   *          the signatureToVerify to set
   */
  public void setSignatureToVerify(int signatureToVerify)
  {
    this.signatureToVerify = signatureToVerify;
  }

  /**
   * @return the verificationTime
   */
  public Date getVerificationTime()
  {
    return this.verificationTime;
  }

  /**
   * @param verificationTime
   *          the verificationTime to set
   */
  public void setVerificationTime(Date verificationTime)
  {
    this.verificationTime = verificationTime;
  }

  /**
   * @return the returnHashInputData
   */
  public boolean isReturnHashInputData()
  {
    return this.returnHashInputData;
  }

  /**
   * @param returnHashInputData
   *          the returnHashInputData to set
   */
  public void setReturnHashInputData(boolean returnHashInputData)
  {
    this.returnHashInputData = returnHashInputData;
  }

   public boolean isReturnNonTextualObjects() {
      return this.returnNonTextualObjects;
   }
   
   /**
    * Tells if non text object of the signed pdf should be extracted and returned.
    * One should show this to the user, especially in case of textual signature.
    * Defaults to <tt>false</tt>
    * 
    * @param returnNonTextualObjects
    */
   public void setReturnNonTextualObjects(boolean returnNonTextualObjects) {
      this.returnNonTextualObjects = returnNonTextualObjects;
   }
   
   /**
    * Set if verify exceptions (because of unknown signatures) are suppressed or not (default).
    * Suppressing can be helpful for multiple signatures if you want to verify the working rest. Unsupported
    * Signatures are reported without throwing an exception via {@link VerifyResult#getVerificationException()}
    * @param suppress
    */
   public void setSuppressVerifyExceptions(boolean suppress) {      
      setSuppressVerify(suppress);     
   }
   
   /**
    * See {@link #setSuppressVerifyExceptions(boolean)}
    * @return
    */
   public static boolean isSuppressVerifyExceptions() {
      if (suppressVerifyExceptions.get() == null) return false;
      return ((Boolean) suppressVerifyExceptions.get()).booleanValue(); 
   }
  
   static void setSuppressVerify(boolean suppress) {
	   suppressVerifyExceptions.set(new Boolean(suppress)); 
   }

}
