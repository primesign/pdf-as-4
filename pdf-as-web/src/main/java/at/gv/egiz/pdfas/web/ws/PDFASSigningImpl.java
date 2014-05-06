package at.gv.egiz.pdfas.web.ws;

import javax.jws.WebService;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOM;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.helper.PdfAsHelper;

@MTOM
@WebService(endpointInterface = "at.gv.egiz.pdfas.web.ws.PDFASSigning")
public class PDFASSigningImpl implements PDFASSigning {

	private static final Logger logger = LoggerFactory
			.getLogger(PDFASSigningImpl.class);
	
	public byte[] signPDFDokument(byte[] inputDocument,
			PDFASSignParameters parameters) {
		try {
			return PdfAsHelper.synchornousServerSignature(inputDocument, parameters);
		} catch(Throwable e) {
			logger.error("Server Signature failed.", e);
			if(WebConfiguration.isShowErrorDetails()) {
				throw new WebServiceException("Server Signature failed.", e);
			} else {
				throw new WebServiceException("Server Signature failed.");
			}
		}
	}

}
