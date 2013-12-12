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

import at.gv.egiz.pdfas.api.PdfAs;
import at.gv.egiz.pdfas.api.sign.SignParameters;

/**
 * A dynamic signature profile. It is used to define a signature profile like the ones from pdf-as/config.properties at runtime.
 * After creation via {@link PdfAs} you can set properties via {@link #setPropertyRaw(String, String)} 
 * or {@link #setFieldValue(String, String)}.<br>
 * You have to call {@link #apply()} to use the profile. The identifying name (e.g. for {@link SignParameters#setSignatureProfileId(String)}
 * can be obtained via {@link #getName()}.<br>
 * Depending on the {@link DynamicSignatureLifetimeEnum} the profile can be alive and usable till you {@link #dispose()} it manually.
 * <p>
 * Sample usage:<br>
 *  <pre>
      SignParameters sp = new SignParameters();
      . . .           
      sp.setSignatureType(Constants.SIGNATURE_TYPE_TEXTUAL);
      sp.setSignatureDevice(Constants.SIGNATURE_DEVICE_MOA);

      // create a new dynamic profile based on SIGNATURBLOCK_DE (every property is copied) with manual lifetime
      DynamicSignatureProfile dsp = pdfAs.createDynamicSignatureProfile("myUniqueName", "SIGNATURBLOCK_DE", 
            DynamicSignatureLifetimeEnum.MANUAL);  
            
      // set something
      dsp.setPropertyRaw("key.SIG_META", "Statement");
      dsp.setPropertyRaw("value.SIG_META", "respect to the man in the icecream van ${subject.EMAIL}");
      dsp.setPropertyRaw("value.SIG_LABEL", "./images/signatur-logo_en.png");
      dsp.setPropertyRaw("table.main.Style.halign", "right");
      
      // mandatory: apply the profile, you have to apply again after changes (overriding your previous setting)
      dsp.apply();                 
      sp.setSignatureProfileId(dsp.getName());            
      
      // execute PDF-AS
      pdfAs.sign(sp);              
      
      . . .
                       
     // your profile is saved and you can obtain it again anytime later:
      dsp = pdfAs.loadDynamicSignatureProfile("myUniqueName");
      // use it for another sign.
      // dont forget to dispose() sometimes because it was manual lifetime
      System.out.println(dsp.getName());
 *  </pre>
 *  </p>
 * 
 * @author exthex
 *
 */
@Deprecated
public interface DynamicSignatureProfile {

   /**
    * Get the name of the dynamic signature profile. Equals the <b>SignatureProfileId</b>
    * @return
    */
   public abstract String getName();   

   /**
    * Set a field value for the profile. Use {@link #setPropertyRaw(String, String)} for setting any property.<br>
    * For example to set <code>sig_obj.MEIN_DYN_SIGNATURBLOCK.value.SIG_META</code> just use <code>SIG_META</code> as fieldName. 
    * @param fieldName the name of the field
    * @param value the value to set
    */
   public abstract void setFieldValue(String fieldName, String value);

   /**
    * Get a field value from the profile. See {@link #setFieldValue(String, String)}
    * @param fieldName
    * @return
    */
   public abstract String getFieldValue(String fieldName);
   
   /**
    * Set any property for the signature profile. 
    * Uses the same keys as the property file without the "prefix" for the profile.
    * For example to set <code>sig_obj.MEIN_DYN_SIGNATURBLOCK.key.SIG_META</code> use <code>key.SIG_META</code>
    * @param key property key
    * @param val property value
    */
   public void setPropertyRaw(String key, String val);

   /**
    * Get any property from the signature profile. See {@link #setPropertyRaw(String, String)} for details.
    * @param key
    * @return
    */
   public String getPropertyRaw(String key);      

   /**
    * Apply the signature profile. Call this after all properties are set and you want to use the profile. It is then added
    * to the globally available signature profiles. Depending on the lifetime model {@link DynamicSignatureLifetimeEnum} you
    * have to {@link #dispose()} it manually when not needed anymore.
    */
   public abstract void apply();

   /**
    * Disposes the signature profile from the global store. Call this for {@link DynamicSignatureLifetimeEnum#MANUAL} only. 
    */
   public abstract void dispose();
  
}