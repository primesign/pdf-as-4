package at.gv.egiz.pdfas.moa;

import iaik.x509.X509Certificate;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2000._09.xmldsig.KeyInfoType;
import org.w3._2000._09.xmldsig.X509DataType;

import at.gv.e_government.reference.namespace.moa._20020822.CMSContentBaseType;
import at.gv.e_government.reference.namespace.moa._20020822.CMSDataObjectOptionalMetaType;
import at.gv.e_government.reference.namespace.moa._20020822.CheckResultType;
import at.gv.e_government.reference.namespace.moa._20020822.MetaInfoType;
import at.gv.e_government.reference.namespace.moa._20020822.VerifyCMSSignatureRequest;
import at.gv.e_government.reference.namespace.moa._20020822.VerifyCMSSignatureResponseType;
import at.gv.e_government.reference.namespace.moa._20020822_.SignatureVerificationPortType;
import at.gv.e_government.reference.namespace.moa._20020822_.SignatureVerificationService;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter.SignatureVerificationLevel;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.lib.impl.verify.IVerifier;
import at.gv.egiz.pdfas.lib.impl.verify.SignatureCheckImpl;
import at.gv.egiz.pdfas.lib.impl.verify.VerifyResultImpl;

public class MOAVerifier implements IVerifier {

	private static final Logger logger = LoggerFactory
			.getLogger(MOAVerifier.class);

	private static final String MOA_VERIFY_URL = "moa.verify.url";
	private static final String MOA_VERIFY_TRUSTPROFILE = "moa.verify.TrustProfileID";

	private String moaEndpoint;
	private String moaTrustProfile;

	
	public List<VerifyResult> verify(byte[] signature, byte[] signatureContent,
			Date verificationTime) throws PdfAsException {
		List<VerifyResult> resultList = new ArrayList<VerifyResult>();
		try {
			logger.info("verification with MOA @ " + this.moaEndpoint);

			//URL moaUrl = new URL(this.moaEndpoint + "?wsdl");
			
			SignatureVerificationService service = new SignatureVerificationService();
			
			SignatureVerificationPortType verificationPort = service.getSignatureVerificationPort();
			BindingProvider provider = (BindingProvider) verificationPort;
			provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, this.moaEndpoint);
			VerifyCMSSignatureRequest verifyCMSSignatureRequest = new VerifyCMSSignatureRequest();
			verifyCMSSignatureRequest.setTrustProfileID(this.moaTrustProfile);
			verifyCMSSignatureRequest.setCMSSignature(signature);
			CMSDataObjectOptionalMetaType metaDataType = new CMSDataObjectOptionalMetaType();
			
			MetaInfoType metaInfoType = new MetaInfoType();
			metaInfoType.setDescription("PDF Document");
			metaInfoType.setMimeType("application/pdf");
			metaDataType.setMetaInfo(metaInfoType);
			
			CMSContentBaseType contentBase = new CMSContentBaseType();
			contentBase.setBase64Content(signatureContent);
			metaDataType.setContent(contentBase);
			
			verifyCMSSignatureRequest.setDataObject(metaDataType);
			
			if (verificationTime != null) {
				GregorianCalendar c = new GregorianCalendar();
				c.setTime(verificationTime);
				XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
				verifyCMSSignatureRequest.setDateTime(date2);
			}
			
			VerifyCMSSignatureResponseType response = verificationPort
					.verifyCMSSignature(verifyCMSSignatureRequest);
			
			logger.debug("Got Verify Response from MOA");
			
			List<JAXBElement<?>> verifySequence = response.getSignerInfoAndSignatureCheckAndCertificateCheck();
			
			VerifyResultImpl result = new VerifyResultImpl();
			
			result.setCertificateCheck(new SignatureCheckImpl(1,""));
			result.setValueCheckCode(new SignatureCheckImpl(1,""));
			result.setVerificationDone(true);
			result.setSignatureData(signatureContent);
			
			for (int i = 0; i < verifySequence.size(); i++) {
				//
				
				JAXBElement<?> element = verifySequence.get(i);

				logger.debug(" ---------------------- ");
				logger.debug("Name: " + element.getName().getLocalPart());
				logger.debug("Class: " + element.getValue().getClass().getName());
				
				if(element.getName().getLocalPart().equals("SignerInfo")) {
					if(!(element.getValue() instanceof KeyInfoType)) {
						// TODO throw Exception
					}
					KeyInfoType keyInfo = (KeyInfoType)element.getValue();

					for(Object obj : keyInfo.getContent()) {
						logger.debug("KeyInfo: " + obj.getClass().toString());
						if(obj instanceof JAXBElement<?>) {
							JAXBElement<?> ele = (JAXBElement<?>)obj;
							logger.debug("KeyInfo: " + ele.getName().getLocalPart());
							logger.debug("KeyInfo: " + ele.getValue().getClass().getName());
							if(ele.getName().getLocalPart().equals("X509Data") && 
									ele.getValue() instanceof X509DataType) {
								X509DataType x509Data = (X509DataType)ele.getValue();
								for(Object o : x509Data.getX509IssuerSerialOrX509SKIOrX509SubjectName()) {
									logger.debug("X509 class: " + o.getClass().getName());
									if(o instanceof JAXBElement<?>) {
										JAXBElement<?> e = (JAXBElement<?>)o;
										logger.debug("X509 class CHILD: " + e.getName().getLocalPart());
										logger.debug("X509 class CHILD: " + e.getValue().getClass().getName());
										if(e.getName().getLocalPart().equals("X509Certificate")) {
											if(e.getValue() instanceof byte[]) {
												X509Certificate signerCertificate = new X509Certificate((byte[])e.getValue());
												result.setSignerCertificate(signerCertificate);
											}
										}
									} /*else if(o instanceof ElementNSImpl) {
										logger.debug("ElementNSImpl name: " + ((ElementNSImpl) o).getNodeValue());
										for(int j = 0; j < ((ElementNSImpl) o).getAttributes().getLength(); j++) {
											
											//logger.debug("ElementNSImpl name: " + ((ElementNSImpl) o).getAttributes().item(j)..getTextContent());
										}
									}*/
								}
							}
						}
					}
					
				} else if(element.getName().getLocalPart().equals("SignatureCheck")) {
					
					if(!(element.getValue() instanceof CheckResultType)) {
						// TODO throw Exception
					}
					
					CheckResultType checkResult = (CheckResultType)element.getValue();
					
					result.setValueCheckCode(new SignatureCheckImpl(
							checkResult.getCode().intValue(), 
							(checkResult.getInfo() != null) ? 
									checkResult.getInfo().toString() : ""
								));
					
				} else if(element.getName().getLocalPart().equals("CertificateCheck")) {
					
					if(!(element.getValue() instanceof CheckResultType)) {
						// TODO throw Exception
					}
					
					CheckResultType checkResult = (CheckResultType)element.getValue();
					
					result.setCertificateCheck(new SignatureCheckImpl(
							checkResult.getCode().intValue(), 
							(checkResult.getInfo() != null) ? 
									checkResult.getInfo().toString() : ""
								));
				}
				
				logger.debug(" ---------------------- ");
			}
			resultList.add(result);
		} catch (Throwable e) {
			logger.warn("Verification failed", e);
			throw new PdfAsException("error.pdf.verify.02", e);
		}
		return resultList;
	}
	
	public void setConfiguration(Configuration config) {
		this.moaEndpoint = config.getValue(MOA_VERIFY_URL);
		this.moaTrustProfile = config.getValue(MOA_VERIFY_TRUSTPROFILE);
	}

	@Override
	public SignatureVerificationLevel getLevel() {
		return SignatureVerificationLevel.FULL_VERIFICATION;
	}

}
