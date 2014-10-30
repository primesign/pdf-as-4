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

import iaik.x509.X509Certificate;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSettingsException;
import at.gv.egiz.pdfas.lib.api.SignaturePosition;
import at.knowcenter.wag.egov.egiz.pdf.TablePos;


public class RequestedSignature implements ICertificateProvider {
    private String signatureProfile;
    private TablePos tablePosition;
    private OperationStatus status;
    private X509Certificate certificate;
    private SignaturePosition signaturePosition = null;
    //private IPlainSigner signer = null;

    public RequestedSignature(OperationStatus status) throws PdfAsException {
    	
    	this.status = status;
    	
    	String profileID = status.getSignParamter().getSignatureProfileId();
    	
    	if(profileID == null) {
    		profileID = status.getGlobalConfiguration().getDefaultSignatureProfile();
    		
    		if(profileID == null) {
    			throw new PdfAsSettingsException("error.pdf.sig.07");
    		}
    	}
		
    	this.signatureProfile = profileID;
    	
    	if(status.getSignParamter().getSignaturePosition() == null) {
    		this.tablePosition = new TablePos();
    	} else {
    		this.tablePosition = new TablePos(status.getSignParamter().getSignaturePosition());
    	}
    }
    
    public boolean isVisual() {
        return this.status.getSignatureProfileConfiguration(signatureProfile).isVisualSignature();
    }
    
    public TablePos getTablePos() {
    	return this.tablePosition;
    }
    
    public String getSignatureProfileID() {
    	return this.signatureProfile;
    }
    
    public void setSignatureProfileID(String signatureProfile) {
    	this.signatureProfile = signatureProfile;
    }
    
    public X509Certificate getCertificate() {
    	return this.certificate;
    }

    public void setCertificate(X509Certificate certificate) {
    	this.certificate = certificate;
    }

	public SignaturePosition getSignaturePosition() {
		return signaturePosition;
	}

	public void setSignaturePosition(SignaturePosition signaturePosition) {
		this.signaturePosition = signaturePosition;
	}

	public OperationStatus getStatus() {
		return status;
	}
}
