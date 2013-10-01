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
}
