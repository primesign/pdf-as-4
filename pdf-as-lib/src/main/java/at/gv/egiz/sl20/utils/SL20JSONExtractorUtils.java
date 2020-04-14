package at.gv.egiz.sl20.utils;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.jose4j.base64url.Base64Url;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import at.gv.egiz.sl20.data.VerificationResult;
import at.gv.egiz.sl20.exceptions.SL20Exception;
import at.gv.egiz.sl20.exceptions.SLCommandoParserException;

public class SL20JSONExtractorUtils {
	private static final Logger log = LoggerFactory.getLogger(SL20JSONExtractorUtils.class);
	
	/**
	 * Extract String value from JSON 
	 * 
	 * @param input
	 * @param keyID
	 * @param isRequired
	 * @return
	 * @throws SLCommandoParserException
	 */
	public static String getStringValue(JsonObject input, String keyID, boolean isRequired) throws SLCommandoParserException {
		try {
			JsonElement internal = getAndCheck(input, keyID, isRequired);
		
			if (internal != null)
				return internal.getAsString();
			else
				return null;
			
		} catch (SLCommandoParserException e) {
			throw e;
			
		} catch (Exception e) {
			log.warn("Can not extract String value with keyId: " + keyID);
			throw new SLCommandoParserException(e);
			
		}		
	}
	
	/**
	 * Extract Boolean value from JSON 
	 * 
	 * @param input
	 * @param keyID
	 * @param isRequired
	 * @return
	 * @throws SLCommandoParserException
	 */
	public static boolean getBooleanValue(JsonObject input, String keyID, boolean isRequired, boolean defaultValue) throws SLCommandoParserException {
		try {
			JsonElement internal = getAndCheck(input, keyID, isRequired);
				
			if (internal != null)
				return internal.getAsBoolean();
			else
				return defaultValue;
			
		} catch (SLCommandoParserException e) {
			throw e;
			
		} catch (Exception e) {
			log.warn("Can not extract Boolean value with keyId: " + keyID);
			throw new SLCommandoParserException(e);
			
		}		
	}
	
	/**
	 * Extract JSONObject value from JSON 
	 * 
	 * @param input
	 * @param keyID
	 * @param isRequired
	 * @return
	 * @throws SLCommandoParserException
	 */
	public static JsonObject getJSONObjectValue(JsonObject input, String keyID, boolean isRequired) throws SLCommandoParserException {
		try {
			JsonElement internal = getAndCheck(input, keyID, isRequired);
				
			if (internal != null)
				return internal.getAsJsonObject();
			else
				return null;
			
		} catch (SLCommandoParserException e) {
			throw e;
			
		} catch (Exception e) {
			log.warn("Can not extract Boolean value with keyId: \" + keyID");
			throw new SLCommandoParserException(e);
			
		}		
	}
	
	/**
	 * Extract Map of Key/Value pairs from a JSON Element
	 * 
	 * @param input parent JSON object
	 * @param keyID KeyId of the child that should be parsed
	 * @param isRequired
	 * @return
	 * @throws SLCommandoParserException
	 */
	public static Map<String, String> getMapOfStringElements(JsonObject input, String keyID, boolean isRequired) throws SLCommandoParserException {
		JsonElement internal = getAndCheck(input, keyID, isRequired);
		return getMapOfStringElements(internal);
		
	}
	
	/**
	 * Extract a List of String elements from a JSON element
	 * 
	 * @param input
	 * @param isRequired 
	 * @param keyID 
	 * @return
	 * @throws SLCommandoParserException
	 */
	public static List<String> getListOfStringElements(JsonObject input, String keyID, boolean isRequired) throws SLCommandoParserException {
		JsonElement internal = getAndCheck(input, keyID, isRequired);

		List<String> result = new ArrayList<String>();
		if (internal != null) {
			if (internal.isJsonArray()) {			
				Iterator<JsonElement> arrayIterator = internal.getAsJsonArray().iterator();
				while(arrayIterator.hasNext()) {
					JsonElement next = arrayIterator.next();
					if (next.isJsonPrimitive())
						result.add(next.getAsString());											
				}
				
			} else if (internal.isJsonPrimitive()) {
				result.add(internal.getAsString());
				
			} else {
				log.warn("JSON Element IS NOT a JSON array or a JSON Primitive");
				throw new SLCommandoParserException();
			}
		}
		
		return result;
	}
	
	
	/**
	 * Extract Map of Key/Value pairs from a JSON Element 
	 * 
	 * @param input
	 * @return
	 * @throws SLCommandoParserException
	 */
	public static Map<String, String> getMapOfStringElements(JsonElement input) throws SLCommandoParserException {		
		Map<String, String> result = new HashMap<String, String>();
				
		if (input != null) {
			if (input.isJsonArray()) {			
				Iterator<JsonElement> arrayIterator = input.getAsJsonArray().iterator();
				while(arrayIterator.hasNext()) {
					JsonElement next = arrayIterator.next();				
					Iterator<Entry<String, JsonElement>> entry = next.getAsJsonObject().entrySet().iterator();
					entitySetToMap(result, entry);
					
				}
				
			} else if (input.isJsonObject()) {
				Iterator<Entry<String, JsonElement>> objectKeys = input.getAsJsonObject().entrySet().iterator();
				entitySetToMap(result, objectKeys);
				
			} else {
				log.warn("JSON Element IS NOT a JSON array or a JSON object");
				throw new SLCommandoParserException();
			}
			
		}
		
		return result;
	}
	
	private static void entitySetToMap(Map<String, String> result, Iterator<Entry<String, JsonElement>> entry) {
		while (entry.hasNext()) {
			Entry<String, JsonElement> el = entry.next();
			if (result.containsKey(el.getKey()))
				log.info("Attr. Map already contains Element with Key: " + el.getKey() + ". Overwrite element ... ");
			
			result.put(el.getKey(), el.getValue().getAsString());
		
		}
		
	}

	public static JsonElement extractSL20Result(JsonObject command, IJOSETools decrypter, boolean mustBeEncrypted) throws SL20Exception {
		JsonElement result = command.get(SL20Constants.SL20_COMMAND_CONTAINER_RESULT);
		JsonElement encryptedResult = command.get(SL20Constants.SL20_COMMAND_CONTAINER_ENCRYPTEDRESULT);
		
		if (result == null && encryptedResult == null) {
			log.warn("NO result OR encryptedResult FOUND.");
			throw new SLCommandoParserException();		
				
		} else if (encryptedResult == null && mustBeEncrypted) {
			log.warn("result MUST be signed.");
			throw new SLCommandoParserException();
				
		} else if (encryptedResult != null && encryptedResult.isJsonPrimitive()) {
			try {
				return decrypter.decryptPayload(encryptedResult.getAsString());
				
			} catch (Exception e) {
				log.info("Can NOT decrypt SL20 result. Reason:" + e.getMessage());
				if (!mustBeEncrypted) {
					log.warn("Decrypted results are disabled by configuration. Parse result in plain if it is possible");

					//dummy code
					try {
						String[] signedPayload = encryptedResult.toString().split("\\.");
						JsonElement payLoad = new JsonParser().parse(new String(Base64Url.decodeToUtf8String(signedPayload[1])));
						return payLoad;
						
					} catch (Exception e1) {
						log.debug("DummyCode FAILED, Reason: " + e1.getMessage() + " Ignore it ...");
						throw new SL20Exception(e.getMessage(), e);
						
					}
					
				} else
					throw e;
			}
		} else if (result != null) {
				return result;

		} else {
			log.error("Internal build error");
			throw new SLCommandoParserException();
		}
	}
		
	/**
	 * Extract payLoad from generic transport container
	 * 
	 * @param container
	 * @param joseTools
	 * @return
	 * @throws SLCommandoParserException
	 */
	public static VerificationResult extractSL20PayLoad(JsonObject container, IJOSETools joseTools, boolean mustBeSigned) throws SL20Exception {
		
		JsonElement sl20Payload = container.get(SL20Constants.SL20_PAYLOAD);
		JsonElement sl20SignedPayload = container.get(SL20Constants.SL20_SIGNEDPAYLOAD);
		
		if (mustBeSigned && joseTools == null) {
			log.warn("'joseTools' MUST be set if 'mustBeSigned' is 'true'");
			throw new SLCommandoParserException();
			
		}	
		
		if (sl20Payload == null && sl20SignedPayload == null) {
			log.warn("NO payLoad OR signedPayload FOUND.");
			throw new SLCommandoParserException();		
		
		} else if (sl20SignedPayload == null && mustBeSigned) {
			log.warn("payLoad MUST be signed.");
			throw new SLCommandoParserException();

		} else if (joseTools != null && sl20SignedPayload != null && sl20SignedPayload.isJsonPrimitive()) {	
			try {
				return joseTools.validateSignature(sl20SignedPayload.getAsString());
				
			} catch (SL20Exception e) {
				if (!mustBeSigned && sl20Payload == null) {
					log.debug("Signature verification FAILED with reason: " + e.getMessage() 
						+ " but response MUST NOT be signed by configuration"
						+ " Starting backup process ... ");
					String[] split = sl20SignedPayload.getAsString().split("\\.");
					if (split.length == 3) {
						JsonElement payLoad = new JsonParser().parse(new String(Base64Url.decodeToUtf8String(split[1])));
						log.info("Signature verification FAILED with reason: " + e.getMessage() + " Use plain result as it is");
						return new VerificationResult(payLoad.getAsJsonObject());
						
					}										
				}
			
				throw e;
				
			}
		
		} else if (sl20Payload != null)
			return new VerificationResult(sl20Payload.getAsJsonObject());
		
		else if (joseTools == null && !mustBeSigned && sl20SignedPayload != null && sl20SignedPayload.isJsonPrimitive())  {
			log.info("Received signed SL20 response, but verification IS NOT required and NOT CONFIGURATED. Skip signature verification ... ");
			String[] split = sl20SignedPayload.getAsString().split("\\.");
			if (split.length == 3) {
				JsonElement payLoad = new JsonParser().parse(new String(Base64Url.decodeToUtf8String(split[1])));
				return new VerificationResult(payLoad.getAsJsonObject());
				
			} else {
				log.warn("Can NOT skip signature verification, because signed result has an unsupported format!");
				throw new SLCommandoParserException();
				
			}
			
		} else {
			log.warn("Internal build error");
			throw new SLCommandoParserException();
			
		 }
			
		
	}
	
	
	/**
	 * Extract generic transport container from httpResponse
	 * 
	 * @param httpResp
	 * @return
	 * @throws SLCommandoParserException
	 */
	public static JsonObject getSL20ContainerFromResponse(HttpResponse httpResp) throws SLCommandoParserException {
		try {
			JsonObject sl20Resp = null;
			if (httpResp.getStatusLine().getStatusCode() == 307) {
				Header[] locationHeader = httpResp.getHeaders("Location");
				if (locationHeader == null) {
					log.warn("Find Redirect statuscode but not Location header");
					throw new SLCommandoParserException();
			
				}
				String sl20RespString = new URIBuilder(locationHeader[0].getValue()).getQueryParams().get(0).getValue();
				sl20Resp = new JsonParser().parse(Base64Url.encode((sl20RespString.getBytes()))).getAsJsonObject();
			
			} else if (httpResp.getStatusLine().getStatusCode() == 200) {
				if (!httpResp.getEntity().getContentType().getValue().startsWith("application/json")) {
					log.warn("SL20 response with a wrong ContentType: " + httpResp.getEntity().getContentType().getValue());
					throw new SLCommandoParserException();
					
				}				
				sl20Resp = parseSL20ResultFromResponse(httpResp.getEntity());
		
			} else if ( (httpResp.getStatusLine().getStatusCode() == 500) || 
					(httpResp.getStatusLine().getStatusCode() == 401) || 
					(httpResp.getStatusLine().getStatusCode() == 400) ) {
				log.info("SL20 response with http-code: " + httpResp.getStatusLine().getStatusCode() 
						+ ". Search for error message");				
				sl20Resp = parseSL20ResultFromResponse(httpResp.getEntity());
				
				
			} else {
				log.warn("SL20 response with http-code: " + httpResp.getStatusLine().getStatusCode());
				throw new SLCommandoParserException();
				
			}

			log.info("Find JSON object in http response");
			return sl20Resp;
			
		} catch (Exception e) {
			log.warn("SL20 response parsing FAILED! Reason: " + e.getMessage(), e);
			throw new SLCommandoParserException(e);
			
		}		
	}
	
	private static JsonObject parseSL20ResultFromResponse(HttpEntity resp) throws Exception {
		if (resp != null && resp.getContent() != null) {
			String htmlRespBody = EntityUtils.toString(resp);
			try {
				JsonElement sl20Resp = new JsonParser().parse(htmlRespBody);
				if (sl20Resp != null && sl20Resp.isJsonObject()) {
					return sl20Resp.getAsJsonObject();
				
				} else {
					log.warn("SL2.0 can NOT parse to a JSON object");
					throw new SLCommandoParserException();
				
				}
				
			} catch (Exception e) {
				log.info("Can NOT parse SL2.0 respone from VDA. Raw SL2.0 response: {}",
						htmlRespBody);
				throw new SLCommandoParserException(e);
				
			}
						
		} else {
			log.warn("Can NOT find content in http response");
			throw new SLCommandoParserException();
			
		}
 					
	}
	
	
	private static JsonElement getAndCheck(JsonObject input, String keyID, boolean isRequired) throws SLCommandoParserException {
		JsonElement internal = input.get(keyID);
		
		if (internal == null && isRequired) {
			log.warn("REQUIRED Element with keyId: " + keyID + " does not exist");
			throw new SLCommandoParserException();
			
		}
		
		return internal;
		
	}
}
