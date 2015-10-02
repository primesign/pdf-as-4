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

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter.SignatureVerificationLevel;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.web.exception.PdfAsWebException;
import at.gv.egiz.pdfas.web.filter.UserAgentFilter;
import at.gv.egiz.pdfas.web.helper.PdfAsHelper;
import at.gv.egiz.pdfas.web.helper.PdfAsParameterExtractor;
import at.gv.egiz.pdfas.web.helper.RemotePDFFetcher;
import at.gv.egiz.pdfas.web.helper.VerifyEncoder;
import at.gv.egiz.pdfas.web.helper.VerifyResultEncoder;
import at.gv.egiz.pdfas.web.stats.StatisticEvent;
import at.gv.egiz.pdfas.web.stats.StatisticFrontend;
import at.gv.egiz.pdfas.web.stats.StatisticEvent.Operation;
import at.gv.egiz.pdfas.web.stats.StatisticEvent.Source;
import at.gv.egiz.pdfas.web.stats.StatisticEvent.Status;

/**
 * Servlet implementation class VerifyServlet
 */
public class VerifyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory
			.getLogger(VerifyServlet.class);

	private static final String UPLOAD_PDF_DATA = "pdf-file";
	private static final String UPLOAD_DIRECTORY = "upload";
	private static final int THRESHOLD_SIZE = 1024 * 1024 * 3; // 3MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public VerifyServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		logger.info("Get verify request");

		String errorUrl = PdfAsParameterExtractor.getInvokeErrorURL(request);
		PdfAsHelper.setErrorURL(request, response, errorUrl);
		
		StatisticEvent statisticEvent = new StatisticEvent();
		statisticEvent.setStartNow();
		statisticEvent.setSource(Source.WEB);
		statisticEvent.setOperation(Operation.VERIFY);
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
			doVerify(request, response, pdfData, statisticEvent);
		} catch (Throwable e) {
			
			statisticEvent.setStatus(Status.ERROR);
			statisticEvent.setException(e);
			if(e instanceof PDFASError) {
				statisticEvent.setErrorCode(((PDFASError)e).getCode());
			}
			statisticEvent.setEndNow();
			statisticEvent.setTimestampNow();
			StatisticFrontend.getInstance().storeEvent(statisticEvent);
			statisticEvent.setLogged(true);
			
			logger.warn("Generic Error: ", e);
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

		logger.info("Post verify request");

		String errorUrl = PdfAsParameterExtractor.getInvokeErrorURL(request);
		PdfAsHelper.setErrorURL(request, response, errorUrl);

		StatisticEvent statisticEvent = new StatisticEvent();
		statisticEvent.setStartNow();
		statisticEvent.setSource(Source.WEB);
		statisticEvent.setOperation(Operation.VERIFY);
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
				factory.setSizeThreshold(THRESHOLD_SIZE);
				factory.setRepository(new File(System
						.getProperty("java.io.tmpdir")));

				ServletFileUpload upload = new ServletFileUpload(factory);
				upload.setFileSizeMax(MAX_FILE_SIZE);
				upload.setSizeMax(MAX_REQUEST_SIZE);

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
					for (int i = 0; i < formItems.size(); i++) {
						Object obj = formItems.get(i);
						if (obj instanceof FileItem) {
							FileItem item = (FileItem) obj;
							if (item.getFieldName().equals(UPLOAD_PDF_DATA)) {
								filecontent = item.get();
								try {
									File f = new File(item.getName());
									String name = f.getName();
									logger.debug("Got upload: "
											+ item.getName());
									if (name != null) {
										if (!(name.endsWith(".pdf") || name
												.endsWith(".PDF"))) {
											name += ".pdf";
										}

										logger.debug("Setting Filename in session: "
												+ name);
										PdfAsHelper.setPDFFileName(request,
												name);
									}
								} catch (Throwable e) {
									logger.warn("In resolving filename", e);
								}
								if (filecontent.length < 10) {
									filecontent = null;
								} else {
									logger.debug("Found pdf Data! Size: "
											+ filecontent.length);
								}
							} else {
								request.setAttribute(item.getFieldName(),
										item.getString());
								logger.debug("Setting " + item.getFieldName()
										+ " = " + item.getString());
							}
						} else {
							logger.debug(obj.getClass().getName() + " - "
									+ obj.toString());
						}
					}
				}
			}

			if (filecontent == null) {
				if (PdfAsParameterExtractor.getPdfUrl(request) != null) {
					filecontent = RemotePDFFetcher
							.fetchPdfFile(PdfAsParameterExtractor
									.getPdfUrl(request));
				}
			}

			if (filecontent == null) {
				Object sourceObj = request.getAttribute("source");
				if (sourceObj != null) {
					String source = sourceObj.toString();
					if (source.equals("internal")) {
						request.setAttribute("FILEERR", true);
						request.getRequestDispatcher("index.jsp").forward(
								request, response);
						
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

			doVerify(request, response, filecontent, statisticEvent);
		} catch (Throwable e) {
			
			statisticEvent.setStatus(Status.ERROR);
			statisticEvent.setException(e);
			if(e instanceof PDFASError) {
				statisticEvent.setErrorCode(((PDFASError)e).getCode());
			}
			statisticEvent.setEndNow();
			statisticEvent.setTimestampNow();
			StatisticFrontend.getInstance().storeEvent(statisticEvent);
			statisticEvent.setLogged(true);
			
			logger.warn("Generic Error: ", e);
			PdfAsHelper.setSessionException(request, response, e.getMessage(),
					e);
			PdfAsHelper.gotoError(getServletContext(), request, response);
		}
	}

	protected void doVerify(HttpServletRequest request,
			HttpServletResponse response, byte[] pdfData, StatisticEvent statisticEvent) throws Exception {
		
		SignatureVerificationLevel lvl = PdfAsParameterExtractor
				.getVerificationLevel(request);
		PdfAsHelper.setVerificationLevel(request, lvl);
		
		String format = PdfAsParameterExtractor.getFormat(request);
		
		logger.debug("doVerify");
		logger.info("Starting verification of pdf dokument");
		
		logger.debug("Format: " + format);
		
		List<VerifyResult> results = PdfAsHelper.synchornousVerify(pdfData, -1, lvl, 
				PdfAsParameterExtractor.getPreProcessorMap(request));

		PdfAsHelper.setVerificationResult(request, results);
		
		// Create HTML Snippet for each Verification Result
		// Put these results into the web page
		// Or create a JSON response with the verification results for automated
		// processing
		
		VerifyResultEncoder encoder = VerifyEncoder.getEncoder(format);
		
		if(encoder == null) {
			encoder = VerifyEncoder.getEncoder(PdfAsParameterExtractor.PARAM_HTML);
		}
		
		statisticEvent.setStatus(Status.OK);
		statisticEvent.setEndNow();
		statisticEvent.setTimestampNow();
		StatisticFrontend.getInstance().storeEvent(statisticEvent);
		statisticEvent.setLogged(true);
		
		encoder.produce(request, response, results);
	}

}
