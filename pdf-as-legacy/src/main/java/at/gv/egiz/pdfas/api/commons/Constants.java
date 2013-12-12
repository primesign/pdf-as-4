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
package at.gv.egiz.pdfas.api.commons;


/**
 * Contains commonly used constants.
 *
 * @author wprinz
 */
@Deprecated
public final class Constants
{

  /**
   * Hidden default constructor.
   */
  private Constants()
  {
    // empty
  }

  /**
   * A binary signature.
   * This value should not be modified due to external dependencies!
   */
  public static final String SIGNATURE_TYPE_BINARY = "binary";

  /**
   * A textual signature.
   * This value should not be modified due to external dependencies!
   */
  public static final String SIGNATURE_TYPE_TEXTUAL = "textual";

  /**
   * The default signature type (one of "textual", "binary", "detachedtextual").
   */
  public static final String DEFAULT_SIGNATURE_TYPE = SIGNATURE_TYPE_BINARY;

  /**
   * A "detached" textual signature.
   *
   * <p>
   * The document text is signed, but instead of returning the pdf with the signature block,
   * the sign result XML of the connector is returned.
   * </p>
   */
  public static final String SIGNATURE_TYPE_DETACHEDTEXTUAL = "detachedtextual";

  /**
   * The signature device moa.
   * This value should not be modified due to external dependencies!
   */
  public static final String SIGNATURE_DEVICE_MOA = "moa";

  /**
   * The signature device bku.
   * This value should not be modified due to external dependencies!
   */
  public static final String SIGNATURE_DEVICE_BKU = "bku";

  /**
   * The signature device a1.
   * This value should not be modified due to external dependencies!
   */
  public static final String SIGNATURE_DEVICE_A1 = "a1";

  /**
   * The signature device MOCCA (online bku).
   * This value should not be modified due to external dependencies!
   */
  public static final String SIGNATURE_DEVICE_MOC = "moc";

  /**
   * Added by rpiazzi
   * The signature device MOBILE.
   * This value should not be modified due to external dependencies!
   */
  public static final String SIGNATURE_DEVICE_MOBILE = "mobile";

  /**
   * Added by rpiazzi
   * The signature device MOBILETEST for the test version of the MOBILE CCS.
   * This value should not be modified due to external dependencies!
   */
  public static final String SIGNATURE_DEVICE_MOBILETEST = "mobiletest";

  /**
   * Only binary signatures are verified.
   */
  public static final String VERIFY_MODE_BINARY_ONLY = "binaryOnly";

  /**
   * Binary and textual signatures are verified with time optimization.
   *
   * <p>
   * This mode of operation tries to minimize the numbers of text extractions,
   * which are very time intensive, at the cost of some rare cases, in which some
   * signatures may not be found.
   * </p>
   */
  public static final String VERIFY_MODE_SEMI_CONSERVATIVE = "semiConservative";

  /**
   * Binary and textual signatures are verified.
   */
  public static final String VERIFY_MODE_FULL_CONSERVATIVE = "fullConservative";

  /**
   * All signatures are verified.
   */
  public static final int VERIFY_ALL = -1;

  /**
   * The system property that may be used to declare the pdf-as configuration folder.
   */
  public static final String CONFIG_DIR_SYSTEM_PROPERTY = "pdf-as.work-dir";

  /**
   * The zip file containing the default configuration.
   */
  public static final String DEFAULT_CONFIGURATION_ZIP_RESOURCE = "DefaultConfiguration.zip";

  /**
   * The configuration folder for pdf-as within the user's home folder.
   */
  public static final String USERHOME_CONFIG_FOLDER = "PDF-AS";

  /**
   * The name of the directory, where temporary files are stored.
   */
  public static final String TEMP_DIR_NAME = "pdfastmp";

  public static final String BKU_HEADER_SIGNATURE_LAYOUT = "SignatureLayout";

  public static final String ADOBE_SIG_FILTER = "Adobe.PDF-AS";

  /**
   * Strict matching mode for placeholder extraction.<br/>
   * If the placeholder with the given id is not found in the document, an exception will be thrown.
   */
  public static final int PLACEHOLDER_MATCH_MODE_STRICT = 0;

  /**
   * A moderate matching mode for placeholder extraction.<br/>
   * If the placeholder with the given id is not found in the document, the first placeholder without an id will be taken.<br/>
   * If there is no such placeholder, the signature will be placed as usual, according to the pos parameter of the signature profile used.
   */
  public static final int PLACEHOLDER_MATCH_MODE_MODERATE = 1;

  /**
   * A more lenient matching mode for placeholder extraction.<br/>
   * If the placeholder with the given id is not found in the document, the first found placeholder will be taken, regardless if it has an id set, or not.<br/>
   * If there is no placeholder at all, the signature will be placed as usual, according to the pos parameter of the signature profile used.
   */
  public static final int PLACEHOLDER_MATCH_MODE_LENIENT = 2;

  /**
   * Identifier for QR based placeholders.
   */
  public static final String QR_PLACEHOLDER_IDENTIFIER = "PDF-AS-POS";

  /**
   * The name of a logger used for statistical logging.
   */
  public static final String STATISTIC_LOGGER_NAME = "statistic";

}

