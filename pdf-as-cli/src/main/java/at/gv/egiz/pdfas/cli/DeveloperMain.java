package at.gv.egiz.pdfas.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSource;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;

public class DeveloperMain {

	public static void main(String[] args) {		
		String user_home = System.getProperty("user.home");
		String pdfas_dir = user_home + File.separator + "PDF-AS";
		PdfAs pdfas = PdfAsFactory.createPdfAs(new File(pdfas_dir));
		Configuration config = pdfas.getConfiguration();
		
		byte[] data;
		try {
			data = StreamUtils.inputStreamToByteArray(new FileInputStream("/home/afitzek/devel/pdfas_neu/simple.pdf"));
			SignParameter parameter = new SignParameter(config, new ByteArrayDataSource(data));
			pdfas.sign(parameter);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (PdfAsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
