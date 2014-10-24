package at.gv.egiz.pdfas.lib.impl.pdfbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.lib.backend.PDFASBackend;
import at.gv.egiz.pdfas.lib.impl.pdfbox.placeholder.PDFBoxPlaceholderExtractor;
import at.gv.egiz.pdfas.lib.impl.placeholder.PlaceholderExtractor;
import at.gv.egiz.pdfas.lib.impl.signing.IPdfSigner;
import at.gv.egiz.pdfas.lib.impl.signing.pdfbox.PADESPDFBOXSigner;
import at.gv.egiz.pdfas.lib.impl.verify.VerifyBackend;
import at.gv.egiz.pdfas.lib.impl.verify.pdfbox.PDFBOXVerifier;

public class PDFBOXBackend implements PDFASBackend {

	private static final String NAME = "PDFBOX_BACKEND";
	
	private static final Logger logger = LoggerFactory
			.getLogger(PDFBOXBackend.class);
	
	static {
		logger.info(" ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		logger.info(" + PDFBOX Backend created");
		logger.info(" + PDFBOX Version used: " + org.apache.pdfbox.Version.getVersion());
		logger.info(" ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}
	
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
