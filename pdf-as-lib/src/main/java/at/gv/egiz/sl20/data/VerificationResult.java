package at.gv.egiz.sl20.data;

import java.security.cert.X509Certificate;
import java.util.List;

import com.google.gson.JsonObject;

public class VerificationResult {

	private Boolean validSigned = null;
	private List<X509Certificate> certs = null;
	private JsonObject payload = null;
	
	public VerificationResult(JsonObject payload) {
		this.payload = payload;
		
	}
	
	public VerificationResult(JsonObject string, List<X509Certificate> certs, boolean wasValidSigned) {
		this.payload = string;
		this.certs = certs;
		this.validSigned = wasValidSigned;
		
	}
	
	public Boolean isValidSigned() {
		return validSigned;
	}
	public List<X509Certificate> getCertChain() {
		return certs;
	}
	public JsonObject getPayload() {
		return payload;
	}
	
	
	
	
}
