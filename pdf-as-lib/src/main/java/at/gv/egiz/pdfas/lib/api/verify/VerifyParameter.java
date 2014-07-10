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
package at.gv.egiz.pdfas.lib.api.verify;

import java.util.Date;

import at.gv.egiz.pdfas.lib.api.PdfAsParameter;

public interface VerifyParameter extends PdfAsParameter {

	/**
	 * The signature Verification Level defines what should be verified 
	 */
	public enum SignatureVerificationLevel {
		/**
		 * Only verifies the the Signatures integrity
		 * 
		 * This option does not perform a verification of the signing Certificate 
		 * and does not requires MOA-SP 
		 */
		INTEGRITY_ONLY_VERIFICATION,
		/**
		 * Uses MOA-SP to verify the Signature and verifies the Certification Path
		 * 
		 * This is the default value
		 */
		FULL_VERIFICATION
	}
	
	/**
	 * Gets which signature should be verified 
	 * 
	 * This is a 0 based index of the signatures 
	 * @return
	 */
	public int getWhichSignature();
	
	/**
	 * Sets which signature should be verified
	 * 
	 * This is a 0 based index of the signatures 
	 * 
	 * @param which The index
	 */
	public void setWhichSignature(int which);
	
	/**
	 * Gets the verification time
	 * @return
	 */
	public Date getVerificationTime();
	
	/**
	 * Sets the verification time.
	 * 
	 * @param verificationTime
	 */
	public void setVerificationTime(Date verificationTime);
	
	/**
	 * Sets the verification Level
	 * 
	 * @param signatureVerificationLevel
	 */
	public void setSignatureVerificationLevel(SignatureVerificationLevel signatureVerificationLevel);
	
	/**
	 * Gets the verification level
	 * 
	 * @return
	 */
	public SignatureVerificationLevel getSignatureVerificationLevel();
}
