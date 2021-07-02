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
package at.gv.egiz.pdfas.lib.impl.stamping;

import static at.gv.egiz.pdfas.common.utils.StringUtils.extractLastID;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsSettingsException;
import at.gv.egiz.pdfas.common.settings.IProfileConstants;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.lib.impl.status.ICertificateProvider;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import at.knowcenter.wag.egov.egiz.pdf.sig.SignatureEntry;
import at.knowcenter.wag.egov.egiz.table.Entry;
import at.knowcenter.wag.egov.egiz.table.Style;
import at.knowcenter.wag.egov.egiz.table.Table;

public class TableFactory implements IProfileConstants {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(TableFactory.class);

    /**
     * The default style definition for images.
     */
    private static Style defaultImageStyle_ = new Style();

    /**
     * The default style definition for captions.
     */
    private static Style defaultCaptionStyle_ = new Style();

    /**
     * The default style definition for values.
     */
    private static Style defaultValueStyle_ = new Style();

    /**
     * Reference from signature key to there corresponding value
     */
    private static Hashtable<String, SignatureEntry> sigEntries_ = new Hashtable<String, SignatureEntry>(8);

    static {
        setDefaultStyles();
    }

    /**
     * This method set the default styles for images, captions and values.
     */
    private static void setDefaultStyles()
    {
        defaultImageStyle_.setPadding(3);
        defaultImageStyle_.setHAlign(Style.CENTER);
        defaultImageStyle_.setVAlign(Style.MIDDLE);

        defaultCaptionStyle_.setHAlign(Style.CENTER);
        defaultCaptionStyle_.setVAlign(Style.MIDDLE);

        defaultValueStyle_.setHAlign(Style.LEFT);
        defaultValueStyle_.setVAlign(Style.MIDDLE);
    }

    /**
     * This method creates an abstract signature table object. It takes all keys
     * and values set by the signature object to create the corresponding abstract
     * table object. The table definition is read from the settings file.
     *
     * @param tableID
     *          is the name of the table definition in the settings file
     * @return a new abstract signature table
     * @throws PdfAsSettingsException 
     * @see at.knowcenter.wag.egov.egiz.table.Style
     * @see at.knowcenter.wag.egov.egiz.table.Table
     * @see at.knowcenter.wag.egov.egiz.table.Entry
     */
    public static Table createSigTable(SignatureProfileSettings profile, String tableID, OperationStatus operationStatus,
    		ICertificateProvider certProvider) throws PdfAsSettingsException
    {
        String table_key_prefix = SIG_OBJ + profile.getProfileID() + "." + TABLE;
        String table_key = table_key_prefix + tableID;
        ISettings configuration = operationStatus.getSettings();
        // String caption_prefix = SignatureTypes.SIG_OBJ + getSignationType() +
        // ".key.";
        // String value_prefix = SignatureTypes.SIG_OBJ + getSignationType() +
        // ".value.";
        // ArrayList table_def_keys = settings_.getKeys(table_key);
        Vector<String> table_defs = configuration.getFirstLevelKeys(table_key);

        if (table_defs == null)
        {
            return null;
        }

        Table sig_table = new Table(tableID);
        //SignatureProfileSettings profile = createProfile(profileID);
        boolean found_style = false;
        Iterator<String> table_def_iter = table_defs.iterator();
        while(table_def_iter.hasNext())
        {
            String table_def_key = table_def_iter.next();
            int dot_idx = (table_def_key.lastIndexOf(".") > 0 ? table_def_key.lastIndexOf(".") + 1 : table_def_key.length());
            String table_def = table_def_key.substring(dot_idx);
            //String table_def_keys_prefix = table_def_key.substring(0, dot_idx-1);
            String table_def_string = configuration.getValue(table_def_key);
            if (table_def.matches("\\D*"))
            {
                // if the table key is not a number (row number index)
                if (COLS_WITH.equals(table_def))
                {
                    String[] cols_s = table_def_string.split(" ");
                    float[] cols_f = new float[cols_s.length];
                    for (int i = 0; i < cols_s.length; i++)
                    {
                        cols_f[i] = Float.parseFloat(cols_s[i]);
                    }
                    sig_table.setColsRelativeWith(cols_f);
                }
                if (STYLE.equals(table_def) && !found_style)
                {
                    Style style = readStyle(table_def_key, configuration);
                    sig_table.setStyle(style);
                    found_style = true;
                }
                continue;
            }
            if (table_def_string != null)
            {
                // analyse the row definition
                String[] elems = table_def_string.split("\\|");
                ArrayList<Entry> row = new ArrayList<Entry>();
                for (int elem_idx = 0; elem_idx < elems.length; elem_idx++)
                {
                    String elem = elems[elem_idx];
                    String[] key_type = elem.split("-");
                    if (key_type.length < 2)
                    {
                        return null;
                    }
                    String key = key_type[0];
                    String type = key_type[1];
                    if (TYPE_TABLE.equals(key))
                    {
                        // add a table entry
                        Table table = createSigTable(profile, type, operationStatus, certProvider);
                        if (table != null)
                        {
                            Entry entry = new Entry(Entry.TYPE_TABLE, table, key);
                            row.add(entry);
                        }
                    }
                    if (TYPE_IMAGE.equals(type))
                    {
                        // add an image entry
                        String value = profile.getValue(key);
                        if (value != null)
                        {
                            Entry entry = new Entry(Entry.TYPE_IMAGE, value, key);
                            entry.setStyle(defaultImageStyle_);
                            row.add(entry);
                        } else {
                            Entry entry = new Entry(Entry.TYPE_VALUE, "IMG MISSING", key);
                            entry.setStyle(defaultValueStyle_);
                            row.add(entry);
                        }
                    }
                    if (TYPE_VALUE.equals(type))
                    {
                        // add a single value entry
                    	 ValueResolver resolver = new ValueResolver(certProvider, operationStatus);
                        String value = profile.getValue(key);
                        Entry entry = new Entry(Entry.TYPE_VALUE, 
                        		resolver.resolve(key, value, profile), key);
                        if (entry != null)
                        {
                            //entry.setColSpan(2);
                            entry.setStyle(defaultValueStyle_);
                            row.add(entry);
                        }
                    }
                    if (TYPE_CAPTION.equals(type))
                    {
                        // add a single value entry
                    	 ValueResolver resolver = new ValueResolver(certProvider, operationStatus);
                        String value = profile.getCaption(key);
                        Entry entry = new Entry(Entry.TYPE_CAPTION, 
                        		resolver.resolve(key, value, profile), key);
                        if (entry != null)
                        {
                            //entry.setColSpan(2);
                            entry.setStyle(defaultCaptionStyle_);
                            row.add(entry);
                        }
                    }
                    
                    if ((TYPE_VALUE + TYPE_CAPTION).equals(type) || (TYPE_CAPTION + TYPE_VALUE).equals(type) || "req".equals(type))
                    {
                        // add a caption value pair
                        String caption = profile.getCaption(key);
                        String value = profile.getValue(key);
                        //String caption = getSigCaption(key);
                        //String value = getSigValue(key);

                        ValueResolver resolver = new ValueResolver(certProvider, operationStatus);
                                                
                        if (value != null) {
                            Entry c_entry = new Entry(Entry.TYPE_CAPTION, caption, key);
                            c_entry.setNoWrap(true);  // dferbas fix bug #331
                            c_entry.setStyle(defaultCaptionStyle_);
                            
                            
                            Entry v_entry = new Entry(Entry.TYPE_VALUE, 
                            		resolver.resolve(key, value, profile), key);
                            v_entry.setStyle(defaultValueStyle_);
                            if (c_entry != null && v_entry != null)
                            {
                                row.add(c_entry);
                                row.add(v_entry);
                            }
                        } else {
                            // RESOLV VALUE!!
                            Entry c_entry = new Entry(Entry.TYPE_CAPTION, caption, key);
                            c_entry.setNoWrap(true);  // dferbas fix bug #331
                            c_entry.setStyle(defaultCaptionStyle_);

                            Entry v_entry = new Entry(Entry.TYPE_VALUE,
                                    resolver.resolve(key, value, profile), key);
                            v_entry.setStyle(defaultValueStyle_);
                            if (c_entry != null && v_entry != null)
                            {
                                row.add(c_entry);
                                row.add(v_entry);
                            }
                        }
                    }
                }
                sig_table.addRow(table_def, row);
            }
        }
        sig_table.normalize();
        return sig_table;
    }

    public static SignatureProfileSettings createProfile(String profileID, ISettings configuration) {
        return new SignatureProfileSettings(profileID, configuration);
    }

    /**
     * This method returns a value for a given signature key. If the key equals to
     * <code>SIG_NORM</code> and the value is <code>null</code> the version
     * string of the current normalizer is returned!
     *
     * @param key
     *          the key to get the value for
     * @return a value for the given key
     */
    public static String getSigValue(String key)
    {

        String value = null;
        SignatureEntry sigEntry = null;
        if (sigEntries_.containsKey(key))
        {
            sigEntry = sigEntries_.get(key);
            value = sigEntry.getValue();
        }
        /*
        if (value == null && SignatureTypes.SIG_NORM.equals(key))
        {
            value = normalizer_.getVersion();
        }
         */  /*
        String overrideVal = OverridePropertyHolder.getProperty(key);
        if (value != null && sigEntry != null && !sigEntry.isPlaceholder &&  overrideVal != null) { 
            value = overrideVal;
            if (logger.isDebugEnabled()) {
                logger.debug("Using override property for key '" + key + "' = " + value);
            }
        }  */

        return value;
    }

    /**
     * This method returns a caption for a given signature key. If the key exists
     * and the coresponding value is <code>null</code> the key itself is
     * returned as caption! If the key does not exist the method returns
     * <code>null</code>.
     *
     * @param key
     *          the key to get the caption for
     * @return a caption for the given key
     */
    @SuppressWarnings("unused")
    private static String getSigCaption(String key)
    {

        String caption = null;
        if (sigEntries_.containsKey(key))
        {
            caption = sigEntries_.get(key).getCaption();
            if (caption == null)
            {
                caption = key;
            }
        }
        return caption;
    }

    /**
     * This method read the style definitions from the settings file.
     *
     * @param styleKey
     *          the key to read the style definitions
     * @return the defined style informations
     * @see at.knowcenter.wag.egov.egiz.table.Style
     */
    private static Style readStyle(String styleKey, ISettings configuration)
    {
        Map<String, String> styles = configuration.getValuesPrefix(styleKey);
        Style style = new Style();
        Iterator<String> keyStyleIt = styles.keySet().iterator();
        while(keyStyleIt.hasNext())
        {
            String style_id_key = keyStyleIt.next();
            String style_val = styles.get(style_id_key);
            String style_id = extractLastID(style_id_key);

            style.setStyle(style_id, style_val);
        }
        return style;
    }

}
