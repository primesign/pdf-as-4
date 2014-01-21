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
	

	static {
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("+ PDF-AS: " + getVersion());
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		
		/*
		 * PropertyConfigurator.configure(ClassLoader
		 * .getSystemResourceAsStream("resources/log4j.properties"));
		 */
		IAIK.addAsProvider();
		ECCelerate.addAsProvider();
	}

	private static boolean log_configured = false;
	private static Object log_mutex = new Object();

	public static void dontConfigureLog4j() {
		synchronized (log_mutex) {
			log_configured = true;
		}
	}

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

	public static SignParameter createSignParameter(
			Configuration configuration, DataSource dataSource) {
		SignParameter param = new SignParameterImpl(configuration, dataSource);
		return param;
	}

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

	public static String getVersion() {
		Package pack = PdfAsFactory.class.getPackage();
		return pack.getImplementationVersion();
		/*
		try {
			
			
			Enumeration<URL> resources = PdfAsFactory.class.getClassLoader()
					.getResources("META-INF/MANIFEST.MF");
			while (resources.hasMoreElements()) {
				Manifest manifest = new Manifest(resources.nextElement()
						.openStream());
				Attributes attributes = manifest.getAttributes(MAN_ATTRIBUTE);
				if (attributes != null) {
					if(attributes.isEmpty()) {
						String value = attributes.getValue(new Attributes.Name(MAN_ATTRIBUTE));
						if(value != null && value.equals(PDF_AS_LIB)) {
							// Got my manifest
							return manifest.getAttributes(IMPL_VERSION).getValue(IMPL_VERSION);
						}
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("Failed to read Version!");
			return "0.0.0";
		}
		return "0.0.0";*/
	}
}
