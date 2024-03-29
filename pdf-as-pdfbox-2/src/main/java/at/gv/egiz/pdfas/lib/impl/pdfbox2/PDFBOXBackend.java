package at.gv.egiz.pdfas.lib.impl.pdfbox2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.lib.backend.PDFASBackend;
import at.gv.egiz.pdfas.lib.impl.pdfbox2.placeholder.PDFBoxPlaceholderExtractor;
import at.gv.egiz.pdfas.lib.impl.placeholder.PlaceholderExtractor;
import at.gv.egiz.pdfas.lib.impl.signing.IPdfSigner;
import at.gv.egiz.pdfas.lib.impl.signing.pdfbox2.LTVEnabledPADESPDFBOXSigner;
import at.gv.egiz.pdfas.lib.impl.verify.VerifyBackend;
import at.gv.egiz.pdfas.lib.impl.verify.pdfbox2.PDFBOXVerifier;

public class PDFBOXBackend implements PDFASBackend {

	private static final String NAME = "PDFBOX_2_BACKEND";
	
	private static final Logger logger = LoggerFactory
			.getLogger(PDFBOXBackend.class);
	
	static {
		logger.info(" ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		logger.info(" + PDFBOX Backend created");
		logger.info(" + PDFBOX Version used: " + org.apache.pdfbox.util.Version.getVersion());
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
		return new LTVEnabledPADESPDFBOXSigner();
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
