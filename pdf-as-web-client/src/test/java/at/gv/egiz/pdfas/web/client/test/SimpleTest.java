package at.gv.egiz.pdfas.web.client.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;

import at.gv.egiz.pdfas.api.ws.PDFASSignParameters;
import at.gv.egiz.pdfas.api.ws.PDFASSignParameters.Connector;
import at.gv.egiz.pdfas.web.client.RemotePDFSigner;
import sun.misc.IOUtils;

public class SimpleTest {

	public static void main(String[] args) {
		try {
			FileInputStream fis = new FileInputStream("/home/afitzek/simple.pdf");
			byte[] inputData = IOUtils.readFully(fis, -1, true);
			
			PDFASSignParameters signParameters = new PDFASSignParameters();
			signParameters.setConnector(Connector.JKS);
			signParameters.setPosition(null);
			signParameters.setProfile("SIGNATURBLOCK_DE");
			
			URL endpoint = new URL("http://localhost:8080/pdf-as-web/wssign?wsdl");
			
			RemotePDFSigner signer = new RemotePDFSigner(endpoint, false);
			
			byte[] outputFile =  signer.signPDFDokument(inputData, signParameters);
		
			FileOutputStream fos = new FileOutputStream("/home/afitzek/signed.pdf");
			fos.write(outputFile);
			fos.close();
			
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}

}
