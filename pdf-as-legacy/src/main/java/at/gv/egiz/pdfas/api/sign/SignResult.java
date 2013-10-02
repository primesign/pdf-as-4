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
package at.gv.egiz.pdfas.api.sign;

import java.security.cert.X509Certificate;
import java.util.List;

import at.gv.egiz.pdfas.api.analyze.NonTextObjectInfo;
import at.gv.egiz.pdfas.api.io.DataSink;
import at.gv.egiz.pdfas.api.sign.pos.SignaturePosition;

/**
 * The result of a sign operation.
 * 
 * @author wprinz
 */
public interface SignResult
{

  /**
   * Returns the filled output data sink.
   * 
   * @return Returns the filled output data sink.
   */
  public DataSink getOutputDocument();

  /**
   * Returns the certificate of the signer.
   * 
   * @return Returns the certificate of the signer.
   */
  public X509Certificate getSignerCertificate();

  /**
   * Returns the position where the signature is finally placed.
   * 
   * <p>
   * This information can be useful for post-processing the document.
   * </p>
   * 
   * <p>
   * Consult the PDF-AS documentation section Commandline for further
   * information about positioning.
   * </p>
   * 
   * @return Returns the position where the signature is finally placed. May
   *         return null if no position information is available.
   */
  public SignaturePosition getSignaturePosition();
  
  /**
   * Returns a list<{@link NonTextObjectInfo} of non textual objects in the pdf document.
   * Only available for textual signatures. Show this to the user who signed the textual content only! 
   * @return  List<{@link NonTextObjectInfo} or <tt>null</tt> of not available (binary signature)
   */
  public List getNonTextualObjects();
  
  /**
   * Returns if pdf has non textual objects (only for textual signature available).
   * @return
   */
  public boolean hasNonTextualObjects();
}
