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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;
import at.gv.egiz.pdfas.lib.impl.PdfAsImpl;
import at.gv.egiz.pdfas.lib.impl.SignParameterImpl;
import at.gv.egiz.pdfas.lib.impl.VerifyParameterImpl;

public class PdfAsFactory {

	private static final Logger logger = LoggerFactory
			.getLogger(PdfAsFactory.class);

	private static final String DEFAULT_CONFIG_RES = "config/config.zip";

	private static final String MAN_ATTRIBUTE = "JARMANIFEST";
	private static final String PDF_AS_LIB = "PDF-AS-LIB";
	private static final String IMPL_VERSION = "Implementation-Version";
	private static final String SCM_REVISION = "SCMREVISION";
	

	static {
		/*
		 * PropertyConfigurator.configure(ClassLoader
		 * .getSystemResourceAsStream("resources/log4j.properties"));
		 */
		IAIK.addAsProvider();
		ECCelerate.addAsProvider();
		
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("+ PDF-AS: " + getVersion());
		System.out.println("+ PDF-AS SCM Revision: " + getSCMRevision());
		System.out.println("+ IAIK-JCE Version: " + IAIK.getVersionInfo());
		System.out.println("+ ECCelerate Version: " + ECCelerate.getInstance().getVersion());
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}

	private static boolean log_configured = false;
	private static Object log_mutex = new Object();

	public static void dontConfigureLog4j() {
		synchronized (log_mutex) {
			log_configured = true;
		}
	}

	/**
	 * Create a new instance of PDF-AS
	 * @param configuration The PDF-AS configuration 
	 * @return
	 */
	public static PdfAs createPdfAs(File configuration) {
		if (!log_configured) {
			synchronized (log_mutex) {
				if (!log_configured) {
					File log4j = new File(configuration.getAbsolutePath()
							+ File.separator + "cfg" + File.separator
							+ "log4j.properties");
					logger.info("Loading log4j configuration: "
							+ log4j.getAbsolutePath());
					if (log4j.exists()) {
						try {
							System.setProperty("pdf-as.work-dir",
									configuration.getAbsolutePath());
							PropertyConfigurator.configure(new FileInputStream(
									log4j));
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
					log_configured = true;
				}
			}
		}

		return new PdfAsImpl(configuration);
	}

	/**
	 * Creates a sign parameter
	 * @param configuration The configuration to be used
	 * @param dataSource The data source to be used
	 * @return
	 */
	public static SignParameter createSignParameter(
			Configuration configuration, DataSource dataSource) {
		SignParameter param = new SignParameterImpl(configuration, dataSource);
		return param;
	}

	/**
	 * Creates a verification parameter
	 * @param configuration The configuration to be used
	 * @param dataSource The data source to be used
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
	 * @return
	 */
	public static String getSCMRevision() {
		Package pack = PdfAsFactory.class.getPackage();
		return pack.getSpecificationVersion();
	}
	
	/** 
	 * Gets the PDF-AS Version 
	 * @return PDF-AS Verison string
	 */
	public static String getVersion() {
		Package pack = PdfAsFactory.class.getPackage();
		return pack.getImplementationVersion();
	}
}
