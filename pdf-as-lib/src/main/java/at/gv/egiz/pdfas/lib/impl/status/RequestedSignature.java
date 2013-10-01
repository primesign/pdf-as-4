package at.gv.egiz.pdfas.lib.impl.status;

import iaik.x509.X509Certificate;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSettingsException;
import at.knowcenter.wag.egov.egiz.pdf.TablePos;


public class RequestedSignature {
    private String signatureProfile;
    private TablePos signaturePosition;
    private OperationStatus status;
    private X509Certificate certificate;
    //private IPlainSigner signer = null;

    public RequestedSignature(OperationStatus status) throws PdfAsException {
    	
    	this.status = status;
    	
    	String profileID = status.getSignParamter().getSignatureProfileId();
    	
    	if(profileID == null) {
    		profileID = status.getGlobalConfiguration().getDefaultSignatureProfile();
    		
    		if(profileID == null) {
    			throw new PdfAsSettingsException("Failed to determine Signature Profile!");
    		}
    	}
    	certificate = status.getSignParamter().getPlainSigner().getCertificate();
		
    	this.signatureProfile = profileID;
    	
    	if(status.getSignParamter().getSignaturePosition() == null) {
    		this.signaturePosition = new TablePos();
    	} else {
    		this.signaturePosition = new TablePos(status.getSignParamter().getSignaturePosition());
    	}
    }
    
    public boolean isVisual() {
        return this.status.getSignatureProfileConfiguration(signatureProfile).isVisualSignature();
    }
    
    public TablePos getTablePos() {
    	return this.signaturePosition;
    }
    
    public String getSignatureProfileID() {
    	return this.signatureProfile;
    }
    
    public X509Certificate getCertificate() {
    	return this.certificate;
    }

}
