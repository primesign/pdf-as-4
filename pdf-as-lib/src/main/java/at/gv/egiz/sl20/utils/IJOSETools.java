package at.gv.egiz.sl20.utils;

import java.security.cert.X509Certificate;

import com.google.gson.JsonElement;

import at.gv.egiz.sl20.data.VerificationResult;
import at.gv.egiz.sl20.exceptions.SL20Exception;
import at.gv.egiz.sl20.exceptions.SLCommandoBuildException;

public interface IJOSETools {

	/**
	 * Check if the JOSE tools are initialized
	 * 
	 * @return
	 */
	public boolean isInitialized();
	
	/**
	 * Create a JWS signature
	 * 
	 * @param payLoad Payload to sign
	 * @throws SLCommandoBuildException 
	 */
	public String createSignature(String payLoad) throws SLCommandoBuildException;

	/**
	 * Validates a JWS signature
	 * 
	 * @param serializedContent
	 * @return
	 * @throws SLCommandoParserException
	 * @throws SL20Exception 
	 */
	public VerificationResult validateSignature(String serializedContent) throws SL20Exception;
	
	/**
	 * Get the encryption certificate for SL2.0 End-to-End encryption
	 * 
	 * @return
	 */
	public X509Certificate getEncryptionCertificate();

	/**
	 * Decrypt a serialized JWE token
	 * 
	 * @param compactSerialization Serialized JWE token
	 * @return decrypted payload
	 * @throws SL20Exception 
	 */
	public JsonElement decryptPayload(String compactSerialization) throws SL20Exception;
	 
}
