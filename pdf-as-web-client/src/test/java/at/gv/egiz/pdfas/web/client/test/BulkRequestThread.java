package at.gv.egiz.pdfas.web.client.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import sun.misc.IOUtils;
import at.gv.egiz.pdfas.api.ws.PDFASBulkSignRequest;
import at.gv.egiz.pdfas.api.ws.PDFASBulkSignResponse;
import at.gv.egiz.pdfas.api.ws.PDFASSignParameters;
import at.gv.egiz.pdfas.api.ws.PDFASSignRequest;
import at.gv.egiz.pdfas.api.ws.PDFASSignResponse;
import at.gv.egiz.pdfas.api.ws.PDFASSignParameters.Connector;
import at.gv.egiz.pdfas.web.client.RemotePDFSigner;

public class BulkRequestThread implements Runnable {
	private Thread t;
	private String threadName;
	private RemotePDFSigner signer;
	PDFASSignParameters signParameters;
	private byte[] inputData;
	int bulkSize;
	int queries;

	public BulkRequestThread(String name, URL endpoint, int queries, int bulkSize)
			throws IOException {
		threadName = name;
		this.queries = queries;
		this.bulkSize = bulkSize;
		System.out.println("Creating " + threadName);

		signer = new RemotePDFSigner(endpoint, false);

		FileInputStream fis = new FileInputStream(
				"/home/afitzek/Documents/arm_arm.pdf");
		inputData = IOUtils.readFully(fis, -1, true);

		signParameters = new PDFASSignParameters();
		signParameters.setConnector(Connector.JKS);
		signParameters.setPosition(null);
		signParameters.setProfile("SIGNATURBLOCK_DE");

	}

	private PDFASSignRequest getNewRequest() {
		PDFASSignRequest request = new PDFASSignRequest();
		request.setInputData(inputData);
		request.setParameters(signParameters);
		request.setRequestID(UUID.randomUUID().toString());
		return request;
	}

	private PDFASBulkSignRequest getBlukRequest(int count) {

		List<PDFASSignRequest> bulk = new ArrayList<PDFASSignRequest>();
		for (int i = 0; i < count; i++) {
			bulk.add(getNewRequest());
		}

		PDFASBulkSignRequest bulkRequest = new PDFASBulkSignRequest();
		bulkRequest.setSignRequests(bulk);

		return bulkRequest;
	}

	public void run() {
		System.out.println("Running " + threadName);
		try {
			for (int i = 0; i < queries; i++) {
				System.out.println("Thread: " + threadName + ", " + i);
				PDFASBulkSignResponse responses = signer
						.signPDFDokument(getBlukRequest(bulkSize));

				for (int j = 0; j < responses.getSignResponses().size(); j++) {
					PDFASSignResponse bulkresponse = responses
							.getSignResponses().get(j);
					System.out.println("Thread: " + threadName + ", " +"ID: " + bulkresponse.getRequestID());
					if (bulkresponse.getError() != null) {
						System.out.println("Thread: " + threadName + ", " + "ERROR: " + bulkresponse.getError());
					} else {
						System.out.println("Thread: " + threadName + ", " + "OK");
					}
				}
				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
			System.out.println("Thread " + threadName + " interrupted.");
		}
		System.out.println("Thread " + threadName + " exiting.");
	}

	public void start() {
		System.out.println("Starting " + threadName);
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}
	
	public void join() throws InterruptedException {
		if(t != null) {
			t.join();
		}
	}
}
