package at.gv.egiz.pdfas.lib.util;

import iaik.asn1.structures.AlgorithmID;
import iaik.x509.X509Certificate;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;

public class CertificateUtils {
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
}
