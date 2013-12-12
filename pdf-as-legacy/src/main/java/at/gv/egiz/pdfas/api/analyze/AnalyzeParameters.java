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

import at.gv.egiz.pdfas.api.commons.Constants;
import at.gv.egiz.pdfas.api.io.DataSource;

/**
 * Parameter object that holds the analyze parameters.
 * 
 * @author wprinz
 */
@Deprecated
public class AnalyzeParameters
{

  /**
   * The document to be analyzed.
   */
  protected DataSource document = null;

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

  protected boolean returnNonTextualObjects = false;
  
  protected boolean hasBeenCorrected = false;
  
  /**
   * @return the document
   */
  public DataSource getDocument()
  {
    return this.document;
  }

  /**
   * @param document the document to set
   */
  public void setDocument(DataSource document)
  {
    this.document = document;
  }

  /**
   * @return the verifyMode
   */
  public String getVerifyMode()
  {
    return this.verifyMode;
  }

  /**
   * @param verifyMode the verifyMode to set
   */
  public void setVerifyMode(String verifyMode)
  {
    this.verifyMode = verifyMode;
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
  
}
