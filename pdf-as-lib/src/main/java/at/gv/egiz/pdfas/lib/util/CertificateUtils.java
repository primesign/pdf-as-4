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
package at.gv.egiz.pdfas.lib.util;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import iaik.asn1.structures.AlgorithmID;
import iaik.x509.X509Certificate;
import iaik.x509.X509ExtensionInitException;
import iaik.x509.extensions.AuthorityKeyIdentifier;

public class CertificateUtils {
	
	private static final Logger log = LoggerFactory.getLogger(CertificateUtils.class);
	
	public static AlgorithmID[] getAlgorithmIDs(X509Certificate signingCertificate)
			throws NoSuchAlgorithmException {
		PublicKey publicKey = signingCertificate.getPublicKey();
		String algorithm = publicKey.getAlgorithm();
		AlgorithmID[] algorithms = new AlgorithmID[2];
		AlgorithmID signatureAlgorithm;
		AlgorithmID digestAlgorithm;

		if ("DSA".equals(algorithm)) {
			signatureAlgorithm = AlgorithmID.dsaWithSHA256;
			digestAlgorithm = AlgorithmID.sha256;
		} else if ("RSA".equals(algorithm)) {
			signatureAlgorithm = AlgorithmID.sha256WithRSAEncryption;
			digestAlgorithm = AlgorithmID.sha256;
		} else if (("EC".equals(algorithm)) || ("ECDSA".equals(algorithm))) {

			int fieldSize = 0;
			if (publicKey instanceof ECPublicKey) {
				ECParameterSpec params = ((ECPublicKey) publicKey).getParams();
				fieldSize = params.getCurve().getField().getFieldSize();
			}

			if (fieldSize >= 512) {
				signatureAlgorithm = AlgorithmID.ecdsa_With_SHA512;
				digestAlgorithm = AlgorithmID.sha512;
			} else if (fieldSize >= 256) {
				signatureAlgorithm = AlgorithmID.ecdsa_With_SHA256;
				digestAlgorithm = AlgorithmID.sha256;
			} else {
				signatureAlgorithm = AlgorithmID.ecdsa_With_SHA1;
				digestAlgorithm = AlgorithmID.sha1;
			}
		} else {
			throw new NoSuchAlgorithmException("Public key algorithm '"
					+ algorithm + "' not supported.");
		}
		
		algorithms[0] = signatureAlgorithm;
		algorithms[1] = digestAlgorithm;
		
		return algorithms;
	}
	
	/**
	 * Returns the signing certificate's authority key identifier entry (if any).
	 * 
	 * @param signingCertificate The signing certificate (required; must not be {@code null}).
	 * @return A hex string (lowercase) representing the authority key identifier (or an empty {@link Optional} in case the
	 *         certificate does not have this extension or the extension could not be initialized).
	 */
	public static Optional<String> getAuthorityKeyIdentifierHexString(@Nonnull X509Certificate signingCertificate) {
		return getAuthorityKeyIdentifier(signingCertificate).map(Hex::encodeHexString);
	}
	
	/**
	 * Returns the signing certificate's authority key identifier entry (if any).
	 * 
	 * @param signingCertificate The signing certificate (required; must not be {@code null}).
	 * @return The authority key identifier (or an empty {@link Optional} in case the certificate does not have this
	 *         extension or the extension could not be initialized).
	 */
	public static Optional<byte[]> getAuthorityKeyIdentifier(@Nonnull X509Certificate signingCertificate) {
		
		try {
			AuthorityKeyIdentifier aki = (AuthorityKeyIdentifier) signingCertificate.getExtension(AuthorityKeyIdentifier.oid);
			if (aki != null) {
				return Optional.ofNullable(aki.getKeyIdentifier());
			}
		} catch (X509ExtensionInitException e) {
			// go a defensive way, do not throw exception
			log.debug("Unable to retrieve authority key identifier from certificate: {}", signingCertificate, e);
		}
		return Optional.empty();
	}

}
