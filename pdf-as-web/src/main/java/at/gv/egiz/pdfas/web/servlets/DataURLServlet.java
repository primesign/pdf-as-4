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
package at.gv.egiz.pdfas.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.web.exception.PdfAsSecurityLayerException;
import at.gv.egiz.pdfas.web.helper.PdfAsHelper;
import at.gv.egiz.sl.schema.CreateCMSSignatureResponseType;
import at.gv.egiz.sl.schema.ErrorResponseType;
import at.gv.egiz.sl.schema.InfoboxReadResponseType;
import at.gv.egiz.sl.util.SLMarschaller;

/**
 * Servlet implementation class DataURL
 */
@MultipartConfig
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
			if(!PdfAsHelper.checkDataUrlAccess(request)) {
				throw new Exception("No valid dataURL access");
			}
			
			PdfAsHelper.setFromDataUrl(request);
			String xmlResponse = request.getParameter("XMLResponse");
			
			//System.out.println(xmlResponse);
			
			JAXBElement<?> jaxbObject = (JAXBElement<?>) SLMarschaller.unmarshalFromString(xmlResponse);
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
				logger.warn("SecurityLayer: " + errorResponseType.getErrorCode() + " " + errorResponseType.getInfo());
				throw new PdfAsSecurityLayerException(errorResponseType.getInfo(), 
						errorResponseType.getErrorCode());
			} else {
				logger.error("Unknown SL response {}", xmlResponse);
				throw new PdfAsSecurityLayerException("Unknown SL response", 
						9999);
			}
		} catch (Exception e) {
			logger.warn("Error in DataURL Servlet. " , e);
			PdfAsHelper.setSessionException(request, response, e.getMessage(),
					e);
			PdfAsHelper.gotoError(getServletContext(), request, response);
		}
	}

}
