package at.gv.egiz.pdfas.lib.impl.verify;

import iaik.x509.X509Certificate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;

import org.apache.axis2.databinding.types.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import at.gv.egiz.moa.SignatureVerificationServiceStub.X509DataTypeSequence;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.messages.CodesResolver;
import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;

public class FullVerifier implements IVerifier {

	private static final Logger logger = LoggerFactory
			.getLogger(FullVerifier.class);

	private static final String MOA_VERIFY_URL = "moa.verify.url";
	private static final String MOA_VERIFY_TRUSTPROFILE = "moa.verify.TrustProfileID";

	private String moaEndpoint;
	private String moaTrustProfile;

	
	public List<VerifyResult> verify(byte[] signature, byte[] signatureContent,
			Date verificationTime) throws PdfAsException {
		List<VerifyResult> resultList = new ArrayList<VerifyResult>();
		try {
			logger.info("verification with MOA @ " + this.moaEndpoint);

			SignatureVerificationServiceStub service = new SignatureVerificationServiceStub(
					this.moaEndpoint);
			VerifyCMSSignatureRequest verifyCMSSignatureRequest = new VerifyCMSSignatureRequest();
			Token token = new Token();
			token.setValue(this.moaTrustProfile);
			verifyCMSSignatureRequest.setTrustProfileID(token);

			CMSDataObjectOptionalMetaType cmsDataObjectOptionalMetaType = new CMSDataObjectOptionalMetaType();
			CMSContentBaseType cmsDataContent = new CMSContentBaseType();
			cmsDataContent.setBase64Content(new DataHandler(
					new ByteArrayDataSource(signatureContent, "application/pdf")));
			DataHandler cmsSignature = new DataHandler(new ByteArrayDataSource(
					signature, "application/pdf"));
			cmsDataObjectOptionalMetaType.setContent(cmsDataContent);
			verifyCMSSignatureRequest.setCMSSignature(cmsSignature);
			verifyCMSSignatureRequest
					.setDataObject(cmsDataObjectOptionalMetaType);
			if (verificationTime != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(verificationTime);
				verifyCMSSignatureRequest.setDateTime(cal);
			}
			// cmsDataObjectOptionalMetaType.
			VerifyCMSSignatureResponse response = service
					.verifyCMSSignature(verifyCMSSignatureRequest);

			logger.debug("Got Verify Response from MOA");
			
			VerifyCMSSignatureResponseTypeSequence[] verifySequence = response
					.getVerifyCMSSignatureResponse()
					.getVerifyCMSSignatureResponseTypeSequence();
			for (int i = 0; i < verifySequence.length; i++) {
				VerifyResultImpl result = new VerifyResultImpl();
				logger.debug(" ---------------------- ");
				logger.debug("Signature: " + i);
				
				SignatureCheckImpl certificateCheck;

				verifySequence[i].getSignerInfo().getKeyInfoTypeChoice()[0]
						.getExtraElement();
				if (verifySequence[i].getCertificateCheck() != null) {
					certificateCheck = new SignatureCheckImpl(verifySequence[i]
							.getCertificateCheck().getCode().intValue(),
							verifySequence[i].getCertificateCheck()
									.isInfoSpecified() ? verifySequence[i]
									.getCertificateCheck().getInfo().toString()
									: "");
				} else {
					certificateCheck = new SignatureCheckImpl(
							1,
							"");
				}

				if(certificateCheck.getMessage() == null || certificateCheck.getMessage().trim().length() == 0) {
					String resourceString = "verify.cert." + certificateCheck.getCode();
					String message = CodesResolver.resolveMessage(resourceString);
					certificateCheck.setMessage(message);
				}
				
				logger.debug("Certificate Check: " + certificateCheck.getCode() + " [" + certificateCheck.getMessage() + "]");
				
				SignatureCheckImpl signatureCheck = new SignatureCheckImpl(
						verifySequence[i].getSignatureCheck().getCode()
								.intValue(),
						verifySequence[i].getSignatureCheck().isInfoSpecified() ? verifySequence[i]
								.getSignatureCheck().getInfo().toString()
								: "");

				if(signatureCheck.getMessage() == null || signatureCheck.getMessage().trim().length() == 0) {
					String resourceString = "verify.value." + signatureCheck.getCode();
					String message = CodesResolver.resolveMessage(resourceString);
					signatureCheck.setMessage(message);
				}
				
				logger.debug("Signature Check: " + signatureCheck.getCode() + " [" + signatureCheck.getMessage() + "]");
				
				result.setCertificateCheck(certificateCheck);
				result.setValueCheckCode(signatureCheck);
				result.setVerificationDone(true);

				KeyInfoTypeChoice[] keyInfo = verifySequence[i].getSignerInfo()
						.getKeyInfoTypeChoice();
				KeyInfoTypeChoice choice = keyInfo[0];

				// extract certificate
				if (choice.isX509DataSpecified()) {
					byte[] certData = null;
					X509DataTypeSequence[] x509Sequence = choice.getX509Data()
							.getX509DataTypeSequence();
					for (int k = 0; k < x509Sequence.length; k++) {
						X509DataTypeSequence x509Data = x509Sequence[k];
						if (x509Data.getX509DataTypeChoice_type0()
								.isX509CertificateSpecified()) {
							DataHandler handler = x509Data
									.getX509DataTypeChoice_type0()
									.getX509Certificate();
							certData = StreamUtils
									.inputStreamToByteArray(handler
											.getInputStream());
						} else if (x509Data.getX509DataTypeChoice_type0()
								.isExtraElementSpecified()) {
							if (x509Data
									.getX509DataTypeChoice_type0()
									.getExtraElement()
									.getLocalName()
									.equals(SignatureVerificationServiceStub.QualifiedCertificate.MY_QNAME
											.getLocalPart())) {
								result.setQualifiedCertificate(true);
							}
						}
					}
					X509Certificate certificate = new X509Certificate(certData);
					result.setSignerCertificate(certificate);
				} else if (choice.isExtraElementSpecified()) {
					String xmldisg = choice.getExtraElement().toString();
					JAXBElement jaxbElement = (JAXBElement) DsigMarschaller
							.unmarshalFromString(xmldisg);
					if (jaxbElement.getValue() instanceof X509DataType) {
						X509DataType x509Data = (X509DataType) jaxbElement
								.getValue();
						List<Object> dsigElements = x509Data
								.getX509IssuerSerialOrX509SKIOrX509SubjectName();
						for (int j = 0; j < dsigElements.size(); j++) {
							Object jaxElement = dsigElements.get(j);
							if (jaxElement instanceof JAXBElement) {
								JAXBElement jaxbElementMember = (JAXBElement) jaxElement;
								if (jaxbElementMember
										.getName()
										.equals(DsigMarschaller.X509DataTypeX509Certificate_QNAME)) {
									if (jaxbElementMember.getValue() instanceof byte[]) {
										byte[] certData = (byte[]) jaxbElementMember
												.getValue();
										X509Certificate certificate = new X509Certificate(
												certData);
										result.setSignerCertificate(certificate);
										break;
									}
								}
							}
						}
					}
				}

				resultList.add(result);
				
				logger.debug(" ---------------------- ");
			}
		} catch (Throwable e) {
			logger.error("Verification failed", e);
			throw new PdfAsException("error.pdf.verify.02", e);
		}
		return resultList;
	}
	
	public void setConfiguration(Configuration config) {
		this.moaEndpoint = config.getValue(MOA_VERIFY_URL);
		this.moaTrustProfile = config.getValue(MOA_VERIFY_TRUSTPROFILE);
	}

}
