package at.gv.egiz.pdfas.web.client.test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PerformanceTest {
	public static void main(String[] args) {
		try {
			List<BulkRequestThread> threads = new ArrayList<BulkRequestThread>();

			URL endpoint = new URL(
					"http://localhost:8080/pdf-as-web/wssign?wsdl");

			for (int i = 0; i < 10; i++) {
				threads.add(new BulkRequestThread("T" + i, endpoint, 10, 10));
			}
			
			
			for (int i = 0; i < threads.size(); i++) {
				threads.get(i).start();
			}
			
			for (int i = 0; i < threads.size(); i++) {
				threads.get(i).join();
			}
			
			System.out.println("DONE");
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
