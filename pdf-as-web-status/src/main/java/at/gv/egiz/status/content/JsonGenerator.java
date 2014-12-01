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

public class JsonGenerator implements ContentGenerator {

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
		
		response.setContentType("application/json");
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");

		Iterator<Entry<String,TestResult>> testResultIterator = results.entrySet().iterator();
		while(testResultIterator.hasNext()) {
			Entry<String,TestResult> entry = testResultIterator.next();
			TestResult result = entry.getValue();
			String testName = entry.getKey();
			
			sb.append("\"");
			sb.append(StringEscapeUtils.escapeJson(testName));
			sb.append("\": {\"Status\": \"");
			sb.append(StringEscapeUtils.escapeJson(TestStatusString.getString(result.getStatus())));
			sb.append("\"");

			if(details) {
				sb.append(", \"Detail\": \"");
				
				StringBuilder detail = new StringBuilder();
				
				Iterator<String> detailStringIt = result.getDetails().iterator();
				
				while(detailStringIt.hasNext()) {
					String detailString = detailStringIt.next();
					detail.append(StringEscapeUtils.escapeJson(detailString));
					detail.append(" ");
				}
				
				sb.append(detail.toString());
				sb.append("\"");
			} 
			
			sb.append("}");
			if(testResultIterator.hasNext()) {
				sb.append(", ");
			}
		}
		
		sb.append("}");
		
		response.getOutputStream().write(sb.toString().getBytes("UTF-8"));
		response.getOutputStream().close();
	}

}
