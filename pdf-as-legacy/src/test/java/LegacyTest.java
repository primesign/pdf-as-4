import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;

import at.gv.egiz.pdfas.PdfAsFactory;
import at.gv.egiz.pdfas.api.PdfAs;
import at.gv.egiz.pdfas.api.sign.SignParameters;
import at.gv.egiz.pdfas.api.sign.SignResult;
import at.gv.egiz.pdfas.api.verify.VerifyParameters;
import at.gv.egiz.pdfas.api.verify.VerifyResult;
import at.gv.egiz.pdfas.api.verify.VerifyResults;

public class LegacyTest {

	public static void main(String[] args) {
		try {
			PdfAs pdfAS = PdfAsFactory.createPdfAs();

			SignParameters signParameters = new SignParameters();
			signParameters.setSignatureDevice("bku");
			signParameters.setSignatureProfileId("SIGNATURBLOCK_DE");
			
			InputStream is = LegacyTest.class.getResourceAsStream("simple.pdf");
			
			byte[] inputData = IOUtils.toByteArray(is);
			ByteArrayDataSink bads = new ByteArrayDataSink();
			signParameters.setDocument(new ByteArrayDataSource(inputData));
			signParameters.setOutput(bads);
			SignResult result = pdfAS.sign(signParameters);
			IOUtils.write(bads.getBytes(), new FileOutputStream("/tmp/test.pdf"));
			
			System.out.println("Signed @ " + result.getSignaturePosition().toString());
			System.out.println("Signed by " + result.getSignerCertificate().getSubjectDN().getName());
			
			VerifyParameters verifyParameters = new VerifyParameters();
			verifyParameters.setDocument(new ByteArrayDataSource(bads.getBytes()));
			verifyParameters.setSignatureToVerify(0);
			
			VerifyResults results = pdfAS.verify(verifyParameters);
			
			Iterator iter = results.getResults().iterator();
			
			while(iter.hasNext()) {
				Object obj = iter.next();
				if(obj instanceof VerifyResult) {
					VerifyResult vresult = (VerifyResult)obj;
					System.out.println("Verified: " +  vresult.getValueCheckCode().getCode() + " " + 
							vresult.getValueCheckCode().getMessage());
				}
			}
			
		} catch (Throwable e) {
			System.out.println("ERROR");
			e.printStackTrace();
		}
	}

}
