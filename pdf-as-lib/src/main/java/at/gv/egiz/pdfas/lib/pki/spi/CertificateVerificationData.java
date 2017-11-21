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

import java.util.Collection;

/**
 * Collection of data required for validation of a certain end entity certificate.
 * 
 * @author Thomas Knall, PrimeSign GmbH
 *
 */
public interface CertificateVerificationData {

	/**
	 * Returns a group of certificates reflecting the chain of a certain end entity certificate.
	 * 
	 * @return The chain of certificates (never {@code null}).
	 * @apiNote The implementation should return the full chain of certificates (end entity certificate, intermediate
	 *          certificates (if any) and root certificate/trust anchor) in any order. The chain should at least contain
	 *          the end entity certificate.
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

}
