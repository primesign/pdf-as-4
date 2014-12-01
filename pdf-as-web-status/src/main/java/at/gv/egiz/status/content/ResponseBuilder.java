package at.gv.egiz.status.content;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.gv.egiz.status.TestResult;

public class ResponseBuilder {

	public enum ContentType {
		HTML("HTML"), JSON("JSON"), XML("XML");
		
		private final String name;       

	    private ContentType(String s) {
	        name = s;
	    }

	    public boolean equalsName(String otherName){
	        return (otherName == null)? false:name.equals(otherName);
	    }

	    public String toString(){
	       return name;
	    }
	}

	private ContentGenerator defaultGenerator;
	
	private Map<String, ContentGenerator> contentBuilder = new HashMap<String, ContentGenerator>();
	
	public ResponseBuilder() {
		defaultGenerator = new HtmlGenerator();
		contentBuilder.put(ContentType.HTML.toString(), defaultGenerator);
		contentBuilder.put(ContentType.JSON.toString(), new JsonGenerator());
		contentBuilder.put(ContentType.XML.toString(), new XMLGenerator());
	}
	
	public void generate(HttpServletRequest request,
			HttpServletResponse response, Map<String, TestResult> results,
			boolean details, String content) throws IOException {
		
		if(contentBuilder.containsKey(content)) {
			contentBuilder.get(content).generate(request, response, results, details);
		} else {
			defaultGenerator.generate(request, response, results, details);
		}
		
	}
}
