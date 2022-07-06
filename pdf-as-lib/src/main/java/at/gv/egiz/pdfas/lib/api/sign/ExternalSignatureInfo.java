package at.gv.egiz.pdfas.lib.api.sign;

import javax.annotation.Nonnull;

import iaik.asn1.structures.AlgorithmID;
import iaik.cms.ContentInfo;

/**
 * This class contains information required in order to sign plain digest values.
 * <p>
 * Besides the {@link #getDigestValue() digestValue} this information involves the {@link #getDigestAlgorithm()
 * digestAlgorithm}, the {@link #getSignatureAlgorithm() signatureAlgorithm} as well as an abstract
 * {@link #getSignatureObject() signatureObject} (e.g. reflecting the ASN.1 encoded CMS {@link ContentInfo}).
 * </p>
 * 
 * @author Thomas Knall, PrimeSign GmbH
 *
 */
public interface ExternalSignatureInfo {

	/**
	 * Returns the digest algorithm.
	 * 
	 * @return The digest digest algorithm. (may be {@code null})
	 */
	@Nonnull
	AlgorithmID getDigestAlgorithm();

	/**
	 * Returns the algorithm of the intended signature.
	 * 
	 * @return The signature algorithm. (may be {@code null})
	 */
	@Nonnull
	AlgorithmID getSignatureAlgorithm();

	/**
	 * Returns the digest value.
	 * 
	 * @return The digest value. (may be {@code null})
	 */
	@Nonnull
	byte[] getDigestValue();

	/**
	 * Returns the signature object (e.g. reflecting the ASN.1 encoded CMS {@link ContentInfo}).
	 * 
	 * @return The encoded signature object. (may be {@code null})
	 */
	@Nonnull
	byte[] getSignatureObject();

}
