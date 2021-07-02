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
package at.gv.egiz.pdfas.web.client.test;

import at.gv.egiz.pdfas.api.ws.PDFASSignParameters;
import at.gv.egiz.pdfas.api.ws.PDFASSignParameters.Connector;
import at.gv.egiz.pdfas.api.ws.PDFASSignRequest;
import at.gv.egiz.pdfas.api.ws.PDFASSignResponse;
import at.gv.egiz.pdfas.web.client.RemotePDFSigner;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;

public class SimpleTestSOAPInvalidParameter {

	public static void main(String[] args) {
		try {
			String file = "/Users/amarsalek/Documents/pdf-as-4/unsigned.pdf";
			FileInputStream fis = new FileInputStream(file);
			byte[] inputData = IOUtils.toByteArray(fis);
 
			PDFASSignParameters signParameters = new PDFASSignParameters();
//			signParameters.setConnector(Connector.JKS);
			signParameters.setConnector(Connector.BKU);
			signParameters.setPosition(null);
			signParameters.setProfile("SIGNATURBLOCK_SMALL_DE1");
			signParameters.setQRCodeContent("TEST CONTENT");
			//signParameters.setKeyIdentifier("test");

			PDFASSignRequest request = new PDFASSignRequest();
			request.setInputData(inputData);
			request.setParameters(signParameters);
			request.setRequestID("SOME TEST ID");
			request.getSignatureBlockParameters().put("abc","SOAP Test");
			//URL endpoint = new
			//URL("http://demo.egiz.gv.at/demoportal-pdf_as/wssign?wsdl");
			//URL endpoint = new
			//		URL("http://www.buergerkarte.at/pdf-as-extern-4/wssign?wsdl");
//			String baseUrl  = "http://demo.egiz.gv.at/demoportal-pdf_as/services/";
			String baseUrl  = "http://localhost:8080/pdf-as-web/services/";
			//URL endpoint = new URL(
			//		"http://192.168.56.10/pdf-as-web/wssign?wsdl");

			URL signEndpoint = new URL(baseUrl + "wssign?wsdl");

			RemotePDFSigner signer = new RemotePDFSigner(signEndpoint, true);

			PDFASSignRequest signrequest = new PDFASSignRequest();
			signrequest.setInputData(inputData);
			signrequest.setParameters(signParameters);
			signParameters.setTransactionId("MYID ....");
			signrequest.getSignatureBlockParameters().put("abc","SOAP Test 5555");
			signrequest.getSignatureBlockParameters().put("way toooooooooooooo long","SOAP Test 5555");
			System.out.println("Simple Request:"); 
			PDFASSignResponse response = signer.signPDFDokument(signrequest);
			
			System.out.println("Sign Error: " + response.getError());
			System.out.println("redirect url: " + response.getRedirectUrl());

			System.out.println("Should see an error page...");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
