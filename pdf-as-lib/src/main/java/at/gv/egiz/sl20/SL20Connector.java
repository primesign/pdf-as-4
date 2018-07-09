package at.gv.egiz.sl20;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jose4j.base64url.Base64Url;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.sl.schema.CreateCMSSignatureResponseType;
import at.gv.egiz.sl.schema.InfoboxReadRequestType;
import at.gv.egiz.sl.schema.InfoboxReadResponseType;
import at.gv.egiz.sl.util.BaseSLConnector;
import at.gv.egiz.sl.util.RequestPackage;
import at.gv.egiz.sl20.exceptions.SLCommandoParserException;
import at.gv.egiz.sl20.utils.SL20Constants;
import at.gv.egiz.sl20.utils.SL20JSONExtractorUtils;

public class SL20Connector extends BaseSLConnector {
	private static final Logger log = LoggerFactory.getLogger(SL20Connector.class);
	
	private String bkuUrl;
	
	public SL20Connector(Configuration config) {
		this.bkuUrl = config.getValue(CONFIG_BKU_URL);
		
	}

	public JsonObject sendSL20Request(JsonObject sl20Req, SignParameter parameter, String vdaURL) {					
		try {
			log.trace("Request VDA via SL20 with: " + org.bouncycastle.util.encoders.Base64.toBase64String(sl20Req.toString().getBytes()));
			//build http client
			CloseableHttpClient httpClient = buildHttpClient();
			
			//build http POST request
			HttpPost httpReq = new HttpPost(new URIBuilder(vdaURL).build());
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();;
			parameters.add(new BasicNameValuePair(SL20Constants.PARAM_SL20_REQ_COMMAND_PARAM, Base64Url.encode(sl20Req.toString().getBytes())));
			httpReq.setEntity(new UrlEncodedFormEntity(parameters ));				
			
			//set native client header
			httpReq.addHeader(SL20Constants.HTTP_HEADER_SL20_CLIENT_TYPE, SL20Constants.HTTP_HEADER_VALUE_NATIVE);
			
			//request VDA
			log.trace("Requesting VDA ... ");
			HttpResponse httpResp = httpClient.execute(httpReq);			
			log.debug("Response from VDA received ");
			
			return SL20JSONExtractorUtils.getSL20ContainerFromResponse(httpResp);
						
		} catch (URISyntaxException | IOException e) {
			log.warn("Can NOT build SL20 http requst. Reason:" + e.getMessage(), e);
			return null;
			
		} catch (SLCommandoParserException e) {
			log.warn("Can NOT parse SL20 response from VDA: Reason: " + e.getMessage(), e);			
			return null;
			
		}								
		
	}
	
	@Override
	public InfoboxReadResponseType sendInfoboxReadRequest(InfoboxReadRequestType request, SignParameter parameter)
			throws PdfAsException {
		log.error("'sendInfoboxReadRequest' is NOT supported by " + SL20Connector.class.getName());
		return null;
	}

	@Override
	public CreateCMSSignatureResponseType sendCMSRequest(RequestPackage pack, SignParameter parameter)
			throws PdfAsException {
		log.error("'sendCMSReques' is NOT supported by " + SL20Connector.class.getName());
		return null;
	}
	
	private CloseableHttpClient buildHttpClient() {
		HttpClientBuilder builder = HttpClientBuilder.create();
		return builder.build();
	}

}
