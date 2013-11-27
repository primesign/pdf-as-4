package at.gv.egiz.sl.util;

import java.security.cert.CertificateException;
import java.util.Iterator;

import iaik.x509.X509Certificate;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.sl.CreateCMSSignatureRequestType;
import at.gv.egiz.sl.CreateCMSSignatureResponseType;
import at.gv.egiz.sl.InfoboxAssocArrayPairType;
import at.gv.egiz.sl.InfoboxReadRequestType;
import at.gv.egiz.sl.InfoboxReadResponseType;

public class ISignatureConnectorSLWrapper implements ISignatureConnector {

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
		CreateCMSSignatureRequestType request = connector.createCMSRequest(input, byteRange);
		CreateCMSSignatureResponseType response = connector.sendCMSRequest(request);
		
		return response.getCMSSignature();
	}

}
