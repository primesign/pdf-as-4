package at.gv.egiz.pdfas.sigs.pades;

import iaik.security.ecc.provider.ECCProvider;
import iaik.security.provider.IAIK;
import iaik.x509.X509Certificate;

import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;

import org.apache.axis2.databinding.types.Token;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;

import at.gv.egiz.dsig.X509DataType;
import at.gv.egiz.dsig.util.DsigMarschaller;
import at.gv.egiz.moa.ByteArrayDataSource;
import at.gv.egiz.moa.SignatureVerificationServiceStub;
import at.gv.egiz.moa.SignatureVerificationServiceStub.CMSContentBaseType;
import at.gv.egiz.moa.SignatureVerificationServiceStub.CMSDataObjectOptionalMetaType;
import at.gv.egiz.moa.SignatureVerificationServiceStub.KeyInfoTypeChoice;
import at.gv.egiz.moa.SignatureVerificationServiceStub.VerifyCMSSignatureRequest;
import at.gv.egiz.moa.SignatureVerificationServiceStub.VerifyCMSSignatureResponse;
import at.gv.egiz.moa.SignatureVerificationServiceStub.VerifyCMSSignatureResponseTypeSequence;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.verify.SignatureCheck;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.lib.impl.verify.FilterEntry;
import at.gv.egiz.pdfas.lib.impl.verify.IVerifyFilter;
import at.gv.egiz.pdfas.lib.impl.verify.SignatureCheckImpl;
import at.gv.egiz.pdfas.lib.impl.verify.VerifyResultImpl;

public class PAdESVerifier  implements IVerifyFilter  {

	private static final String MOA_VERIFY_URL = "moa.verify.url";
	private static final String MOA_VERIFY_TRUSTPROFILE = "moa.verify.TrustProfileID";
	
	private String moaEndpoint;
	private String moaTrustProfile;
	
	public PAdESVerifier(Configuration config) {
		IAIK.getInstance();
		ECCProvider.addAsProvider();
		this.moaEndpoint = config.getValue(MOA_VERIFY_URL);
		this.moaTrustProfile = config.getValue(MOA_VERIFY_TRUSTPROFILE);
	}
	
	@SuppressWarnings("rawtypes")
	public List<VerifyResult> verify(byte[] contentData, byte[] signatureContent)
			throws PdfAsException {

		List<VerifyResult> resultList = new ArrayList<VerifyResult>();
		try {
			SignatureVerificationServiceStub service = new SignatureVerificationServiceStub(
					this.moaEndpoint);
			VerifyCMSSignatureRequest verifyCMSSignatureRequest = new VerifyCMSSignatureRequest();
			Token token = new Token();
			token.setValue(this.moaTrustProfile);
			verifyCMSSignatureRequest.setTrustProfileID(token);

			byte[] data = contentData;
			byte[] signature = signatureContent;

			CMSDataObjectOptionalMetaType cmsDataObjectOptionalMetaType = new CMSDataObjectOptionalMetaType();
			CMSContentBaseType cmsDataContent = new CMSContentBaseType();
			cmsDataContent.setBase64Content(new DataHandler(
					new ByteArrayDataSource(data, "application/pdf")));
			DataHandler cmsSignature = new DataHandler(new ByteArrayDataSource(
					signature, "application/pdf"));
			cmsDataObjectOptionalMetaType.setContent(cmsDataContent);
			verifyCMSSignatureRequest.setCMSSignature(cmsSignature);
			verifyCMSSignatureRequest
					.setDataObject(cmsDataObjectOptionalMetaType);
			
			// cmsDataObjectOptionalMetaType.
			VerifyCMSSignatureResponse response = service
					.verifyCMSSignature(verifyCMSSignatureRequest);
			
			VerifyCMSSignatureResponseTypeSequence[] verifySequence = response.getVerifyCMSSignatureResponse().getVerifyCMSSignatureResponseTypeSequence();
			for(int i = 0 ; i < verifySequence.length; i++) {
				VerifyResultImpl result = new VerifyResultImpl();
				
				SignatureCheck certificateCheck;
				
				 verifySequence[i].getSignerInfo().getKeyInfoTypeChoice()[0].getExtraElement();
				if(verifySequence[i].getCertificateCheck() != null) {
					certificateCheck = new SignatureCheckImpl(
						verifySequence[i].getCertificateCheck().getCode().intValue(),
						verifySequence[i].getCertificateCheck().isInfoSpecified() ?
						verifySequence[i].getCertificateCheck().getInfo().toString() : 
							"");
				} else {
					certificateCheck = new SignatureCheckImpl(
							1,
							"Es konnte keine formal korrekte Zertifikatskette vom Signatorzertifikat zu einem vertrauenswürdigen Wurzelzertifikat konstruiert werden.");
				}
				
				
				SignatureCheck signatureCheck = new SignatureCheckImpl(
						verifySequence[i].getSignatureCheck().getCode().intValue(),
						verifySequence[i].getSignatureCheck().isInfoSpecified() ?
								verifySequence[i].getSignatureCheck().getInfo().toString() : 
									"");
				
				result.setCertificateCheck(certificateCheck);
				result.setValueCheckCode(signatureCheck);
				result.setVerificationDone(true);
				
				KeyInfoTypeChoice[] keyInfo = verifySequence[i].getSignerInfo().getKeyInfoTypeChoice();
				String xmldisg = keyInfo[0].getExtraElement().toString();
				JAXBElement jaxbElement = (JAXBElement) DsigMarschaller.unmarshalFromString(xmldisg);
				result.setSignatureData(signatureContent);
				if(jaxbElement.getValue() instanceof X509DataType) {
					X509DataType x509Data = (X509DataType)jaxbElement.getValue();
					List<Object> dsigElements = x509Data.getX509IssuerSerialOrX509SKIOrX509SubjectName();
					for(int j = 0; j < dsigElements.size(); j++) {
						Object jaxElement = dsigElements.get(j);
						if(jaxElement instanceof JAXBElement) {
							JAXBElement jaxbElementMember = (JAXBElement)jaxElement;
							if(jaxbElementMember.getName().equals(
									DsigMarschaller.X509DataTypeX509Certificate_QNAME)) {
								if(jaxbElementMember.getValue() instanceof byte[]) {
									byte[] certData = (byte[])jaxbElementMember.getValue();
									X509Certificate certificate = new X509Certificate(certData);
									result.setSignerCertificate(certificate);
									break;
								}
							}
						}
					}
				}

				resultList.add(result);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return resultList;
	}

	public List<FilterEntry> getFiters() {
		List<FilterEntry> result = new ArrayList<FilterEntry>();
		result.add(new FilterEntry(PDSignature.FILTER_ADOBE_PPKLITE, PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED));
		return result;
	}

}
