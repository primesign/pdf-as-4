package at.gv.egiz.pdfas.lib.api.sign;

import java.io.Closeable;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;

import javax.activation.DataSource;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Data structure for storing and passing signature related data supporting external signatures. An external signature
 * creation device is able to take the digest value and to apply the signature.
 * 
 * @author Thomas Knall, PrimeSign GmbH
 *
 * @implNote {@link Closeable Closing} the signature context {@link Closeable#close() closes} the
 *           {@link #getPreparedDocument() prepared document}.
 */
@ParametersAreNullableByDefault
public class ExternalSignatureContext implements Closeable {

	private String digestAlgorithmOid;
	private byte[] digestValue;

	private String signatureAlgorithmOid;
	private byte[] signatureObject;
	private int[] signatureByteRange;

	// Note for further improvement: Instead of the signing certificate the complete chain may be stored, allowing to embedd
	// the full chain into the signature.
	private X509Certificate signingCertificate;

	private DataSource preparedDocument;

	private Calendar signingTime;

	/**
	 * Returns the digest algorithm object identifier as String.
	 * 
	 * @return The digest algorithm object identifier. (may be {@code null})
	 */
	@Nullable
	public String getDigestAlgorithmOid() {
		return digestAlgorithmOid;
	}

	/**
	 * Returns the digest value.
	 * 
	 * @return The digest value. (may be {@code null})
	 */
	@Nullable
	public byte[] getDigestValue() {
		return digestValue;
	}

	/**
	 * Returns the signature algorithm object identifier as String.
	 * 
	 * @return The signature algorithm object identifier. (may be {@code null})
	 */
	@Nullable
	public String getSignatureAlgorithmOid() {
		return signatureAlgorithmOid;
	}

	/**
	 * Returns the abstract signature object (e.g. the ASN.1 object of the encoded CMS ContentInfo).
	 * 
	 * @return The signature object. (may be {@code null})
	 */
	@Nullable
	public byte[] getSignatureObject() {
		return signatureObject;
	}

	/**
	 * Returns the signing certificate.
	 * 
	 * @return The signing certificate (may be {@code null}).
	 */
	@Nullable
	public X509Certificate getSigningCertificate() {
		return signingCertificate;
	}

	/**
	 * Returns the DataSource that has been used to prepare the document to be signed.
	 * 
	 * @return The data souce. (optional; may be {@code null})
	 */
	@Nullable
	public DataSource getPreparedDocument() {
		return preparedDocument;
	}

	/**
	 * Returns the respective signing time.
	 * 
	 * @return The signing time. (optional; may be {@code null})
	 */
	@Nullable
	public Calendar getSigningTime() {
		return signingTime;
	}

	/**
	 * Sets the Object Identifier (as String) of the assumed digest algorithm.
	 * 
	 * @param digestAlgorithmOid The object identifier as String. (optional; may be {@code null})
	 * @see #setDigestValue(byte[])
	 */
	public void setDigestAlgorithmOid(String digestAlgorithmOid) {
		this.digestAlgorithmOid = digestAlgorithmOid;
	}

	/**
	 * Sets the digest value corresponding to the {@#getDigestAlgorithmOid() digest algorithm oid}.
	 * 
	 * @param digestValue The digest value. (optional; may be {@code null})
	 * @see #setDigestAlgorithmOid(String)
	 */
	public void setDigestValue(byte[] digestValue) {
		this.digestValue = digestValue;
	}

	/**
	 * Sets the Object Identifier (as String) of the assumed signature algorithm.
	 * 
	 * @param signatureAlgorithmOid The object identifier as String. (optional; may be {@code null})
	 */
	public void setSignatureAlgorithmOid(String signatureAlgorithmOid) {
		this.signatureAlgorithmOid = signatureAlgorithmOid;
	}

	/**
	 * Sets the abstract signature data object, e.g. the ASN.1 object of the encoded CMS ContentInfo.
	 * 
	 * @param signatureObject The signature object. (optional; may be {@code null})
	 */
	public void setSignatureObject(byte[] signatureObject) {
		this.signatureObject = signatureObject;
	}

	/**
	 * Sets the signing certificate.
	 * 
	 * @param signingCertificate The signing certificate. (optional; may be {@code null})
	 */
	public void setSigningCertificate(X509Certificate signingCertificate) {
		this.signingCertificate = signingCertificate;
	}

	/**
	 * Sets the DataSource to be used for holding the prepared (modified) document (document with signature data but without
	 * signature value yet).
	 * 
	 * @param preparedDocument The DataSource for the prepared document. (optional; may be {@code null})
	 */
	public void setPreparedDocument(DataSource preparedDocument) {
		this.preparedDocument = preparedDocument;
	}

	/**
	 * Returns the signature byte range.
	 * 
	 * @return The signature byte range in the form of tuples (offset, length). (may be {@code null}).
	 */
	@Nullable
	public int[] getSignatureByteRange() {
		return signatureByteRange;
	}

	/**
	 * Sets the signature byte range.
	 * 
	 * @param signatureByteRange The signature byte range in the form of tuples (offset, length). (optional; may be
	 *                           {@code null})
	 */
	public void setSignatureByteRange(@Nullable int[] signatureByteRange) {
		this.signatureByteRange = signatureByteRange;
	}

	/**
	 * Sets the signing time.
	 * 
	 * @param signingTime The signing time. (optional; may be {@code null})
	 */
	public void setSigningTime(@Nullable Calendar signingTime) {
		this.signingTime = signingTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (digestAlgorithmOid != null) {
			builder.append("digestAlgorithmOid=").append(digestAlgorithmOid);
		}
		if (digestValue != null) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append("digestValue=").append(Base64.getEncoder().encodeToString(digestValue));
		}
		if (signatureAlgorithmOid != null) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append("signatureAlgorithmOid=").append(signatureAlgorithmOid);
		}
		if (signatureByteRange != null) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append("signatureByteRange=").append(Arrays.toString(signatureByteRange));
		}
		if (signingTime != null) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append("signingTime=").append(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(signingTime.getTime()));
		}
		if (signingCertificate != null) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			try {
				builder.append("signingCertificate=").append(DigestUtils.sha1Hex(signingCertificate.getEncoded()));
			} catch (CertificateEncodingException e) {
				// should never occur
				throw new RuntimeException("Unable to encode certificate.", e);
			}
		}
		if (signatureObject != null) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append("signatureObject=").append(StringUtils.abbreviate(Hex.encodeHexString(signatureObject), 20));
		}
		if (preparedDocument != null) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append("preparedDocument=").append(preparedDocument);
		}
		builder.insert(0, "ExternalSignatureContext [");
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Closes the {@link #getPreparedDocument() prepared document} DataSource releasing resources.
	 */
	@Override
	public void close() throws IOException {
		if (preparedDocument instanceof Closeable) {
			try {
				((Closeable) preparedDocument).close();
			} finally {
				preparedDocument = null;
			}
		}
	}

}
