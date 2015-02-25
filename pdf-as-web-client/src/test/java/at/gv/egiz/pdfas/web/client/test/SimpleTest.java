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

import java.io.FileInputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;

import at.gv.egiz.pdfas.api.ws.PDFASSignParameters;
import at.gv.egiz.pdfas.api.ws.PDFASSignParameters.Connector;
import at.gv.egiz.pdfas.api.ws.PDFASSignRequest;
import at.gv.egiz.pdfas.api.ws.PDFASSignResponse;
import at.gv.egiz.pdfas.api.ws.PDFASVerifyRequest;
import at.gv.egiz.pdfas.api.ws.PDFASVerifyResponse;
import at.gv.egiz.pdfas.api.ws.PDFASVerifyResult;
import at.gv.egiz.pdfas.api.ws.VerificationLevel;
import at.gv.egiz.pdfas.web.client.RemotePDFSigner;
import at.gv.egiz.pdfas.web.client.RemotePDFVerifier;

public class SimpleTest {

	public static void main(String[] args) {
		try {
			FileInputStream fis = new FileInputStream(
					"/home/afitzek/simple.pdf");
			byte[] inputData = IOUtils.toByteArray(fis);

			PDFASSignParameters signParameters = new PDFASSignParameters();
			signParameters.setConnector(Connector.JKS);
			signParameters.setPosition(null);
			signParameters.setProfile("SIGNATURBLOCK_SMALL_DE");
			signParameters.setQRCodeContent("https://docs.google.com/document/d/1DSE9aO8-q9PAlRPyYpH1sgont4rSB_Q5BCeS-X3p6WA/edit?usp=sharing");
			//signParameters.setKeyIdentifier("test");

			PDFASSignRequest request = new PDFASSignRequest();
			request.setInputData(inputData);
			request.setParameters(signParameters);
			request.setRequestID("SOME TEST ID");
			
			//URL endpoint = new
			//URL("http://demo.egiz.gv.at/demoportal-pdf_as/wssign?wsdl");
			//URL endpoint = new
			//		URL("http://www.buergerkarte.at/pdf-as-extern-4/wssign?wsdl");
			String baseUrl  = "http://demo.egiz.gv.at/demoportal-pdf_as/services/";
			//String baseUrl  = "http://localhost:8080/pdf-as-web/services/";
			//URL endpoint = new URL(
			//		"http://192.168.56.10/pdf-as-web/wssign?wsdl");

			URL signEndpoint = new URL(baseUrl + "wssign?wsdl");
			URL verifyEndpoint = new URL(baseUrl + "wsverify?wsdl");
			
			RemotePDFSigner signer = new RemotePDFSigner(signEndpoint, true);
			RemotePDFVerifier verifier = new RemotePDFVerifier(verifyEndpoint, true);
			
			PDFASSignRequest signrequest = new PDFASSignRequest();
			signrequest.setInputData(inputData);
			signrequest.setParameters(signParameters);
			signParameters.setTransactionId("MYID ....");
			System.out.println("Simple Request:"); 
			PDFASSignResponse response = signer.signPDFDokument(signrequest);
			
			System.out.println("Sign Error: " + response.getError());
			
			PDFASVerifyRequest verifyRequest = new PDFASVerifyRequest();
			verifyRequest.setInputData(response.getSignedPDF());
			verifyRequest.setVerificationLevel(VerificationLevel.INTEGRITY_ONLY);
			
			PDFASVerifyResponse verifyResponse = verifier.verifyPDFDokument(verifyRequest);
			
			List<PDFASVerifyResult> results = verifyResponse.getVerifyResults();
			
			for(int i = 0; i < results.size(); i++) {
				PDFASVerifyResult result = results.get(i);
				printVerifyResult(result);
			}
			
			/*
			 * System.out.println("Simple Request:"); byte[] outputFile =
			 * signer.signPDFDokument(inputData, signParameters);
			 * 
			 * FileOutputStream fos = new FileOutputStream(
			 * "/home/afitzek/simple_request_signed.pdf");
			 * fos.write(outputFile); fos.close();
			 * 
			 * System.out.println("Simple Request Obj:"); PDFASSignResponse
			 * response = signer.signPDFDokument(request);
			 * 
			 * if (response.getSignedPDF() != null) { FileOutputStream fos2 =
			 * new FileOutputStream(
			 * "/home/afitzek/simple_request_obj_signed.pdf");
			 * fos2.write(response.getSignedPDF()); fos2.close(); }
			 * 
			 * if(response.getError() != null) { System.out.println("ERROR: " +
			 * response.getError()); }
			 */

			/*
			List<PDFASSignRequest> bulk = new ArrayList<PDFASSignRequest>();
			for (int i = 0; i < 10; i++) {
				bulk.add(request);
			}

			PDFASBulkSignRequest bulkRequest = new PDFASBulkSignRequest();
			bulkRequest.setSignRequests(bulk);

			for (int j = 0; j < 10; j++) {
				System.out.println("Bulk Request:");
				PDFASBulkSignResponse responses = signer
						.signPDFDokument(bulkRequest);

				for (int i = 0; i < responses.getSignResponses().size(); i++) {
					PDFASSignResponse bulkresponse = responses
							.getSignResponses().get(i);
					System.out.println("ID: " + bulkresponse.getRequestID());

					if (bulkresponse.getError() != null) {
						System.out.println("ERROR: " + bulkresponse.getError());
					} else {
						System.out.println("OK");
					}
				}
			}
			*/
			System.out.println("Done!");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private static void printVerifyResult(PDFASVerifyResult result) {
		System.out.println(result.getSignatureIndex());
		System.out.println("  Certificate: " + result.getCertificate());
		System.out.println("  Cert Messag: " + result.getCertificateMessage());
		System.out.println("  Cert Code  : " + result.getCertificateCode());
		System.out.println("  Value Code : " + result.getValueCode());
		System.out.println("  Value Messg: " + result.getValueMessage());
		System.out.println("  SignedBy   : " + result.getSignedBy());
		System.out.println("  Processed  : " + result.getProcessed());
		System.out.println("  Signed Data: " + result.getSignedData());
	}

}
