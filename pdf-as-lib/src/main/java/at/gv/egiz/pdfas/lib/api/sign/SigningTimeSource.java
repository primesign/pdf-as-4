package at.gv.egiz.pdfas.lib.api.sign;

import java.util.Calendar;

import javax.annotation.Nonnull;

import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;

/**
 * Reflects the concept of an externally provided pdf signing time.
 * 
 * @author Thomas Knall, PrimeSign GmbH
 *
 */
@FunctionalInterface
public interface SigningTimeSource {

	/**
	 * Returns a signing time to be used as time of signature.
	 * 
	 * @param requestedSignature The signature context. (required; must not be {@code null})
	 * @return The signing time. (never {@code null})
	 */
	@Nonnull
	Calendar getSigningTime(@Nonnull RequestedSignature requestedSignature);

}
