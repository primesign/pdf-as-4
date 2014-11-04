package at.gv.egiz.pdfas.lib.test.mains;

import static org.junit.Assert.*;

import org.junit.Test;

import at.gv.egiz.pdfas.common.exceptions.SLPdfAsException;
import at.gv.egiz.sl.util.BKUSLConnector;

public class LegacySLExtractor {

	public static final String TEST_LEGACY_RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<sl10:ErrorResponse xmlns:sl10=\"http://www.buergerkarte.at/namespaces/securitylayer/20020225#\">" + 
			"<sl10:ErrorCode>1501</sl10:ErrorCode>" +
			"<sl10:Info>Fehler in XML-Struktur der Anfrage. (Element content is invalid according to the DTD/Schema.)</sl10:Info>" +
			"</sl10:ErrorResponse>";
	
	public static final String TEST_LEGACY_RESPONSE_NOINFO = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<sl10:ErrorResponse xmlns:sl10=\"http://www.buergerkarte.at/namespaces/securitylayer/20020225#\">" + 
			"<sl10:ErrorCode>1501</sl10:ErrorCode>" +
			"</sl10:ErrorResponse>";
	
	public static final String TEST_LEGACY_RESPONSE_PLAIN = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<sl10:ErrorResponse xmlns:sl10=\"http://www.buergerkarte.at/namespaces/securitylayer/20020225#\">" + 
			"</sl10:ErrorResponse>";
	
	public static final String TEST_LEGACY_SOMETHING_ELSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<sl10:Errorresponse xmlns:sl10=\"http://www.buergerkarte.at/namespaces/securitylayer/20020225#\">" + 
			"<sl10:ErrorCode>1501</sl10:ErrorCode>" +
			"<sl10:Info>Fehler in XML-Struktur der Anfrage. (Element content is invalid according to the DTD/Schema.)</sl10:Info>" +
			"</sl10:Errorresponse>";
	
	@Test
	public void test() {
		SLPdfAsException e = BKUSLConnector.generateLegacySLException(TEST_LEGACY_RESPONSE);
		
		if(e == null) {
			fail("Failed to extract SL Error");
		}
		
		if(e.getCode() != 1501 || !e.getInfo().equals("Fehler in XML-Struktur der Anfrage. (Element content is invalid according to the DTD/Schema.)")) {
			fail("Failed to extract SL Error");
		}
		
		SLPdfAsException e1 = BKUSLConnector.generateLegacySLException(TEST_LEGACY_RESPONSE_NOINFO);
		
		if(e1 == null) {
			fail("Failed to extract SL Error");
		}
		
		if(e1.getCode() != 1501 || e1.getInfo() != null) {
			fail("Failed to extract SL Error");
		}
		
		SLPdfAsException e2 = BKUSLConnector.generateLegacySLException(TEST_LEGACY_RESPONSE_PLAIN);
		
		if(e2 != null) {
			fail("Extracted invalid error info");
		}
		
		SLPdfAsException e3 = BKUSLConnector.generateLegacySLException(TEST_LEGACY_SOMETHING_ELSE);
		
		if(e3 != null) {
			fail("Extracted invalid error info");
		}
		
		SLPdfAsException e4 = BKUSLConnector.generateLegacySLException(null);
		
		if(e4 != null) {
			fail("Extracted invalid error info");
		}
	}

}
