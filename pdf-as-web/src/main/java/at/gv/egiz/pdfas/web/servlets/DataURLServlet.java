package at.gv.egiz.pdfas.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.web.exception.PdfAsSecurityLayerException;
import at.gv.egiz.pdfas.web.helper.PdfAsHelper;
import at.gv.egiz.sl.CreateCMSSignatureResponseType;
import at.gv.egiz.sl.ErrorResponseType;
import at.gv.egiz.sl.InfoboxReadResponseType;
import at.gv.egiz.sl.util.SLMarschaller;

/**
 * Servlet implementation class DataURL
 */
public class DataURLServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory
			.getLogger(DataURLServlet.class);
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DataURLServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.process(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.process(request, response);
	}

	protected void process(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			PdfAsHelper.setFromDataUrl(request);
			String xmlResponse = request.getParameter("XMLResponse");
			
			//System.out.println(xmlResponse);
			
			JAXBElement jaxbObject = (JAXBElement) SLMarschaller.unmarshalFromString(xmlResponse);
			if(jaxbObject.getValue() instanceof InfoboxReadResponseType) {
				InfoboxReadResponseType infoboxReadResponseType = (InfoboxReadResponseType)jaxbObject.getValue();
				logger.info("Got InfoboxReadResponseType");
				PdfAsHelper.injectCertificate(request, response, infoboxReadResponseType, getServletContext());
			} else if(jaxbObject.getValue() instanceof CreateCMSSignatureResponseType) {
				CreateCMSSignatureResponseType createCMSSignatureResponseType = (CreateCMSSignatureResponseType)jaxbObject.getValue();
				logger.info("Got CreateCMSSignatureResponseType");
				PdfAsHelper.injectSignature(request, response, createCMSSignatureResponseType, getServletContext());
			} else if(jaxbObject.getValue() instanceof ErrorResponseType) {
				ErrorResponseType errorResponseType = (ErrorResponseType)jaxbObject.getValue();
				logger.error("SecurityLayer: " + errorResponseType.getErrorCode() + " " + errorResponseType.getInfo());
				throw new PdfAsSecurityLayerException(errorResponseType.getInfo(), 
						errorResponseType.getErrorCode());
			} else {
				throw new PdfAsSecurityLayerException("Unknown SL response", 
						9999);
			}
		} catch (Exception e) {
			logger.error("Error in DataURL Servlet. " , e);
			PdfAsHelper.setSessionException(request, response, e.getMessage(),
					e);
			PdfAsHelper.gotoError(getServletContext(), request, response);
		}
	}

}
