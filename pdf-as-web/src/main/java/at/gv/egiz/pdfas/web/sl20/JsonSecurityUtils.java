package at.gv.egiz.pdfas.web.sl20;

import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwx.JsonWebStructure;
import org.jose4j.keys.X509Util;
import org.jose4j.keys.resolvers.X509VerificationKeyResolver;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.sl20.data.VerificationResult;
import at.gv.egiz.sl20.exceptions.SL20Exception;
import at.gv.egiz.sl20.exceptions.SL20SecurityException;
import at.gv.egiz.sl20.exceptions.SLCommandoBuildException;
import at.gv.egiz.sl20.exceptions.SLCommandoParserException;
import at.gv.egiz.sl20.utils.IJOSETools;
import at.gv.egiz.sl20.utils.SL20Constants;

public class JsonSecurityUtils implements IJOSETools{

	private static final Logger logger = LoggerFactory.getLogger(JsonSecurityUtils.class);
	
	private Key signPrivKey = null;
	private X509Certificate[] signCertChain = null;	
	private Key encPrivKey = null;
	private X509Certificate[] encCertChain = null;	
	private List<X509Certificate> trustedCerts = new ArrayList<X509Certificate>();
	
	private boolean isInitialized = false;
	
	private static JsonSecurityUtils instance = null;
		
	public static JsonSecurityUtils getInstance() {
		if (instance == null) {
			instance = new JsonSecurityUtils();
			instance.initalize();
		}
		
		return instance;
	}
	
	private JsonSecurityUtils() {
		
		
	}
	
	protected synchronized void initalize() {
		logger.info("Initialize SL2.0 authentication security constrains ... ");
		try {
			String keyStorePath = getKeyStoreFilePath();
			
			if (StringUtils.isNotEmpty(keyStorePath)) {
				KeyStore keyStore = KeyStoreUtils.loadKeyStore(getKeyStoreFilePath(), 
						getKeyStorePassword());
				
				//load signing key
				signPrivKey = keyStore.getKey(getSigningKeyAlias(), getSigningKeyPassword().toCharArray());
				Certificate[] certChainSigning = keyStore.getCertificateChain(getSigningKeyAlias());
				signCertChain = new X509Certificate[certChainSigning.length];
				for (int i=0; i<certChainSigning.length; i++) {
					if (certChainSigning[i] instanceof X509Certificate) {
						signCertChain[i] = (X509Certificate)certChainSigning[i];
					} else
						logger.warn("NO X509 certificate for signing: ");
					
				}
				
				//load encryption key
				try {
					encPrivKey = keyStore.getKey(getEncryptionKeyAlias(), getEncryptionKeyPassword().toCharArray());
					if (encPrivKey != null) {
						Certificate[] certChainEncryption = keyStore.getCertificateChain(getEncryptionKeyAlias());
						encCertChain = new X509Certificate[certChainEncryption.length];
						for (int i=0; i<certChainEncryption.length; i++) {
							if (certChainEncryption[i] instanceof X509Certificate) {
								encCertChain[i] = (X509Certificate)certChainEncryption[i];
							} else
								logger.warn("NO X509 certificate for encryption: ");
						}									
					} else
						logger.info("No encryption key for SL2.0 found. End-to-End encryption is not used.");
					
				} catch (Exception e) {
					logger.warn("No encryption key for SL2.0 found. End-to-End encryption is not used. Reason: " + e.getMessage(), e);
			
				}
				
				//load trusted certificates
				Enumeration<String> aliases = keyStore.aliases();
				while(aliases.hasMoreElements()) {
					String el = aliases.nextElement();
					logger.trace("Process TrustStoreEntry: " + el);
					if (keyStore.isCertificateEntry(el)) {
						Certificate cert = keyStore.getCertificate(el); 
						if (cert != null && cert instanceof X509Certificate)
							trustedCerts.add((X509Certificate) cert);
						else
							logger.info("Can not process entry: " + el + ". Reason: ");
					}
				}
	
				//some short validation
				if (signPrivKey == null || !(signPrivKey instanceof PrivateKey)) {
					logger.info("Can NOT open privateKey for SL2.0 signing. KeyStore=");
					throw new SL20Exception("sl20.03");
				}
				
				if (signCertChain == null || signCertChain.length == 0) {
					logger.info("NO certificate for SL2.0 signing. KeyStore=");
					throw new SL20Exception("sl20.03");
				}
				
				isInitialized = true;
				logger.info("SL2.0 authentication security constrains initialized.");
				
			} else
				logger.info("SL2.0 security constrains not configurated!");
			
		} catch ( Exception e) {
			logger.error("SL2.0 security constrains initialization FAILED.", e);
			
		}
		
	}
	
	
	@Override
	public String createSignature(String payLoad) throws SLCommandoBuildException {
		try {
			JsonWebSignature jws = new JsonWebSignature();
			
			//set payload
			jws.setPayload(payLoad);
		
			//set basic header		
			jws.setContentTypeHeaderValue(SL20Constants.SL20_CONTENTTYPE_SIGNED_COMMAND);
		
			//set signing information
			jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
			jws.setKey(signPrivKey);
			
			//TODO:
			jws.setCertificateChainHeaderValue(signCertChain);
			jws.setX509CertSha256ThumbprintHeaderValue(signCertChain[0]);
						
			return jws.getCompactSerialization();
			
		} catch (JoseException e) {
			logger.warn("Can NOT sign SL2.0 command.", e);
			throw new SLCommandoBuildException(e);
			
		}
		
	}
	
	@Override
	public VerificationResult validateSignature(String serializedContent) throws SL20Exception {
		try {
			JsonWebSignature jws = new JsonWebSignature();
			//set payload
			jws.setCompactSerialization(serializedContent);
				
			//set security constrains
			jws.setAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST,   
					SL20Constants.SL20_ALGORITHM_WHITELIST_SIGNING.toArray(new String[SL20Constants.SL20_ALGORITHM_WHITELIST_SIGNING.size()])));
			
			//load signinc certs
			Key selectedKey = null;
			List<X509Certificate> x5cCerts = jws.getCertificateChainHeaderValue();
			String x5t256 = jws.getX509CertSha256ThumbprintHeaderValue();
			if (x5cCerts != null) {
				logger.debug("Found x509 certificate in JOSE header ... ");
				logger.trace("Sorting received X509 certificates ... ");
				List<X509Certificate> sortedX5cCerts  = X509Utils.sortCertificates(x5cCerts);
				
				if (trustedCerts.contains(sortedX5cCerts.get(0))) {
					selectedKey = sortedX5cCerts.get(0).getPublicKey();
					
				} else {
					logger.info("Can NOT find JOSE certificate in truststore.");
				}
				
			} else if (StringUtils.isNotEmpty(x5t256)) {
				logger.debug("Found x5t256 fingerprint in JOSE header .... ");
				X509VerificationKeyResolver x509VerificationKeyResolver = new X509VerificationKeyResolver(trustedCerts);
				selectedKey = x509VerificationKeyResolver.resolveKey(jws, Collections.<JsonWebStructure>emptyList());
				
			} else {
				logger.info("Signed SL2.0 response contains NO signature certificate or NO certificate fingerprint");
				throw new SLCommandoParserException();
				
			}
					
			if (selectedKey == null) {
				logger.info("Can NOT select verification key for JWS. Signature verification FAILED.");
				throw new SLCommandoParserException();
				
			}
			
			//set verification key
			jws.setKey(selectedKey);
			
			//validate signature
			boolean valid = jws.verifySignature();
			if (!valid) {
				logger.info("JWS signature invalide. Stopping authentication process ...");
				logger.debug("Received JWS msg: " + serializedContent);
				throw new SL20SecurityException();
				
			}
				
			
			//load payLoad
			logger.debug("SL2.0 commando signature validation sucessfull");
			JsonElement sl20Req = new JsonParser().parse(jws.getPayload());
			
			return new VerificationResult(sl20Req.getAsJsonObject(), null, valid) ;
			
		} catch (JoseException e) {
			logger.warn("SL2.0 commando signature validation FAILED", e);
			throw new SL20SecurityException(e);
			
		}
					
	}
	

	@Override
	public JsonElement decryptPayload(String compactSerialization) throws SL20Exception {
		try {			
			JsonWebEncryption receiverJwe = new JsonWebEncryption();
					
			//set security constrains
			receiverJwe.setAlgorithmConstraints(
					new AlgorithmConstraints(ConstraintType.WHITELIST,
							SL20Constants.SL20_ALGORITHM_WHITELIST_KEYENCRYPTION.toArray(new String[SL20Constants.SL20_ALGORITHM_WHITELIST_KEYENCRYPTION.size()])));
			receiverJwe.setContentEncryptionAlgorithmConstraints(
					new AlgorithmConstraints(ConstraintType.WHITELIST,
							SL20Constants.SL20_ALGORITHM_WHITELIST_ENCRYPTION.toArray(new String[SL20Constants.SL20_ALGORITHM_WHITELIST_ENCRYPTION.size()])));
		
			//set payload
			receiverJwe.setCompactSerialization(compactSerialization);

			
			//validate key from header against key from config
			List<X509Certificate> x5cCerts = receiverJwe.getCertificateChainHeaderValue();
			String x5t256 = receiverJwe.getX509CertSha256ThumbprintHeaderValue();
			if (x5cCerts != null) {
				logger.debug("Found x509 certificate in JOSE header ... ");
				logger.trace("Sorting received X509 certificates ... ");
				List<X509Certificate> sortedX5cCerts  = X509Utils.sortCertificates(x5cCerts);
				
				if (!sortedX5cCerts.get(0).equals(encCertChain[0])) {
					logger.info("Certificate from JOSE header does NOT match encryption certificate");
					logger.debug("JOSE certificate: " + sortedX5cCerts.get(0).toString());
					throw new SL20Exception("sl20.05");
				}
				
			} else if (StringUtils.isNotEmpty(x5t256)) {
				logger.debug("Found x5t256 fingerprint in JOSE header .... ");
				String certFingerPrint = X509Util.x5tS256(encCertChain[0]);
				if (!certFingerPrint.equals(x5t256)) {
					logger.info("X5t256 from JOSE header does NOT match encryption certificate");
					throw new SL20Exception("sl20.05");
					
				}
				
			} else {
				logger.info("Signed SL2.0 response contains NO signature certificate or NO certificate fingerprint");
				throw new SLCommandoParserException();
			}
						
			//set key
			receiverJwe.setKey(encPrivKey);
			
						
			//decrypt payload			
			return new JsonParser().parse(receiverJwe.getPlaintextString());
			
		} catch (JoseException e) {
			logger.warn("SL2.0 result decryption FAILED", e);
			throw new SL20SecurityException(e);
			
		} catch ( JsonSyntaxException e) {
			logger.warn("Decrypted SL2.0 result is NOT a valid JSON.", e);
			throw new SLCommandoParserException(e);
			
		}
		
	}
	
	
	
	@Override
	public X509Certificate getEncryptionCertificate() {
		//TODO: maybe update after SL2.0 update on encryption certificate parts
		if (encCertChain !=null && encCertChain.length > 0)
			return encCertChain[0];
		else
			return null;
	}
	
	@Override
	public boolean isInitialized() {
		return isInitialized;
		
	}
	
	private String getKeyStoreFilePath() {
		return WebConfiguration.getSL20KeyStorePath();
	}
	
	private String getKeyStorePassword() {
		return WebConfiguration.getSL20KeyStorePassword();

	}
	
	private String getSigningKeyAlias() {
		return WebConfiguration.getSL20KeySigningAlias();
	}
	
	private String getSigningKeyPassword() {
		return WebConfiguration.getSL20KeySigningPassword();
		
	}

	private String getEncryptionKeyAlias() {
		return WebConfiguration.getSL20KeyEncryptionAlias();
	}
	
	private String getEncryptionKeyPassword() {
		return WebConfiguration.getSL20KeyEncryptionPassword();
	}
	
}
