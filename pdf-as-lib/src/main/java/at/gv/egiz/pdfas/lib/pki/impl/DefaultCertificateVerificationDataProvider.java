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
package at.gv.egiz.pdfas.lib.pki.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.pki.spi.CertificateVerificationData;
import at.gv.egiz.pdfas.lib.pki.spi.CertificateVerificationData.CertificateAndRevocationStatus;
import at.gv.egiz.pdfas.lib.pki.spi.CertificateVerificationData.RevocationStatus;
import at.gv.egiz.pdfas.lib.pki.spi.CertificateVerificationDataProviderSpi;
import at.gv.egiz.pdfas.lib.util.CertificateUtils;
import iaik.asn1.structures.DistributionPoint;
import iaik.utils.Util;
import iaik.x509.RevokedCertificate;
import iaik.x509.X509CRL;
import iaik.x509.X509Certificate;
import iaik.x509.X509ExtensionInitException;
import iaik.x509.extensions.CRLDistributionPoints;
import iaik.x509.ocsp.BasicOCSPResponse;
import iaik.x509.ocsp.CertStatus;
import iaik.x509.ocsp.OCSPResponse;
import iaik.x509.ocsp.SingleResponse;

/**
 * Provides basic support for gathering certificate verification information.
 * 
 * @author Thomas Knall, PrimeSign GmbH
 * @implNote This class is immutable and therefore thread-safe. Note that this implementation might not support
 *           resolving chains with certificates that have been re-certified.
 */
public class DefaultCertificateVerificationDataProvider implements CertificateVerificationDataProviderSpi {
	
	private static final Logger log = LoggerFactory.getLogger(DefaultCertificateVerificationDataProvider.class);

	// Timeouts for OCSP and CRL connections
	private final int DEFAULT_CONNECTION_TIMEOUT_MS  = 10000;
	private final int DEFAULT_READ_TIMEOUT_MS        = 10000;
	
	/**
	 * Name of configuration folder containing PKCS#7 certificate chains.
	 */
	private final String DEFAULT_CHAINSTORE_FOLDER_NAME = "/certchains";
	
	@Override
	public boolean canHandle(java.security.cert.X509Certificate eeCertificate, ISettings settings) {
		return findChainFile(toIAIKX509Certificate(Objects.requireNonNull(eeCertificate)), Objects.requireNonNull(settings)) != null;
	}
	
	@Override
	public CertificateVerificationData getCertificateVerificationData(java.security.cert.X509Certificate eeCertificate, ISettings settings) throws CertificateException, IOException {

		X509Certificate iaikEeCertificate = toIAIKX509Certificate(Objects.requireNonNull(eeCertificate));
		
		// @formatter:off
		final Set<CertificateAndRevocationStatus> certsAndRevStatus = new LinkedHashSet<>();  // not thread-safe
		final List<byte[]>                        ocsps             = new ArrayList<>();      // not thread-safe
		final Set<java.security.cert.X509CRL>     crls              = new LinkedHashSet<>();  // not thread-safe
		// @formatter:on
		
		StopWatch sw = new StopWatch();
		sw.start();
		
		if (log.isDebugEnabled()) {
			log.debug("Retrieving certificate validation info info for {}", iaikEeCertificate.getSubjectDN());
		} else if (log.isInfoEnabled()) {
			log.info("Retrieving certificate validation data for certificate (SHA-1 fingerprint): {}", Hex.encodeHexString(iaikEeCertificate.getFingerprintSHA()));
		}
		
		// retrieve certificate chain for eeCertificate
		X509Certificate[] caChainCertificates = retrieveChain(iaikEeCertificate, Objects.requireNonNull(settings));
		// build up full (sorted) chain including eeCertificate
		X509Certificate[] fullChainCertificates = Util.createCertificateChain(iaikEeCertificate, caChainCertificates);
		
		// determine revocation info, preferring OCSP
		// assume last certificate in chain is trust anchor
		OCSPClient ocspClient = OCSPClient.builder()
				.setConnectTimeOutMillis(DEFAULT_CONNECTION_TIMEOUT_MS)
				.setSocketTimeOutMillis(DEFAULT_READ_TIMEOUT_MS)
				.build();
		
		// iterate over all chain certificates except for the trust anchor
		for (int i = 0; i < fullChainCertificates.length - 1; i++) {
			
			final X509Certificate subjectCertificate = fullChainCertificates[i];
			final X509Certificate issuerCertificate = fullChainCertificates[i+1];
			
			DefaultCertificateAndRevocationStatus certAndRevStatus = new DefaultCertificateAndRevocationStatus(subjectCertificate);
			
			OCSPResponse ocspResponse = null;
			if (OCSPClient.Util.hasOcspResponder(subjectCertificate)) {
				
				try {
					
					ocspResponse = ocspClient.getOcspResponse(issuerCertificate, subjectCertificate);

					// determine status from response
					// The currently used OCSP client support BasicOCSPResponse only, otherwise an exception would have been
					// thrown earlier. Therefore we can safely cast to BasicOCSPResponse here.
					BasicOCSPResponse basicOCSPResponse = (BasicOCSPResponse) ocspResponse.getResponse();
					SingleResponse singleResponse = basicOCSPResponse.getSingleResponse(subjectCertificate, issuerCertificate, null);
					if (singleResponse != null) {
						CertStatus certStatus = singleResponse.getCertStatus();
						switch (certStatus.getCertStatus()) {
						case CertStatus.GOOD:
							certAndRevStatus.setRevocationStatus(RevocationStatus.GOOD);
							break;
						case CertStatus.REVOKED:
							certAndRevStatus.setRevocationStatus(RevocationStatus.REVOKED);
							break;
						default:
							certAndRevStatus.setRevocationStatus(RevocationStatus.UNKNOWN);
							break;
						}
					} else {
						log.info("OCSP response retrieved, but unable to match the requested certificate.");
						certAndRevStatus.setRevocationStatus(RevocationStatus.CHECK_FAILED);
					}
					
					// add ocsp signer certificate to certs
					ocsps.add(ocspResponse.getEncoded());
					
					X509Certificate ocspSignerCertificate = basicOCSPResponse.getSignerCertificate();
					if (ocspSignerCertificate != null) {
						// add ocsp signer certificate without revocation check - in case the certificate has not been added before
						certsAndRevStatus.add(new DefaultCertificateAndRevocationStatus(ocspSignerCertificate));
					}
					
				} catch (Exception e) {
					log.warn("Unable to retrieve OCSP response.", e);
					certAndRevStatus.setRevocationStatus(RevocationStatus.CHECK_FAILED);
				}
				
			}
			
			if (ocspResponse == null) {

				// fall back to CRL
				
				CRLDistributionPoints cRLDistributionPoints;
				try {
					cRLDistributionPoints = (CRLDistributionPoints) subjectCertificate.getExtension(CRLDistributionPoints.oid);
				} catch (X509ExtensionInitException e) {
					throw new IllegalStateException("Unable to initialize extension CRLDistributionPoints.", e);
				}
				X509CRL x509Crl = null;
				if (cRLDistributionPoints != null) {
					
					if (log.isDebugEnabled()) {
						log.debug("Retrieving CRL revocation info for: {}", subjectCertificate.getSubjectDN());
					} else if (log.isInfoEnabled()) {
						log.info("Retrieving CRL revocation info for certificate (SHA-1 fingerprint): {}", Hex.encodeHexString(subjectCertificate.getFingerprintSHA()));
					}
					
					Exception lastException = null;
					@SuppressWarnings("unchecked")
					Enumeration<DistributionPoint> e = cRLDistributionPoints.getDistributionPoints();
					while (e.hasMoreElements() && x509Crl == null) {
						DistributionPoint distributionPoint = e.nextElement();
						
						// inspect distribution point
						if (distributionPoint.containsUriDpName()) {
							
							String[] distributionPointNameURIs = distributionPoint.getDistributionPointNameURIs();
							for (String distributionPointNameURI : distributionPointNameURIs) {
								URL url;
								try {
									log.debug("Trying to download crl from distribution point: {}", distributionPointNameURI);
									if (distributionPointNameURI.toLowerCase().startsWith("ldap://")) {
										url = new URL(null, distributionPointNameURI, new iaik.x509.net.ldap.Handler());
									} else {
										url = new URL(distributionPointNameURI);
									}
									URLConnection urlConnection = url.openConnection();
									urlConnection.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT_MS);
									urlConnection.setReadTimeout(DEFAULT_READ_TIMEOUT_MS);
									try (InputStream in = urlConnection.getInputStream()) {
										x509Crl = new X509CRL(in);
										// we got crl, exit loop
										break;
									} catch (CRLException e1) {
										lastException = e1;
										log.debug("Unable to parse CRL read from distribution point: {} ({})", distributionPointNameURI, e1.getMessage());
									}
								} catch (MalformedURLException e1) {
									log.debug("Unsupported CRL distribution point uri: {} ({})", distributionPointNameURI, e1.getMessage());
									lastException = e1;
								} catch (IOException e1) {
									log.debug("Error reading from CRL distribution point uri: {} ({})", distributionPointNameURI, e1.getMessage());
									lastException = e1;
								} catch (Exception e1) {
									log.debug("Unknown error reading from CRL distribution point uri: {} ({})", distributionPointNameURI, e1.getMessage());
									lastException = e1;
								}
							}
							
						}
						
					}
					
					if (x509Crl != null) {
						
						RevokedCertificate revokedCertificate = x509Crl.containsCertificate(subjectCertificate);
						if (revokedCertificate != null) {
							switch (revokedCertificate.getRevocationReason()) {
							case CERTIFICATE_HOLD:
								certAndRevStatus.setRevocationStatus(RevocationStatus.SUSPENDED);
								break;
							case REMOVE_FROM_CRL:
								// used for delta-crls indicating that the certificate has already been removed from crl
								certAndRevStatus.setRevocationStatus(RevocationStatus.GOOD);
								break;
							default:
								certAndRevStatus.setRevocationStatus(RevocationStatus.REVOKED);
								break;
							}
						} else {
							certAndRevStatus.setRevocationStatus(RevocationStatus.GOOD);
						}

						crls.add(x509Crl);

					} else if (lastException != null) {

						log.warn("Unable to load CRL.", lastException);
						certAndRevStatus.setRevocationStatus(RevocationStatus.CHECK_FAILED);

					}

				}

			}

			// remove an already existing certificate (without revocation checks) (the OCSP responder certificate)...
			certsAndRevStatus.remove(certAndRevStatus);
			// ...and add the certificate with revocation checks
			certsAndRevStatus.add(certAndRevStatus);

		}

		// add (trust) anchor certificate without any revocation checks
		certsAndRevStatus.add(new DefaultCertificateAndRevocationStatus(fullChainCertificates[fullChainCertificates.length - 1]));

		sw.stop();
		log.debug("Querying certificate validation info took: {}ms", sw.getTime());
				
		return new CertificateVerificationData() {
			
			@Override
			public Set<CertificateAndRevocationStatus> getChainCertsWithRevocationStatus() {
				return certsAndRevStatus;
			}

			@Override
			public List<byte[]> getEncodedOCSPResponses() {
				return ocsps;
			}
			
			@Override
			public List<java.security.cert.X509Certificate> getChainCerts() {
				return certsAndRevStatus.stream().map(CertificateAndRevocationStatus::getCertificate).collect(Collectors.toList());
			}
			
			@Override
			public Set<java.security.cert.X509CRL> getCRLs() {
				return crls;
			}
			
		};
	}

	/**
	 * Looks for an certificate chain from configuration folder chain suitable for the provided certificate.
	 * 
	 * @param certificate
	 *            The (end entity) certificate (the signing certificate actually) (required; must not be {@code null}).
	 * @param settings
	 *            The configuration of the PDF-AS environment (required; must not be {@code null}).
	 * @return A readable URL reflecting the a PKCS7 resource or {@code null}.
	 */
	private File findChainFile(X509Certificate certificate, ISettings settings) {
		Optional<String> issuerSkiHex = CertificateUtils.getAuthorityKeyIdentifierHexString(certificate);
		final String subjectFingerprintHex = Hex.encodeHexString(certificate.getFingerprintSHA());
				
		if (issuerSkiHex.isPresent()) {
			log.debug("Looking for certificate chain file for certificate with SHA-1 fingerprint {} (issuer subject key identifier {}).", subjectFingerprintHex, issuerSkiHex.get());
			File certChainDirectory = new File(settings.getWorkingDirectory(), DEFAULT_CHAINSTORE_FOLDER_NAME);
			if (certChainDirectory.exists() && certChainDirectory.isDirectory()) {
				File certChainFile = new File(certChainDirectory, "certchain-ski" + issuerSkiHex.get() + ".p7b");
				if (certChainFile.exists()) {
					if (certChainFile.canRead()) {
						log.debug("Found chain file for certificate (SHA-1 fingerprint {}): {}", subjectFingerprintHex, certChainFile.getAbsolutePath());
						return certChainFile;
					} else {
						log.warn("Found chain file for certificate (SHA-1 fingerprint {}), but file is not readable: {}", subjectFingerprintHex, certChainFile.getAbsolutePath());
					}
				} else {
					log.debug("No chain file for certificate (SHA-1 fingerprint {}) provided by configuration: {}", subjectFingerprintHex, certChainFile.getAbsolutePath());
				}
			} else {
				log.trace("Certificate chain folder does not exist: {}", certChainDirectory.getAbsolutePath());
			}
		} else {
			log.warn("Unable to determine authority key identifier of certificate with SHA-1 fingerprint {}.", subjectFingerprintHex);
		}
		return null;
	}
	
	/**
	 * Retrieves the chain for a provided end entity certificate.
	 * 
	 * @param eeCertificate
	 *            The end entity certificate.
	 * @param settings
	 *            The configuration of the PDF-AS environment (required; must not be {@code null}).
	 * @return The CA chain (never {@code null}).
	 * @throws IOException
	 *             Thrown in case the chain could not be read.
	 * @throws CertificateException
	 *             Thrown in case of an error parsing the chain.
	 * @throws IllegalStateException
	 *             In case the {@code eeCertificate}'s chain is not supported. Use
	 *             {@link #canHandle(java.security.cert.X509Certificate, ISettings)} in order to assure the CA is supported before calling this
	 *             method).
	 */
	private X509Certificate[] retrieveChain(X509Certificate eeCertificate, ISettings settings) throws IOException, CertificateException {
		
		File certChainFile = findChainFile(eeCertificate, settings);
		if (certChainFile == null) {
			throw new IllegalStateException("Unsupported CA. Use canHandle(eeCertificate) in order to test if the eeCertificate's CA is supported.");
		}

		// load certificate chain
		try (InputStream certChainIn = new FileInputStream(certChainFile)) {
			Collection<? extends Certificate> certificates;
			try {
				CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509"); // not guaranteed to be thread-safe
				certificates = certificateFactory.generateCertificates(certChainIn);
			} catch (CertificateException e) {
				// should never occur (therefore not mentioned in javadoc)
				throw new IllegalStateException("X.509 certificates not supported.");
			}
			return Util.convertCertificateChain(certificates.toArray(new Certificate[certificates.size()]));
		}
	}
	
	/**
	 * Converts a "Java" X.509 certificate to "IAIK" X.509 certificate. May return the very same {@code eeCertificate}
	 * in case it is already a IAIK certificate.
	 * 
	 * @param certificate
	 *            The certificate (required; must not be {@code null}).
	 * @return The IAIK certificate (never {@code null}).
	 */
	private X509Certificate toIAIKX509Certificate(java.security.cert.X509Certificate certificate) {
		if (Objects.requireNonNull(certificate) instanceof X509Certificate) {
			return (X509Certificate) certificate;
		} else {
			try {
				return new X509Certificate(certificate.getEncoded());
			} catch (CertificateException e) {
				throw new IllegalStateException("Unable to encode/decode certificate.", e);
			}
		}
	}
	
	/**
	 * Associates a certain certificate with its revocation status.
	 * 
	 * @author Thomas Knall, PrimeSign GmbH
	 * 
	 * @implNote The object's {@link #equals(Object)} and {@link #hashCode()} consider the underlying certificate but not
	 *           the respective state.
	 *
	 */
	private static class DefaultCertificateAndRevocationStatus implements CertificateAndRevocationStatus {
		
		private final java.security.cert.X509Certificate certificate;
		private RevocationStatus revocationStatus;

		/**
		 * Creates a new association.
		 * 
		 * @param certificate      The underlying certificate (required; must not be {@code null}).
		 * @param revocationStatus The respective revocation status (required; must not be {@code null}).
		 */
		public DefaultCertificateAndRevocationStatus(java.security.cert.X509Certificate certificate, RevocationStatus revocationStatus) {
			this.certificate = Objects.requireNonNull(certificate, "'certificate' must not be null.");
			setRevocationStatus(revocationStatus);
		}
		
		/**
		 * Creates a new association assuming an initial revocation status {@link RevocationStatus#NOT_CHECKED}).
		 * 
		 * @param certificate      The underlying certificate (required; must not be {@code null}).
		 */
		public DefaultCertificateAndRevocationStatus(java.security.cert.X509Certificate certificate) {
			this(certificate, RevocationStatus.NOT_CHECKED);
		}

		/**
		 * Sets the revocation status.
		 * 
		 * @param revocationStatus The revocation status (required; must not be {@code null}).
		 */
		public void setRevocationStatus(RevocationStatus revocationStatus) {
			this.revocationStatus = Objects.requireNonNull(revocationStatus, "'revocationStatus' must not be null.");
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((certificate == null) ? 0 : certificate.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DefaultCertificateAndRevocationStatus other = (DefaultCertificateAndRevocationStatus) obj;
			if (certificate == null) {
				if (other.certificate != null)
					return false;
			} else if (!certificate.equals(other.certificate))
				return false;
			return true;
		}

		@Override
		public java.security.cert.X509Certificate getCertificate() {
			return certificate;
		}

		@Override
		public RevocationStatus getRevocationStatus() {
			return revocationStatus;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			// @formatter:off
			builder
				.append("DefaultCertificateAndRevocationStatus [")
					.append("certificate=<SubjectDN='").append(certificate.getSubjectDN()).append("'>")
					.append(", revocationStatus=").append(revocationStatus)
				.append("]");
			// @formatter:on
			return builder.toString();
		}
		
	}
	
}
