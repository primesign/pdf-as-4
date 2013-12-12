package at.gv.egiz.pdfas.lib.impl.status;

import iaik.x509.X509Certificate;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSettingsException;
import at.gv.egiz.pdfas.lib.api.SignaturePosition;
import at.knowcenter.wag.egov.egiz.pdf.TablePos;


public class RequestedSignature {
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
    
    
    
}
