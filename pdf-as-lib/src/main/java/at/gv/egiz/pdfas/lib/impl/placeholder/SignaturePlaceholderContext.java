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

/**
 * Store and retrieve {@link SignaturePlaceholderData} in/from a thread local context.
 * 
 * @author exthex
 *
 */
public class SignaturePlaceholderContext {
   
   private ThreadLocal<SignaturePlaceholderData> sigHolder = new ThreadLocal<SignaturePlaceholderData>();
   
   private static SignaturePlaceholderContext instance = new SignaturePlaceholderContext();
   
   /**
    * Constructor. Private because this is a singleton.
    */
   private SignaturePlaceholderContext() {
      
   }

   /**
    * Get the {@link SignaturePlaceholderData} which is currently bound to this thread.
    * Might be null.
    * 
    * @return
    */
   public static SignaturePlaceholderData getSignaturePlaceholderData(){
      return instance.sigHolder.get();
   }

   /**
    * 
    * @return true if there is currently a {@link SignaturePlaceholderData} bound to this thread, false otherwise.
    */
   public static boolean isSignaturePlaceholderDataSet() {
      return instance.sigHolder.get() != null;
   }

   /**
    * Bind a {@link SignaturePlaceholderData} to this thread.
    * If the given data is null, the context will be cleared.
    * 
    * @param data if null, clears the ThreadLocal, else binds the data to the current thread.
    */
   public static void setSignaturePlaceholderData(SignaturePlaceholderData data) {
      instance.sigHolder.set(data);
   }
}
