package at.gv.egiz.pdfas.lib.impl.status;

import java.util.HashMap;
import java.util.Map;

import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.impl.configuration.GlobalConfiguration;
import at.gv.egiz.pdfas.lib.impl.configuration.PlaceholderConfiguration;
import at.gv.egiz.pdfas.lib.impl.configuration.SignatureProfileConfiguration;

public class OperationStatus {
	
	private SignParameter signParamter;
	private PDFObject pdfObject = new PDFObject();
	
	
	private ISettings configuration;
	private PlaceholderConfiguration placeholderConfiguration = null;
	private GlobalConfiguration gloablConfiguration = null;
	private Map<String, SignatureProfileConfiguration> signatureProfiles = 
				new HashMap<String, SignatureProfileConfiguration>();
	
	public OperationStatus(ISettings configuration, SignParameter signParameter) {
		this.configuration = configuration;
		this.signParamter = signParameter;
	}
	
	// ========================================================================
	
	public PlaceholderConfiguration getPlaceholderConfiguration() {
		if(this.placeholderConfiguration == null) {
			this.placeholderConfiguration = 
					new PlaceholderConfiguration(this.configuration);
		}
		return this.placeholderConfiguration;
	}
	
	public GlobalConfiguration getGlobalConfiguration() {
		if(this.gloablConfiguration == null) {
			this.gloablConfiguration = 
					new GlobalConfiguration(this.configuration);
		}
		return this.gloablConfiguration;
	}
	
	public SignatureProfileConfiguration getSignatureProfileConfiguration(String profileID) {
		
		SignatureProfileConfiguration signatureProfileConfiguration = signatureProfiles.get(profileID);
		if(signatureProfileConfiguration == null) {
			signatureProfileConfiguration = new SignatureProfileConfiguration(this.configuration, profileID);
			signatureProfiles.put(profileID, signatureProfileConfiguration);
		}
		
		return signatureProfileConfiguration;
	}
	
	// ========================================================================
	
	public PDFObject getPdfObject() {
		return pdfObject;
	}

	public void setPdfObject(PDFObject pdfObject) {
		this.pdfObject = pdfObject;
	}

	public SignParameter getSignParamter() {
		return signParamter;
	}

	public void setSignParamter(SignParameter signParamter) {
		this.signParamter = signParamter;
	}
	
}
