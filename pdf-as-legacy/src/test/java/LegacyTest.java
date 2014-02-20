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
