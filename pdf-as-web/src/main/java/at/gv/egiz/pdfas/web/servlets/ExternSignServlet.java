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

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSettingsValidationException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter.SignatureVerificationLevel;
import at.gv.egiz.pdfas.lib.impl.configuration.PlaceholderWebConfiguration;
import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.exception.PdfAsWebException;
import at.gv.egiz.pdfas.web.filter.UserAgentFilter;
import at.gv.egiz.pdfas.web.helper.DigestHelper;
import at.gv.egiz.pdfas.web.helper.PdfAsHelper;
import at.gv.egiz.pdfas.web.helper.PdfAsParameterExtractor;
import at.gv.egiz.pdfas.web.helper.RemotePDFFetcher;
import at.gv.egiz.pdfas.web.stats.StatisticEvent;
import at.gv.egiz.pdfas.web.stats.StatisticEvent.Operation;
import at.gv.egiz.pdfas.web.stats.StatisticEvent.Source;
import at.gv.egiz.pdfas.web.stats.StatisticEvent.Status;
import at.gv.egiz.pdfas.web.stats.StatisticFrontend;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Servlet implementation class Sign
 */
public class ExternSignServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static final String PDF_AS_WEB_CONF = "pdf-as-web.conf";
	
	private static final String UPLOAD_PDF_DATA = "pdf-file";
	private static final String UPLOAD_DIRECTORY = "upload";

	private static final Logger logger = LoggerFactory
			.getLogger(ExternSignServlet.class);
	
	/**
	 * Default constructor.

	 */
	public ExternSignServlet(){
		String webconfig = System.getProperty(PDF_AS_WEB_CONF);
		
		if(webconfig == null) {
			logger.error("No web configuration provided! Please specify: " + PDF_AS_WEB_CONF);
			throw new RuntimeException("No web configuration provided! Please specify: " + PDF_AS_WEB_CONF);
		}
		
		WebConfiguration.configure(webconfig);
		PdfAsHelper.init();
		
		try {
			PdfAsFactory.validateConfiguration((ISettings)PdfAsHelper.getPdfAsConfig());
		} catch (PdfAsSettingsValidationException e) {
			// TODO Auto-generated catch block
			logger.error(e.getLocalizedMessage(),e.getCause());
			//e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		//PdfAsHelper.regenerateSession(request);
		
		logger.debug("Get signing request");
		
		String errorUrl = PdfAsParameterExtractor.getInvokeErrorURL(request);
		PdfAsHelper.setErrorURL(request, response, errorUrl);
		
		StatisticEvent statisticEvent = new StatisticEvent();
		statisticEvent.setStartNow();
		statisticEvent.setSource(Source.WEB);
		statisticEvent.setOperation(Operation.SIGN);
		statisticEvent.setUserAgent(UserAgentFilter.getUserAgent());
		
		try {
			// Mandatory Parameters on Get Request:
			String invokeUrl = PdfAsParameterExtractor.getInvokeURL(request);
			PdfAsHelper.setInvokeURL(request, response, invokeUrl);

			String invokeTarget = PdfAsParameterExtractor.getInvokeTarget(request);
			PdfAsHelper.setInvokeTarget(request, response, invokeTarget);
			
			String pdfUrl = PdfAsParameterExtractor.getPdfUrl(request);

			if (pdfUrl == null) {
				throw new PdfAsWebException(
						"No PDF URL given! Use POST request to sign without PDF URL.");
			}

			byte[] pdfData = RemotePDFFetcher.fetchPdfFile(pdfUrl);
			doSignature(request, response, pdfData, statisticEvent);
		} catch (Exception e) {
			logger.error("Signature failed", e);
			statisticEvent.setStatus(Status.ERROR);
			statisticEvent.setException(e);
			if(e instanceof PDFASError) {
				statisticEvent.setErrorCode(((PDFASError)e).getCode());
			}
			statisticEvent.setEndNow();
			statisticEvent.setTimestampNow();
			StatisticFrontend.getInstance().storeEvent(statisticEvent);
			statisticEvent.setLogged(true);
			
			PdfAsHelper.setSessionException(request, response, e.getMessage(),
					e);
			PdfAsHelper.gotoError(getServletContext(), request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		//PdfAsHelper.regenerateSession(request);
		
		logger.debug("Post signing request");
		
		String errorUrl = PdfAsParameterExtractor.getInvokeErrorURL(request);
		PdfAsHelper.setErrorURL(request, response, errorUrl);
		
		StatisticEvent statisticEvent = new StatisticEvent();
		statisticEvent.setStartNow();
		statisticEvent.setSource(Source.WEB);
		statisticEvent.setOperation(Operation.SIGN);
		statisticEvent.setUserAgent(UserAgentFilter.getUserAgent());
		
		try {
			byte[] filecontent = null;

			// checks if the request actually contains upload file
			if (!ServletFileUpload.isMultipartContent(request)) {
				// No Uploaded data!
				if (PdfAsParameterExtractor.getPdfUrl(request) != null) {
					doGet(request, response);
					return;
				} else {
					throw new PdfAsWebException("No Signature data defined!");
				}
			} else {

				// configures upload settings
				DiskFileItemFactory factory = new DiskFileItemFactory();
				factory.setSizeThreshold(WebConfiguration.getFilesizeThreshold());
				factory.setRepository(new File(System
						.getProperty("java.io.tmpdir")));

				ServletFileUpload upload = new ServletFileUpload(factory);
				upload.setFileSizeMax(WebConfiguration.getMaxFilesize());
				upload.setSizeMax(WebConfiguration.getMaxRequestsize());

				// constructs the directory path to store upload file
				String uploadPath = getServletContext().getRealPath("")
						+ File.separator + UPLOAD_DIRECTORY;
				// creates the directory if it does not exist
				File uploadDir = new File(uploadPath);
				if (!uploadDir.exists()) {
					uploadDir.mkdir();
				}

				List<?> formItems = upload.parseRequest(request);
				logger.debug(formItems.size() + " Items in form data");
				if (formItems.size() < 1) {
					// No Uploaded data!
					// Try do get
					// No Uploaded data!
					if (PdfAsParameterExtractor.getPdfUrl(request) != null) {
						doGet(request, response);
						return;
					} else {
						throw new PdfAsWebException(
								"No Signature data defined!");
					}
				} else {
					for(int i = 0; i < formItems.size(); i++) {
						Object obj = formItems.get(i);
						if(obj instanceof FileItem) {
							FileItem item = (FileItem) obj;
							if(item.getFieldName().equals(UPLOAD_PDF_DATA)) {
								filecontent = item.get();
								try {
									File f = new File(item.getName());
									String name = f.getName();
									logger.debug("Got upload: " + item.getName());
									if(name != null) {
										if(!(name.endsWith(".pdf") || name.endsWith(".PDF"))) {
											name += ".pdf";
										}
										
										logger.debug("Setting Filename in session: " + name);
										PdfAsHelper.setPDFFileName(request, name);
									}
								}
								catch(Throwable e) {
									logger.warn("In resolving filename", e);
								}
								if(filecontent.length < 10) {
									filecontent = null;
								} else {
									logger.debug("Found pdf Data! Size: " + filecontent.length);
								}
							} else {
								request.setAttribute(item.getFieldName(), item.getString());
								logger.debug("Setting " + item.getFieldName() + " = " + item.getString());
							}
						} else {
							logger.debug(obj.getClass().getName() +  " - " + obj.toString());
						}
					}
				}
			}
			
			if(filecontent == null) {
				if (PdfAsParameterExtractor.getPdfUrl(request) != null) {
					filecontent = RemotePDFFetcher.fetchPdfFile(PdfAsParameterExtractor.getPdfUrl(request));
				}
			}

			if(filecontent == null) {
				Object sourceObj = request.getAttribute("source");
				if(sourceObj != null) {
					String source = sourceObj.toString();
					if(source.equals("internal")) {
						request.setAttribute("FILEERR", true);
						request.getRequestDispatcher("index.jsp").forward(request, response);
						
						statisticEvent.setStatus(Status.ERROR);
						statisticEvent.setException(new Exception("No file uploaded"));
						statisticEvent.setEndNow();
						statisticEvent.setTimestampNow();
						StatisticFrontend.getInstance().storeEvent(statisticEvent);
						statisticEvent.setLogged(true);
						
						return;
					}
				}
				throw new PdfAsException("No Signature data available");
			}
			
			doSignature(request, response, filecontent, statisticEvent);
		} catch (Exception e) {
			logger.error("Signature failed", e);
			statisticEvent.setStatus(Status.ERROR);
			statisticEvent.setException(e);
			if(e instanceof PDFASError) {
				statisticEvent.setErrorCode(((PDFASError)e).getCode());
			}
			statisticEvent.setEndNow();
			statisticEvent.setTimestampNow();
			StatisticFrontend.getInstance().storeEvent(statisticEvent);
			statisticEvent.setLogged(true);
			
			PdfAsHelper.setSessionException(request, response, e.getMessage(),
					e);
			PdfAsHelper.gotoError(getServletContext(), request, response);
		}
	}

	protected void doSignature(HttpServletRequest request,
			HttpServletResponse response, byte[] pdfData, StatisticEvent statisticEvent) throws Exception {
		if(pdfData == null) {
			throw new PdfAsException("No Signature data available");
		}
		
		if(pdfData[0] != 0x25 || pdfData[1] != 0x50 || pdfData[2] != 0x44 || pdfData[3] != 0x46) {
			throw new PdfAsWebException(
					"Received data is not a valid PDF-Document");
		}
		
		// Get Connector
		String connector = PdfAsParameterExtractor.getConnector(request);
		
		String transactionId = PdfAsParameterExtractor.getTransactionId(request);
		
		statisticEvent.setFilesize(pdfData.length);
		statisticEvent.setProfileId(null);
		statisticEvent.setDevice(connector);

		String invokeUrl = PdfAsParameterExtractor.getInvokeURL(request);
		PdfAsHelper.setInvokeURL(request, response, invokeUrl);
		
		SignatureVerificationLevel lvl = PdfAsParameterExtractor.getVerificationLevel(request);
		PdfAsHelper.setVerificationLevel(request, lvl);
		
		String qrcodeContent = PdfAsParameterExtractor.getQRCodeContent(request);
		PdfAsHelper.setQRCodeContent(request, qrcodeContent);
		
		String invokeTarget = PdfAsParameterExtractor.getInvokeTarget(request);
		PdfAsHelper.setInvokeTarget(request, response, invokeTarget);
		
		String errorUrl = PdfAsParameterExtractor.getInvokeErrorURL(request);
		PdfAsHelper.setErrorURL(request, response, errorUrl);
		
		String locale = PdfAsParameterExtractor.getLocale(request);
		PdfAsHelper.setLocale(request, response, locale);

		//read and set placholder web id
		String placeholder_id = PdfAsParameterExtractor.getPlaceholderId(request);
		PlaceholderWebConfiguration.setValue(IConfigurationConstants.PLACEHOLDER_WEB_ID, placeholder_id);
		
		String filename = PdfAsParameterExtractor.getFilename(request);
		if(filename != null) {
			logger.debug("Setting Filename in session: " + filename);
			PdfAsHelper.setPDFFileName(request, filename);
		}
		
		String pdfDataHash = DigestHelper.getHexEncodedHash(pdfData);
		
		PdfAsHelper.setSignatureDataHash(request, pdfDataHash);
		logger.debug("Storing signatures data hash: " + pdfDataHash);
		
		logger.debug("Starting signature creation with: " + connector);
		
		//IPlainSigner signer;
		if (connector.equals("bku") || connector.equals("onlinebku") || connector.equals("mobilebku")
				|| connector.equals("sl20")) {
			// start asynchronous signature creation
			
			if(connector.equals("bku")) {
				if(WebConfiguration.getLocalBKUURL() == null) {
					throw new PdfAsWebException("Invalid connector bku is not supported");
				}
			}
			if(connector.equals("mobilebku")) {
				if(WebConfiguration.getHandyBKUURL() == null) {
					throw new PdfAsWebException("Invalid connector mobilebku is not supported");
				}
			}
			if(connector.equals("onlinebku")) {
				if(WebConfiguration.getOnlineBKUURL() == null) {
					throw new PdfAsWebException("Invalid connector bku is not supported");
				}
			}
			if (connector.equals("sl20")) {
				if(WebConfiguration.getSecurityLayer20URL() == null) {
					throw new PdfAsWebException("Invalid connector bku is not supported");
				}
			}

			PdfAsHelper.setStatisticEvent(request, response, statisticEvent);
			
			PdfAsHelper.startSignature(request, response, getServletContext(), pdfData, connector, 
					PdfAsHelper.buildPosString(request, response), transactionId, PdfAsParameterExtractor
					.getSigType(request), PdfAsParameterExtractor.getPreProcessorMap(request), 
					PdfAsParameterExtractor.getOverwriteMap(request));
			return;
		} else if (connector.equals("jks") || connector.equals("moa")) {
			// start synchronous siganture creation
			
			if(connector.equals("jks")) {
				
				String keyIdentifier = PdfAsParameterExtractor.getKeyIdentifier(request);

				boolean ksEnabled = false;

				if (keyIdentifier != null) {
					ksEnabled = WebConfiguration.getKeystoreEnabled(keyIdentifier);
				} else {
					ksEnabled = WebConfiguration.getKeystoreDefaultEnabled();
				}

				if (!ksEnabled) {
					if(keyIdentifier != null) {
						throw new PdfAsWebException("JKS connector [" + keyIdentifier + "] disabled or not existing.");
					} else {
						throw new PdfAsWebException("DEFAULT JKS connector disabled.");
					}
				}
			}
			
			if(connector.equals("moa")) {
				if(!WebConfiguration.getMOASSEnabled()) {
					throw new PdfAsWebException("Invalid connector moa is not supported");
				}
			}

			byte[] pdfSignedData = PdfAsHelper.synchornousSignature(request,
					response, pdfData);
			PdfAsHelper.setSignedPdf(request, response, pdfSignedData);
			
			statisticEvent.setStatus(Status.OK);
			statisticEvent.setEndNow();
			statisticEvent.setTimestampNow();
			StatisticFrontend.getInstance().storeEvent(statisticEvent);
			statisticEvent.setLogged(true);
			
			PdfAsHelper.gotoProvidePdf(getServletContext(), request, response);
			return;
		} else {
			throw new PdfAsWebException("Invalid connector (bku | moa | jks)");
		}
	}
}
