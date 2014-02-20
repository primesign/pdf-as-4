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
package at.gv.egiz.pdfas.api.xmldsig;

import java.util.List;

import at.gv.egiz.pdfas.api.commons.Constants;
import at.gv.egiz.pdfas.api.commons.SignatureInformation;

/**
 * The result of a reconstructXMLDsig call.<br/>
 * This is just a wrapper for a list of {@link ExtendedSignatureInformation}s
 * 
 * 
 * @author exthex
 */
public class ReconstructXMLDsigResult {

   private List extendedSignatures;
   
   private String device;
   
   /**
    * 
    * @param extendedSignatureInfos
    * @param signatureDevice
    */
   public ReconstructXMLDsigResult(List extendedSignatureInfos, String signatureDevice) {
      this.extendedSignatures = extendedSignatureInfos;
      this.device = signatureDevice;
   }

   /**
    * Get the signature device that was used to create this result.
    * 
    * @return {@link Constants#SIGNATURE_DEVICE_MOA} or {@link Constants#SIGNATURE_DEVICE_BKU}
    */
   public String getDevice() {
      return device;
   }
   
   /**
    * Returns the list of found signatures.
    * 
    * @return Returns a list of {@link ExtendedSignatureInformation} objects representing all
    *         found signatures + {@link XMLDsigData}.
    * @see SignatureInformation
    */
   public List getExtendedSignatures() {
      return this.extendedSignatures;
   }
   
}
