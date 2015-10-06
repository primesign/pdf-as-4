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
package at.gv.egiz.pdfas.lib.impl.placeholder;

import java.io.Serializable;

import at.knowcenter.wag.egov.egiz.pdf.TablePos;

/**
 * This class represents all the data which can be extracted from a placeholder image.
 * 
 * @author exthex
 *
 */
public class SignaturePlaceholderData implements Serializable {

	private static final long serialVersionUID = 1L;

   public static final String ID_KEY = "id";

   public static final String PROFILE_KEY = "profile";

   public static final String TYPE_KEY = "type";

   public static final String SIG_KEY_KEY = "key";

   private String profile;

   private String type;

   private String key;

   private String id;

   private TablePos tablePos;

   private String placeholderName;

   /**
    * 
    * @param profile
    * @param type
    * @param sigKey
    * @param id 
    */
   public SignaturePlaceholderData(String profile, String type, String sigKey, String id) {
      this.profile = profile;
      this.type = type;
      this.key = sigKey;
      this.id = id;
   }

   /**
    * Get the table position for the signature block.<br/>
    * The table position is created from the page number, the upper left corner and the width of the placeholder image.
    * 
    * @return
    */
   public TablePos getTablePos() {
      return tablePos;
   }

   public void setTablePos(TablePos tablePos) {
      this.tablePos = tablePos;
   }

   /**
    * The profile name. Might be null if not included in the qr-code.
    * 
    * @return
    */
   public String getProfile() {
      return profile;
   }

   public void setProfile(String profile) {
      this.profile = profile;
   }

   /**
    * The signature type: "textual" or "binary". Might be null if not included in the qr-code.
    * @return
    */
   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   /**
    * The key identifier for MOA signature. Might be null if not included in the qr-code.
    * 
    * @return
    */
   public String getKey() {
      return key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public String toString() {
      return getClass().toString() + ": profile=" + profile + "; type=" + type + "; sigKey=" + key + "; table pos=" + tablePos;
   }

   public void setPlaceholderName(String name) {
      this.placeholderName = name;
   }

   /**
    * The name of the placeholder image.
    * 
    * @return
    */
   public String getPlaceholderName() {
      return placeholderName;
   }

   /**
    * The id associated with this placeholder.
    * 
    * @return
    */
   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

}
