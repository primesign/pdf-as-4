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

import iaik.x509.X509Certificate;

import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSignatureException;
import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.lib.impl.SignResultImpl;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import at.gv.egiz.pdfas.lib.util.SignatureUtils;
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

	public byte[] sign(byte[] input, int[] byteRange, 
			SignParameter parameter, RequestedSignature requestedSignature) throws PdfAsException {
		RequestPackage pack = connector.createCMSRequest(
				input, byteRange, parameter);
		CreateCMSSignatureResponseType response = connector
				.sendCMSRequest(pack, parameter);
		
		VerifyResult verifyResult = SignatureUtils.verifySignature(response.getCMSSignature(), input);

		if(!StreamUtils.dataCompare(requestedSignature.getCertificate().getFingerprintSHA(),
				verifyResult.getSignerCertificate().getFingerprintSHA())) {
			throw new PdfAsSignatureException("Certificates missmatch!");
		}
		
		return response.getCMSSignature();
	}

}
