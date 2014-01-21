package at.gv.egiz.pdfas.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import at.gv.egiz.pdfas.lib.api.DataSource;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.sign.SignResult;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.sigs.pades.PAdESSigner;
import at.gv.egiz.pdfas.sigs.pades.PAdESSignerKeystore;
import at.gv.egiz.pdfas.sigs.pkcs7detached.PKCS7DetachedSigner;
import at.gv.egiz.sl.util.BKUSLConnector;
import at.gv.egiz.sl.util.MOAConnector;

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

	public static final String CLI_ARG_DEPLOY_SHORT = "d";
	public static final String CLI_ARG_DEPLOY = "deploy";

	public static final String CLI_ARG_VERIFY_WHICH_SHORT = "vw";
	public static final String CLI_ARG_VERIFY_WHICH = "verify_which";

	public static final String CLI_ARG_KEYSTORE_FILE_SHORT = "ksf";
	public static final String CLI_ARG_KEYSTORE_FILE = "ks_file";

	public static final String CLI_ARG_KEYSTORE_ALIAS = "ks_alias";
	public static final String CLI_ARG_KEYSTORE_ALIAS_SHORT = "ksa";

	public static final String CLI_ARG_KEYSTORE_TYPE = "ks_type";
	public static final String CLI_ARG_KEYSTORE_TYPE_SHORT = "kst";

	public static final String CLI_ARG_KEYSTORE_STOREPASS = "ks_storepass";
	public static final String CLI_ARG_KEYSTORE_STOREPASS_SHORT = "kssp";

	public static final String CLI_ARG_KEYSTORE_KEYPASS = "ks_keypass";
	public static final String CLI_ARG_KEYSTORE_KEYPASS_SHORT = "kskp";

	public static final String STANDARD_CONFIG_LOCATION = System
			.getProperty("user.home") + "/.pdfas/";

	public static final String STANDARD_POSITION_STRING = "x:auto;y:auto;w:auto;p:auto;f:0";

	private static Options createOptions() {
		Options cliOptions = new Options();

		Option modeOption = new Option(CLI_ARG_MODE_SHORT, CLI_ARG_MODE, true,
				"Mode of operation (sign | verify)");
		// modeOption.setRequired(true);
		cliOptions.addOption(modeOption);

		Option deployOption = new Option(CLI_ARG_DEPLOY_SHORT, CLI_ARG_DEPLOY,
				false, "Deploys default configuration");
		// modeOption.setRequired(true);
		cliOptions.addOption(deployOption);

		Option helpOption = new Option(CLI_ARG_HELP_SHORT, CLI_ARG_HELP, false,
				"Shows this help message");
		cliOptions.addOption(helpOption);

		Option connectorOption = new Option(CLI_ARG_CONNECTOR_SHORT,
				CLI_ARG_CONNECTOR, true,
				"Connector to use (bku | ks (Keystore) | moa(not available yet))");
		cliOptions.addOption(connectorOption);

		Option keystoreFileOption = new Option(CLI_ARG_KEYSTORE_FILE_SHORT,
				CLI_ARG_KEYSTORE_FILE, true, "Software keystore file");
		cliOptions.addOption(keystoreFileOption);

		Option keystoreTypeOption = new Option(CLI_ARG_KEYSTORE_TYPE_SHORT,
				CLI_ARG_KEYSTORE_TYPE, true,
				"Software keystore type (PKCS12 | JKS ...)");
		cliOptions.addOption(keystoreTypeOption);

		Option keystoreAliasOption = new Option(CLI_ARG_KEYSTORE_ALIAS_SHORT,
				CLI_ARG_KEYSTORE_ALIAS, true, "Key Alias in keystore");
		cliOptions.addOption(keystoreAliasOption);

		Option keystoreStorePassOption = new Option(
				CLI_ARG_KEYSTORE_STOREPASS_SHORT, CLI_ARG_KEYSTORE_STOREPASS,
				true, "Password for keystore");
		cliOptions.addOption(keystoreStorePassOption);

		Option keystoreKeyPassOption = new Option(
				CLI_ARG_KEYSTORE_KEYPASS_SHORT, CLI_ARG_KEYSTORE_KEYPASS, true,
				"Password for key");
		cliOptions.addOption(keystoreKeyPassOption);

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

			if (cli.hasOption(CLI_ARG_DEPLOY_SHORT)) {
				PdfAsFactory.deployDefaultConfiguration(new File(
						STANDARD_CONFIG_LOCATION));
			}

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

			if (mode == ModeOfOperation.INVALID
					&& !cli.hasOption(CLI_ARG_DEPLOY_SHORT)) {
				usage();
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
			e.printStackTrace(System.err);
			System.exit(-1);
		}
	}

	private static void deployConfigIfNotexisting() {
		File configurationLocation = new File(STANDARD_CONFIG_LOCATION);
		try {
			if (!configurationLocation.exists()) {
				PdfAsFactory.deployDefaultConfiguration(configurationLocation);
			}
		} catch (Exception e) {
			System.out.println("Failed to deploy default confiuration to "
					+ configurationLocation.getAbsolutePath());
			e.printStackTrace();
		}
	}

	private static void perform_sign(CommandLine cli) throws Exception {

		String configurationFile = null;

		if (cli.hasOption(CLI_ARG_CONF_SHORT)) {
			configurationFile = cli.getOptionValue(CLI_ARG_CONF_SHORT);
		} else {
			configurationFile = STANDARD_CONFIG_LOCATION;
			deployConfigIfNotexisting();
		}

		String positionString = null;

		if (cli.hasOption(CLI_ARG_POSITION_SHORT)) {
			positionString = cli.getOptionValue(CLI_ARG_POSITION_SHORT);
		} else {
			positionString = null;
		}

		String profilID = null;

		if (cli.hasOption(CLI_ARG_PROFILE_SHORT)) {
			profilID = cli.getOptionValue(CLI_ARG_PROFILE_SHORT);
		}

		String outputFile = null;

		if (cli.hasOption(CLI_ARG_OUTPUT_SHORT)) {
			outputFile = cli.getOptionValue(CLI_ARG_OUTPUT_SHORT);
		}

		String connector = null;

		if (cli.hasOption(CLI_ARG_CONNECTOR_SHORT)) {
			connector = cli.getOptionValue(CLI_ARG_CONNECTOR_SHORT);
		}

		String pdfFile = null;

		pdfFile = cli.getArgs()[cli.getArgs().length - 1];

		File inputFile = new File(pdfFile);

		if (!inputFile.exists()) {
			throw new Exception("Input file does not exists");
		}

		if (outputFile == null) {
			if (pdfFile.endsWith(".pdf")) {
				outputFile = pdfFile.subSequence(0,
						pdfFile.length() - ".pdf".length())
						+ "_signed.pdf";
			} else {
				outputFile = pdfFile + "_signed.pdf";
			}
		}

		File outputPdfFile = new File(outputFile);

		DataSource dataSource = new ByteArrayDataSource(
				StreamUtils.inputStreamToByteArray(new FileInputStream(
						inputFile)));

		ByteArrayDataSink dataSink = new ByteArrayDataSink();

		PdfAs pdfAs = null;

		pdfAs = PdfAsFactory.createPdfAs(new File(configurationFile));

		Configuration configuration = pdfAs.getConfiguration();

		SignParameter signParameter = PdfAsFactory.createSignParameter(
				configuration, dataSource);

		IPlainSigner slConnector = null;

		if (connector != null) {
			if (connector.equalsIgnoreCase("bku")) {
				slConnector = new PAdESSigner(new BKUSLConnector(configuration));
			} else if (connector.equalsIgnoreCase("moa")) {
				slConnector = new PAdESSigner(new MOAConnector(configuration));
			} else if (connector.equalsIgnoreCase("ks")) {
				String keystoreFilename = null;
				String keystoreAlias = null;
				String keystoreType = null;
				String keystoreStorepass = null;
				String keystoreKeypass = null;

				if (cli.hasOption(CLI_ARG_KEYSTORE_FILE_SHORT)) {
					keystoreFilename = cli
							.getOptionValue(CLI_ARG_KEYSTORE_FILE_SHORT);
				}

				if (cli.hasOption(CLI_ARG_KEYSTORE_ALIAS_SHORT)) {
					keystoreAlias = cli
							.getOptionValue(CLI_ARG_KEYSTORE_ALIAS_SHORT);
				}
				if (cli.hasOption(CLI_ARG_KEYSTORE_TYPE_SHORT)) {
					keystoreType = cli
							.getOptionValue(CLI_ARG_KEYSTORE_TYPE_SHORT);
				}
				if (cli.hasOption(CLI_ARG_KEYSTORE_STOREPASS_SHORT)) {
					keystoreStorepass = cli
							.getOptionValue(CLI_ARG_KEYSTORE_STOREPASS_SHORT);
				}
				if (cli.hasOption(CLI_ARG_KEYSTORE_KEYPASS_SHORT)) {
					keystoreKeypass = cli
							.getOptionValue(CLI_ARG_KEYSTORE_KEYPASS_SHORT);
				}

				if (keystoreFilename == null) {
					throw new Exception(
							"You need to provide a keystore file if using ks connector");
				}
				if (keystoreAlias == null) {
					throw new Exception(
							"You need to provide a key alias if using ks connector");
				}
				if (keystoreType == null) {
					keystoreType = "PKCS12";
					System.out.println("Defaulting to " + keystoreType
							+ " keystore type.");
				}

				if (keystoreStorepass == null) {
					keystoreStorepass = "";
				}

				if (keystoreKeypass == null) {
					keystoreKeypass = "";
				}

				slConnector = new PAdESSignerKeystore(keystoreFilename,
						keystoreAlias, keystoreStorepass, keystoreKeypass,
						keystoreType);
			}
		}
		if (slConnector == null) {
			slConnector = new PAdESSigner(new BKUSLConnector(configuration));
		}

		signParameter.setOutput(dataSink);
		signParameter.setPlainSigner(slConnector);
		signParameter.setDataSource(dataSource);
		signParameter.setSignaturePosition(positionString);
		signParameter.setSignatureProfileId(profilID);
		System.out.println("Starting signature for " + pdfFile);
		System.out.println("Selected signature Profile " + profilID);
		SignResult result = pdfAs.sign(signParameter);

		FileOutputStream fos = new FileOutputStream(outputPdfFile, false);
		fos.write(dataSink.getData());
		fos.close();
		System.out.println("Signed document " + outputFile);
	}

	private static void perform_verify(CommandLine cli) throws Exception {

		String configurationFile = null;

		if (cli.hasOption(CLI_ARG_CONF_SHORT)) {
			configurationFile = cli.getOptionValue(CLI_ARG_CONF_SHORT);
		} else {
			configurationFile = STANDARD_CONFIG_LOCATION;
			deployConfigIfNotexisting();
		}

		int which = -1;

		if (cli.hasOption(CLI_ARG_VERIFY_WHICH_SHORT)) {
			String whichValue = cli.getOptionValue(CLI_ARG_VERIFY_WHICH_SHORT);
			which = Integer.parseInt(whichValue);
		}

		String confOutputFile = null;
		
		if (cli.hasOption(CLI_ARG_OUTPUT_SHORT)) {
			confOutputFile = cli.getOptionValue(CLI_ARG_OUTPUT_SHORT);
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

		VerifyParameter verifyParameter = PdfAsFactory.createVerifyParameter(
				configuration, dataSource);

		verifyParameter.setDataSource(dataSource);
		verifyParameter.setConfiguration(configuration);
		verifyParameter.setWhichSignature(which);

		List<VerifyResult> results = pdfAs.verify(verifyParameter);

		Iterator<VerifyResult> resultIterator = results.iterator();

		int idx = 0;
		while (resultIterator.hasNext()) {
			VerifyResult verifyResult = resultIterator.next();
			dumpVerifyResult(verifyResult, pdfFile, idx, confOutputFile);
			idx++;
		}
	}

	private static void dumpVerifyResult(VerifyResult verifyResult,
			String inputFile, int idx, String confOutputFile) {
		System.out.println("Verification Result:");
		System.out.println("\tValue Check: "
				+ verifyResult.getValueCheckCode().getMessage() + " ["
				+ verifyResult.getValueCheckCode().getCode() + "]");
		System.out.println("\tCertificate Check: "
				+ verifyResult.getCertificateCheck().getMessage() + " ["
				+ verifyResult.getCertificateCheck().getCode() + "]");
		System.out.println("\tQualified Certificate: "
				+ verifyResult.isQualifiedCertificate());
		System.out.println("\tVerification done: "
				+ verifyResult.isVerificationDone());
		try {
			if (verifyResult.isVerificationDone()
					&& verifyResult.getValueCheckCode().getCode() == 0) {
				String outputFile = null;

				if (confOutputFile == null) {
					if (inputFile.endsWith(".pdf")) {
						outputFile = inputFile.subSequence(0,
								inputFile.length() - ".pdf".length())
								+ "_verified_" + idx + ".pdf";
					} else {
						outputFile = inputFile + "_verified_" + idx + ".pdf";
					}
				} else {
					if (confOutputFile.endsWith(".pdf")) {
						outputFile = confOutputFile.subSequence(0,
								confOutputFile.length() - ".pdf".length())
								+ "_" + idx + ".pdf";
					} else {
						outputFile = confOutputFile + "_" + idx + ".pdf";
					}
				}

				File outputPdfFile = new File(outputFile);
				FileOutputStream fos = new FileOutputStream(outputPdfFile,
						false);
				fos.write(verifyResult.getSignatureData());
				fos.close();
				System.out.println("\tSigned PDF: " + outputFile);
			}
		} catch (Exception e) {
			System.out.println("\tFailed to save signed PDF! ["
					+ e.getMessage() + "]");
			e.printStackTrace();
		}
	}
}
