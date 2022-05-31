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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.lib.util.CertificateUtils;
import iaik.asn1.CodingException;
import iaik.asn1.ObjectID;
import iaik.asn1.structures.AccessDescription;
import iaik.asn1.structures.AlgorithmID;
import iaik.asn1.structures.Name;
import iaik.x509.X509Certificate;
import iaik.x509.X509ExtensionException;
import iaik.x509.X509ExtensionInitException;
import iaik.x509.extensions.AuthorityInfoAccess;
import iaik.x509.ocsp.BasicOCSPResponse;
import iaik.x509.ocsp.CertID;
import iaik.x509.ocsp.CertStatus;
import iaik.x509.ocsp.OCSPException;
import iaik.x509.ocsp.OCSPRequest;
import iaik.x509.ocsp.OCSPResponse;
import iaik.x509.ocsp.ReqCert;
import iaik.x509.ocsp.Request;
import iaik.x509.ocsp.SingleResponse;
import iaik.x509.ocsp.UnknownResponseException;

/**
 * Simple OCSP client for requesting OCSP responses via http. Uses Apache http client.
 * 
 * @author Thomas Knall, PrimeSign GmbH
 * @see <a href="https://tools.ietf.org/html/rfc6960#appendix-A">PKIX OCSP - OCSP over HTTP</a>
 * @implNote This class is immutable and thread-safe.
 */
@ThreadSafe
@Immutable
public class OCSPClient implements AutoCloseable {
	
	private Logger log = LoggerFactory.getLogger(OCSPClient.class);
	
	// @formatter:off
	private final CloseableHttpClient httpClient;
	private final RequestConfig       requestConfig;
	private final boolean             httpCachingEnabled;
	// @formatter:on
	
	/**
	 * Builder for creating an OCSP client.
	 * 
	 * @author tknall
	 */
	public static class Builder {
		
		// @formatter:off
		private boolean httpCachingEnabled   = false;
		private int     connectTimeOutMillis = 10000;
		private int     socketTimeOutMillis  = 10000;
		// @formatter:on
		
		Builder() {
		}

		/**
		 * Enables support for HTTP caching by sending OCSP requests using HTTP GET.
		 * <p>
		 * <a href="https://tools.ietf.org/html/rfc6960#appendix-A">RFC6960</a> says: <i>HTTP-based OCSP requests can
		 * use either the GET or the POST method to submit their requests. To enable HTTP caching, small requests (that
		 * after encoding are less than 255 bytes) MAY be submitted using GET. If HTTP caching is not important or if
		 * the request is greater than 255 bytes, the request SHOULD be submitted using POST.</i>
		 * </p>
		 * 
		 * @param httpCachingEnabled
		 *            {@code true} in order to allow for caching, {@code false} otherwise.
		 * @return This builder (fluent interface).
		 * @implNote Default: {@code false}<p>Note that in case the encoded request is greater than 255 bytes, this setting is ignored and the
		 *           request will still being sent using POST.</p>
		 */
		public Builder setHttpCachingEnabled(boolean httpCachingEnabled) {
			this.httpCachingEnabled = httpCachingEnabled;
			return this;
		}

		/**
		 * Sets the socket timeout in milliseconds.
		 * 
		 * @param socketTimeOutMillis
		 *            The timeout in milliseconds (-1 means no limit).
		 * @return This builder (fluent interface).
		 * @implNote Default: {@code 10000}
		 */
		public Builder setSocketTimeOutMillis(int socketTimeOutMillis) {
			this.socketTimeOutMillis = socketTimeOutMillis;
			return this;
		}

		/**
		 * Sets the connection timeout in milliseconds.
		 * 
		 * @param connectTimeOutMillis
		 *            The timeout in milliseconds (-1 means no limit).
		 * @return This builder (fluent interface).
		 * @implNote Default: {@code 10000}
		 */
		public Builder setConnectTimeOutMillis(int connectTimeOutMillis) {
			this.connectTimeOutMillis = connectTimeOutMillis;
			return this;
		}
		
		/**
		 * Creates a new OCSP client using the builder's settings. 
		 * @return The OCSP client.
		 */
		public OCSPClient build() {
			// @formatter:off
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(socketTimeOutMillis)
					.setConnectTimeout(connectTimeOutMillis)
				.build();
			// @formatter:on
			return new OCSPClient(httpCachingEnabled, requestConfig);
		}
		
	}

	/**
	 * Creates the client using the settings from the builder.
	 * 
	 * @param httpCachingEnabled
	 *            {@code true} in order to support HTTP caching, {@code false} otherwise.
	 * @param requestConfig
	 *            The settings to be applied to the respective http request (required; must not be {@code null}).
	 */
	private OCSPClient(boolean httpCachingEnabled, RequestConfig requestConfig) {
		this.httpCachingEnabled = httpCachingEnabled;
		this.requestConfig = Objects.requireNonNull(requestConfig);
		httpClient = HttpClients.createSystem();
	}
	
	/**
	 * Returns a builder to be used for creating the OCSP client.
	 * 
	 * @return The builder (never {@code null}).
	 */
	public static Builder builder() {
		return new Builder();
	}

	@Override
	public void close() throws Exception {
		httpClient.close();
	}

	@Override
	protected void finalize() throws Throwable {
		close();
	}

	/**
	 * Utility class with useful helper methods in terms of OCSP handling. 
	 * @author tknall
	 *
	 */
	public static class Util {
		
		private Util() {
		}

		/**
		 * Determines if the provided certificate has an OCSP responder url.
		 * 
		 * @param x509Certificate
		 *            The certificate (required; must not be {@code null}).
		 * @return {@code true} if a responder url is available, {@code false} if not.
		 * @apiNote When this method returns {@code true} it is regarded safe to invoke
		 *          {@link #getOcspResponse(X509Certificate, X509Certificate)}.
		 */
		public static boolean hasOcspResponder(X509Certificate x509Certificate) {
			return getOcspUrl(x509Certificate) != null;
		}
		
		/**
		 * Determines the certificate's OCSP responder url (if any).
		 * 
		 * @param x509Certificate
		 *            The certificate (required; must not be {@code null}).
		 * @return The OCSP responder url or {@code null} if the certificate does not provide an OCSP responder url.
		 */
		static String getOcspUrl(X509Certificate x509Certificate) {
			AuthorityInfoAccess aia;
			try {
				aia = (AuthorityInfoAccess) Objects.requireNonNull(x509Certificate).getExtension(AuthorityInfoAccess.oid);
			} catch (X509ExtensionInitException e) {
				throw new IllegalStateException("Unable to initialize cert extension AuthorityInfoAccess.", e);
			}
			if (aia != null) {
				AccessDescription ad = aia.getAccessDescription(ObjectID.ocsp);
				if (ad != null) {
					return ad.getUriAccessLocation();
				}
			}
			return null;
		}
		
	}
	
	/**
	 * Retrieves an OCSP response for a certain certificate ({@code eeCertificate}) of a certain CA.
	 * <p>
	 * Note that {@code issuerCertificate} is optional, but nevertheless recommended. Is omitted, data required to create
	 * the {@code OCSPRequest} (issuer public key hash, issuer name hash) is derived from the provided
	 * {@code eeCertificate}. In case the OCSP responder returns multiple results selecting a suitable one may be done using
	 * the {@code issuerCertificate} if provided.
	 * </p>
	 * 
	 * @param issuerCertificate The issuer certificate (optional; may be {@code null}).
	 * @param eeCertificate     The end entity certificate (required; must not be {@code null}).
	 * @return The OCSP response (never {@code null}) with guaranteed response status "successful" and with <strong>any
	 *         revocation state</strong>.
	 * @throws IOException              Thrown in case of error communicating with OCSP responder.
	 * @throws OCSPClientException      In case the client could not process the response (e.g. non-successful response
	 *                                  state like malformedRequest, internalError... or an unknown/unsupported response
	 *                                  type).
	 * @throws IllegalArgumentException In case the provided {@code eeCertificate} does not provide an OCSP responder url
	 *                                  (use {@link Util#hasOcspResponder(X509Certificate)} in order to determine if it is
	 *                                  safe to call this method) or the provided certificates could not be used for OCSP
	 *                                  request creation. <strong>Or in case the {@code issuerCertificate} was omitted and
	 *                                  {@code eeCertificate} does not provide authority key identifier extension.</strong>
	 * @implNote This implementation just returns OCSP responses (<strong>of any revocation status</strong>) as they were
	 *           retrieved from the OCSP responder (provided the response status indicates a successful response) without
	 *           performing further checks like OCSP signature verification or OCSP responder certificate validation.
	 */
	public OCSPResponse getOcspResponse(@Nullable X509Certificate issuerCertificate, @Nonnull X509Certificate eeCertificate) throws IOException, OCSPClientException {
	
		Objects.requireNonNull(eeCertificate, "End-entity certificate required... must not be null.");
		
		StopWatch sw = new StopWatch();
		sw.start();

		if (log.isDebugEnabled()) {
			log.debug("Retrieving OCSP revocation info for: {}", eeCertificate.getSubjectDN());
		} else if (log.isInfoEnabled()) {
			log.info("Retrieving OCSP revocation info for certificate (SHA-1 fingerprint): {}", Hex.encodeHexString(eeCertificate.getFingerprintSHA()));
		}
		
		String ocspUrl = Util.getOcspUrl(eeCertificate);
		if (ocspUrl == null) {
			throw new IllegalArgumentException("The provided certificate does not feature an ocsp responder url.");
		}
		
		// create request
		byte[] ocspRequestEncoded;
		final ReqCert reqCert;
		byte[] nonce = new byte[32];
		try {

			final CertID certID;
			if (issuerCertificate != null) {
				certID = new CertID(AlgorithmID.sha1, issuerCertificate, eeCertificate);
			} else {
				Name issuerName = (Name) eeCertificate.getIssuerDN();
				byte[] issuerNameHash = MessageDigest.getInstance("SHA-1").digest(issuerName.getEncoded());
				byte[] issuerKeyHash = CertificateUtils.getAuthorityKeyIdentifier(eeCertificate)
						.orElseThrow(() -> new IllegalArgumentException("Unable to encode ocsp request with the provided end-entity certificate which does not provide authority key identifier."));
				certID = new CertID(AlgorithmID.sha1, issuerNameHash, issuerKeyHash, eeCertificate.getSerialNumber());
			}
			reqCert = new ReqCert(ReqCert.certID, certID);
			Request request = new Request(reqCert);
			OCSPRequest ocspRequest = new OCSPRequest();
			try {
				new SecureRandom().nextBytes(nonce);
				ocspRequest.setNonce(nonce);
				if (log.isDebugEnabled()) {
					log.debug("Setting random nonce for ocsp request: {}", Hex.encodeHexString(nonce));
				}
			} catch (X509ExtensionException e) {
				nonce = null;
				log.info("Unable to set random nonce for ocsp request: {}", String.valueOf(e));
			}
			ocspRequest.setRequestList(new Request[] { request });
			ocspRequestEncoded = ocspRequest.getEncoded();
			
			if (log.isTraceEnabled()) {
				log.trace("Creating OCSP request: {}", request);
			}

		} catch (NoSuchAlgorithmException e) {
			// should not occur actually
			throw new IllegalStateException("Required algorithm (SHA-1) not available.", e);
		} catch (CodingException e) {
			throw new IllegalArgumentException("Unable to encode ocsp request with the provided issuer and end-entity certificates.", e);
		}

		// https://tools.ietf.org/html/rfc6960
		// GET {url}/{url-encoding of base-64 encoding of the DER encoding of the OCSPRequest}
		String b64OcspRequest = org.apache.commons.codec.binary.Base64.encodeBase64String(ocspRequestEncoded);
		String urlEncodedB64OcspRequest = URLEncoder.encode(b64OcspRequest, "UTF-8");

		HttpRequestBase request;
		
		if (httpCachingEnabled && urlEncodedB64OcspRequest.length() <= 255) {
			// spec proposes GET request
			URI ocspResponderUri;
			try {
				URIBuilder uriBuilder = new URIBuilder(ocspUrl);
				uriBuilder.setPath(StringUtils.appendIfMissing(uriBuilder.getPath(), "/") + urlEncodedB64OcspRequest);
				ocspResponderUri = uriBuilder.build();
			} catch (URISyntaxException e) {
				// can only occur with eeCertificate containing invalid ocsp responder url
				throw new IllegalArgumentException("Unable process OCSP responder uri of provided certificate: " + ocspUrl, e);
			}
			
			request = new HttpGet(ocspResponderUri);
			log.info("Sending OCSP request using HTTP GET to: {}", ocspUrl);
			
		} else {
			// spec proposes POST request
			HttpPost httpPost = new HttpPost(ocspUrl);
			httpPost.setEntity(new ByteArrayEntity(ocspRequestEncoded, ContentType.create("application/ocsp-request")));
			request = httpPost;
			log.info("Sending OCSP request using HTTP POST to: {}", ocspUrl);
		}
		
		request.setConfig(requestConfig);
		
		try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {
			
			StatusLine statusLine = httpResponse.getStatusLine();
			log.debug("OCSP response HTTP status: {}", statusLine);
			if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
				throw new IOException("OCSP responder did not report HTTP 200: " + statusLine);
			}
			
			HttpEntity responseEntity = httpResponse.getEntity();
			
			OCSPResponse ocspResponse;
			try (InputStream in = responseEntity.getContent()) {
				
				ocspResponse = new OCSPResponse(in);
				
			} catch (UnknownResponseException e) {
				throw new OCSPClientException("Unknown (unsupported) OCSP response type: " + e.getResponseType(), e);
			}
			
			if (log.isTraceEnabled()) {
				log.trace("OCSP response: {}", ocspResponse);
			}
			
			log.debug("OCSP response status: {}", ocspResponse.getResponseStatusName());
			if (ocspResponse.getResponseStatus() != OCSPResponse.successful) {
				throw new OCSPClientException("OCSP response status was not successful, got response status: " + ocspResponse.getResponseStatusName());
			}
			
			// get the basic ocsp response (which is the only type currently supported, otherwise an
			// UnknownResponseException would have been thrown during parsing the response)
			BasicOCSPResponse basicOCSPResponse = (BasicOCSPResponse) ocspResponse.getResponse();

			// consider nonce
			if (nonce != null) {
				log.trace("Validating nonce of ocsp response.");
				try {
					byte[] responseNonce = basicOCSPResponse.getNonce();
					if (!Arrays.equals(nonce, responseNonce)) {
						throw new IllegalStateException("Nonce of request (" + Hex.encodeHexString(nonce) + ") does not match nonce of response (" + Hex.encodeHexString(responseNonce) + ").");
					}
					log.trace("Matching request & response nonse.");
				} catch (X509ExtensionInitException e) {
					throw new IllegalStateException("Unable to get nonce from response although used for request.", e);
				}
			}

			// for future improvement: verify ocsp response, responder certificate...
			
			SingleResponse singleResponse;
			try {
				log.trace("Looking for OCSP response specific for: {}", reqCert);
				singleResponse = basicOCSPResponse.getSingleResponse(reqCert);
			} catch (OCSPException e) {
				try {
					singleResponse = basicOCSPResponse.getSingleResponse(eeCertificate, issuerCertificate, null);
				} catch (OCSPException e1) {
					throw new OCSPClientException("Unable to process received OCSP response for the provided certificate (SHA-1 fingerprint): " + Hex.encodeHexString(eeCertificate.getFingerprintSHA()), e1);
				}
			}
			
			if (log.isTraceEnabled()) {
				log.trace("OCSP respose for specific certificate: {}", singleResponse);
			}
			
			CertStatus certStatus = singleResponse.getCertStatus();
			String formattedThisUpdate = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(singleResponse.getThisUpdate());
			log.info("Certificate revocation state (@{}}: {}", formattedThisUpdate, certStatus);
			
			sw.stop();
			log.debug("OCSP query took: {}ms", sw.getTime());
			
			return ocspResponse;

		} // close httpResponse
			
	}

}

