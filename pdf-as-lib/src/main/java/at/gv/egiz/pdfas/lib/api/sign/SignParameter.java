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
package at.gv.egiz.pdfas.lib.api.sign;

import java.io.OutputStream;

import at.gv.egiz.pdfas.lib.api.PdfAsParameter;

public interface SignParameter extends PdfAsParameter {

	// certification levels

	/** Approval signature */
	public static final int NOT_CERTIFIED = 0;

	/** Author signature, no changes allowed */
	public static final int CERTIFIED_NO_CHANGES_ALLOWED = 1;

	/** Author signature, form filling allowed */
	public static final int CERTIFIED_FORM_FILLING = 2;

	/** Author signature, form filling and annotations allowed */
	public static final int CERTIFIED_FORM_FILLING_AND_ANNOTATIONS = 3;

	/**
	 * Gets the signature profile to use
	 * @return
	 */
	public String getSignatureProfileId();

	/**
	 * Sets the signature profile to use
	 * 
	 * @param signatureProfileId The signature profile
	 */
	public void setSignatureProfileId(String signatureProfileId);

	/** 
	 * Gets the signature position
	 * @return
	 */
	public String getSignaturePosition();

	/**
	 * Sets the signature position
	 * @param signaturePosition The signature position string
	 */
	public void setSignaturePosition(String signaturePosition);
	
	/**
	 * Sets the signer to use
	 * 
	 * 
	 * @param signer
	 */
	public void setPlainSigner(IPlainSigner signer);
	
	/**
	 * Gets the signer to use.
	 * @return
	 */
	public IPlainSigner getPlainSigner();
	
	/**
	 * Gets the outputstream, where the signed document will be written to
	 * @return
	 */
	public OutputStream getSignatureResult();

	/**
	 * Gets the signature certification level.
	 *
	 * @return the signature certification level
	 */
	int getSignatureCertificationLevel();

	/**
	 * Sets the signature certification level.
	 *
	 * @param signatureCertificationLevel
	 *            the signature certificationLevellevel to set
	 */
	void setSignatureCertificationLevel(int signatureCertificationLevel);
	
	/**
	 * Sets the approach for integrating LTV verification data with signatures.
	 * 
	 * @param ltvMode
	 *            The LTV mode (required; must not be {@code null}).
	 */
	void setLTVMode(LTVMode ltvMode);

	/**
	 * Returns the currently set approach for integrating LTV verification data with signatures.
	 * 
	 * @return The LTV mode (never {@code null}).
	 */
	LTVMode getLTVMode();

	/**
	 * Reflects the approach PDF-AS is using in terms of LTV. Allows to disable LTV (default mode), to enforce LTV or to
	 * apply LTV if possible.
	 * 
	 * @author Thomas Knall, PrimeSign GmbH
	 *
	 */
	public enum LTVMode {

		/**
		 * PDF-AS should neither resolve or retrieve nor embed any LTV related information. LTV is disabled.
		 * <p>This is the default mode.</p>
		 */
		NONE,

		/**
		 * PDF-AS should try to resolve, retrieve and embed LTV related information but SHOULD NOT FAIL signature in
		 * case of error (e.g. timeout when retrieving revocation infos). LTV is enabled.
		 * <p>
		 * Note that this mode may slow down signatures since retrieval of certificate chains and revocation infos may
		 * take some time.
		 * </p>
		 */
		OPTIONAL,

		/**
		 * PDF-AS should try to resolve, retrieve and embed LTV related information but SHOULD FAIL signature in case of
		 * error (e.g. timeout when retrieving revocation infos). LTV is enabled.
		 * <p>
		 * Note that this mode may slow down signatures since retrieval of certificate chains and revocation infos may
		 * take some time.
		 * </p>
		 */
		REQUIRED

	}

}
