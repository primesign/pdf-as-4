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
 *
 * $Id: SignatureTypes.java,v 1.5 2006/10/31 08:18:56 wprinz Exp $
 */
package at.knowcenter.wag.egov.egiz.sig;

import org.apache.commons.lang3.ArrayUtils;

public class SignatureTypes {

	/**
	 * Defines all supported states for {@link SignatureTypes} (signature
	 * profiles). Signature types can be enabled ("on"), can be set to support
	 * signature only ("sign_only"), to verification only ("verify_only") or can
	 * be disabled ("off" or any other value not covered by other enum values).
	 * 
	 * @author Datentechnik Innovation GmbH
	 */
	public enum State {

		/**
		 * Enables a signature profile.
		 */
		ON("on", "yes", "true", "enabled"),

		/**
		 * Disables a signature profile.
		 */
		OFF(),

		/**
		 * Restricts the signature profile so that is can only be used for
		 * verification purposes and not for signature.
		 */
		VERIFY_ONLY("verify_only", "verify-only", "verifyonly", "verify only",
				"verify"),

		/**
		 * Allows the signature profile to be used for signature but not for
		 * verification.
		 */
		SIGN_ONLY("sign_only", "sign-only", "signonly", "sign only", "sign");

		/**
		 * Sets the default state when no valid value was provided.
		 */
		private static final State DEFAULT = OFF;

		/**
		 * States that allow signatures.
		 */
		private static final State[] CAN_SIGN = { ON, SIGN_ONLY };

		/**
		 * States that allow verification.
		 */
		private static final State[] CAN_VERIFY = { ON, VERIFY_ONLY };

		private String[] keyWords;

		private State(String... keyWords) {
			this.keyWords = keyWords;
		}

		/**
		 * Returns a valid State from a given {@code keyWord}. If the
		 * {@code keyWord} cannot be matched to a certain state, the default
		 * State {@link #OFF} is returned.
		 * 
		 * @param keyWord
		 *            A valid keyword like "on", "sign_only"...
		 * @return The enum State.
		 */
		public static State fromString(String keyWord) {
			if (keyWord == null) {
				return DEFAULT;
			}
			try {
				return valueOf(keyWord.toUpperCase());
			} catch (IllegalArgumentException e) {
				for (State candidate : values()) {
					for (String candidateKeyWord : candidate.keyWords) {
						if (keyWord.equalsIgnoreCase(candidateKeyWord)) {
							return candidate;
						}
					}
				}
				return DEFAULT;
			}
		}

		/**
		 * Returns {@code true} when the current state is one of the given
		 * candidate {@code states}.
		 * 
		 * @param states
		 *            The candidate states.
		 * @return {@code true} when the current state is one of the given
		 *         candidate states, {@code false} if not.
		 */
		public boolean in(State... states) {
			if (states != null) {
				for (State state : states) {
					if (this == state) {
						return true;
					}
				}
			}
			return false;
		}

		/**
		 * Returns if the respective state allows signatures.
		 * 
		 * @return {@code true} if signatures are allowed, {@code false} if not.
		 */
		public boolean canSign() {
			return in(CAN_SIGN);
		}

		/**
		 * Returns if the respective state allows verification.
		 * 
		 * @return {@code true} if verification is allowed, {@code false} if
		 *         not.
		 */
		public boolean canVerify() {
			return in(CAN_VERIFY);
		}

	}
	
	/**
	   * Standard key get/set the singature name
	   */
	  public static final String SIG_NAME = "SIG_NAME";

	  /**
	   * Standard key get/set the signature date
	   */
	  public static final String SIG_DATE = "SIG_DATE";

	  /**
	   * Standard key get/set the signator issuer
	   */
	  public static final String SIG_ISSUER = "SIG_ISSUER";

	  /**
	   * Standard key get/set the signature value
	   */
	  public static final String SIG_VALUE = "SIG_VALUE";

	  /**
	   * Standard key get/set the normalisation method used
	   */
	  public static final String SIG_NORM = "SIG_NORM";

	  /**
	   * Standard key get/set the signation id's used by BKU signated documents
	   */
	  public static final String SIG_ID = "SIG_ID";
	  
	  /**
	   * The EGIZ Algorithm "Kennzeichnung".
	   */
	  public static final String SIG_KZ = "SIG_KZ";

	  /**
	   * Standard key get/set the reference to the signature label (image mark)
	   */
	  public static final String SIG_LABEL = "SIG_LABEL";

	  /**
	   * Standard key get/set the serial number of the signature
	   */
	  public static final String SIG_NUMBER = "SIG_NUMBER";

	  // public static final String SIG_TYPE = "SIG_TYPE";
	  /**
	   * Standard key get/set the signature meta informations
	   */
	  public static final String SIG_META = "SIG_META";
	  
	  /**
	   * Standard key get/set the signature algorithm (sign + hash)
	   */
	  public static final String SIG_ALG = "SIG_ALG";
	  
	  /**
	   * Standard key get/set the signature note
	   * added by rpiazzi
	   */
	  public static final String SIG_NOTE = "SIG_NOTE";
	  
	  
	  /**
	   * Standard key get/set the signature subject
	   * Added to be able to define static signator name within config file
	   * added by rpiazzi
	   */
	  public static final String SIG_SUBJECT = "SIG_SUBJECT";

	public static String[] REQUIRED_SIG_KEYS = new String[] { SIG_DATE,
	      SIG_ISSUER, SIG_VALUE, SIG_NUMBER, SIG_ID, SIG_KZ };
	
	public static boolean isRequredSigTypeKey(String name) {
		return ArrayUtils.contains(REQUIRED_SIG_KEYS, name);
	}
}
