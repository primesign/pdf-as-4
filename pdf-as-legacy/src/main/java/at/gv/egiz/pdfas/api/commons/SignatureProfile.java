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

import java.util.Properties;

import at.knowcenter.wag.egov.egiz.sig.SignatureTypes.State;

/**
 * Definition of a signature profile.
 * 
 * @author wprinz
 */
@Deprecated
public interface SignatureProfile {

   // TODO: implement full profile support

   /**
    * Returns the profile id.
    * 
    * @return Returns the profile id.
    */
   public String getProfileId();

   /**
    * Returns the MOA KeyIdentifier.
    * 
    * @return Returns the MOA KeyIdentifier.
    */
   public String getMOAKeyIdentifier();

   /**
    * Returns the entries relevant to the search algorithm for signature blocks.<br/>
    * e.g. properties starting with <code>sig_obj.PROFILE.key.</code> and
    * properties of the form <code>sig_obj.PROFILE.table.TABLENAME.NUMBER</code>
    * where <code>PROFILE</code> is the name of the current profile,
    * <code>TABLENAME</code> is the name of a table and <code>NUMBER</code>
    * is the number of the specific row within the table <code>TABLENAME</code>.
    * 
    * @return The entries relevant to the signature block search algorithm as
    *         Java properties.
    */
   public Properties getSignatureBlockEntries();

   /**
    * Returns the profile description.
    * 
    * @return The profile description.
    */
   public String getProfileDescription();

   /**
    * True only if this is the default profile according to config.
    * @return
    */
   public boolean isDefault();
   
	/**
	 * Returns the state of the signature profile. Signature profiles may be restricted to signature (
	 * {@link State#SIGN_ONLY}) or to verification ({@link State#VERIFY_ONLY}).
	 * 
	 * @return The state of the profile.
	 */
	public State getState();
   
}
