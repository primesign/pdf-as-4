package at.gv.egiz.pdfas.cli.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.activation.DataSource;
import javax.activation.FileDataSource;

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.impl.PdfAsImpl;
import at.gv.egiz.pdfas.sigs.pades.PAdESSigner;
import at.gv.egiz.sl.util.BKUSLConnector;

public class CorruptPDF {

	public static void main(String[] args) throws IOException, PDFASError {
		final File CFG_FOLDER = new File(
				"/home/afitzek/.pdfas");

		final File OUT_FOLDER = new File("/home/afitzek/tmp");

		final String PROFILE = "UNTERSCHREIBE_AT_Q_EN";

		final File LOGO = new File(CFG_FOLDER,
				"images/WIRELESS_MONTENEGRO_logo.png");

		final String SCALE_TO_FIT = "41.102;31.802";

		final File FILE_TO_BE_SIGNED = new File("/home/afitzek/empty.pdf");

		PdfAsImpl pdfAsImpl = new PdfAsImpl(CFG_FOLDER);

		FileOutputStream fout = new FileOutputStream(new File(OUT_FOLDER,
				PROFILE + ".pdf"));

		DataSource toBeSignedDataSource = new FileDataSource(FILE_TO_BE_SIGNED);

		Configuration configuration = pdfAsImpl.getConfiguration();

		//configuration.setValue(REGISTER_PROVIDER, Boolean.toString(true));

		PdfAs pdfAsApi = PdfAsFactory.createPdfAs((ISettings) configuration);

		Configuration config = pdfAsApi.getConfiguration();

		if (SCALE_TO_FIT != null) {

			config.setValue("sig_obj." + PROFILE
					+ ".table.main.Style.imagescaletofit", SCALE_TO_FIT);

		}

		config.setValue("sig_obj." + PROFILE + ".value.SIG_LABEL",
				LOGO.getCanonicalPath());

		SignParameter signParameters = PdfAsFactory.createSignParameter(config,
				toBeSignedDataSource, fout);

		signParameters.setSignatureProfileId(PROFILE);

		signParameters.setPlainSigner(new PAdESSigner(
				new BKUSLConnector(config)));

		pdfAsApi.sign(signParameters);

		fout.flush();

		fout.close();
	}

}
