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

import java.io.Serializable;

/**
 * Pseudo enum defining lifetime models for {@link DynamicSignatureProfile}s.
 * 
 * @author exthex
 *
 */
public final class DynamicSignatureLifetimeEnum implements Serializable {
   private static final long serialVersionUID = 1L;
   
   private int value;
   
   /**
    * Automatic lifetime bound to one sign process
    */   
   public static final DynamicSignatureLifetimeEnum AUTO = new DynamicSignatureLifetimeEnum(1);
   
   /**
    * Manual lifetime making YOU responsible for calling {@link DynamicSignatureProfile#dispose()}.
    */
   public static final DynamicSignatureLifetimeEnum MANUAL = new DynamicSignatureLifetimeEnum(2);
   
   private DynamicSignatureLifetimeEnum(int val) {
      this.value = val;      
   }

   public int hashCode() {
      return value;
   }

   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      DynamicSignatureLifetimeEnum other = (DynamicSignatureLifetimeEnum) obj;
      if (value != other.value)
         return false;
      return true;
   }
   
   

}
