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
package at.gv.egiz.pdfas.common.settings;

/**
 * Created with IntelliJ IDEA.
 * User: afitzek
 * Date: 9/10/13
 * Time: 12:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IProfileConstants {
    /**
     * The settings key prefix for signature definitions. <code>"sig_obj."</code>
     */
    String SIG_OBJ = "sig_obj.";

    String SIG_DATE = "SIG_DATE";

    /**
     * The settings key prefix for the signature table object definition
     */
    String TABLE = "table.";

    /**
     * The settings value refering to a table
     */
    String TYPE_TABLE = "TABLE";

    /**
     * The settings value refering to an image
     */
    String TYPE_IMAGE = "i";

    /**
     * The settings value refering to a text caption
     */
    String TYPE_CAPTION = "c";

    /**
     * The settings value refering to a text value
     */
    String TYPE_VALUE = "v";

    /**
     * The settings key sub prefix getting the width of columns for a table
     * definition
     */
    String COLS_WITH = "ColsWidth";

    /**
     * The settings key sub prefix getting the style definition
     */
    String STYLE = "Style";

    String PROFILE_VALUE = "value";

    String PROFILE_KEY = "key";

    String KEY_SEPARATOR = ".";

    String INCLUDE = "include";

    String CFG_DIR = "cfg";
    String CFG_FILE = "config.properties";

    String TMP_DIR = "default.pdfastmp_dir";
    String TMP_DIR_DEFAULT_VALUE = "pdfastmp";

    String SIGNING_REASON = "adobeSignReasonValue";
    String SIGNFIELD_VALUE = "adobeSignFieldValue";
    String TIMEZONE_BASE = "timezone";
    String SIG_PDFA1B_VALID = "SIG_PDFA1B_VALID";
    String SIG_PDFA_VALID = "SIG_PDFA_VALID";
    String SIG_PDFUA_FORCE = "SIG_PDFUA_FORCE";
    String LATIN1_ENCODING = "latin1_encoding";

	/**
	 * Configuration key (segment) for declaration of transformation/transliteration according to ICU4J pattern.
	 *
	 * @see <a href="http://userguide.icu-project.org">ICU User Guide</a>
	 */
	String PROFILE_TRANSFORM_PATTERN = "transformPattern";

    String SIGNATURE_BLOCK_PARAMETER = "sbp";

}
