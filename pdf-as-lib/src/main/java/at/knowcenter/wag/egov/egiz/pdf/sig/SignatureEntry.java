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
 *
 * $Id: SignatureEntry.java,v 1.3 2006/08/25 17:09:41 wprinz Exp $
 */
package at.knowcenter.wag.egov.egiz.pdf.sig;

import java.io.Serializable;

/**
 * This class is to store a signature entry. The signature entry is 3-tupel. A key that is defined
 * or declarated in the settings file, an optional caption or a value. <br>
 * An additional helper value is a marker for the start index of the key, if the key is found in an
 * analysing process extracting captions and values from a raw signature text.
 * 
 * @author wlackner
 * @see at.knowcenter.wag.egov.egiz.sig.SignatureObject
 */
public class SignatureEntry implements Serializable {

  /**
   * SVUID.
   */
  private static final long serialVersionUID = 4640380069301731879L;
  
  /**
   * The signature key.
   */
  private String key_ = null;
  /**
   * The signature caption for the key found or set in the signature text.
   */
  private String caption_ = null;
  /**
   * The signature value for the key found or set in the signature text.
   */
  private String value_ = null;
  /**
   * The starting index position of the key if it is found in the signature text.
   */
  private int startIndex_ = -1;
  
  public boolean isPlaceholder = false;

  /**
   * The empty constructor.
   */
  public SignatureEntry() {
  }

  /**
   * A new <code>SignatureEntry</code> init with the key.
   * 
   * @param key
   */
  public SignatureEntry(String key) {
    key_ = key;
  }

  /**
   * Returns the caption off the current key.
   * 
   * @return Returns the caption.
   */
  public String getCaption() {
    return caption_;
  }

  /**
   * Set the caption of the current key.
   * 
   * @param caption The caption to set.
   */
  public void setCaption(String caption) {
    caption_ = caption;
  }

  /**
   * Return the current key.
   * 
   * @return Returns the key.
   */
  public String getKey() {
    return key_;
  }

  /**
   * Set the current key.
   * 
   * @param key The key to set.
   */
  public void setKey(String key) {
    key_ = key;
  }

  /**
   * Return the start position of the key that caption is found in the signature text.
   * 
   * @return Returns the startIndex.
   */
  public int getStartIndex() {
    return startIndex_;
  }

  /**
   * Set the start position of the current key.
   * 
   * @param startIndex The startIndex to set.
   */
  public void setStartIndex(int startIndex) {
    startIndex_ = startIndex;
  }

  /**
   * Return the value of the current key.
   * 
   * @return Returns the value.
   */
  public String getValue() {
    return value_;
  }

  /**
   * Set the value of the current key.
   * 
   * @param value The value to set.
   */
  public void setValue(String value) {
    value_ = value;
  }

  /**
   * The toString method, used for tests or debugging.
   */
  public String toString() {
    String the_string = "";
    the_string += "\n    Key:" + key_;
    the_string += "\nCaption:" + caption_;
    the_string += "\n  Value:" + value_;
//    the_string += "\nStart I:" + startIndex_;
    return the_string;
  }
}