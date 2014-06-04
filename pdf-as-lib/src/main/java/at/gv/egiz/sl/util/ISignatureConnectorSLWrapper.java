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
package at.gv.egiz.sl.util;

import iaik.cms.CMSException;
import iaik.cms.SignedData;
import iaik.cms.SignerInfo;
import iaik.x509.X509Certificate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSignatureException;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.impl.verify.VerifyResultImpl;
import at.gv.egiz.sl.schema.CreateCMSSignatureResponseType;
import at.gv.egiz.sl.schema.InfoboxAssocArrayPairType;
import at.gv.egiz.sl.schema.InfoboxReadRequestType;
import at.gv.egiz.sl.schema.InfoboxReadResponseType;

public class ISignatureConnectorSLWrapper implements ISignatureConnector {

	public static final String SL_USE_BASE64 = "";
	
	private static final Logger logger = LoggerFactory
			.getLogger(ISignatureConnectorSLWrapper.class);

	private ISLConnector connector;

	public ISignatureConnectorSLWrapper(ISLConnector connector) {
		this.connector = connector;
	}

	public X509Certificate getCertificate(SignParameter parameter) throws PdfAsException {
		X509Certificate certificate = null;
		try {
			InfoboxReadRequestType request = connector
					.createInfoboxReadRequest(parameter);
			InfoboxReadResponseType response = connector
					.sendInfoboxReadRequest(request, parameter);

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
			throw new PdfAsSignatureException("error.pdf.sig.01", e);
		}
		return certificate;
	}

	public byte[] sign(byte[] input, int[] byteRange, SignParameter parameter) throws PdfAsException {
		RequestPackage pack = connector.createCMSRequest(
				input, byteRange, parameter);
		CreateCMSSignatureResponseType response = connector
				.sendCMSRequest(pack, parameter);
		try {
			SignedData signedData = new SignedData(new ByteArrayInputStream(
					response.getCMSSignature()));

			signedData.setContent(input);

			// get the signer infos
			SignerInfo[] signerInfos = signedData.getSignerInfos();
			if (signerInfos.length == 0) {
				throw new PdfAsSignatureException("Invalid Signature (no signer info created!)", null);
			}
			// verify the signatures
			for (int i = 0; i < signerInfos.length; i++) {
				VerifyResultImpl verifyResult = new VerifyResultImpl();
				try {
					logger.info("Signature Algo: {}, Digest {}", signedData
							.getSignerInfos()[i].getSignatureAlgorithm(),
							signedData.getSignerInfos()[i].getDigestAlgorithm());
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
					logger.error(
							"Signature ERROR from signer: "
									+ signedData.getCertificate(
											signerInfos[i]
													.getSignerIdentifier())
											.getSubjectDN(), ex);

					verifyResult.setSignerCertificate(signedData
							.getCertificate(signerInfos[i]
									.getSignerIdentifier()));
					throw new PdfAsSignatureException("error.pdf.sig.08", ex);
				}
			}
		} catch (CMSException e) {
			throw new PdfAsSignatureException("error.pdf.sig.08", e);
		} catch (IOException e) {
			throw new PdfAsSignatureException("error.pdf.sig.08", e);
		}

		return response.getCMSSignature();
	}

}
