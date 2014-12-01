package at.gv.egiz.status.content;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import at.gv.egiz.status.TestResult;
import at.gv.egiz.status.TestStatus;
import at.gv.egiz.status.impl.TestStatusString;

public class XMLGenerator implements ContentGenerator {

	@Override
	public void generate(HttpServletRequest request,
			HttpServletResponse response, Map<String, TestResult> results,
			boolean details) throws IOException {
		boolean allOk = true;
		
		Iterator<TestResult> testIterator = results.values().iterator();
		while(testIterator.hasNext()) {
			TestResult result = testIterator.next();
			if(!result.getStatus().equals(TestStatus.OK)){
				allOk = false;
				break;
			}
		}
		
		if(!allOk) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
		}
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/xml");
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");

		sb.append("<tests>");
		
		Iterator<Entry<String,TestResult>> testResultIterator = results.entrySet().iterator();
		while(testResultIterator.hasNext()) {
			Entry<String,TestResult> entry = testResultIterator.next();
			TestResult result = entry.getValue();
			String testName = entry.getKey();
			
			sb.append("<test><name>");
			sb.append(StringEscapeUtils.escapeXml10(testName));
			sb.append("</name><status>");
			sb.append(StringEscapeUtils.escapeXml10(TestStatusString.getString(result.getStatus())));
			sb.append("</status>");

			if(details) {
				sb.append("<detail>");
				
				StringBuilder detail = new StringBuilder();
				
				Iterator<String> detailStringIt = result.getDetails().iterator();
				
				while(detailStringIt.hasNext()) {
					String detailString = detailStringIt.next();
					detail.append(StringEscapeUtils.escapeXml10(detailString));
					detail.append(" ");
				}
				
				sb.append(detail.toString());
				sb.append("</detail>");
			} 
			
			sb.append("</test>");
		}
		
		sb.append("</tests>");
		
		response.getOutputStream().write(sb.toString().getBytes("UTF-8"));
		response.getOutputStream().close();
	}

}
