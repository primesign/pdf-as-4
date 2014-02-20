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
package at.gv.egiz.pdfas.api.analyze;

import java.util.List;

import at.gv.egiz.pdfas.api.commons.SignatureInformation;
import at.gv.egiz.pdfas.api.exceptions.PdfAsException;

/**
 * The result of an analyze operation, which is a list of verifyable signatures.
 * 
 * @author wprinz
 * 
 */
@Deprecated
public interface AnalyzeResult
{
  /**
   * Returns the list of found signatures.
   * 
   * @return Returns a list of {@link SignatureInformation} objects representing all
   *         found signatures.
   * @throws PdfAsException
   *           Thrown on error.
   * 
   * @see SignatureInformation
   */
  public List getSignatures() throws PdfAsException;
  
  public List getNoSignatures();
  
  /**
   * Tells if the document has been corrected before verification. The correction maybe done
   * after a first failing parse to repair a document (if enabled in the configuration 
   * <code>correct_document_on_verify_if_necessary</code>). The correction can only work for textual 
   * signatures. Binary signatures are lost anyhow.
   * @return
   */
  public boolean hasBeenCorrected();

  
}
