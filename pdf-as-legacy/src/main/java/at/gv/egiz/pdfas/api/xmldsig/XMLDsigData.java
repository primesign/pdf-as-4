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

/**
 * A container for XMLDsig data.
 * 
 * @author exthex
 *
 */
public class XMLDsigData {

   private String xmlDsig;
   
   private boolean detached;
   
   /**
    * Constructor.
    * 
    * @param xmldsig the xml string of the xmldsig. 
    * @param detached true if detached, false otherwise
    */
   public XMLDsigData(String xmldsig, boolean detached) {
      this.xmlDsig = xmldsig;
      this.detached = detached;
   }

   /**
    * Get the xmldsig string
    * @return
    */
   public String getXmlDsig() {
      return xmlDsig;
   }

   /**
    * Set the xmldsig string.
    * 
    * @param xmlDsig
    */
   public void setXmlDsig(String xmlDsig) {
      this.xmlDsig = xmlDsig;
   }

   /**
    * 
    * @return true if detached, false otherwise
    */
   public boolean isDetached() {
      return detached;
   }

   /**
    * Set the detached.
    * 
    * @param detached
    */
   public void setDetached(boolean detached) {
      this.detached = detached;
   }

}
