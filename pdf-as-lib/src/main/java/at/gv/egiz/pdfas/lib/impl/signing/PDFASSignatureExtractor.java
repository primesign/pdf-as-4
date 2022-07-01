package at.gv.egiz.pdfas.lib.impl.signing;

import javax.annotation.Nullable;

public interface PDFASSignatureExtractor extends PDFASSignatureInterface {
	
	byte[] getSignatureData();

	// TODO[PDFAS-114]: Add javadoc
	@Nullable
	int[] getByteRange();
	
}
