package at.gv.egiz.pdfas.web.sl20;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.jose4j.base64url.Base64Url;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import at.gv.egiz.sl20.utils.SL20Constants;

public class SL20HttpBindingUtils {
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(SL20HttpBindingUtils.class);
	
	public static void writeIntoResponse(HttpServletRequest request, HttpServletResponse response, JsonObject sl20Forward, String redirectURL) throws IOException, URISyntaxException {
		//forward SL2.0 command
		log.trace("SL20 command: " + sl20Forward.toString());
		if (request.getHeader(SL20Constants.HTTP_HEADER_SL20_CLIENT_TYPE) != null && 
				request.getHeader(SL20Constants.HTTP_HEADER_SL20_CLIENT_TYPE).equals(SL20Constants.HTTP_HEADER_VALUE_NATIVE)) {
			log.debug("Client request containts 'native client' header ... ");												
			StringWriter writer = new StringWriter();
			writer.write(sl20Forward.toString());						
			final byte[] content = writer.toString().getBytes("UTF-8");
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentLength(content.length);
			response.setContentType(ContentType.APPLICATION_JSON.toString());						
			response.getOutputStream().write(content);
											
		} else {
			log.debug("Client request containts is no native client ... ");
			URIBuilder clientRedirectURI = new URIBuilder(redirectURL);
			clientRedirectURI.addParameter(
					SL20Constants.PARAM_SL20_REQ_COMMAND_PARAM, 
					Base64Url.encode(sl20Forward.toString().getBytes()));
			response.setStatus(307);
			response.setHeader("Location", clientRedirectURI.build().toString());
			
		}
		
	}
}
