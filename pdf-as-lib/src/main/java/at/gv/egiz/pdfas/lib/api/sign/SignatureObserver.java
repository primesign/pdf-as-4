package at.gv.egiz.pdfas.lib.api.sign;

import at.gv.egiz.pdfas.lib.impl.status.PDFObject;

/**
 * Interface that allows tracking signature states.
 *
 * @author Thomas Knall, PrimeSign GmbH
 *
 */
public interface SignatureObserver {

	/**
	 * Called right before the cryptographic signature is calculated from the document's content. Note that the signature
	 * widget might already be added to the document while the signature value is still empty (for obvious reasons).
	 *
	 * <p>
	 * The provided pdf object reflects an implementation-agnostic reference to the document being processed. Observing
	 * units being aware of the actual pdf processor being used (e.g. PDFBox 1.x) may cast the object accordingly.
	 * </p>
	 *
	 * @param pdfObject The processed pdf object (required; must not be {@code null}).
	 */
	void onBeforeSignature(PDFObject pdfObject);

}
