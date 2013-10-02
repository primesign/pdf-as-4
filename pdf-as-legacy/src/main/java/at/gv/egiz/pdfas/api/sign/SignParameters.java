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

import java.util.Properties;

import at.gv.egiz.pdfas.api.commons.Constants;
import at.gv.egiz.pdfas.api.io.DataSink;
import at.gv.egiz.pdfas.api.io.DataSource;
import at.gv.egiz.pdfas.api.sign.pos.SignaturePositioning;
import at.gv.egiz.pdfas.api.timestamp.TimeStamper;
import at.knowcenter.wag.egov.egiz.sig.SignatureTypes;

/**
 * Parameter object that holds the sign parameters.
 *
 * @author wprinz
 */
public class SignParameters
{
// 23.11.2010 changed by exthex - added parameters for placeholder handling

  /**
   * The document to be signed.
   *
   * <p>
   * The DataSource implementation encapsulates the actual representaion of the
   * data. E.g. the DataSource may be File based or byte array based. See
   * package at.gv.egiz.pdfas.framework.input and at.gv.pdfas.impl.input
   * </p>
   */
  protected DataSource document = null;

  /**
   * The type of the signature.
   *
   * <p>
   * May be {@link Constants#SIGNATURE_TYPE_BINARY} or
   * {@link Constants#SIGNATURE_TYPE_TEXTUAL}.
   * </p>
   */
  protected String signatureType = Constants.DEFAULT_SIGNATURE_TYPE;

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
   * The signature profile identifier identifying the profile to be used in the
   * config file.
   *
   * <p>
   * Note: In near future it will be possible to provide a full specified
   * profile here instead of the profile id.
   * </p>
   */
  protected String signatureProfileId = null;

  /**
   * The signature key identifier specifying which signature key should be used
   * by the signature device to perform the signature.
   *
   * <p>
   * Providing a null value (default) means that no explicit signature key
   * identifier is provided. The selected signature device will then use its
   * default mechanism for retrieving this information (which is usually to read
   * the key from the provided signature profile).
   * </p>
   * <p>
   * Note that not all signature devices may support this parameter.
   * If a signature device doesn't support this parameter the value should be null.
   * </p>
   * <p>
   * This key is usually passed straight through to the signature device and
   * thereby has to contain an appropriate value for the signature device
   * chosen.
   * </p>
   * <p>
   * Currently, only the {@link Constants#SIGNATURE_DEVICE_MOA} signature device
   * evaluates this parameter and passes the provided String to MOA as the MOA
   * key group identifier. If null is provided, the MOA signature device will
   * determine the signature key identifier to be used from the provided profile
   * and, if not specified there either, from the MOA default configuration.
   * </p>
   */
  protected String signatureKeyIdentifier = null;

  /**
   * The signature position. Consult the PDF-AS documentation section
   * Commandline.
   */
  protected SignaturePositioning signaturePositioning = null;

  /**
   * The output DataSink that will receive the signed document.
   */
  protected DataSink output = null;

  protected TimeStamper timeStamperImpl;

  /**
   * The flag to de-/activate placeholder search
   */
  protected Boolean checkForPlaceholder = null;

  /**
   * The id of the placeholder which should be replaced.
   */
  protected String placeholderId;

  /**
   * The matching mode for placeholder extraction.<br/>
   * If a {@link SignParameters#placeholderId} is set, the match mode determines what is to be done, if no matching placeholder is found in the document.
   * <br/>
   * Defaults to {@link Constants#PLACEHOLDER_MATCH_MODE_MODERATE}.
   */
  protected int placeholderMatchMode = Constants.PLACEHOLDER_MATCH_MODE_MODERATE;

  protected Properties overrideProps = new Properties();





  /**
   * {@link #setTimeStamperImpl(TimeStamper)}
   * @return
   */
  public TimeStamper getTimeStamperImpl() {
     return this.timeStamperImpl;
  }

  /**
   * Set a {@link TimeStamper} to create a timestamp on the signature value. Will be
   * called after sign. For binary signatures only. Timestamp will be embedded in egiz dict /TimeStamp.
   * @param timeStamperImpl
   */
  public void setTimeStamperImpl(TimeStamper timeStamperImpl) {
     this.timeStamperImpl = timeStamperImpl;
  }

/**
   * @return the document
   */
  public DataSource getDocument()
  {
    return document;
  }

  /**
   * @param document
   *          the document to set
   */
  public void setDocument(DataSource document)
  {
    this.document = document;
  }

  /**
   * @return the signatureType
   */
  public String getSignatureType()
  {
    return signatureType;
  }

  /**
   * @param signatureType
   *          the signatureType to set
   */
  public void setSignatureType(String signatureType)
  {
    this.signatureType = signatureType;
  }

  /**
   * @return the signatureDevice
   */
  public String getSignatureDevice()
  {
    return signatureDevice;
  }

  /**
   * @param signatureDevice
   *          the signatureDevice to set
   */
  public void setSignatureDevice(String signatureDevice)
  {
    this.signatureDevice = signatureDevice;
  }

  /**
   * @return the signatureProfileId
   */
  public String getSignatureProfileId()
  {
    return signatureProfileId;
  }

  /**
   * @param signatureProfileId
   *          the signatureProfileId to set
   */
  public void setSignatureProfileId(String signatureProfileId)
  {
    this.signatureProfileId = signatureProfileId;
  }

  /**
   * @return the signaturePositioning
   */
  public SignaturePositioning getSignaturePositioning()
  {
    return this.signaturePositioning;
  }

  /**
   * @param signaturePositioning
   *          the signaturePositioning to set
   */
  public void setSignaturePositioning(SignaturePositioning signaturePositioning)
  {
    this.signaturePositioning = signaturePositioning;
  }

  /**
   * @return the output
   */
  public DataSink getOutput()
  {
    return output;
  }

  /**
   * @param output
   *          the output to set
   */
  public void setOutput(DataSink output)
  {
    this.output = output;
  }

  /**
   * @return the signatureKeyIdentifier
   */
  public String getSignatureKeyIdentifier()
  {
    return this.signatureKeyIdentifier;
  }

  /**
   * @param signatureKeyIdentifier the signatureKeyIdentifier to set
   */
  public void setSignatureKeyIdentifier(String signatureKeyIdentifier)
  {
    this.signatureKeyIdentifier = signatureKeyIdentifier;
  }

  /**
   * Override user defined values from the used signature profile like "value.SIG_META".
   * You cannot override pre defined values like SIG_VALUE, SIG_DATE {@link SignatureTypes#REQUIRED_SIG_KEYS}.
   * The override values are bound to the {@link SignParameters} instance.
   * <p>
   * Sample usage:
   * <pre>
      SignParameters sp = new SignParameters();
      . . .

      sp.setSignatureProfileId("SIGNATURBLOCK_DE");

      // expressions do not work on binary signature fields without phlength setting!!
      sp.setProfileOverrideValue("SIG_META", "It's nice to be important, but it is more important to be nice ${subject.L}");;
      sp.setProfileOverrideValue("SIG_LABEL", "./images/signatur-logo_en.png");

      // execute sign using the overrides
      pdfAs.sign(sp);
  </pre>
   * </p>
   * @param key the name of the setting to override e.g. "SIG_META"
   * @param value The new value
   */
   public void setProfileOverrideValue(String key, String value) {
      if (SignatureTypes.isRequredSigTypeKey(key)) {
         throw new RuntimeException("cannot set value for pre defined signature field names");
      }
      this.overrideProps.put(key, value);
   }

   /**
    * Get override values created via {@link #setProfileOverrideValue(String, String)}
    * @return
    */
   public Properties getProfileOverrideProperties() {
      return this.overrideProps;

   }

  /**
   * Get the value of the checkForPlaceholder flag.
   *
   * @return
   */
  public Boolean isCheckForPlaceholder() {
    return this.checkForPlaceholder;
  }

  /**
   * Set this to true, if you want a search for placeholder images to be performed and
   * appropriate images to be replaced.
   * If this is not set, a search will only be performed if the configuration property "enable_placeholder_search" is set to true.
   *
   * @param check
   */
  public void setCheckForPlaceholder(Boolean searchForPlaceHolder) {
    this.checkForPlaceholder = searchForPlaceHolder;
  }

  /**
   * Set an explicit placeholder id.
   * Only placeholder images that have a matching ID property embedded will be considered for replacement.
   *
   * @param placeholderId
   */
  public void setPlaceholderId(String placeholderId) {
    this.placeholderId = placeholderId;
  }

  /**
   * The id of the placeholder to replace.
   *
   * @return the placeholderId
   */
  public String getPlaceholderId() {
    return placeholderId;
  }

  /**
   * Set the behavior if no exactly matching placeholder could be found.<br/>
   * Exactly matching meaning:<br/>
   * <ul><li>If a placeholderId is set: a placeholder which has exactly this id embedded</li>
   * <li>If no placeholderId is set: a placeholder without an embedded id is found</li></ul>
   *
   * @see Constants#PLACEHOLDER_MATCH_MODE_LENIENT
   * @see Constants#PLACEHOLDER_MATCH_MODE_MODERATE
   * @see Constants#PLACEHOLDER_MATCH_MODE_STRICT
   *
   * Defaults to {@link Constants#PLACEHOLDER_MATCH_MODE_MODERATE}.
   *
   * @param placeholderMatchMode
   */
  public void setPlaceholderMatchMode(int placeholderMatchMode) {
    this.placeholderMatchMode = placeholderMatchMode;
  }

  /**
   * Get the placeholder matching mode.
   *
   * @see SignParameters#getPlaceholderMatchMode()
   * @return the placeholderMatchMode
   */
  public int getPlaceholderMatchMode() {
    return this.placeholderMatchMode;
  }

}
