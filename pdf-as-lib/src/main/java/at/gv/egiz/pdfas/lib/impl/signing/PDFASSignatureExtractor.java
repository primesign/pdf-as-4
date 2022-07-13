package at.gv.egiz.pdfas.lib.impl.signing;

import javax.annotation.Nullable;

public interface PDFASSignatureExtractor extends PDFASSignatureInterface {

	byte[] getSignatureData();

	/**
	 * Returns the byte range that should be used for signature digest calculation.
	 * 
	 * @return The byte range. (may be {@code null}).
	 */
	@Nullable
	int[] getByteRange();

}
