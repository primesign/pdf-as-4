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
    public static final String SIG_OBJ = "sig_obj.";

    public static final String SIG_DATE = "SIG_DATE";

    /**
     * The settings key prefix for the signature table object definition
     */
    public static final String TABLE = "table.";

    /**
     * The settings value refering to a table
     */
    public final static String TYPE_TABLE = "TABLE";

    /**
     * The settings value refering to an image
     */
    public final static String TYPE_IMAGE = "i";

    /**
     * The settings value refering to a text caption
     */
    public final static String TYPE_CAPTION = "c";

    /**
     * The settings value refering to a text value
     */
    public final static String TYPE_VALUE = "v";

    /**
     * The settings key sub prefix getting the width of columns for a table
     * definition
     */
    public final static String COLS_WITH = "ColsWidth";

    /**
     * The settings key sub prefix getting the style definition
     */
    public final static String STYLE = "Style";

    public final static String PROFILE_VALUE = "value";

    public final static String PROFILE_KEY = "key";

    public final static String KEY_SEPARATOR = ".";
    
    public final static String INCLUDE = "include";
    
    public final static String CFG_DIR = "cfg";
    public final static String CFG_FILE = "config.properties";
    
    public final static String TMP_DIR = "default.pdfastmp_dir";
    public final static String TMP_DIR_DEFAULT_VALUE = "pdfastmp";
    
    public final static String SIGNING_REASON = "adobeSignReasonValue";
    public final static String SIGNFIELD_VALUE = "adobeSignFieldValue";
    public final static String TIMEZONE_BASE = "timezone";
    public final static String SIG_PDFA1B_VALID = "SIG_PDFA1B_VALID";
}
