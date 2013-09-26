package at.gv.egiz.pdfas.lib.api;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;

import at.gv.egiz.pdfas.lib.impl.PdfAsImpl;

public class PdfAsFactory {
	
	static {
		PropertyConfigurator.configure(ClassLoader.getSystemResourceAsStream("resources/log4j.properties"));
	}
	
	public static PdfAs createPdfAs(File configuration) {
		return new PdfAsImpl(configuration);
	}
}
