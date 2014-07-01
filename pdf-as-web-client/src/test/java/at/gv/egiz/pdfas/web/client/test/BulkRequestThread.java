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
