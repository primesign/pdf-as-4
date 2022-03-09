package at.gv.egiz.sl20.utils;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import at.gv.egiz.sl20.exceptions.SLCommandoBuildException;

public class SL20JSONBuilderUtils {
	private static final Logger log = LoggerFactory.getLogger(SL20JSONBuilderUtils.class);
	
	/**
	 * Create command request
	 * @param name
	 * @param params
	 * @throws SLCommandoBuildException
	 * @return
	 */
	public static JsonObject createCommand(String name, JsonElement params) throws SLCommandoBuildException {
		JsonObject command = new JsonObject();		
		addSingleStringElement(command, SL20Constants.SL20_COMMAND_CONTAINER_NAME, name, true);
		addSingleJSONElement(command, SL20Constants.SL20_COMMAND_CONTAINER_PARAMS, params, true);				
		return command;
		
	}
	
	/**
	 * Create signed command request
	 * 
	 * @param name
	 * @param params
	 * @param signer
	 * @return
	 * @throws SLCommandoBuildException
	 */
	public static String createSignedCommand(String name, JsonElement params, IJOSETools signer) throws SLCommandoBuildException {
		JsonObject command = new JsonObject();		
		addSingleStringElement(command, SL20Constants.SL20_COMMAND_CONTAINER_NAME, name, true);
		addSingleJSONElement(command, SL20Constants.SL20_COMMAND_CONTAINER_PARAMS, params, true);		
		return signer.createSignature(command.toString());
				
	}
	
	/**
	 * Create command result
	 * 
	 * @param name
	 * @param result
	 * @param encryptedResult
	 * @throws SLCommandoBuildException
	 * @return
	 */
	public static JsonObject createCommandResponse(String name, JsonElement result, String encryptedResult) throws SLCommandoBuildException {
		JsonObject command = new JsonObject();
		addSingleStringElement(command, SL20Constants.SL20_COMMAND_CONTAINER_NAME, name, true);		
		addOnlyOnceOfTwo(command, 
				SL20Constants.SL20_COMMAND_CONTAINER_RESULT, SL20Constants.SL20_COMMAND_CONTAINER_ENCRYPTEDRESULT, 
				result, encryptedResult);			
		return command;
		
	}
	
	/**
	 * Create command result
	 * 
	 * @param name
	 * @param result
	 * @param encryptedResult
	 * @throws SLCommandoBuildException
	 * @return
	 */
	public static String createSignedCommandResponse(String name, JsonElement result, String encryptedResult, IJOSETools signer) throws SLCommandoBuildException {
		JsonObject command = new JsonObject();
		addSingleStringElement(command, SL20Constants.SL20_COMMAND_CONTAINER_NAME, name, true);		
		addOnlyOnceOfTwo(command, 
				SL20Constants.SL20_COMMAND_CONTAINER_RESULT, SL20Constants.SL20_COMMAND_CONTAINER_ENCRYPTEDRESULT, 
				result, encryptedResult);			
		return signer.createSignature(command.toString());
						
	}
	
	/**
	 * Create parameters for Redirect command
	 *  
	 * @param url
	 * @param command
	 * @param signedCommand
	 * @param ipcRedirect
	 * @return
	 * @throws SLCommandoBuildException
	 */
	public static JsonObject createRedirectCommandParameters(String url, JsonElement command, JsonElement signedCommand, Boolean ipcRedirect) throws SLCommandoBuildException{
		JsonObject redirectReqParams = new JsonObject();
		addOnlyOnceOfTwo(redirectReqParams, 
				SL20Constants.SL20_COMMAND_PARAM_GENERAL_REDIRECT_COMMAND, SL20Constants.SL20_COMMAND_PARAM_GENERAL_REDIRECT_SIGNEDCOMMAND, 
				command, signedCommand);		
		addSingleStringElement(redirectReqParams, SL20Constants.SL20_COMMAND_PARAM_GENERAL_REDIRECT_URL, url, false);
		addSingleBooleanElement(redirectReqParams, SL20Constants.SL20_COMMAND_PARAM_GENERAL_REDIRECT_IPCREDIRECT, ipcRedirect, false);
		return redirectReqParams;
		
	}
	
	/**
	 * Create parameters for Call command
	 * 
	 * @param url
	 * @param method
	 * @param includeTransactionId
	 * @param reqParameters
	 * @return
	 * @throws SLCommandoBuildException
	 */
	public static JsonObject createCallCommandParameters(String url, String method, Boolean includeTransactionId, Map<String, String> reqParameters) throws SLCommandoBuildException {
		JsonObject callReqParams = new JsonObject();
		addSingleStringElement(callReqParams, SL20Constants.SL20_COMMAND_PARAM_GENERAL_CALL_URL, url, true);
		addSingleStringElement(callReqParams, SL20Constants.SL20_COMMAND_PARAM_GENERAL_CALL_METHOD, method, true);
		addSingleBooleanElement(callReqParams, SL20Constants.SL20_COMMAND_PARAM_GENERAL_CALL_INCLUDETRANSACTIONID, includeTransactionId, false);
		addArrayOfStringElements(callReqParams, SL20Constants.SL20_COMMAND_PARAM_GENERAL_CALL_REQPARAMETER, reqParameters);		
		return callReqParams;
		
	}
	
	/**
	 * Create result for Error command
	 * 
	 * @param errorCode
	 * @param errorMsg
	 * @return
	 * @throws SLCommandoBuildException
	 */
	public static JsonObject createErrorCommandResult(String errorCode, String errorMsg) throws SLCommandoBuildException {
		JsonObject result = new JsonObject();
		addSingleStringElement(result, SL20Constants.SL20_COMMAND_PARAM_GENERAL_RESPONSE_ERRORCODE, errorCode, true);
		addSingleStringElement(result, SL20Constants.SL20_COMMAND_PARAM_GENERAL_RESPONSE_ERRORMESSAGE, errorMsg, true);
		return result;
		
	}
	
	
	/**
	 * Create parameters for qualifiedeID command
	 * 
	 * @param authBlockId
	 * @param dataUrl
	 * @param additionalReqParameters
	 * @param x5cEnc
	 * @return
	 * @throws CertificateEncodingException
	 * @throws SLCommandoBuildException
	 */
	public static JsonObject createQualifiedeIDCommandParameters(String authBlockId,  String dataUrl, 
			Map<String, String> additionalReqParameters, X509Certificate x5cEnc) throws CertificateEncodingException, SLCommandoBuildException {
		JsonObject params = new JsonObject();
		addSingleStringElement(params, SL20Constants.SL20_COMMAND_PARAM_EID_AUTHBLOCKID, authBlockId, true);
		addSingleStringElement(params, SL20Constants.SL20_COMMAND_PARAM_EID_DATAURL, dataUrl, true);
		addArrayOfStringElements(params, SL20Constants.SL20_COMMAND_PARAM_EID_ATTRIBUTES, additionalReqParameters);
		addSingleCertificateElement(params, SL20Constants.SL20_COMMAND_PARAM_EID_X5CENC, x5cEnc, false);		
		return params;

	}
	
	public static JsonObject createGetCertificateCommandParameters(String keyId,  String dataUrl, 
			X509Certificate x5cEnc) throws CertificateEncodingException, SLCommandoBuildException {		
		JsonObject params = new JsonObject();
		addSingleStringElement(params, SL20Constants.SL20_COMMAND_PARAM_GETCERTIFICATE_KEYID, keyId, true);
		addSingleStringElement(params, SL20Constants.SL20_COMMAND_PARAM_GETCERTIFICATE_DATAURL, dataUrl, true);
		addSingleCertificateElement(params, SL20Constants.SL20_COMMAND_PARAM_GETCERTIFICATE_X5CENC, x5cEnc, false);		
		return params;
		
	}
		
	public static JsonObject createCreateCAdESCommandParameters(String keyId,
			byte[] content, String contentUrl, String contentMode, String mimeType, boolean padesCompatiblem, List<JsonElement> byteRanges, String cadesLevel,			
			String dataUrl, X509Certificate x5cEnc) throws CertificateEncodingException, SLCommandoBuildException {		
		JsonObject params = new JsonObject();
		addSingleStringElement(params, SL20Constants.SL20_COMMAND_PARAM_CREATE_SIG_CADES_KEYID, keyId, true);		
		
		if (content != null && contentUrl != null) {
			log.warn(SL20Constants.SL20_COMMAND_PARAM_CREATE_SIG_CADES_CONTENT + " and " 
					+ SL20Constants.SL20_COMMAND_PARAM_CREATE_SIG_CADES_CONTENTURL + " can not SET TWICE");
			throw new SLCommandoBuildException();
			
		}
		
		if (content != null)
			addSingleByteElement(params, SL20Constants.SL20_COMMAND_PARAM_CREATE_SIG_CADES_CONTENT, content, true);
		
		else if (contentUrl != null )
			addSingleStringElement(params, SL20Constants.SL20_COMMAND_PARAM_CREATE_SIG_CADES_CONTENTURL, contentUrl, true);
		
		else {
			log.warn(SL20Constants.SL20_COMMAND_PARAM_CREATE_SIG_CADES_CONTENT + " and " 
					+ SL20Constants.SL20_COMMAND_PARAM_CREATE_SIG_CADES_CONTENTURL + " is NULL");
			throw new SLCommandoBuildException();
			
		}
			
		addSingleStringElement(params, SL20Constants.SL20_COMMAND_PARAM_CREATE_SIG_CADES_CONTENTMODE, contentMode, true);
		addSingleStringElement(params, SL20Constants.SL20_COMMAND_PARAM_CREATE_SIG_CADES_MIMETYPE, mimeType, true);		
		addSingleBooleanElement(params, SL20Constants.SL20_COMMAND_PARAM_CREATE_SIG_CADES_PADES_COMBATIBILTY, padesCompatiblem, false);		
		
		//addArrayOfStrings(params, SL20Constants.SL20_COMMAND_PARAM_CREATE_SIG_CADES_EXCLUDEBYTERANGE, byteRanges);		
		addArrayOfElements(params, SL20Constants.SL20_COMMAND_PARAM_CREATE_SIG_CADES_EXCLUDEBYTERANGE, byteRanges);
		addSingleStringElement(params, SL20Constants.SL20_COMMAND_PARAM_CREATE_SIG_CADES_CADESLEVEL, cadesLevel, false);		
		addSingleStringElement(params, SL20Constants.SL20_COMMAND_PARAM_GETCERTIFICATE_DATAURL, dataUrl, true);
		addSingleCertificateElement(params, SL20Constants.SL20_COMMAND_PARAM_GETCERTIFICATE_X5CENC, x5cEnc, false);		
		return params;
		
	}
	
	/**
	 * Create result for qualifiedeID command
	 * 
	 * @param idl
	 * @param authBlock
	 * @param ccsURL
	 * @param LoA
	 * @return
	 * @throws SLCommandoBuildException
	 */
	public static JsonObject createQualifiedeIDCommandResult(byte[] idl, byte[] authBlock, String ccsURL, String LoA) throws SLCommandoBuildException {
		JsonObject result = new JsonObject();
		addSingleByteElement(result, SL20Constants.SL20_COMMAND_PARAM_EID_RESULT_IDL, idl, true);
		addSingleByteElement(result, SL20Constants.SL20_COMMAND_PARAM_EID_RESULT_AUTHBLOCK, authBlock, true);
		addSingleStringElement(result, SL20Constants.SL20_COMMAND_PARAM_EID_RESULT_CCSURL, ccsURL, true);
		addSingleStringElement(result, SL20Constants.SL20_COMMAND_PARAM_EID_RESULT_LOA, LoA, true);
		return result;
		
	}
	
	
	/**
	 * Create Binding-Key command parameters
	 * 
	 * @param kontoId
	 * @param subjectName
	 * @param keySize
	 * @param keyAlg
	 * @param policies
	 * @param dataUrl
	 * @param x5cVdaTrust
	 * @param reqUserPassword
	 * @param x5cEnc
	 * @return
	 * @throws SLCommandoBuildException
	 * @throws CertificateEncodingException
	 */
	public static JsonObject createBindingKeyCommandParams(String kontoId, String subjectName, int keySize, String keyAlg, 
			Map<String, String> policies, String dataUrl, X509Certificate x5cVdaTrust, Boolean reqUserPassword, X509Certificate x5cEnc) throws SLCommandoBuildException, CertificateEncodingException {
		JsonObject params = new JsonObject();
		addSingleStringElement(params, SL20Constants.SL20_COMMAND_PARAM_BINDING_CREATE_KONTOID, kontoId, true);
		addSingleStringElement(params, SL20Constants.SL20_COMMAND_PARAM_BINDING_CREATE_SN, subjectName, true);
		addSingleNumberElement(params, SL20Constants.SL20_COMMAND_PARAM_BINDING_CREATE_KEYLENGTH, keySize, true);
		addSingleStringElement(params, SL20Constants.SL20_COMMAND_PARAM_BINDING_CREATE_KEYALG, keyAlg, true);		
		addArrayOfStringElements(params, SL20Constants.SL20_COMMAND_PARAM_BINDING_CREATE_POLICIES, policies);		
		addSingleStringElement(params, SL20Constants.SL20_COMMAND_PARAM_BINDING_CREATE_DATAURL, dataUrl, true);
		addSingleCertificateElement(params, SL20Constants.SL20_COMMAND_PARAM_BINDING_CREATE_X5CVDATRUST, x5cVdaTrust, false);
		addSingleBooleanElement(params, SL20Constants.SL20_COMMAND_PARAM_BINDING_CREATE_REQUESTUSERPASSWORD, reqUserPassword, false);
		addSingleCertificateElement(params, SL20Constants.SL20_COMMAND_PARAM_BINDING_CREATE_X5CENC, x5cEnc, false);
		return params;
		
	}
	
	/**
	 * Create Binding-Key command result
	 * 
	 * @param appId
	 * @param csr
	 * @param attCert
	 * @param password
	 * @return
	 * @throws SLCommandoBuildException
	 * @throws CertificateEncodingException
	 */
	public static JsonObject createBindingKeyCommandResult(String appId, byte[] csr, X509Certificate attCert, byte[] password) throws SLCommandoBuildException, CertificateEncodingException {
		JsonObject result = new JsonObject();
		addSingleStringElement(result, SL20Constants.SL20_COMMAND_PARAM_BINDING_CREATE_RESULT_APPID, appId, true);
		addSingleByteElement(result, SL20Constants.SL20_COMMAND_PARAM_BINDING_CREATE_RESULT_CSR, csr, true);
		addSingleCertificateElement(result, SL20Constants.SL20_COMMAND_PARAM_BINDING_CREATE_RESULT_KEYATTESTATIONZERTIFICATE, attCert, false);
		addSingleByteElement(result, SL20Constants.SL20_COMMAND_PARAM_BINDING_CREATE_RESULT_USERPASSWORD, password, false);		
		return result;
		
	}
	
	/**
	 * Create Store Binding-Certificate command parameters
	 * 
	 * @param cert
	 * @param dataUrl
	 * @return
	 * @throws CertificateEncodingException
	 * @throws SLCommandoBuildException
	 */
	public static JsonObject createStoreBindingCertCommandParams(X509Certificate cert, String dataUrl) throws CertificateEncodingException, SLCommandoBuildException {
		JsonObject params = new JsonObject();
		addSingleCertificateElement(params, SL20Constants.SL20_COMMAND_PARAM_BINDING_STORE_CERTIFICATE, cert, true);
		addSingleStringElement(params, SL20Constants.SL20_COMMAND_PARAM_BINDING_STORE_DATAURL, dataUrl, true);		
		return params;
		
	}
	
	/**
	 * Create Store Binding-Certificate command result
	 * 
	 * @return
	 * @throws SLCommandoBuildException
	 */
	public static JsonObject createStoreBindingCertCommandSuccessResult() throws SLCommandoBuildException {
		JsonObject result = new JsonObject();
		addSingleStringElement(result, SL20Constants.SL20_COMMAND_PARAM_BINDING_STORE_RESULT_SUCESS, 
				SL20Constants.SL20_COMMAND_PARAM_BINDING_STORE_RESULT_SUCESS_VALUE, true);
		return result;
		
	}
	
	
	/**
	 * Create idAndPassword command parameters
	 * 
	 * @param keyAlg
	 * @param dataUrl
	 * @param x5cEnc
	 * @return
	 * @throws SLCommandoBuildException
	 * @throws CertificateEncodingException
	 */
	public static JsonObject createIdAndPasswordCommandParameters(String keyAlg, String dataUrl, X509Certificate x5cEnc) throws SLCommandoBuildException, CertificateEncodingException {
		JsonObject params = new JsonObject();		
		addSingleStringElement(params, SL20Constants.SL20_COMMAND_PARAM_AUTH_IDANDPASSWORD_KEYALG, keyAlg, true);
		addSingleStringElement(params, SL20Constants.SL20_COMMAND_PARAM_AUTH_IDANDPASSWORD_DATAURL, dataUrl, true);
		addSingleCertificateElement(params, SL20Constants.SL20_COMMAND_PARAM_AUTH_IDANDPASSWORD_X5CENC, x5cEnc, false);
		return params;
		
	}
	
	/**
	 * Create idAndPassword command result
	 * 
	 * @param kontoId
	 * @param password
	 * @return
	 * @throws SLCommandoBuildException
	 */
	public static JsonObject createIdAndPasswordCommandResult(String kontoId, byte[] password) throws SLCommandoBuildException {
		JsonObject result = new JsonObject();
		addSingleStringElement(result, SL20Constants.SL20_COMMAND_PARAM_AUTH_IDANDPASSWORD_RESULT_KONTOID, kontoId, true);
		addSingleByteElement(result, SL20Constants.SL20_COMMAND_PARAM_AUTH_IDANDPASSWORD_RESULT_USERPASSWORD, password, true);		
		return result;
		
	}
	
	/**
	 * Create JWS Token Authentication command
	 * 
	 * @param nonce
	 * @param dataUrl
	 * @param displayData
	 * @param displayUrl
	 * @return
	 * @throws SLCommandoBuildException
	 */
	public static JsonObject createJwsTokenAuthCommandParams(String nonce, String dataUrl, List<String> displayData, List<String> displayUrl) throws SLCommandoBuildException {
		JsonObject params = new JsonObject();
		addSingleStringElement(params, SL20Constants.SL20_COMMAND_PARAM_AUTH_JWSTOKEN_NONCE, nonce, true);
		addSingleStringElement(params, SL20Constants.SL20_COMMAND_PARAM_AUTH_JWSTOKEN_DATAURL, dataUrl, true);
		addArrayOfStrings(params, SL20Constants.SL20_COMMAND_PARAM_AUTH_JWSTOKEN_DISPLAYDATA, displayData);
		addArrayOfStrings(params, SL20Constants.SL20_COMMAND_PARAM_AUTH_JWSTOKEN_DISPLAYURL, displayUrl);		
		return params;
		
	}
	
	/**
	 * Create JWS Token Authentication command result
	 * 
	 * @param nonce
	 * @return
	 * @throws SLCommandoBuildException
	 */
	public static JsonObject createJwsTokenAuthCommandResult(String nonce) throws SLCommandoBuildException {
		JsonObject result = new JsonObject();
		addSingleStringElement(result, SL20Constants.SL20_COMMAND_PARAM_AUTH_JWSTOKEN_RESULT_NONCE, nonce, true);		
		return result;
		
	}
	
	
	/**
	 * Create Generic Request Container
	 * 
	 * @param reqId
	 * @param transactionId
	 * @param payLoad
	 * @param signedPayload
	 * @return
	 * @throws SLCommandoBuildException
	 */
	public static JsonObject createGenericRequest(String reqId, String transactionId, JsonElement payLoad, String signedPayload) throws SLCommandoBuildException {
		JsonObject req = new JsonObject();
		addSingleIntegerElement(req, SL20Constants.SL20_VERSION, SL20Constants.CURRENT_SL20_VERSION, true);
		addSingleStringElement(req, SL20Constants.SL20_REQID, reqId, true);
		addSingleStringElement(req, SL20Constants.SL20_TRANSACTIONID, transactionId, false);		
		addOnlyOnceOfTwo(req, SL20Constants.SL20_PAYLOAD, SL20Constants.SL20_SIGNEDPAYLOAD, 
				payLoad, signedPayload);		
		return req;
		
	}
	
	/**
	 * Create Generic Response Container
	 * 
	 * @param respId
	 * @param inResponseTo
	 * @param transactionId
	 * @param payLoad
	 * @param signedPayload
	 * @return
	 * @throws SLCommandoBuildException
	 */
	public static final JsonObject createGenericResponse(String respId, String inResponseTo, String transactionId, 
			JsonElement payLoad, String signedPayload) throws SLCommandoBuildException {
		
		JsonObject req = new JsonObject();
		addSingleIntegerElement(req, SL20Constants.SL20_VERSION, SL20Constants.CURRENT_SL20_VERSION, true);
		addSingleStringElement(req, SL20Constants.SL20_RESPID, respId, true);
		addSingleStringElement(req, SL20Constants.SL20_INRESPTO, inResponseTo, true);
		addSingleStringElement(req, SL20Constants.SL20_TRANSACTIONID, transactionId, false);		
		addOnlyOnceOfTwo(req, SL20Constants.SL20_PAYLOAD, SL20Constants.SL20_SIGNEDPAYLOAD, 
				payLoad, signedPayload);		
		return req;
		
	}
	
	/**
	 * Add one element of two possible elements <br>
	 * This method adds either the first element or the second element to parent JSON, but never both.  
	 * 
	 * @param parent Parent JSON element
	 * @param firstKeyId first element Id
	 * @param secondKeyId second element Id
	 * @param first first element
	 * @param second second element
	 * @throws SLCommandoBuildException
	 */
	public static void addOnlyOnceOfTwo(JsonObject parent, String firstKeyId, String secondKeyId, JsonElement first, String second) throws SLCommandoBuildException {
		if (first == null && (second == null  || second.isEmpty())) {
			log.warn(firstKeyId + " and " + secondKeyId + " is NULL");
			throw new SLCommandoBuildException();
		
		} else if (first != null && second != null) {
			log.warn(firstKeyId + " and " + secondKeyId + " can not SET TWICE");
			throw new SLCommandoBuildException();
		
		} else if (first != null)
			parent.add(firstKeyId, first);
		
		else if (second != null && !second.isEmpty())
			parent.addProperty(secondKeyId, second);
		
		else {
			log.warn("Internal build error");
			throw new SLCommandoBuildException();
			
		}
	}
	
	private static void addArrayOfElements(JsonObject parent, String keyId, List<JsonElement> values) throws SLCommandoBuildException {		
		validateParentAndKey(parent, keyId);
		if (values != null) {
			JsonArray callReqParamsArray = new JsonArray();
			parent.add(keyId, callReqParamsArray  );
			for(JsonElement el : values)
				callReqParamsArray.add(el);
			
		}
		
	}
	
	private static void addArrayOfStrings(JsonObject parent, String keyId, List<String> values) throws SLCommandoBuildException {
		validateParentAndKey(parent, keyId);		
		if (values != null) {
			JsonArray callReqParamsArray = new JsonArray();
			parent.add(keyId, callReqParamsArray  );
			for(String el : values)
				callReqParamsArray.add(el);
			
		}
	}
	
	
	private static void addArrayOfStringElements(JsonObject parent, String keyId, Map<String, String> keyValuePairs) throws SLCommandoBuildException {
		validateParentAndKey(parent, keyId);		
		if (keyValuePairs != null) {			
			JsonArray callReqParamsArray = new JsonArray();
			parent.add(keyId, callReqParamsArray  );
			
			for(Entry<String, String> el : keyValuePairs.entrySet()) {
				JsonObject callReqParams = new JsonObject();
				//callReqParams.addProperty(SL20Constants.SL20_COMMAND_PARAM_GENERAL_REQPARAMETER_KEY, el.getKey());
				//callReqParams.addProperty(SL20Constants.SL20_COMMAND_PARAM_GENERAL_REQPARAMETER_VALUE, el.getValue());
				callReqParams.addProperty(el.getKey(), el.getValue());
				callReqParamsArray.add(callReqParams);
				
			}
		}
	}
	
	private static void addSingleCertificateElement(JsonObject parent, String keyId, X509Certificate cert, boolean isRequired) throws CertificateEncodingException, SLCommandoBuildException {
		if (cert != null)
			addSingleByteElement(parent, keyId, cert.getEncoded(), isRequired);
		
		else if (isRequired) {
			log.warn(keyId + " is marked as REQUIRED");
			throw new SLCommandoBuildException();
			
		}
		
	}
	
	
	
	private static void addSingleByteElement(JsonObject parent, String keyId, byte[] value, boolean isRequired) throws SLCommandoBuildException {
		validateParentAndKey(parent, keyId);
		
		if (isRequired && value == null) {
			log.warn(keyId + " has NULL value");
			throw new SLCommandoBuildException();
		
		} else if (value != null)
			parent.addProperty(keyId, org.bouncycastle.util.encoders.Base64.toBase64String(value));
		
	}
	
	private static void addSingleBooleanElement(JsonObject parent, String keyId, Boolean value, boolean isRequired) throws SLCommandoBuildException {
		validateParentAndKey(parent, keyId);
		
		if (isRequired && value == null) {
			log.warn(keyId + " has a NULL value");
			throw new SLCommandoBuildException();
			
		} else if (value != null)
			parent.addProperty(keyId, value);
		
	}
	
	private static void addSingleNumberElement(JsonObject parent, String keyId, Integer value, boolean isRequired) throws SLCommandoBuildException {
		validateParentAndKey(parent, keyId);
		
		if (isRequired && value == null) {
			log.warn(keyId + " has a NULL value");
			throw new SLCommandoBuildException();
		
		} else if (value != null)
			parent.addProperty(keyId, value);;
		
	}
	
	private static void addSingleStringElement(JsonObject parent, String keyId, String value, boolean isRequired) throws SLCommandoBuildException {
		validateParentAndKey(parent, keyId);
		
		if (isRequired && (value == null || value.isEmpty())) {
			log.warn(keyId + " has an empty value");
			throw new SLCommandoBuildException();
		
		} else if (value != null && !value.isEmpty())
			parent.addProperty(keyId, value);
		
	}
	
	private static void addSingleIntegerElement(JsonObject parent, String keyId, Integer value, boolean isRequired) throws SLCommandoBuildException {
		validateParentAndKey(parent, keyId);
		
		if (isRequired && value == null) {
			log.warn(keyId + " has an empty value");
			throw new SLCommandoBuildException();
		
		} else if (value != null)
			parent.addProperty(keyId, value);
		
	}
	
	private static void addSingleJSONElement(JsonObject parent, String keyId, JsonElement element, boolean isRequired) throws SLCommandoBuildException {
		validateParentAndKey(parent, keyId);
		
		if (isRequired && element == null) {
			log.warn("No commando name included");
			throw new SLCommandoBuildException();
		
		} else if (element != null)
			parent.add(keyId, element);
		
	}
		
	private static void addOnlyOnceOfTwo(JsonObject parent, String firstKeyId, String secondKeyId, JsonElement first, JsonElement second) throws SLCommandoBuildException {
		if (first == null && second == null) {
			log.warn(firstKeyId + " and " + secondKeyId + " is NULL");
			throw new SLCommandoBuildException();
		
		} else if (first != null && second != null) {
			log.warn(firstKeyId + " and " + secondKeyId + " can not SET TWICE");
			throw new SLCommandoBuildException();
		
		} else if (first != null)
			parent.add(firstKeyId, first);
		
		else if (second != null)
			parent.add(secondKeyId, second);
		
		else {
			log.warn("Internal build error");
			throw new SLCommandoBuildException();
			
		}
	}
	
	private static void validateParentAndKey(JsonObject parent, String keyId) throws SLCommandoBuildException {
		if (parent == null) {
			log.warn("NO parent JSON element");
			throw new SLCommandoBuildException();
			
		}
		if (keyId == null || keyId.isEmpty()) {
			log.warn("NO JSON element identifier");
			throw new SLCommandoBuildException();
			
		}
	}
}
