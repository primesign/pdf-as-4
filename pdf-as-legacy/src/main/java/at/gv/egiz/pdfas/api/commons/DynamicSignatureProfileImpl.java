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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import at.gv.egiz.pdfas.api.PdfAs;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.knowcenter.wag.egov.egiz.sig.SignatureTypes;

// TODO exception types?
/**
 * Implementation class of the {@link DynamicSignatureProfile}. Don't use this class directly. Use {@link PdfAs} to create and the 
 * {@link DynamicSignatureProfile} interface for manipulation.
 * @author exthex
 *
 */
@Deprecated
public class DynamicSignatureProfileImpl implements DynamicSignatureProfile {
   private String name;   
   private Properties newProps = new Properties();
   private int dynamicTypeCounter = 0;   
   private static Map<String, DynamicSignatureProfile> profiles = 
		   new HashMap<String, DynamicSignatureProfile>();
   private static ThreadLocal<DynamicSignatureProfile> localProfiles = new ThreadLocal<DynamicSignatureProfile>();
   private DynamicSignatureLifetimeEnum lifeMode; 
   private Configuration configuration;

   private DynamicSignatureProfileImpl(DynamicSignatureLifetimeEnum mode, String name, 
		   Configuration configuration) {
      if (name != null) {
         this.name = name;
      } else {
         this.name = createDynamicTypeName();
      }
      this.configuration = configuration;
      this.lifeMode = mode;
   }
   
   public static DynamicSignatureProfileImpl createFromParent(String myUniqueName, String parentProfile, 
		   DynamicSignatureLifetimeEnum mode, Configuration configuration) {
      DynamicSignatureProfileImpl res = new DynamicSignatureProfileImpl(mode, myUniqueName, configuration);
      res.initFromParent(parentProfile);
      return res;
   }
   
   private void store() {
      if (lifeMode.equals(DynamicSignatureLifetimeEnum.MANUAL)) {
         profiles.put(this.getName(), this);
      } else if (lifeMode.equals(DynamicSignatureLifetimeEnum.AUTO)) {
         localProfiles.set(this);
      }
   }
   
   private void remove() {
      if (lifeMode.equals(DynamicSignatureLifetimeEnum.MANUAL)) {
         profiles.remove(this);
      } else if (lifeMode.equals(DynamicSignatureLifetimeEnum.AUTO)) {
         localProfiles.set(null);
      }
   }
   
   public static void disposeLocalProfile() {
      DynamicSignatureProfileImpl profile = (DynamicSignatureProfileImpl) localProfiles.get();
      if (profile != null) {       
         profile.dispose();
      }
   }
   
   public static DynamicSignatureProfileImpl createEmptyProfile(String myUniqueName, DynamicSignatureLifetimeEnum mode,
		   Configuration configuration) {
      return new DynamicSignatureProfileImpl(mode, myUniqueName, configuration);      
   }
   
   public static DynamicSignatureProfileImpl loadProfile(String name) {
      return (DynamicSignatureProfileImpl) profiles.get(name);
   }
   
   private synchronized String createDynamicTypeName() {
      return "dynprofile__#" + this.dynamicTypeCounter++;     
   }

   /* (non-Javadoc)
    * @see at.gv.egiz.pdfas.api.commons.DynamicSignatureProfile#getName()
    */
   public String getName() {
      return name;
   }
   
   /* (non-Javadoc)
    * @see at.gv.egiz.pdfas.api.commons.DynamicSignatureProfile#setName(String)
    */
   public void setName(String uniqueName) {
      this.name = uniqueName;
   }

   public void setPropertyRaw(String key, String val) {
      this.newProps.setProperty(localPropName(key), val);
   }
   
   public String getPropertyRaw(String key) {
      return this.newProps.getProperty(localPropName(key));
   }
   
   private void assertPropExists(String key) {
      if (!this.newProps.containsKey(localPropName(key))) {
         throw new RuntimeException("property '" + key + "'not existing, cannot add one");
      }
   }

   private String localPropName(String key) {
      return "sig_obj." + this.name + "." + key;
   }
   
   /* (non-Javadoc)
    * @see at.gv.egiz.pdfas.api.commons.DynamicSignatureProfile#setFieldValue(java.lang.String, java.lang.String)
    */
   public void setFieldValue(String fieldName, String value) {
      if (SignatureTypes.isRequredSigTypeKey(fieldName)) {
         throw new RuntimeException("cannot set value for pre defined signature field names");
      }
      
      String key = "value." +fieldName;
      assertPropExists(key);
      setPropertyRaw(key, value);
   }
   
   /* (non-Javadoc)
    * @see at.gv.egiz.pdfas.api.commons.DynamicSignatureProfile#getFieldValue(java.lang.String)
    */
   public String getFieldValue(String fieldName) {
      return getPropertyRaw("value."+fieldName);           
   }

   private void initFromParent(String parentProfile) {
      try {
         ISettings cfg = null;

         cfg = (ISettings)configuration;         
         String parentKey = "sig_obj." + parentProfile + ".";
         Map<String, String> properties = cfg.getValuesPrefix(parentKey);
         //Properties props = cfg.getProperties();
         // DTI: props.keys() does not support default properties, therefore we should better use props.propertyNames()
//         for (Enumeration e = props.keys(); e.hasMoreElements();) {
         /*for (Enumeration e = props.propertyNames(); e.hasMoreElements();) {
            String oldKey = (String) e.nextElement();
            if (oldKey.startsWith("sig_obj." + parentProfile + ".")) {
               String newKey = StringUtils.replace(oldKey, parentProfile, name);
               String val = props.getProperty(oldKey);
               this.newProps.put(newKey, val);
            }
         }*/
         
         Iterator<String> keyIt = properties.keySet().iterator();
         
         while(keyIt.hasNext()) {
        	 String oldKey = keyIt.next();
        	 String newKey = oldKey.replaceAll(parentProfile, name);
             String val = properties.get(oldKey);
             this.newProps.put(newKey, val);
         }
         
         this.newProps.put("sig_obj.types." + name, "on");
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
   
   /* (non-Javadoc)
    * @see at.gv.egiz.pdfas.api.commons.DynamicSignatureProfile#register()
    */
   public synchronized void apply() {
      try {
    	  Configuration cfg = this.configuration;
         for (Enumeration<Object> e = newProps.keys(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            cfg.setValue(key, newProps.getProperty(key));
         }
     
         store();
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
   
   /* (non-Javadoc)
    * @see at.gv.egiz.pdfas.api.commons.DynamicSignatureProfile#dispose()
    */
   public synchronized void dispose() {
      try {
    	  Configuration cfg = this.configuration;
    	  for (Enumeration<Object> e = newProps.keys(); e.hasMoreElements();) {
              String key = (String) e.nextElement();
              cfg.setValue(key, null);
           }
         remove();
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
   
}
