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
import java.util.Date;
import java.util.List;
import java.util.Map;

import at.gv.egiz.pdfas.api.analyze.NonTextObjectInfo;
import at.gv.egiz.pdfas.api.io.DataSource;
import at.gv.egiz.pdfas.api.sign.pos.SignaturePosition;

/**
 * A container for all relevant signature related data.
 * 
 * @author exthex
 */
public interface SignatureDetailInformation
{
  public DataSource getSignatureData();

  /**
   * Returns the position where the signature table was actually placed.
   * 
   * @return Returns the position where the signature table was actually placed.
   */
  public SignaturePosition getSignaturePosition();
  
  /**
   * Returns a list<{@link NonTextObjectInfo} of non textual objects in the pdf document.
   * Only available for textual signatures. Show this to the user who signed the textual content only! 
   * @return  List<{@link NonTextObjectInfo} or <tt>null</tt> of not available (binary signature)
   */
  public List getNonTextualObjects();
  
  /**
   * Returns the date of signature extracted from the signature.
   * @return
   */
  public Date getSignDate();
  
  /**
   * Get the name of the issuer. 
   * Short for {@link SignatureDetailInformation#getX509Certificate()#getIssuer()#getName()}
   * 
   * @return
   */
  public String getIssuer();

  /**
   * Short for {@link SignatureDetailInformation#getX509Certificate()#getIssuerDNMap()}
   * 
   * @return
   */
  public Map getIssuerDNMap();
  
  /**
   * Short for {@link SignatureDetailInformation#getX509Certificate()#getSubjectName()#toString()}
   * 
   * @return
   */
  public String getSubjectName();
  
  /**
   * Short for {@link SignatureDetailInformation#getX509Certificate()#getSerialNumber()#toString()}
   * 
   * @return
   */
  public String getSerialNumber();
  
  /**
   * Get the algorithm the signature was created with.
   * @return
   */
  public String getSigAlgorithm();
  
  /**
   * 
   * @return the signature id.
   */
  public String getSigID();
  
  /**
   * 
   * @return the signature method.
   */
  public String getSigKZ();
  
  /**
   * 
   * @return the signature value.
   */
  public String getSignatureValue();
  
  /**
   * 
   * @return the signature time stamp.
   */
  public String getSigTimeStamp();
  
  /**
   * Short for {@link SignatureDetailInformation#getX509Certificate()#getSubjectDNMap()}
   * 
   * @return
   */
  public Map getSubjectDNMap();

  /**
   * 
   * @return the certificate used for signature.
   */
  public X509Certificate getX509Certificate();
  
  /**
   * 
   * @return true if the signature is textual, false otherwise.
   */
  public boolean isTextual();
  
  /**
   * 
   * @return true if this signature is binary, false otherwise.
   */
  public boolean isBinary();
}
