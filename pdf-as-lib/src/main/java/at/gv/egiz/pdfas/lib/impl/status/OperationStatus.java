/*******************************************************************************
 * <copyright> Copyright 2014 by E-Government Innovation Center EGIZ, Graz, Austria </copyright>
 * PDF-AS has been contracted by the E-Government Innovation Center EGIZ, a
 * joint initiative of the Federal Chancellery Austria and Graz University of
 * Technology.
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * http://www.osor.eu/eupl/
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * 
 * This product combines work with different licenses. See the "NOTICE" text
 * file for details on the various modules and licenses.
 * The "NOTICE" text file is part of the distribution. Any derivative works
 * that you distribute must include a readable copy of the "NOTICE" text file.
 ******************************************************************************/
package at.gv.egiz.pdfas.lib.impl.status;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.common.utils.TempFileHelper;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.impl.configuration.GlobalConfiguration;
import at.gv.egiz.pdfas.lib.impl.configuration.PlaceholderConfiguration;
import at.gv.egiz.pdfas.lib.impl.configuration.SignatureProfileConfiguration;

public class OperationStatus implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2985007198666388528L;
	
	
	private SignParameter signParamter;
	private PDFObject pdfObject = new PDFObject(this);
	
	private ISettings configuration;
	private PlaceholderConfiguration placeholderConfiguration = null;
	private GlobalConfiguration gloablConfiguration = null;
	private Map<String, SignatureProfileConfiguration> signatureProfiles = 
				new HashMap<String, SignatureProfileConfiguration>();
	private TempFileHelper helper;
	private RequestedSignature requestedSignature;
	private Calendar signingDate;
	
	public OperationStatus(ISettings configuration, SignParameter signParameter) {
		this.configuration = configuration;
		this.signParamter = signParameter;
		helper = new TempFileHelper(configuration);
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
	
	
	
	// ========================================================================
	
	public RequestedSignature getRequestedSignature() {
		return requestedSignature;
	}

	public void setRequestedSignature(RequestedSignature requestedSignature) {
		this.requestedSignature = requestedSignature;
	}

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
	
	public TempFileHelper getTempFileHelper() {
		return this.helper;
	}
	
	public ISettings getSettings() {
		return this.configuration;
	}

	public Calendar getSigningDate() {
		return signingDate;
	}

	public void setSigningDate(Calendar signingDate) {
		this.signingDate = signingDate;
	}
	
	
}
