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
package at.gv.egiz.pdfas.api.xmldsig;

import at.gv.egiz.pdfas.api.analyze.AnalyzeResult;
import at.gv.egiz.pdfas.api.commons.Constants;

/**
 * Parameters for the reconstructXMLDsig method which is to be called after a analyze call.
 * 
 * @author exthex
 *
 */
public class ReconstructXMLDsigAfterAnalysisParameters {
   

   /**
    * The list of signatures to be verified.
    */
   protected AnalyzeResult analyzeResult = null;

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
    * @return the analyzeResult
    */
   public AnalyzeResult getAnalyzeResult()
   {
     return this.analyzeResult;
   }

   /**
    * @param analyzeResult
    *          the analyzeResult to set
    */
   public void setAnalyzeResult(AnalyzeResult analyzeResult)
   {
     this.analyzeResult = analyzeResult;
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
}
