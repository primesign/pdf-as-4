package at.gv.egiz.pdfas.web.client.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import sun.misc.IOUtils;
import at.gv.egiz.pdfas.api.ws.PDFASBulkSignRequest;
import at.gv.egiz.pdfas.api.ws.PDFASBulkSignResponse;
import at.gv.egiz.pdfas.api.ws.PDFASSignParameters;
import at.gv.egiz.pdfas.api.ws.PDFASSignParameters.Connector;
import at.gv.egiz.pdfas.api.ws.PDFASSignRequest;
import at.gv.egiz.pdfas.api.ws.PDFASSignResponse;
import at.gv.egiz.pdfas.web.client.RemotePDFSigner;

public class SimpleTest {

	public static void main(String[] args) {
		try {
			FileInputStream fis = new FileInputStream(
					"/home/afitzek/Documents/arm_arm.pdf");
			byte[] inputData = IOUtils.readFully(fis, -1, true);

			PDFASSignParameters signParameters = new PDFASSignParameters();
			signParameters.setConnector(Connector.JKS);
			signParameters.setPosition(null);
			signParameters.setProfile("SIGNATURBLOCK_DE");

			PDFASSignRequest request = new PDFASSignRequest();
			request.setInputData(inputData);
			request.setParameters(signParameters);
			request.setRequestID("SOME TEST ID");

			// URL endpoint = new
			// URL("http://demo.egiz.gv.at/demoportal-pdf_as/wssign?wsdl");
			URL endpoint = new URL(
					"http://localhost:8080/pdf-as-web/wssign?wsdl");

			RemotePDFSigner signer = new RemotePDFSigner(endpoint, false);

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
					/*
					 * if (bulkresponse.getSignedPDF() != null) {
					 * FileOutputStream fos2 = new FileOutputStream(
					 * "/home/afitzek/simple_request_obj_signed_"+ i +".pdf");
					 * fos2.write(bulkresponse.getSignedPDF()); fos2.close(); }
					 */
					if (bulkresponse.getError() != null) {
						System.out.println("ERROR: " + bulkresponse.getError());
					} else {
						System.out.println("OK");
					}
				}
			}

			System.out.println("Done!");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
