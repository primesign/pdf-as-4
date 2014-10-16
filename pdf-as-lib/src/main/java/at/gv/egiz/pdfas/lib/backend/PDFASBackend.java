package at.gv.egiz.pdfas.lib.backend;

import at.gv.egiz.pdfas.lib.impl.placeholder.PlaceholderExtractor;
import at.gv.egiz.pdfas.lib.impl.signing.IPdfSigner;
import at.gv.egiz.pdfas.lib.impl.verify.VerifyBackend;

public interface PDFASBackend {
	public String getName();
	public boolean usedAsDefault();
	public IPdfSigner getPdfSigner();
	public PlaceholderExtractor getPlaceholderExtractor();
	public VerifyBackend getVerifier();
}
