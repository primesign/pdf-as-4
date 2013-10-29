package at.gv.egiz.pdfas.lib.api;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;

import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;
import at.gv.egiz.pdfas.lib.impl.PdfAsImpl;
import at.gv.egiz.pdfas.lib.impl.SignParameterImpl;
import at.gv.egiz.pdfas.lib.impl.VerifyParameterImpl;

public class PdfAsFactory {
	
	static {
		PropertyConfigurator.configure(ClassLoader.getSystemResourceAsStream("resources/log4j.properties"));
	}
	
	public static PdfAs createPdfAs(File configuration) {
		return new PdfAsImpl(configuration);
	}
	
	public static SignParameter createSignParameter(Configuration configuration, DataSource dataSource) {
		SignParameter param = new SignParameterImpl(configuration, dataSource);
		return param;
	}
	
	public static VerifyParameter createVerifyParameter(Configuration configuration, DataSource dataSource) {
		VerifyParameter param = new VerifyParameterImpl(configuration, dataSource);
		return param;
	}
}
