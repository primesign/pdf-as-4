package at.gv.egiz.pdfas.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSink;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSource;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.DataSink;
import at.gv.egiz.pdfas.lib.api.DataSource;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.sign.SignResult;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;

public class Main {

	public static final String CLI_ARG_MODE_SHORT = "m";
	public static final String CLI_ARG_MODE = "mode";

	public static final String CLI_ARG_HELP_SHORT = "h";
	public static final String CLI_ARG_HELP = "help";

	public static final String CLI_ARG_CONNECTOR_SHORT = "c";
	public static final String CLI_ARG_CONNECTOR = "connector";

	public static final String CLI_ARG_OUTPUT_SHORT = "o";
	public static final String CLI_ARG_OUTPUT = "output";

	public static final String CLI_ARG_PROFILE_SHORT = "p";
	public static final String CLI_ARG_PROFILE = "profile";

	public static final String CLI_ARG_POSITION_SHORT = "pos";
	public static final String CLI_ARG_POSITION = "position";

	public static final String CLI_ARG_CONF_SHORT = "conf";
	public static final String CLI_ARG_CONF = "configuration";

	public static final String CLI_ARG_VERIFY_WHICH_SHORT = "vw";
	public static final String CLI_ARG_VERIFY_WHICH = "verify_which";

	public static final String STANDARD_CONFIG_LOCATION = System
			.getProperty("user.home") + "/.pdfas/cfg/";

	public static final String STANDARD_POSITION_STRING = "x:auto;y:auto;w:auto;p:auto;f:0";

	private static Options createOptions() {
		Options cliOptions = new Options();

		Option modeOption = new Option(CLI_ARG_MODE_SHORT, CLI_ARG_MODE, true,
				"Mode of operation");
		// modeOption.setRequired(true);
		cliOptions.addOption(modeOption);

		Option helpOption = new Option(CLI_ARG_HELP_SHORT, CLI_ARG_HELP, false,
				"Shows this help message");
		cliOptions.addOption(helpOption);

		Option connectorOption = new Option(CLI_ARG_CONNECTOR_SHORT,
				CLI_ARG_CONNECTOR, true, "Connector to use");
		cliOptions.addOption(connectorOption);

		Option profileOption = new Option(CLI_ARG_PROFILE_SHORT,
				CLI_ARG_PROFILE, true, "Signature profile to use");
		cliOptions.addOption(profileOption);

		Option positionOption = new Option(
				CLI_ARG_POSITION_SHORT,
				CLI_ARG_POSITION,
				true,
				"Position string: position has the format [x:x_algo];[y:y_algo];[w:w_algo][p:p_algo];[f:f_algo]");
		cliOptions.addOption(positionOption);

		Option confOption = new Option(CLI_ARG_CONF_SHORT, CLI_ARG_CONF, true,
				"Configuration file to use");
		cliOptions.addOption(confOption);

		Option verifywhichOption = new Option(
				CLI_ARG_VERIFY_WHICH_SHORT,
				CLI_ARG_VERIFY_WHICH,
				true,
				"[optional] zero based number of the signature to be verified. If omitted, all signatures are verified.");
		cliOptions.addOption(verifywhichOption);

		Option outputOption = new Option(CLI_ARG_OUTPUT_SHORT, CLI_ARG_OUTPUT,
				true, "The output file");
		cliOptions.addOption(outputOption);

		return cliOptions;
	}

	private static void usage() {
		HelpFormatter formater = new HelpFormatter();
		formater.printHelp("pdf-as [OPTIONS] <input file>", createOptions());
	}

	public static void main(String[] args) {
		// create the command line parser
		CommandLineParser parser = new GnuParser();
		ModeOfOperation mode = ModeOfOperation.INVALID;
		try {
			CommandLine cli = parser.parse(createOptions(), args);

			if (cli.hasOption(CLI_ARG_MODE_SHORT)) {
				String modevalue = cli.getOptionValue(CLI_ARG_MODE_SHORT);
				if (modevalue.toLowerCase().trim().equals("sign")) {
					mode = ModeOfOperation.SIGN;
				} else if (modevalue.toLowerCase().trim().equals("verify")) {
					mode = ModeOfOperation.VERIFY;
				} else {
					throw new ParseException("Invalid value for option "
							+ CLI_ARG_MODE_SHORT + ": " + modevalue);
				}
			}

			if (cli.hasOption(CLI_ARG_HELP_SHORT)) {
				usage();
				System.exit(0);
			}

			if (mode == ModeOfOperation.INVALID) {
				throw new ParseException("Missing required option: "
						+ CLI_ARG_MODE_SHORT);
			} else if (mode == ModeOfOperation.SIGN) {
				perform_sign(cli);
			} else if (mode == ModeOfOperation.VERIFY) {
				perform_verify(cli);
			}

		} catch (ParseException e) {
			System.err.println("Invalid arguments: " + e.getMessage());
			usage();
			System.exit(-1);
		} catch (Exception e) {
			System.err.println("PDF-AS Error: " + e.getMessage());
			System.exit(-1);
		}
	}

	private static void perform_sign(CommandLine cli) throws Exception {

		String configurationFile = null;

		if (cli.hasOption(CLI_ARG_CONF_SHORT)) {
			configurationFile = cli.getOptionValue(CLI_ARG_CONF_SHORT);
		} else {
			configurationFile = STANDARD_CONFIG_LOCATION;
		}

		String positionString = null;

		if (cli.hasOption(CLI_ARG_POSITION_SHORT)) {
			positionString = cli.getOptionValue(CLI_ARG_POSITION_SHORT);
		} else {
			positionString = STANDARD_POSITION_STRING;
		}

		String profilID = null;

		if (cli.hasOption(CLI_ARG_PROFILE_SHORT)) {
			profilID = cli.getOptionValue(CLI_ARG_PROFILE_SHORT);
		}

		String pdfFile = null;

		pdfFile = cli.getArgs()[cli.getArgs().length - 1];

		File inputFile = new File(pdfFile);

		if (!inputFile.exists()) {
			throw new Exception("Input file does not exists");
		}

		DataSource dataSource = new ByteArrayDataSource(
				StreamUtils.inputStreamToByteArray(new FileInputStream(
						inputFile)));

		DataSink dataSink = new ByteArrayDataSink();

		PdfAs pdfAs = null;

		pdfAs = PdfAsFactory.createPdfAs(new File(configurationFile));

		Configuration configuration = pdfAs.getConfiguration();

		SignParameter signParameter = PdfAsFactory.createSignParameter(
				configuration, dataSource);

		signParameter.setOutput(dataSink);

		signParameter.setDataSource(dataSource);
		signParameter.setSignaturePosition(positionString);
		signParameter.setSignatureProfileId(profilID);

		// Set SL Signer! This will need connector value from cli
		// signParameter.setPlainSigner(signer);

		SignResult result = pdfAs.sign(signParameter);

		// TODO write result to file
	}

	private static void perform_verify(CommandLine cli) throws Exception {

		String configurationFile = null;

		if (cli.hasOption(CLI_ARG_CONF_SHORT)) {
			configurationFile = cli.getOptionValue(CLI_ARG_CONF_SHORT);
		} else {
			configurationFile = STANDARD_CONFIG_LOCATION;
		}
		
		int which = -1;

		if (cli.hasOption(CLI_ARG_VERIFY_WHICH_SHORT)) {
			String whichValue = cli.getOptionValue(CLI_ARG_VERIFY_WHICH_SHORT);
			which = Integer.parseInt(whichValue);
		} 
		
		String pdfFile = null;

		pdfFile = cli.getArgs()[cli.getArgs().length - 1];

		File inputFile = new File(pdfFile);

		if (!inputFile.exists()) {
			throw new Exception("Input file does not exists");
		}

		DataSource dataSource = new ByteArrayDataSource(
				StreamUtils.inputStreamToByteArray(new FileInputStream(
						inputFile)));

		PdfAs pdfAs = null;

		pdfAs = PdfAsFactory.createPdfAs(new File(configurationFile));

		Configuration configuration = pdfAs.getConfiguration();
		
		VerifyParameter verifyParameter = 
				PdfAsFactory.createVerifyParameter(configuration, dataSource);
		
		verifyParameter.setDataSource(dataSource);
		verifyParameter.setConfiguration(configuration);
		verifyParameter.setWhichSignature(which);
		
		List<VerifyResult> results = pdfAs.verify(verifyParameter);
		
		Iterator<VerifyResult> resultIterator = results.iterator();
		
		while(resultIterator.hasNext()) {
			VerifyResult verifyResult = resultIterator.next();
			dumpVerifyResult(verifyResult);
		}
	}
	
	private static void dumpVerifyResult(VerifyResult verifyResult) {
		System.out.println("Verification Result:");
		System.out.println("\tValue Check: " + 
				verifyResult.getValueCheckCode().getMessage() + 
				" [" + verifyResult.getValueCheckCode().getCode() + "]");
		System.out.println("\tCertificate Check: " + 
				verifyResult.getCertificateCheck().getMessage() + 
				" [" + verifyResult.getCertificateCheck().getCode() + "]");
		System.out.println("\tQualified Certificate: " + 
				verifyResult.isQualifiedCertificate());
		System.out.println("\tVerification done: " + 
				verifyResult.isVerificationDone());
	}
}
