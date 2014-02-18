package at.gv.egiz.pdfas.web.helper;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBElement;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.lib.api.ByteArrayDataSink;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSource;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.DataSink;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.StatusRequest;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.sign.SignResult;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.sigs.pades.PAdESSigner;
import at.gv.egiz.pdfas.sigs.pkcs7detached.PKCS7DetachedSigner;
import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.exception.PdfAsWebException;
import at.gv.egiz.sl.CreateCMSSignatureRequestType;
import at.gv.egiz.sl.CreateCMSSignatureResponseType;
import at.gv.egiz.sl.InfoboxAssocArrayPairType;
import at.gv.egiz.sl.InfoboxReadRequestType;
import at.gv.egiz.sl.InfoboxReadResponseType;
import at.gv.egiz.sl.ObjectFactory;
import at.gv.egiz.sl.util.BKUSLConnector;
import at.gv.egiz.sl.util.MOAConnector;
import at.gv.egiz.sl.util.SLMarschaller;

public class PdfAsHelper {

	private static final String PDF_CONFIG = "PDF_CONFIG";
	private static final String PDF_STATUS = "PDF_STATUS";
	private static final String PDF_SL_CONNECTOR = "PDF_SL_CONNECTOR";
	private static final String PDF_SIGNER = "PDF_SIGNER";
	private static final String PDF_SL_INTERACTIVE = "PDF_SL_INTERACTIVE";
	private static final String PDF_SIGNED_DATA = "PDF_SIGNED_DATA";
	private static final String PDF_ERR_MESSAGE = "PDF_ERR_MESSAGE";
	private static final String PDF_ERR_THROWABLE = "PDF_ERR_THROWABLE";
	private static final String PDF_ERROR_PAGE = "/ErrorPage";
	private static final String PDF_PROVIDE_PAGE = "/ProvidePDF";
	private static final String PDF_PDFDATA_PAGE = "/PDFData";
	private static final String PDF_DATAURL_PAGE = "/DataURL";
	private static final String PDF_ERR_URL = "PDF_ERR_URL";
	private static final String PDF_FILE_NAME = "PDF_FILE_NAME";
	private static final String PDF_INVOKE_URL = "PDF_INVOKE_URL";
	private static final String REQUEST_FROM_DU = "REQ_DATA_URL";
	private static final String SIGNATURE_DATA_HASH = "SIGNATURE_DATA_HASH";
	private static final String SIGNATURE_ACTIVE = "SIGNATURE_ACTIVE";
	private static final String VERIFICATION_RESULT = "VERIFICATION_RESULT";

	private static final Logger logger = LoggerFactory
			.getLogger(PdfAsHelper.class);

	private static PdfAs pdfAs;
	private static ObjectFactory of = new ObjectFactory();

	static {
		logger.debug("Creating PDF-AS");
		pdfAs = PdfAsFactory.createPdfAs(new File(WebConfiguration
				.getPdfASDir()));
		logger.debug("Creating PDF-AS done");
	}

	public static void init() {
		logger.debug("PDF-AS Helper initialized");
	}

	private static void validatePdfSize(HttpServletRequest request,
			HttpServletResponse response, byte[] pdfData)
			throws PdfAsWebException {
		// Try to check num-bytes
		String pdfSizeString = PdfAsParameterExtractor.getNumBytes(request);
		if (pdfSizeString != null) {
			long pdfSize = -1;
			try {
				pdfSize = Long.parseLong(pdfSizeString);
			} catch (NumberFormatException e) {
				throw new PdfAsWebException(
						PdfAsParameterExtractor.PARAM_NUM_BYTES
								+ " parameter has to be a positiv number!", e);
			}
			if (pdfSize <= 0) {
				throw new PdfAsWebException(
						"Invalid PDF Size! Has to bigger than zero!");
			}

			if (pdfData.length != pdfSize) {
				throw new PdfAsWebException("Signature Data Size and "
						+ PdfAsParameterExtractor.PARAM_NUM_BYTES
						+ " missmatch!");
			}
		}
	}

	private static String buildPosString(HttpServletRequest request,
			HttpServletResponse response) throws PdfAsWebException {
		String posP = PdfAsParameterExtractor.getSigPosP(request);
		String posX = PdfAsParameterExtractor.getSigPosX(request);
		String posY = PdfAsParameterExtractor.getSigPosY(request);
		String posW = PdfAsParameterExtractor.getSigPosW(request);

		if (posP == null && posW == null && posX == null && posY == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		if (posX != null) {
			try {
				Float.parseFloat(posX);
			} catch (NumberFormatException e) {
				throw new PdfAsWebException(
						PdfAsParameterExtractor.PARAM_SIG_POS_X
								+ " has invalid value!", e);
			}
			sb.append("x:" + posX.trim() + ";");
		} else {
			sb.append("x:auto;");
		}

		if (posY != null) {
			try {
				Float.parseFloat(posY);
			} catch (NumberFormatException e) {
				throw new PdfAsWebException(
						PdfAsParameterExtractor.PARAM_SIG_POS_Y
								+ " has invalid value!", e);
			}
			sb.append("y:" + posY.trim() + ";");
		} else {
			sb.append("y:auto;");
		}

		if (posW != null) {
			try {
				Float.parseFloat(posW);
			} catch (NumberFormatException e) {
				throw new PdfAsWebException(
						PdfAsParameterExtractor.PARAM_SIG_POS_W
								+ " has invalid value!", e);
			}
			sb.append("w:" + posW.trim() + ";");
		} else {
			sb.append("w:auto;");
		}

		if (posP != null) {
			if (!(posP.equals("auto") || posP.equals("new"))) {
				try {
					Integer.parseInt(posP);
				} catch (NumberFormatException e) {
					throw new PdfAsWebException(
							PdfAsParameterExtractor.PARAM_SIG_POS_P
									+ " has invalid value! (auto | new )");
				}
			}
			sb.append("p:" + posP.trim() + ";");
		} else {
			sb.append("p:auto;");
		}

		return sb.toString();
	}

	public static List<VerifyResult> synchornousVerify(
			HttpServletRequest request, HttpServletResponse response,
			byte[] pdfData) throws Exception {
		String signidxString = PdfAsParameterExtractor.getSigIdx(request);
		int signIdx = -1;
		if (signidxString != null) {
			try {
				signIdx = Integer.parseInt(signidxString);
			} catch (Throwable e) {
				logger.error("Failed to parse Signature Index: "
						+ signidxString);
			}
		}

		logger.error("Verifing Signature index: " + signIdx);

		Configuration config = pdfAs.getConfiguration();

		ByteArrayDataSource dataSource = new ByteArrayDataSource(pdfData);

		VerifyParameter verifyParameter = PdfAsFactory.createVerifyParameter(
				config, dataSource);

		verifyParameter.setDataSource(dataSource);
		verifyParameter.setConfiguration(config);
		verifyParameter.setWhichSignature(signIdx);

		List<VerifyResult> results = pdfAs.verify(verifyParameter);

		return results;
	}

	/**
	 * Create synchronous PDF Signature
	 * 
	 * @param request
	 *            The Web request
	 * @param response
	 *            The Web response
	 * @param pdfData
	 *            The pdf data
	 * @return The signed pdf data
	 * @throws Exception
	 */
	public static byte[] synchornousSignature(HttpServletRequest request,
			HttpServletResponse response, byte[] pdfData) throws Exception {
		validatePdfSize(request, response, pdfData);

		Configuration config = pdfAs.getConfiguration();

		// Generate Sign Parameter
		SignParameter signParameter = PdfAsFactory.createSignParameter(config,
				new ByteArrayDataSource(pdfData));

		// Get Connector
		String connector = PdfAsParameterExtractor.getConnector(request);

		if (!connector.equals("moa") && !connector.equals("jks")) {
			throw new PdfAsWebException("Invalid connector (moa | jks)");
		}

		IPlainSigner signer;
		if (connector.equals("moa")) {
			signer = new PAdESSigner(new MOAConnector(config));
		} else {
			signer = new PKCS7DetachedSigner(
					WebConfiguration.getKeystoreFile(),
					WebConfiguration.getKeystoreAlias(),
					WebConfiguration.getKeystorePass(),
					WebConfiguration.getKeystoreKeyPass(),
					WebConfiguration.getKeystoreType());
		}

		signParameter.setPlainSigner(signer);

		// set Signature Profile (null use default ...)
		signParameter.setSignatureProfileId(PdfAsParameterExtractor
				.getSigType(request));

		ByteArrayDataSink output = new ByteArrayDataSink();
		signParameter.setOutput(output);

		// set Signature Position
		signParameter.setSignaturePosition(buildPosString(request, response));

		pdfAs.sign(signParameter);

		return output.getData();
	}

	public static void startSignature(HttpServletRequest request,
			HttpServletResponse response, ServletContext context, byte[] pdfData)
			throws Exception {

		// TODO: Protect session so that only one PDF can be signed during one
		// session
		/*
		 * if(PdfAsHelper.isSignatureActive(request)) { throw new
		 * PdfAsException("Signature is active in this session"); }
		 * 
		 * PdfAsHelper.setSignatureActive(request, true);
		 */

		validatePdfSize(request, response, pdfData);

		HttpSession session = request.getSession();

		logger.info("Starting signature in session: " + session.getId());

		Configuration config = pdfAs.getConfiguration();
		session.setAttribute(PDF_CONFIG, config);

		// Generate Sign Parameter
		SignParameter signParameter = PdfAsFactory.createSignParameter(config,
				new ByteArrayDataSource(pdfData));

		// Get Connector
		String connector = PdfAsParameterExtractor.getConnector(request);

		IPlainSigner signer;
		if (connector.equals("bku") || connector.equals("onlinebku")
				|| connector.equals("mobilebku")) {
			BKUSLConnector conn = new BKUSLConnector(config);
			signer = new PAdESSigner(conn);
			session.setAttribute(PDF_SL_CONNECTOR, conn);
		} else {
			throw new PdfAsWebException(
					"Invalid connector (bku | onlinebku | mobilebku | moa | jks)");
		}

		signParameter.setPlainSigner(signer);
		session.setAttribute(PDF_SIGNER, signer);
		session.setAttribute(PDF_SL_INTERACTIVE, connector);

		// set Signature Profile (null use default ...)
		signParameter.setSignatureProfileId(PdfAsParameterExtractor
				.getSigType(request));

		ByteArrayDataSink dataSink = new ByteArrayDataSink();
		signParameter.setOutput(dataSink);

		// set Signature Position
		signParameter.setSignaturePosition(buildPosString(request, response));

		StatusRequest statusRequest = pdfAs.startSign(signParameter);
		session.setAttribute(PDF_STATUS, statusRequest);

		PdfAsHelper.process(request, response, context);
	}

	private static byte[] getCertificate(
			InfoboxReadResponseType infoboxReadResponseType) {
		byte[] data = null;
		if (infoboxReadResponseType.getAssocArrayData() != null) {
			List<InfoboxAssocArrayPairType> pairs = infoboxReadResponseType
					.getAssocArrayData().getPair();
			Iterator<InfoboxAssocArrayPairType> pairIterator = pairs.iterator();
			while (pairIterator.hasNext()) {
				InfoboxAssocArrayPairType pair = pairIterator.next();
				if (pair.getKey().equals("SecureSignatureKeypair")) {
					return pair.getBase64Content();
				}
			}
		}
		// SecureSignatureKeypair

		return data;
	}

	public static void injectCertificate(HttpServletRequest request,
			HttpServletResponse response,
			InfoboxReadResponseType infoboxReadResponseType,
			ServletContext context) throws Exception {

		HttpSession session = request.getSession();
		StatusRequest statusRequest = (StatusRequest) session
				.getAttribute(PDF_STATUS);

		if (statusRequest == null) {
			throw new PdfAsWebException("No Signature running in session:"
					+ session.getId());
		}

		statusRequest.setCertificate(getCertificate(infoboxReadResponseType));
		statusRequest = pdfAs.process(statusRequest);
		session.setAttribute(PDF_STATUS, statusRequest);

		PdfAsHelper.process(request, response, context);
	}

	public static void injectSignature(HttpServletRequest request,
			HttpServletResponse response,
			CreateCMSSignatureResponseType createCMSSignatureResponseType,
			ServletContext context) throws Exception {

		logger.info("Got CMS Signature Response");

		HttpSession session = request.getSession();
		StatusRequest statusRequest = (StatusRequest) session
				.getAttribute(PDF_STATUS);

		if (statusRequest == null) {
			throw new PdfAsWebException("No Signature running in session:"
					+ session.getId());
		}

		statusRequest.setSigature(createCMSSignatureResponseType
				.getCMSSignature());
		statusRequest = pdfAs.process(statusRequest);
		session.setAttribute(PDF_STATUS, statusRequest);

		PdfAsHelper.process(request, response, context);
	}

	public static void logAccess(HttpServletRequest request) {
		HttpSession session = request.getSession();
		logger.debug("Access to " + request.getServletPath() + " in Session: "
				+ session.getId());
	}

	public static void process(HttpServletRequest request,
			HttpServletResponse response, ServletContext context)
			throws Exception {

		HttpSession session = request.getSession();
		StatusRequest statusRequest = (StatusRequest) session
				.getAttribute(PDF_STATUS);
		IPlainSigner plainSigner = (IPlainSigner) session
				.getAttribute(PDF_SIGNER);

		String connector = (String) session.getAttribute(PDF_SL_INTERACTIVE);

		if (connector.equals("bku") || connector.equals("onlinebku")
				|| connector.equals("mobilebku")) {
			BKUSLConnector bkuSLConnector = (BKUSLConnector) session
					.getAttribute(PDF_SL_CONNECTOR);

			// TODO Handle logic for BKU interaction

			Configuration config = (Configuration) session
					.getAttribute(PDF_CONFIG);

			if (statusRequest.needCertificate()) {
				logger.info("Needing Certificate from BKU");
				// build SL Request to read certificate
				InfoboxReadRequestType readCertificateRequest = bkuSLConnector
						.createInfoboxReadRequest();

				JAXBElement<InfoboxReadRequestType> readRequest = of
						.createInfoboxReadRequest(readCertificateRequest);

				String url = generateDataURL(request, response);
				String slRequest = SLMarschaller.marshalToString(readRequest);
				String template = getTemplateSL();
				template = template.replace("##BKU##",
						generateBKUURL(connector));
				template = template.replace("##XMLRequest##",
						StringEscapeUtils.escapeHtml4(slRequest));
				template = template.replace("##DataURL##", url);
				response.getWriter().write(template);
				response.getWriter().close();
			} else if (statusRequest.needSignature()) {
				logger.info("Needing Signature from BKU");
				// build SL Request for cms signature
				CreateCMSSignatureRequestType createCMSSignatureRequestType = bkuSLConnector
						.createCMSRequest(statusRequest.getSignatureData(),
								statusRequest.getSignatureDataByteRange());

				String slRequest = SLMarschaller
						.marshalToString(of
								.createCreateCMSSignatureRequest(createCMSSignatureRequestType));

				response.setContentType("text/xml");
				response.getWriter().write(slRequest);
				response.getWriter().close();

			} else if (statusRequest.isReady()) {
				// TODO: store pdf document redirect to Finish URL
				logger.info("Document ready!");

				SignResult result = pdfAs.finishSign(statusRequest);
				DataSink output = result.getOutputDocument();
				if (output instanceof ByteArrayDataSink) {
					ByteArrayDataSink byteDataSink = (ByteArrayDataSink) output;
					PdfAsHelper.setSignedPdf(request, response,
							byteDataSink.getData());
					PdfAsHelper.gotoProvidePdf(context, request, response);
				} else {
					// TODO: no signature data available!
				}

			} else {
				// TODO: invalid state
			}
		} else {
			// TODO Handle logic for
		}
	}

	private static String getTemplateSL() throws IOException {
		String xml = FileUtils.readFileToString(FileUtils
				.toFile(PdfAsHelper.class.getResource("/template_sl.html")));
		return xml;
	}

	public static String getErrorRedirectTemplateSL() throws IOException {
		String xml = FileUtils.readFileToString(FileUtils
				.toFile(PdfAsHelper.class
						.getResource("/template_error_redirect.html")));
		return xml;
	}

	public static String getProvideTemplate() throws IOException {
		String xml = FileUtils
				.readFileToString(FileUtils.toFile(PdfAsHelper.class
						.getResource("/template_provide.html")));
		return xml;
	}

	public static String getErrorTemplate() throws IOException {
		String xml = FileUtils.readFileToString(FileUtils
				.toFile(PdfAsHelper.class.getResource("/template_error.html")));
		return xml;
	}

	public static String getInvokeRedirectTemplateSL() throws IOException {
		String xml = FileUtils.readFileToString(FileUtils
				.toFile(PdfAsHelper.class
						.getResource("/template_invoke_redirect.html")));
		return xml;
	}

	public static byte[] getSignedPdf(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		Object signedData = session.getAttribute(PDF_SIGNED_DATA);
		if (signedData == null) {
			return null;
		}

		if (signedData instanceof byte[]) {
			return (byte[]) signedData;
		}
		logger.warn("PDF_SIGNED_DATA in session is not a byte[] type!");
		return null;
	}

	public static void setSignedPdf(HttpServletRequest request,
			HttpServletResponse response, byte[] signedData) {
		HttpSession session = request.getSession();
		session.setAttribute(PDF_SIGNED_DATA, signedData);
	}

	public static void setSessionException(HttpServletRequest request,
			HttpServletResponse response, String message, Throwable e) {
		HttpSession session = request.getSession();
		session.setAttribute(PDF_ERR_MESSAGE, message);
		session.setAttribute(PDF_ERR_THROWABLE, e);
	}

	public static String getSessionErrMessage(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute(PDF_ERR_MESSAGE);
		return obj == null ? null : obj.toString();
	}

	public static Throwable getSessionException(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute(PDF_ERR_THROWABLE);
		if (obj == null) {
			return null;
		}

		if (obj instanceof Throwable) {
			return (Throwable) obj;
		}
		logger.warn("PDF_ERR_THROWABLE in session is not a throwable type!");
		return null;
	}

	public static void gotoError(ServletContext context,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (PdfAsHelper.getFromDataUrl(request)) {
			response.sendRedirect(generateErrorURL(request, response));
		} else {
			RequestDispatcher dispatcher = context
					.getRequestDispatcher(PDF_ERROR_PAGE);
			dispatcher.forward(request, response);
		}
	}

	public static void gotoProvidePdf(ServletContext context,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (PdfAsHelper.getFromDataUrl(request)) {
			response.sendRedirect(generateProvideURL(request, response));
		} else {
			RequestDispatcher dispatcher = context
					.getRequestDispatcher(PDF_PROVIDE_PAGE);
			dispatcher.forward(request, response);
		}
	}

	public static void setErrorURL(HttpServletRequest request,
			HttpServletResponse response, String url) {
		HttpSession session = request.getSession();
		session.setAttribute(PDF_ERR_URL, url);
	}

	public static String getErrorURL(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute(PDF_ERR_URL);
		return obj == null ? null : obj.toString();
	}

	public static void setInvokeURL(HttpServletRequest request,
			HttpServletResponse response, String url) {
		HttpSession session = request.getSession();
		session.setAttribute(PDF_INVOKE_URL, url);
		logger.info("External Invoke URL: " + url);
	}

	public static String getInvokeURL(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute(PDF_INVOKE_URL);
		return obj == null ? null : obj.toString();
	}

	private static String generateURL(HttpServletRequest request,
			HttpServletResponse response, String Servlet) {
		HttpSession session = request.getSession();
		String publicURL = WebConfiguration.getPublicURL();
		String dataURL = null;
		if (publicURL != null) {
			dataURL = publicURL + Servlet + ";jsessionid=" + session.getId();
		} else {
			if ((request.getScheme().equals("http") && request.getServerPort() == 80)
					|| (request.getScheme().equals("https") && request
							.getServerPort() == 443)) {
				dataURL = request.getScheme() + "://" + request.getServerName()
						+ request.getContextPath() + Servlet + ";jsessionid="
						+ session.getId();
			} else {
				dataURL = request.getScheme() + "://" + request.getServerName()
						+ ":" + request.getServerPort()
						+ request.getContextPath() + Servlet + ";jsessionid="
						+ session.getId();
			}
		}
		logger.info("Generated URL: " + dataURL);
		return dataURL;
	}

	public static void regenerateSession(HttpServletRequest request) {
		request.getSession(false).invalidate();
		request.getSession(true);
	}

	public static String generateDataURL(HttpServletRequest request,
			HttpServletResponse response) {
		return generateURL(request, response, PDF_DATAURL_PAGE);
	}

	public static String generateProvideURL(HttpServletRequest request,
			HttpServletResponse response) {
		return generateURL(request, response, PDF_PROVIDE_PAGE);
	}

	public static String generateErrorURL(HttpServletRequest request,
			HttpServletResponse response) {
		return generateURL(request, response, PDF_ERROR_PAGE);
	}

	public static String generatePdfURL(HttpServletRequest request,
			HttpServletResponse response) {
		return generateURL(request, response, PDF_PDFDATA_PAGE);
	}

	public static String generateBKUURL(String connector) {
		if (connector.equals("bku")) {
			return WebConfiguration.getLocalBKUURL();
		} else if (connector.equals("onlinebku")) {
			return WebConfiguration.getOnlineBKUURL();
		} else if (connector.equals("mobilebku")) {
			return WebConfiguration.getHandyBKUURL();
		}
		return WebConfiguration.getLocalBKUURL();
	}

	public static void setFromDataUrl(HttpServletRequest request) {
		request.setAttribute(REQUEST_FROM_DU, (Boolean) true);
	}

	public static boolean getFromDataUrl(HttpServletRequest request) {
		Object obj = request.getAttribute(REQUEST_FROM_DU);
		if (obj != null) {
			if (obj instanceof Boolean) {
				return ((Boolean) obj).booleanValue();
			}
		}
		return false;
	}

	public static void setSignatureDataHash(HttpServletRequest request,
			String value) {
		HttpSession session = request.getSession();
		session.setAttribute(SIGNATURE_DATA_HASH, value);
	}

	public static String getSignatureDataHash(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute(SIGNATURE_DATA_HASH);
		if (obj != null) {
			return obj.toString();
		}
		return "";
	}

	public static void setPDFFileName(HttpServletRequest request, String value) {
		HttpSession session = request.getSession();
		session.setAttribute(PDF_FILE_NAME, value);
	}

	public static String getPDFFileName(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute(PDF_FILE_NAME);
		if (obj != null) {
			return obj.toString();
		}
		return "document.pdf";
	}

	public static void setVerificationResult(HttpServletRequest request,
			List<VerifyResult> value) {
		HttpSession session = request.getSession();
		session.setAttribute(VERIFICATION_RESULT, value);
	}

	public static List<VerifyResult> getVerificationResult(
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute(VERIFICATION_RESULT);
		if (obj != null) {
			try {
				return (List<VerifyResult>) obj;
			} catch (Throwable e) {
				logger.error("Invalid object type");
			}
		}
		return null;
	}

	public static void setSignatureActive(HttpServletRequest request,
			boolean value) {
		HttpSession session = request.getSession();
		session.setAttribute(SIGNATURE_ACTIVE, new Boolean(value));
	}

	public static boolean isSignatureActive(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute(SIGNATURE_ACTIVE);
		if (obj != null) {
			if (obj instanceof Boolean) {
				return ((Boolean) obj).booleanValue();
			}
		}
		return false;
	}
}
