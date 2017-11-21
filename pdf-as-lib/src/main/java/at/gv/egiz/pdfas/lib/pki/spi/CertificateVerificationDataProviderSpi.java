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

import java.io.IOException;
import java.security.cert.CertificateException;

import at.gv.egiz.pdfas.common.settings.ISettings;

/**
 * Interface for components that provide {@link CertificateVerificationData}.
 * 
 * @author Thomas Knall, PrimeSign GmbH
 *
 */
public interface CertificateVerificationDataProviderSpi {

	/**
	 * Determines if this provider supports the provided end entity certificate (e.g. the certificate's certification
	 * authority).
	 * 
	 * @param eeCertificate
	 *            The end entity certificate (required; must not be {@code null}).
	 * @param settings
	 *            The configuration of the PDF-AS environment (required; must not be {@code null}).
	 * @return {@code true} if supported, {@code false} otherwise.
	 * @apiNote If returning {@code false} {@link #getCertificateVerificationData(java.security.cert.X509Certificate, ISettings)}
	 *          must not be invoked (may throw exceptions).
	 */
	boolean canHandle(java.security.cert.X509Certificate eeCertificate, ISettings settings);

	/**
	 * Retrieves {@link CertificateVerificationData} for a certain {@code eeCertificate}. The provider may use various
	 * kind of sources (PKI modules, CertPath API, LDAP stores...) in order to retrieve the required information.
	 * 
	 * @param eeCertificate
	 *            The end entity certificate (required; must not be {@code null}).
	 * @param settings
	 *            The configuration of the PDF-AS environment (required; must not be {@code null}).
	 * @return Certificate verification data (never {@code null}).
	 * @throws CertificateException
	 *             Thrown in case a problem occurs while encoding of decoding certificates.
	 * @throws IOException
	 *             Thrown in case retrieval of data (OCSP, CRL, LDAP...) could not be performed due to IO errors.
	 */
	CertificateVerificationData getCertificateVerificationData(java.security.cert.X509Certificate eeCertificate, ISettings settings)
			throws CertificateException, IOException;

}
