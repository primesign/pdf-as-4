package at.gv.egiz.pdfas.lib.impl.pdfbox;

import at.gv.egiz.pdfas.lib.backend.PDFASBackend;
import at.gv.egiz.pdfas.lib.impl.pdfbox.placeholder.PDFBoxPlaceholderExtractor;
import at.gv.egiz.pdfas.lib.impl.placeholder.PlaceholderExtractor;
import at.gv.egiz.pdfas.lib.impl.signing.IPdfSigner;
import at.gv.egiz.pdfas.lib.impl.signing.pdfbox.PADESPDFBOXSigner;
import at.gv.egiz.pdfas.lib.impl.verify.VerifyBackend;
import at.gv.egiz.pdfas.lib.impl.verify.pdfbox.PDFBOXVerifier;

public class PDFBOXBackend implements PDFASBackend {

	private static final String NAME = "PDFBOX_BACKEND";
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean usedAsDefault() {
		return true;
	}

	@Override
	public IPdfSigner getPdfSigner() {
		return new PADESPDFBOXSigner();
	}

	@Override
	public PlaceholderExtractor getPlaceholderExtractor() {
		return new PDFBoxPlaceholderExtractor();
	}

	@Override
	public VerifyBackend getVerifier() {
		return new PDFBOXVerifier();
	}

}
