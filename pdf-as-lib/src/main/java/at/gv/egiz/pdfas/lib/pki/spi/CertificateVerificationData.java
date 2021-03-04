/*******************************************************************************
 * <copyright> Copyright 2017 by PrimeSign GmbH, Graz, Austria </copyright>
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
package at.gv.egiz.pdfas.lib.pki.spi;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Collection of data required for validation of a certain end entity certificate.
 * 
 * @author Thomas Knall, PrimeSign GmbH
 *
 */
public interface CertificateVerificationData {

	/**
	 * Returns several certificates relevant for validating the pdf signature (e.g. chain with signature certificate, OCSP
	 * responder certificate etc.).
	 * 
	 * @return A collection of certificates. (never {@code null})
	 * @apiNote The implementation should return the full chain of certificates (end entity certificate, intermediate
	 *          certificates (if any) and root certificate/trust anchor) in any order. The chain should at least contain the
	 *          end entity certificate.
	 * @see #getChainCertsWithRevocationStatus()
	 */
	Collection<java.security.cert.X509Certificate> getChainCerts();

	/**
	 * Returns a group of BER encoded OCSP Responses (RFC 2560) that may be used to validate the certificates returned
	 * by {@link #getChainCerts()}.
	 * 
	 * @return Collection of encoded OCSP responses (may be {@code null} or empty).
	 * @see #getChainCerts()
	 */
	Collection<byte[]> getEncodedOCSPResponses();

	/**
	 * Returns a group of Certificate Revocation Lists (CRL) according to RFC 5280 that may be used to validate the
	 * certificates returned by {@link #getChainCerts()}.
	 * <p>
	 * Note that CRLs may become considerably big. Therefore OCSP should be preferred over CRL (if possible).
	 * 
	 * @return Collection of CRLs (may be {@code null} or empty).
	 * @see #getChainCerts()
	 */
	Collection<java.security.cert.X509CRL> getCRLs();
	
	/**
	 * Returns several certificates relevant for validating the pdf signature (e.g. chain with signature certificate, OCSP
	 * responder certificate etc.) including their individual revocation status.
	 * 
	 * @return A collection of certificates with their revocation status. (never {@code null})
	 * @apiNote The implementation should return the full chain of certificates (end entity certificate, intermediate
	 *          certificates (if any) and root certificate/trust anchor) in any order. The chain should at least contain the
	 *          end entity certificate.
	 * @see #getChainCerts()
	 */
	default Collection<CertificateAndRevocationStatus> getChainCertsWithRevocationStatus() {
		
		Collection<X509Certificate> chainCerts = getChainCerts();
		if (chainCerts == null || chainCerts.isEmpty()) {
			return Collections.emptyList();
		}

		// @formatter:off
		return chainCerts.stream()
			.map(certificate -> new CertificateAndRevocationStatus() {
				@Override
				public X509Certificate getCertificate() {
					return certificate;
				}
				@Override
				public RevocationStatus getRevocationStatus() {
					return RevocationStatus.NOT_CHECKED;
				}})
			.collect(Collectors.toList());
		// @formatter:on
	}
	
	/**
	 * Associates a certain certificate with its revocation status at the time of signature.
	 * 
	 * @author Thomas Knall, PrimeSign GmbH
	 *
	 */
	interface CertificateAndRevocationStatus {

		/**
		 * Returns the underlying certificate.
		 * 
		 * @return The certificate (never {@code null}).
		 */
		X509Certificate getCertificate();

		/**
		 * Returns the certificate's revocation status.
		 * 
		 * @return The revocation status (never {@code null}).
		 */
		RevocationStatus getRevocationStatus();

	}

	/**
	 * Reflects a certain revocation status.
	 * 
	 * @author Thomas Knall, PrimeSign GmbH
	 *
	 */
	enum RevocationStatus {

		/**
		 * The revocation check has not been performed.
		 */
		NOT_CHECKED,

		/**
		 * The revocation was performed but failed (e.g. unreachable OCSP responder).
		 */
		CHECK_FAILED,

		/**
		 * The revocation check was performed an returned an "unknown" state.
		 */
		UNKNOWN,

		/**
		 * The revocation check was performed an returned a "good" state (the certificate has not been revoked).
		 */
		GOOD,

		/**
		 * The revocation check was performed: the certificate has been suspended.
		 */
		SUSPENDED,

		/**
		 * The revocation check was performed: the certificate has been revoked.
		 */
		REVOKED

	}

}
