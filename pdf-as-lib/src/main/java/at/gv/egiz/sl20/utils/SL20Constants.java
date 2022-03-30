package at.gv.egiz.sl20.utils;

import java.util.Arrays;
import java.util.List;

import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jws.AlgorithmIdentifiers;

public class SL20Constants {
	public static final int CURRENT_SL20_VERSION = 10;
	
	//http binding parameters
	public static final String PARAM_SL20_REQ_COMMAND_PARAM = "slcommand";
	public static final String PARAM_SL20_REQ_COMMAND_PARAM_OLD = "sl2command";
	
	public static final String PARAM_SL20_REQ_ICP_RETURN_URL_PARAM = "slIPCReturnUrl";
	public static final String PARAM_SL20_REQ_TRANSACTIONID = "slTransactionID";
	
	public static final String HTTP_HEADER_SL20_CLIENT_TYPE = "SL2ClientType";
	public static final String HTTP_HEADER_SL20_VDA_TYPE = "X-MOA-VDA";
	public static final String HTTP_HEADER_VALUE_NATIVE = "nativeApp";
	
	
	//*******************************************************************************************
	//JSON signing and encryption headers
	public static final String JSON_ALGORITHM = "alg";
	public static final String JSON_CONTENTTYPE = "cty";
	public static final String JSON_X509_CERTIFICATE = "x5c";
	public static final String JSON_X509_FINGERPRINT = "x5t#S256";
	public static final String JSON_ENCRYPTION_PAYLOAD = "enc";
	
	public static final String JSON_ALGORITHM_SIGNING_RS256 = AlgorithmIdentifiers.RSA_USING_SHA256;
	public static final String JSON_ALGORITHM_SIGNING_RS512 = AlgorithmIdentifiers.RSA_USING_SHA512;
	public static final String JSON_ALGORITHM_SIGNING_ES256 = AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256;
	public static final String JSON_ALGORITHM_SIGNING_ES512 = AlgorithmIdentifiers.ECDSA_USING_P521_CURVE_AND_SHA512;
	public static final String JSON_ALGORITHM_SIGNING_PS256 = AlgorithmIdentifiers.RSA_PSS_USING_SHA256;
	public static final String JSON_ALGORITHM_SIGNING_PS512 = AlgorithmIdentifiers.RSA_PSS_USING_SHA512;

	public static final List<String> SL20_ALGORITHM_WHITELIST_SIGNING = Arrays.asList(
			JSON_ALGORITHM_SIGNING_RS256,
			JSON_ALGORITHM_SIGNING_RS512,
			JSON_ALGORITHM_SIGNING_ES256,
			JSON_ALGORITHM_SIGNING_ES512,
			JSON_ALGORITHM_SIGNING_PS256,
			JSON_ALGORITHM_SIGNING_PS512
			);
	
	public static final String JSON_ALGORITHM_ENC_KEY_RSAOAEP = KeyManagementAlgorithmIdentifiers.RSA_OAEP;
	public static final String JSON_ALGORITHM_ENC_KEY_RSAOAEP256 = KeyManagementAlgorithmIdentifiers.RSA_OAEP_256;
	
	public static final List<String> SL20_ALGORITHM_WHITELIST_KEYENCRYPTION = Arrays.asList(
			JSON_ALGORITHM_ENC_KEY_RSAOAEP,
			JSON_ALGORITHM_ENC_KEY_RSAOAEP256
			);
	
	public static final String JSON_ALGORITHM_ENC_PAYLOAD_A128CBCHS256 = ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256;
	public static final String JSON_ALGORITHM_ENC_PAYLOAD_A256CBCHS512 = ContentEncryptionAlgorithmIdentifiers.AES_256_CBC_HMAC_SHA_512;
	public static final String JSON_ALGORITHM_ENC_PAYLOAD_A128GCM = ContentEncryptionAlgorithmIdentifiers.AES_128_GCM;
	public static final String JSON_ALGORITHM_ENC_PAYLOAD_A256GCM = ContentEncryptionAlgorithmIdentifiers.AES_256_GCM;
	
	public static final List<String> SL20_ALGORITHM_WHITELIST_ENCRYPTION = Arrays.asList(
			JSON_ALGORITHM_ENC_PAYLOAD_A128CBCHS256,
			JSON_ALGORITHM_ENC_PAYLOAD_A256CBCHS512,
			JSON_ALGORITHM_ENC_PAYLOAD_A128GCM,
			JSON_ALGORITHM_ENC_PAYLOAD_A256GCM
		);
	
	
	//*********************************************************************************************
	//Object identifier for generic transport container
	public static final String SL20_CONTENTTYPE_SIGNED_COMMAND ="application/sl2.0;command";
	public static final String SL20_CONTENTTYPE_ENCRYPTED_RESULT ="application/sl2.0;result";
	
	public static final String SL20_VERSION = "v";
	public static final String SL20_REQID = "reqID";
	public static final String SL20_RESPID = "respID";
	public static final String SL20_INRESPTO = "inResponseTo";
	public static final String SL20_TRANSACTIONID = "transactionID";
	public static final String SL20_PAYLOAD = "payload";
	public static final String SL20_SIGNEDPAYLOAD = "signedPayload";
	
	//Generic Object identifier for commands
	public static final String SL20_COMMAND_CONTAINER_NAME = "name";
	public static final String SL20_COMMAND_CONTAINER_PARAMS = "params";
	public static final String SL20_COMMAND_CONTAINER_RESULT = "result";
	public static final String SL20_COMMAND_CONTAINER_ENCRYPTEDRESULT = "encryptedResult";
		
	//COMMAND Object identifier
	public static final String SL20_COMMAND_IDENTIFIER_REDIRECT = "redirect";
	public static final String SL20_COMMAND_IDENTIFIER_CALL = "call";
	public static final String SL20_COMMAND_IDENTIFIER_ERROR = "error";
	public static final String SL20_COMMAND_IDENTIFIER_QUALIFIEDEID = "qualifiedeID";
	//public static final String SL20_COMMAND_IDENTIFIER_QUALIFIEDSIG = "qualifiedSig";
	
	public static final String SL20_COMMAND_IDENTIFIER_GETCERTIFICATE = "getCertificate";
	public static final String SL20_COMMAND_IDENTIFIER_CREATE_SIG_CADES = "createCAdES";
	
	
	public static final String SL20_COMMAND_IDENTIFIER_BINDING_CREATE_KEY = "createBindingKey";
	public static final String SL20_COMMAND_IDENTIFIER_BINDING_STORE_CERT = "storeBindingCert";
	
	public static final String SL20_COMMAND_IDENTIFIER_AUTH_IDANDPASSWORD = "idAndPassword";
	public static final String SL20_COMMAND_IDENTIFIER_AUTH_JWSTOKENFACTOR = "jwsTokenAuth";
	public static final String SL20_COMMAND_IDENTIFIER_AUTH_QRCODEFACTOR = "qrCodeFactor";
	
	//*****COMMAND parameter identifier******
	//general Identifier
	public static final String SL20_COMMAND_PARAM_GENERAL_REQPARAMETER_VALUE = "value";
	public static final String SL20_COMMAND_PARAM_GENERAL_REQPARAMETER_KEY = "key";	
	public static final String SL20_COMMAND_PARAM_GENERAL_DATAURL = "dataUrl";
	public static final String SL20_COMMAND_PARAM_GENERAL_RESPONSEENCRYPTIONCERTIFICATE = "x5cEnc";
	public static final String SL20_COMMAND_PARAM_GENERAL_RESPONSEENCRYPTIONJWK = "jwkEnc";
	
	//Redirect command
	public static final String SL20_COMMAND_PARAM_GENERAL_REDIRECT_URL = "url";
	public static final String SL20_COMMAND_PARAM_GENERAL_REDIRECT_COMMAND = "command";
	public static final String SL20_COMMAND_PARAM_GENERAL_REDIRECT_SIGNEDCOMMAND = "signedCommand";
	public static final String SL20_COMMAND_PARAM_GENERAL_REDIRECT_IPCREDIRECT = "IPCRedirect";		
	
	//Call command
	public static final String SL20_COMMAND_PARAM_GENERAL_CALL_URL = SL20_COMMAND_PARAM_GENERAL_REDIRECT_URL;
	public static final String SL20_COMMAND_PARAM_GENERAL_CALL_METHOD = "method";
	public static final String SL20_COMMAND_PARAM_GENERAL_CALL_METHOD_GET = "get";
	public static final String SL20_COMMAND_PARAM_GENERAL_CALL_METHOD_POST = "post";
	public static final String SL20_COMMAND_PARAM_GENERAL_CALL_INCLUDETRANSACTIONID = "includeTransactionID";	
	public static final String SL20_COMMAND_PARAM_GENERAL_CALL_REQPARAMETER = "reqParams";

	//error command
	public static final String SL20_COMMAND_PARAM_GENERAL_RESPONSE_ERRORCODE = "errorCode";
	public static final String SL20_COMMAND_PARAM_GENERAL_RESPONSE_ERRORMESSAGE = "errorMessage";
	
	//qualified eID command
	public static final String SL20_COMMAND_PARAM_EID_AUTHBLOCKID = "authBlockTemplateID";
	public static final String SL20_COMMAND_PARAM_EID_DATAURL = SL20_COMMAND_PARAM_GENERAL_DATAURL; 
	public static final String SL20_COMMAND_PARAM_EID_ATTRIBUTES = "attributes";
	public static final String SL20_COMMAND_PARAM_EID_ATTRIBUTES_MANDATEREFVALUE = "MANDATE-REFERENCE-VALUE";
	public static final String SL20_COMMAND_PARAM_EID_ATTRIBUTES_SPUNIQUEID = "SP-UNIQUEID";
	public static final String SL20_COMMAND_PARAM_EID_ATTRIBUTES_SPFRIENDLYNAME = "SP-FRIENDLYNAME";
	public static final String SL20_COMMAND_PARAM_EID_ATTRIBUTES_SPCOUNTRYCODE = "SP-COUNTRYCODE";
	public static final String SL20_COMMAND_PARAM_EID_X5CENC = SL20_COMMAND_PARAM_GENERAL_RESPONSEENCRYPTIONCERTIFICATE;
	public static final String SL20_COMMAND_PARAM_EID_JWKCENC = SL20_COMMAND_PARAM_GENERAL_RESPONSEENCRYPTIONJWK;
	public static final String SL20_COMMAND_PARAM_EID_RESULT_IDL = "EID-IDENTITY-LINK";
	public static final String SL20_COMMAND_PARAM_EID_RESULT_AUTHBLOCK = "EID-AUTH-BLOCK";
	public static final String SL20_COMMAND_PARAM_EID_RESULT_CCSURL = "EID-CCS-URL";
	public static final String SL20_COMMAND_PARAM_EID_RESULT_LOA = "EID-CITIZEN-QAA-LEVEL";
	
	//qualified Signature comamnd
//	public static final String SL20_COMMAND_PARAM_QUALSIG_DATAURL = SL20_COMMAND_PARAM_GENERAL_DATAURL;
//	public static final String SL20_COMMAND_PARAM_QUALSIG_X5CENC = SL20_COMMAND_PARAM_GENERAL_RESPONSEENCRYPTIONCERTIFICATE;
	
	
	//getCertificate
	public static final String SL20_COMMAND_PARAM_GETCERTIFICATE_KEYID = "keyId";
	public static final String SL20_COMMAND_PARAM_GETCERTIFICATE_DATAURL = SL20_COMMAND_PARAM_GENERAL_DATAURL;
	public static final String SL20_COMMAND_PARAM_GETCERTIFICATE_X5CENC = SL20_COMMAND_PARAM_GENERAL_RESPONSEENCRYPTIONCERTIFICATE;
	public static final String SL20_COMMAND_PARAM_GETCERTIFICATE_JWKCENC = SL20_COMMAND_PARAM_GENERAL_RESPONSEENCRYPTIONJWK;
	public static final String SL20_COMMAND_PARAM_GETCERTIFICATE_RESULT_CERTIFICATE = "x5c";
	
	//createCAdES Signture
	public static final String SL20_COMMAND_PARAM_CREATE_SIG_CADES_KEYID = "keyId";	
	public static final String SL20_COMMAND_PARAM_CREATE_SIG_CADES_CONTENT = "content";
	public static final String SL20_COMMAND_PARAM_CREATE_SIG_CADES_CONTENTURL = "contentUrl";
	public static final String SL20_COMMAND_PARAM_CREATE_SIG_CADES_CONTENTMODE = "contentMode";	
	public static final String SL20_COMMAND_PARAM_CREATE_SIG_CADES_MIMETYPE = "mimeType";
	public static final String SL20_COMMAND_PARAM_CREATE_SIG_CADES_PADES_COMBATIBILTY = "padesComatibility";
	public static final String SL20_COMMAND_PARAM_CREATE_SIG_CADES_EXCLUDEBYTERANGE = "excludedByteRange";
	public static final String SL20_COMMAND_PARAM_CREATE_SIG_CADES_CADESLEVEL = "cadesLevel";	
	public static final String SL20_COMMAND_PARAM_CREATE_SIG_CADES_DATAURL = SL20_COMMAND_PARAM_GENERAL_DATAURL;
	public static final String SL20_COMMAND_PARAM_CREATE_SIG_CADES_X5CENC = SL20_COMMAND_PARAM_GENERAL_RESPONSEENCRYPTIONCERTIFICATE;
	public static final String SL20_COMMAND_PARAM_CREATE_SIG_CADES_JWKCENC = SL20_COMMAND_PARAM_GENERAL_RESPONSEENCRYPTIONJWK;
	public static final String SL20_COMMAND_PARAM_CREATE_SIG_CADES_RESULT_SIGNATURE = "signature";
	
	public static final String SL20_COMMAND_PARAM_CREATE_SIG_CADES_CADESLEVEL_BASIC = "cAdES";
	public static final String SL20_COMMAND_PARAM_CREATE_SIG_CADES_CADESLEVEL_T = "cAdES-T";
	public static final String SL20_COMMAND_PARAM_CREATE_SIG_CADES_CADESLEVEL_C = "cAdES-C";
	public static final String SL20_COMMAND_PARAM_CREATE_SIG_CADES_CADESLEVEL_X = "cAdES-X";
	public static final String SL20_COMMAND_PARAM_CREATE_SIG_CADES_CADESLEVEL_XL = "cAdES-X-L";
	public static final String SL20_COMMAND_PARAM_CREATE_SIG_CADES_CADESLEVEL_A = "cAdES-A";
	
	public static final String SL20_COMMAND_PARAM_CREATE_SIG_CADES_CONTENTMODE_DETACHED = "detached";
	public static final String SL20_COMMAND_PARAM_CREATE_SIG_CADES_CONTENTMODE_ENVELOPING = "enveloping";
	
	//create binding key command
	public static final String SL20_COMMAND_PARAM_BINDING_CREATE_KONTOID = "kontoID";
	public static final String SL20_COMMAND_PARAM_BINDING_CREATE_SN = "SN";
	public static final String SL20_COMMAND_PARAM_BINDING_CREATE_KEYLENGTH = "keyLength";
	public static final String SL20_COMMAND_PARAM_BINDING_CREATE_KEYALG = "keyAlg";
	public static final String SL20_COMMAND_PARAM_BINDING_CREATE_POLICIES = "policies";
	public static final String SL20_COMMAND_PARAM_BINDING_CREATE_DATAURL = SL20_COMMAND_PARAM_GENERAL_DATAURL;
	public static final String SL20_COMMAND_PARAM_BINDING_CREATE_X5CVDATRUST = "x5cVdaTrust";
	public static final String SL20_COMMAND_PARAM_BINDING_CREATE_REQUESTUSERPASSWORD = "reqUserPassword";	
	public static final String SL20_COMMAND_PARAM_BINDING_CREATE_X5CENC = SL20_COMMAND_PARAM_GENERAL_RESPONSEENCRYPTIONCERTIFICATE;
	
	public static final String SL20_COMMAND_PARAM_BINDING_CREATE_KEYALG_RSA = "RSA";
	public static final String SL20_COMMAND_PARAM_BINDING_CREATE_KEYALG_SECPR256R1 = "secp256r1";
	
	public static final String SL20_COMMAND_PARAM_BINDING_CREATE_POLICIES_LIFETIME = "lifeTime";
	public static final String SL20_COMMAND_PARAM_BINDING_CREATE_POLICIES_USESECUREELEMENT = "useSecureElement";
	public static final String SL20_COMMAND_PARAM_BINDING_CREATE_POLICIES_KEYTIMEOUT = "keyTimeout";
	public static final String SL20_COMMAND_PARAM_BINDING_CREATE_POLICIES_NEEDUSERAUTH = "needUserAuth";
	
	public static final String SL20_COMMAND_PARAM_BINDING_CREATE_RESULT_APPID = "appID";
	public static final String SL20_COMMAND_PARAM_BINDING_CREATE_RESULT_CSR = "csr";
	public static final String SL20_COMMAND_PARAM_BINDING_CREATE_RESULT_KEYATTESTATIONZERTIFICATE = "attCert";
	public static final String SL20_COMMAND_PARAM_BINDING_CREATE_RESULT_USERPASSWORD = "encodedPass";
		
	
	//store binding certificate command
	public static final String SL20_COMMAND_PARAM_BINDING_STORE_CERTIFICATE = "x5c";
	public static final String SL20_COMMAND_PARAM_BINDING_STORE_DATAURL = SL20_COMMAND_PARAM_GENERAL_DATAURL;
	public static final String SL20_COMMAND_PARAM_BINDING_STORE_RESULT_SUCESS = "success";
	public static final String SL20_COMMAND_PARAM_BINDING_STORE_RESULT_SUCESS_VALUE = "OK";
	
	// Username and password authentication
	public static final String SL20_COMMAND_PARAM_AUTH_IDANDPASSWORD_KEYALG = "keyAlg";
	public static final String SL20_COMMAND_PARAM_AUTH_IDANDPASSWORD_KEYALG_VALUE_PLAIN = "plain";
	public static final String SL20_COMMAND_PARAM_AUTH_IDANDPASSWORD_KEYALG_VALUE_PBKDF2 = "PBKDF2";
	public static final String SL20_COMMAND_PARAM_AUTH_IDANDPASSWORD_DATAURL = SL20_COMMAND_PARAM_GENERAL_DATAURL;
	public static final String SL20_COMMAND_PARAM_AUTH_IDANDPASSWORD_X5CENC = SL20_COMMAND_PARAM_GENERAL_RESPONSEENCRYPTIONCERTIFICATE;	
	public static final String SL20_COMMAND_PARAM_AUTH_IDANDPASSWORD_RESULT_KONTOID = SL20_COMMAND_PARAM_BINDING_CREATE_KONTOID;
	public static final String SL20_COMMAND_PARAM_AUTH_IDANDPASSWORD_RESULT_USERPASSWORD = SL20_COMMAND_PARAM_BINDING_CREATE_RESULT_USERPASSWORD;
	
	//JWS Token authentication
	public static final String SL20_COMMAND_PARAM_AUTH_JWSTOKEN_NONCE = "nonce";
	public static final String SL20_COMMAND_PARAM_AUTH_JWSTOKEN_DISPLAYDATA = "displayData";
	public static final String SL20_COMMAND_PARAM_AUTH_JWSTOKEN_DISPLAYURL = "displayUrl";
	public static final String SL20_COMMAND_PARAM_AUTH_JWSTOKEN_DATAURL = SL20_COMMAND_PARAM_GENERAL_DATAURL;	
	public static final String SL20_COMMAND_PARAM_AUTH_JWSTOKEN_RESULT_NONCE = SL20_COMMAND_PARAM_AUTH_JWSTOKEN_NONCE;
	
	//QR-Code authentication
	public static final String SL20_COMMAND_PARAM_AUTH_QRCODE_QRCODE = "qrCode";
	public static final String SL20_COMMAND_PARAM_AUTH_QRCODE_DATAURL = SL20_COMMAND_PARAM_GENERAL_DATAURL;
	
}
