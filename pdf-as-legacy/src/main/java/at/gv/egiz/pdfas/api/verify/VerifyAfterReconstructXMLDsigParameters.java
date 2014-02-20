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

import at.gv.egiz.pdfas.api.commons.Constants;
import at.gv.egiz.pdfas.api.xmldsig.ReconstructXMLDsigResult;

/**
 * This class represents the parameters needed for verify after reconstructXMLDsig has already been executed. 
 * 
 * @author exthex
 *
 */
public class VerifyAfterReconstructXMLDsigParameters {

   /**
    * The list of signatures to be verified.
    */
   protected ReconstructXMLDsigResult reconstructXMLDsigResult = null;

   /**
    * The signature device to perform the actual signature.
    * 
    * <p>
    * May be {@link Constants#SIGNATURE_DEVICE_MOA} or
    * {@link Constants#SIGNATURE_DEVICE_BKU}.
    * </p>
    */
   protected String signatureDevice;

   /**
    * Allows to pass a VerificationTime to the signature device.
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

   /**
    * The index of the signature to be verified. A value < 0 indicates to verify all signatures.
    */
   protected int verifySignatureIndex = -1;

   /**
    * @return the reconstructXMLDsigResult
    */
   public ReconstructXMLDsigResult getReconstructXMLDsigResult()
   {
     return this.reconstructXMLDsigResult;
   }

   /**
    * @param reconstructXMLDsigResult
    *          the reconstructXMLDsigResult to set
    */
   public void setReconstructXMLDsigResult(ReconstructXMLDsigResult reconstructXMLDsigResult)
   {
     this.reconstructXMLDsigResult = reconstructXMLDsigResult;
   }

   /**
    * @return the signatureDevice
    */
   public String getSignatureDevice()
   {
     return this.signatureDevice;
   }

   /**
    * Set the signature device to use for verification. 
    * If none is set here, the signature device that was used for reconstructXMLDsig will be used.
    *  
    * @param signatureDevice
    *          the signatureDevice to set
    */
   public void setSignatureDevice(String signatureDevice)
   {
     this.signatureDevice = signatureDevice;
   }

   /**
    * @return the verificationTime
    */
   public Date getVerificationTime()
   {
     return this.verificationTime;
   }

   /**
    * @param verificationTime the verificationTime to set
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

   /**
    * Set the index of the signature to verify (index starting at 0). A value < 0 indicates to verify all values.
    * @param verify_which
    */
   public void setVerifySignatureIndex(int verify_which) {
     this.verifySignatureIndex  = verify_which;
   }

   public int getVerifySignatureIndex() {
      return verifySignatureIndex;
   }

   /**
    * @see VerifyParameters#setSuppressVerifyExceptions(boolean)
    * @param suppress
    */
   public void setSuppressVerifyExceptions(boolean suppress) {
      VerifyParameters.setSuppressVerify(suppress);
   }

   public boolean isSuppressVerifyExceptions() {
      return VerifyParameters.isSuppressVerifyExceptions();
   }

}
