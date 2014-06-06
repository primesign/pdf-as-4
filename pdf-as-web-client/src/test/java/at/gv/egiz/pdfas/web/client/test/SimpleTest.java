package at.gv.egiz.pdfas.web.client.test;

import java.io.FileInputStream;
import java.net.URL;

import sun.misc.IOUtils;
import at.gv.egiz.pdfas.api.ws.PDFASSignParameters;
import at.gv.egiz.pdfas.api.ws.PDFASSignParameters.Connector;
import at.gv.egiz.pdfas.api.ws.PDFASSignRequest;
import at.gv.egiz.pdfas.api.ws.PDFASSignResponse;
import at.gv.egiz.pdfas.web.client.RemotePDFSigner;

public class SimpleTest {

	public static void main(String[] args) {
		try {
			FileInputStream fis = new FileInputStream(
					"/home/afitzek/simple.pdf");
			byte[] inputData = IOUtils.readFully(fis, -1, true);

			PDFASSignParameters signParameters = new PDFASSignParameters();
			signParameters.setConnector(Connector.MOBILEBKU);
			signParameters.setPosition(null);
			signParameters.setProfile("SIGNATURBLOCK_SMALL_DE");

			PDFASSignRequest request = new PDFASSignRequest();
			request.setInputData(inputData);
			request.setParameters(signParameters);
			request.setRequestID("SOME TEST ID");

			//URL endpoint = new
			//URL("http://demo.egiz.gv.at/demoportal-pdf_as/wssign?wsdl");
			URL endpoint = new
					URL("http://www.buergerkarte.at/pdf-as-extern-4/wssign?wsdl");
			//URL endpoint = new URL(
			//		"http://localhost:8080/pdf-as-web/wssign?wsdl");
			//URL endpoint = new URL(
			//		"http://192.168.56.10/pdf-as-web/wssign?wsdl");

			RemotePDFSigner signer = new RemotePDFSigner(endpoint, false);

			PDFASSignRequest signrequest = new PDFASSignRequest();
			signrequest.setInputData(inputData);
			signrequest.setParameters(signParameters);
			signParameters.setTransactionId("MYID ....");
			signParameters.setPosition("f:80;w:230;p:2");
			System.out.println("Simple Request:"); 
			PDFASSignResponse response = signer.signPDFDokument(signrequest);
			
			System.out.println("User URL: " + response.getRedirectUrl());
			
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

}
