package at.gv.egiz.pdfas.wrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;

import at.gv.egiz.pdfas.PdfAsFactory;
import at.gv.egiz.pdfas.api.PdfAs;
import at.gv.egiz.pdfas.api.sign.SignParameters;
import at.gv.egiz.pdfas.api.verify.VerifyParameters;
import at.gv.egiz.pdfas.api.verify.VerifyResult;
import at.gv.egiz.pdfas.api.verify.VerifyResults;

public class LegacyMainTest {
	public static void main(String[] args) {
		try {
			PdfAs pdfAsOld = PdfAsFactory.createPdfAs();
			SignParameters signParameters = new SignParameters();
			signParameters.setSignatureProfileId("SIGNATURBLOCK_DE");
			signParameters.setSignatureDevice("bku");
			signParameters.setSignatureType("binary");
			
			FileDataSource dataSource = new FileDataSource(new File("/home/afitzek/simple.pdf"));
			signParameters.setDocument(dataSource);
			ByteArrayDataSink_OLD dataSink = new ByteArrayDataSink_OLD();
			signParameters.setOutput(dataSink);
			pdfAsOld.sign(signParameters);
			
			FileOutputStream fos = new FileOutputStream(new File("/home/afitzek/simple_osigned.pdf"));
			fos.write(dataSink.getBAOS().toByteArray());
			fos.close();
			
			VerifyParameters parameters = new VerifyParameters();
			parameters.setDocument(new FileDataSource(new File("/home/afitzek/simple_osigned.pdf")));
			parameters.setSignatureDevice("bku");
			
			VerifyResults verifyResults = pdfAsOld.verify(parameters);
			
			Iterator<Object> verifyIt = verifyResults.getResults().iterator();
			
			while(verifyIt.hasNext()) {
				Object obj = verifyIt.next();
				VerifyResult verify = (VerifyResult)obj;
				System.out.println("Verify Code: " + verify.getValueCheckCode().getCode());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
