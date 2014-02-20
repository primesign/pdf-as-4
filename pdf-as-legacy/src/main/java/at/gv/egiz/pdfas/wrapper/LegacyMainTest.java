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
