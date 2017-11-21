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
package at.gv.egiz.pdfas.lib.pki;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.pki.impl.DefaultCertificateVerificationDataProvider;
import at.gv.egiz.pdfas.lib.pki.spi.CertificateVerificationData;
import at.gv.egiz.pdfas.lib.pki.spi.CertificateVerificationDataProviderSpi;

/**
 * Service that helps to retrieve {@link CertificateVerificationData} for a certain end entity certificate.
 * <p>
 * The service asks available info {@linkplain CertificateVerificationDataProviderSpi Providers} for the required
 * information. Providers are able to decide if they support the specific certificate's chain (certification authority)
 * or not.
 * </p>
 * </p>
 * Providers may be registered using the "Service Loader" mechanism as described in
 * "<a href="https://docs.oracle.com/javase/tutorial/ext/basics/spi.html#register-service-providers">Creating Extensible
 * Applications</a>". The registry text file must match the name of the info Provider interface:
 * {@link at.gv.egiz.pdfas.lib.pki.spi.CertificateVerificationDataProviderSpi}
 * </p>
 * <p>
 * Note that there is a basic (default) provider implementation ({@link DefaultCertificateVerificationDataProvider})
 * that will be used in case there are not further provider registered.
 * </p>
 * 
 * @author Thomas Knall, PrimeSign GmbH
 *
 */
public class CertificateVerificationDataService {

	final List<CertificateVerificationDataProviderSpi> providers;
	
	/**
	 * Detects and loads all available {@link CertificateVerificationDataProviderSpi} providers including the internal
	 * {@link DefaultCertificateVerificationDataProvider}.
	 */
	private CertificateVerificationDataService() {
		// ServiceLoader is not thread-safe, so we need to fetch providers here (due to initialization-on-demand holder
		// approach in guaranteed thread-safe environment) and not later on (potential concurrent) invocations.
		ServiceLoader<CertificateVerificationDataProviderSpi> serviceLoader = ServiceLoader.load(CertificateVerificationDataProviderSpi.class);
		ArrayList<CertificateVerificationDataProviderSpi> providerList = new ArrayList<>();
		for (CertificateVerificationDataProviderSpi provider : serviceLoader) {
			providerList.add(provider);
		}
		providerList.add(new DefaultCertificateVerificationDataProvider());
		providerList.trimToSize();
		providers = Collections.unmodifiableList(providerList);
	}

	private static class LazyHolder {
		private static final CertificateVerificationDataService SERVICE_INSTANCE;
		static {
			SERVICE_INSTANCE = new CertificateVerificationDataService();
		}
	}

	/**
	 * Returns a singleton instance.
	 * 
	 * @return The instance (never {@code null}).
	 */
	public static final CertificateVerificationDataService getInstance() {
		return LazyHolder.SERVICE_INSTANCE;
	}
	
	/**
	 * Determines if the service supports the provided certificate (e.g. the certificate's certification authority)
	 * meaning that there is at least one {@link CertificateVerificationDataProviderSpi} available that is able to handle the
	 * certificate.
	 * 
	 * @param eeCertificate
	 *            The end entity certificate (required; must not be {@code null}).
	 * @param settings
	 *            The configuration of the PDF-AS environment (required; must not be {@code null}).
	 * @return {@code true} if supported, {@code false} otherwise.
	 * @apiNote If returning {@code false} {@link #getCertificateVerificationData(java.security.cert.X509Certificate, ISettings)} must not
	 *          be invoked (may throw exceptions).
	 */
	public boolean canHandle(java.security.cert.X509Certificate eeCertificate, ISettings settings) {
		for (CertificateVerificationDataProviderSpi provider : providers) {
			if (provider.canHandle(eeCertificate, settings)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Retrieves {@link CertificateVerificationData} for a certain {@code eeCertificate}. The service delegates the
	 * request to registered {@link CertificateVerificationDataProviderSpi}s in order to retrieve the required
	 * information.
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
	public CertificateVerificationData getCertificateVerificationData(java.security.cert.X509Certificate eeCertificate, ISettings settings) throws CertificateException, IOException {
		for (CertificateVerificationDataProviderSpi provider : providers) {
			if (provider.canHandle(eeCertificate, settings)) {
				CertificateVerificationData certificateVerificationData = provider.getCertificateVerificationData(eeCertificate, settings);
				if (certificateVerificationData != null) {
					return certificateVerificationData;
				}
			}
		}
		return null;
	}
	
}
