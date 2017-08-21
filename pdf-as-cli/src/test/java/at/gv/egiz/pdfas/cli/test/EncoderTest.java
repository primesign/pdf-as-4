package at.gv.egiz.pdfas.cli.test;

import java.net.URL;
import java.net.URLEncoder;

public class EncoderTest {

	public static void main(String[] args) throws Exception {
		
		System.out.println("URLEncoder");
		System.out.println(URLEncoder.encode("http://teasasd.host.asd/asdasd/asda/asd?asdqwqe=asdwqe812331&adijoij=123123", "UTF-8"));
		System.out.println(URLEncoder.encode("_topXXXX\"><script>alert()</script>", "UTF-8"));
		
		
		URL url = new URL("_topXXXX\"><script>alert()</script>");
		
	}
}
