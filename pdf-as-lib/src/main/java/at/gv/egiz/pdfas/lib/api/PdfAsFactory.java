/*******************************************************************************
 * <copyright> Copyright 2014 by E-Government Innovation Center EGIZ, Graz, Austria </copyright>
 * PDF-AS has been contracted by the E-Government Innovation Center EGIZ, a
 * joint initiative of the Federal Chancellery Austria and Graz University of
 * Technology.
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * http://www.osor.eu/eupl/
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * 
 * This product combines work with different licenses. See the "NOTICE" text
 * file for details on the various modules and licenses.
 * The "NOTICE" text file is part of the distribution. Any derivative works
 * that you distribute must include a readable copy of the "NOTICE" text file.
 ******************************************************************************/
package at.gv.egiz.pdfas.lib.api;

import iaik.security.ec.provider.ECCelerate;
import iaik.security.provider.IAIK;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Provider;
import java.security.Security;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.activation.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;
import at.gv.egiz.pdfas.lib.impl.PdfAsImpl;
import at.gv.egiz.pdfas.lib.impl.SignParameterImpl;
import at.gv.egiz.pdfas.lib.impl.VerifyParameterImpl;

public class PdfAsFactory implements IConfigurationConstants {

	private static final Logger logger = LoggerFactory
			.getLogger(PdfAsFactory.class);

	private static final String DEFAULT_CONFIG_RES = "config/config.zip";

	protected static void registerProvider(Provider provider, int position) {
		String name = provider.getName();
		if (Security.getProvider(name) == null) {
			// register IAIK provider at first position
			try {
				if (position < 0) {
					// add provider add default position.
					Security.addProvider(provider);
				} else {
					Security.insertProviderAt(provider, position);
				}
			} catch (SecurityException e) {
				logger.info("Failed to register required security Provider.", e);
			}
		} else {
			logger.info("Required security Provider {} already registered.",
					name);
		}

	}

	protected static void listRegisteredSecurityProviders() {
		Provider[] providers = Security.getProviders();
		logger.debug("Registered Security Providers:");
		for (int i = 0; i < providers.length; i++) {
			logger.debug("    {}: {} => {}", i, providers[i].getName(),
					providers[i].getInfo());
		}
	}

	private static boolean initialized = false;
	private static Object init_mutex = new Object();

	private static void registerSecurityProvider(ISettings configuration) {
		boolean doRegister = true;

		String register = configuration.getValue(REGISTER_PROVIDER);
		if (register != null) {
			if (register.equals("false")) {
				doRegister = false;
			}
		}

		if (doRegister) {
			logger.info("Registering Security Providers!");

			registerProvider(new IAIK(), 1);
			// TODO: register ECCelerate in second position when TLS issue is
			// fixed
			registerProvider(new ECCelerate(), -1);

			teeInformation("+ IAIK-JCE Version: " + IAIK.getVersionInfo());
			teeInformation("+ ECCelerate Version: " + ECCelerate.getInstance().getVersion());
		} else {
			logger.info("Skipping Security Provider registration!");
		}
	}

	private static void teeInformation(String str) {
		System.out.println(str);
		logger.info(str);
	}

	/**
	 * Configure log.
	 *
	 * @param configuration
	 *            the configuration
	 */
	private static void initialize(ISettings configuration) {
		if (!initialized) {
			synchronized (init_mutex) {
				if (!initialized) {
					initialized = true;

					teeInformation("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
					teeInformation("+ PDF-AS: " + getVersion());
					teeInformation("+ PDF-AS SCM Revision: " + getSCMRevision());
					registerSecurityProvider(configuration);
					teeInformation("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

					listRegisteredSecurityProviders();
				}
			}
		}
	}

	/**
	 * Create a new instance of PDF-AS
	 * 
	 * @param configuration
	 *            The PDF-AS configuration
	 * @return
	 */
	public static PdfAs createPdfAs(File configuration) {
		PdfAs pdfas = new PdfAsImpl(configuration);

		if (!initialized) {
			synchronized (init_mutex) {
				initialize((ISettings) pdfas.getConfiguration());
			}
		}

		return pdfas;
	}

	/**
	 * Creates a new PdfAs object.
	 *
	 * @param settings
	 *            the settings
	 * @return the pdf as
	 */
	public static PdfAs createPdfAs(ISettings settings) {
		if (!initialized) {
			synchronized (init_mutex) {
				initialize(settings);
			}
		}

		return new PdfAsImpl(settings);
	}

	/**
	 * Creates a sign parameter
	 * 
	 * @param configuration
	 *            The configuration to be used
	 * @param dataSource
	 *            The data source to be used
	 * @return
	 */
	public static SignParameter createSignParameter(
			Configuration configuration, DataSource dataSource, OutputStream output) {
		SignParameter param = new SignParameterImpl(configuration, dataSource, output);
		return param;
	}

	/**
	 * Creates a verification parameter
	 * 
	 * @param configuration
	 *            The configuration to be used
	 * @param dataSource
	 *            The data source to be used
	 * @return
	 */
	public static VerifyParameter createVerifyParameter(
			Configuration configuration, DataSource dataSource) {
		VerifyParameter param = new VerifyParameterImpl(configuration,
				dataSource);
		return param;
	}

	/**
	 * Deploy default configuration to targetDirectory
	 * 
	 * The targetDirectory will be deleted and
	 * 
	 * @param targetDirectory
	 * @throws Exception
	 */
	public static void deployDefaultConfiguration(File targetDirectory)
			throws Exception {
		if (targetDirectory.exists()) {
			targetDirectory.delete();
		}

		if (!targetDirectory.exists()) {
			targetDirectory.mkdir();
		}
		InputStream is = ClassLoader
				.getSystemResourceAsStream(DEFAULT_CONFIG_RES);
		ZipInputStream zip = null;
		try {
			zip = new ZipInputStream(is);

			ZipEntry entry = zip.getNextEntry();
			while (entry != null) {

				File destinationPath = new File(
						targetDirectory.getAbsolutePath(), entry.getName());

				// create parent directories
				destinationPath.getParentFile().mkdirs();

				// if the entry is a file extract it
				if (entry.isDirectory()) {
					destinationPath.mkdir();
					zip.closeEntry();
					entry = zip.getNextEntry();
					continue;
				} else {

					logger.debug("Extracting file: " + destinationPath);

					int b;
					byte buffer[] = new byte[1024];

					FileOutputStream fos = new FileOutputStream(destinationPath);

					BufferedOutputStream bos = new BufferedOutputStream(fos,
							1024);

					while ((b = zip.read(buffer, 0, 1024)) != -1) {
						bos.write(buffer, 0, b);
					}

					bos.close();
					zip.closeEntry();

				}
				entry = zip.getNextEntry();
			}

		} catch (IOException ioe) {
			System.out.println("Error opening zip file" + ioe);
		} finally {
			try {
				if (zip != null) {
					zip.close();
				}
			} catch (IOException ioe) {
				System.out.println("Error while closing zip file" + ioe);
			}
		}
	}

	/**
	 * Gets the PDF-AS SCM Revision
	 * 
	 * @return
	 */
	public static String getSCMRevision() {
		Package pack = PdfAsFactory.class.getPackage();
		return pack.getSpecificationVersion();
	}

	/**
	 * Gets the PDF-AS Version
	 * 
	 * @return PDF-AS Verison string
	 */
	public static String getVersion() {
		Package pack = PdfAsFactory.class.getPackage();
		return pack.getImplementationVersion();
	}
}
