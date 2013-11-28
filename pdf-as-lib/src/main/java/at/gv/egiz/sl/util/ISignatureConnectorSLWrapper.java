package at.gv.egiz.sl.util;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import iaik.cms.SignedData;
import iaik.cms.SignerInfo;
import iaik.x509.X509Certificate;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.utils.StringUtils;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.impl.verify.VerifyResultImpl;
import at.gv.egiz.sl.CreateCMSSignatureRequestType;
import at.gv.egiz.sl.CreateCMSSignatureResponseType;
import at.gv.egiz.sl.InfoboxAssocArrayPairType;
import at.gv.egiz.sl.InfoboxReadRequestType;
import at.gv.egiz.sl.InfoboxReadResponseType;

public class ISignatureConnectorSLWrapper implements ISignatureConnector {

	private static final Logger logger = LoggerFactory
			.getLogger(ISignatureConnectorSLWrapper.class);

	private ISLConnector connector;

	public ISignatureConnectorSLWrapper(ISLConnector connector) {
		this.connector = connector;
	}

	public X509Certificate getCertificate() throws PdfAsException {
		X509Certificate certificate = null;
		try {
			InfoboxReadRequestType request = connector
					.createInfoboxReadRequest();
			InfoboxReadResponseType response = connector
					.sendInfoboxReadRequest(request);

			Iterator<InfoboxAssocArrayPairType> iterator = response
					.getAssocArrayData().getPair().iterator();

			while (iterator.hasNext()) {
				InfoboxAssocArrayPairType pair = iterator.next();
				if (pair.getKey().equals("SecureSignatureKeypair")) {
					byte[] certData = pair.getBase64Content();
					certificate = new X509Certificate(certData);
					break;
				}
			}
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return certificate;
	}

	public byte[] sign(byte[] input, int[] byteRange) throws PdfAsException {
		CreateCMSSignatureRequestType request = connector.createCMSRequest(
				input, byteRange);
		CreateCMSSignatureResponseType response = connector
				.sendCMSRequest(request);
		try {
			SignedData signedData = new SignedData(new ByteArrayInputStream(
					response.getCMSSignature()));

			signedData.setContent(input);

			// get the signer infos
			SignerInfo[] signerInfos = signedData.getSignerInfos();
			// verify the signatures
			for (int i = 0; i < signerInfos.length; i++) {
				VerifyResultImpl verifyResult = new VerifyResultImpl();
				try {

					// verify the signature for SignerInfo at index i
					X509Certificate signer_cert = signedData.verify(i);
					// if the signature is OK the certificate of the
					// signer is returned
					logger.info("Signature OK from signer: "
							+ signer_cert.getSubjectDN());
					verifyResult.setSignerCertificate(signer_cert);
				} catch (SignatureException ex) {
					// if the signature is not OK a SignatureException
					// is thrown
					logger.info("Signature ERROR from signer: "
							+ signedData.getCertificate(
									signerInfos[i].getSignerIdentifier())
									.getSubjectDN());

					verifyResult.setSignerCertificate(signedData
							.getCertificate(signerInfos[i]
									.getSignerIdentifier()));
				}
			}
		} catch (Exception e) {
			logger.error("ERROR", e);
		}

		return response.getCMSSignature();
	}

}
