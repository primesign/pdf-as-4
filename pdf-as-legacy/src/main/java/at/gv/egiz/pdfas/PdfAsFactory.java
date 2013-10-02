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
package at.gv.egiz.pdfas;

import java.io.File;

import at.gv.egiz.pdfas.api.PdfAs;
import at.gv.egiz.pdfas.api.exceptions.PdfAsException;
import at.gv.egiz.pdfas.wrapper.PdfAsObject;

/**
 * Main factory for creating a PDF-AS API Instance (PdfAs Interface).
 * 
 * @see PdfAs
 * 
 * @author wprinz
 */
public class PdfAsFactory
{
   /**
    * Creates a PDF-AS API instance for the given work directory.
    * 
    * @param workDirectory
    *          The work directory. If <code>null</code> the configuration is assumed to be located
    *          within the user's home directory. Note: IAIK JCE and IAIK ECC security provders are
    *          automatically registered.
    * 
    * @return Returns an instance of the PDF-AS API.
    * @throws IllegalArgumentException
    *           Thrown, if the workDirectory doesn't exist.
    * @throws PdfAsException
    *           Thrown, if the work directory does not meet its requirements, or
    *           if the config file is invalid.
    * @see PdfAS#USERHOME_CONFIG_FOLDER          
    */
   public static PdfAs createPdfAs(File workDirectory) throws PdfAsException
   {
      return createPdfAs(workDirectory);
   }
   
   /**
    * Creates a PDF-AS API instance for the given work directory.
    * 
    * WARNING registerProvider is IGNORED as ov Version 4.0
    * 
    * @param workDirectory
    *          The work directory. If <code>null</code> the configuration is assumed to be located
    *          within the user's home directory.
    * 
   * @param registerProvider <code>true</code>: automatically registers IAIK JCE and ECC Provider;
   * <code>false</code>: providers will NOT be automatically registered, providers
   * needed have to be registered by the API user 
    * @return Returns an instance of the PDF-AS API.
    * @throws IllegalArgumentException
    *           Thrown, if the workDirectory doesn't exist.
    * @throws PdfAsException
    *           Thrown, if the work directory does not meet its requirements, or
    *           if the config file is invalid.
    * @see PdfAS#USERHOME_CONFIG_FOLDER          
    */
   public static PdfAs createPdfAs(File workDirectory, boolean registerProvider) throws PdfAsException
   {
     return new PdfAsObject(workDirectory);
   }
   
   /**
    * Creates a PDF-AS API instance assuming that the configuration is located within the user's
    * home directory. Note: IAIK JCE and IAIK ECC security providers are automatically registered.
    * 
    * @return Returns an instance of the PDF-AS API.
    * @throws IllegalArgumentException
    *           Thrown, if the work directory doesn't exist within the user's home directory.
    * @throws PdfAsException
    *           Thrown, if the work directory does not meet its requirements, or
    *           if the config file is invalid.
    * @see PdfAS#USERHOME_CONFIG_FOLDER          
    */
   public static PdfAs createPdfAs() throws PdfAsException
   {
      return createPdfAs(null);
   }
   
   /**
    * Creates a PDF-AS API instance assuming that the configuration is located within the user's
    * home directory.
    * 
    * WARNING registerProvider is IGNORED as ov Version 4.0
    * 
    * @return Returns an instance of the PDF-AS API.
    * @param registerProvider <code>true</code>: automatically registers IAIK JCE and ECC Provider;
    * <code>false</code>: providers will NOT be automatically registered, providers
    * needed have to be registered by the API user 
    * @throws IllegalArgumentException
    *           Thrown, if the work directory doesn't exist within the user's home directory.
    * @throws PdfAsException
    *           Thrown, if the work directory does not meet its requirements, or
    *           if the config file is invalid.
    * @see PdfAS#USERHOME_CONFIG_FOLDER          
    */
   public static PdfAs createPdfAs(boolean registerProvider) throws PdfAsException
   {
     return createPdfAs(null, registerProvider);
   }

}
