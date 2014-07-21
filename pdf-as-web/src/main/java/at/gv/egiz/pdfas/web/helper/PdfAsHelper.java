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
package at.gv.egiz.pdfas.web.helper;

import iaik.x509.X509Certificate;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBElement;
import javax.xml.ws.WebServiceException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.api.ws.PDFASSignParameters;
import at.gv.egiz.pdfas.api.ws.PDFASSignParameters.Connector;
import at.gv.egiz.pdfas.api.ws.PDFASSignResponse;
import at.gv.egiz.pdfas.api.ws.PDFASVerificationResponse;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
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
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter.SignatureVerificationLevel;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.sigs.pades.PAdESSigner;
import at.gv.egiz.pdfas.sigs.pades.PAdESSignerKeystore;
import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.exception.PdfAsWebException;
import at.gv.egiz.pdfas.web.servlets.UIEntryPointServlet;
import at.gv.egiz.sl.schema.CreateCMSSignatureResponseType;
import at.gv.egiz.sl.schema.InfoboxAssocArrayPairType;
import at.gv.egiz.sl.schema.InfoboxReadRequestType;
import at.gv.egiz.sl.schema.InfoboxReadResponseType;
import at.gv.egiz.sl.schema.ObjectFactory;
import at.gv.egiz.sl.util.BKUSLConnector;
import at.gv.egiz.sl.util.MOAConnector;
import at.gv.egiz.sl.util.RequestPackage;
import at.gv.egiz.sl.util.SLMarschaller;

public class PdfAsHelper {

	private static final String PDF_CONFIG = "PDF_CONFIG";
	private static final String PDF_STATUS = "PDF_STATUS";
	private static final String PDF_SL_CONNECTOR = "PDF_SL_CONNECTOR";
	private static final String PDF_SIGNER = "PDF_SIGNER";
	private static final String PDF_SL_INTERACTIVE = "PDF_SL_INTERACTIVE";
	private static final String PDF_SIGNED_DATA = "PDF_SIGNED_DATA";
	private static final String PDF_LOCALE = "PDF_LOCALE";
	private static final String PDF_ERR_MESSAGE = "PDF_ERR_MESSAGE";
	private static final String PDF_ERR_THROWABLE = "PDF_ERR_THROWABLE";
	private static final String PDF_ERROR_PAGE = "/ErrorPage";
	private static final String PDF_PROVIDE_PAGE = "/ProvidePDF";
	private static final String PDF_PDFDATA_PAGE = "/PDFData";
	private static final String PDF_DATAURL_PAGE = "/DataURL";
	private static final String PDF_USERENTRY_PAGE = "/userentry";
	private static final String PDF_ERR_URL = "PDF_ERR_URL";
	private static final String PDF_FILE_NAME = "PDF_FILE_NAME";
	private static final String PDF_SIGNER_CERT = "PDF_SIGNER_CERT";
	private static final String PDF_VER_LEVEL = "PDF_VER_LEVEL";
	private static final String PDF_VER_RESP = "PDF_VER_RESP";
	private static final String PDF_INVOKE_URL = "PDF_INVOKE_URL";
	private static final String PDF_INVOKE_TARGET = "PDF_INVOKE_TARGET";
	private static final String REQUEST_FROM_DU = "REQ_DATA_URL";
	private static final String SIGNATURE_DATA_HASH = "SIGNATURE_DATA_HASH";
	private static final String SIGNATURE_ACTIVE = "SIGNATURE_ACTIVE";
	private static final String VERIFICATION_RESULT = "VERIFICATION_RESULT";
	
	private static final Logger logger = LoggerFactory
			.getLogger(PdfAsHelper.class);

	private static PdfAs pdfAs;
	private static ObjectFactory of = new ObjectFactory();

	static {
		reloadConfig();
	}

	public static void init() {
		logger.info("PDF-AS Helper initialized");
	}

	public static synchronized void reloadConfig() {
		logger.info("Creating PDF-AS");
		pdfAs = PdfAsFactory.createPdfAs(new File(WebConfiguration
				.getPdfASDir()));
		logger.info("Creating PDF-AS done");
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

	public static String buildPosString(HttpServletRequest request,
			HttpServletResponse response) throws PdfAsWebException {
		String posP = PdfAsParameterExtractor.getSigPosP(request);
		String posX = PdfAsParameterExtractor.getSigPosX(request);
		String posY = PdfAsParameterExtractor.getSigPosY(request);
		String posW = PdfAsParameterExtractor.getSigPosW(request);
		String posR = PdfAsParameterExtractor.getSigPosR(request);
		String posF = PdfAsParameterExtractor.getSigPosF(request);

		if (posP == null && posW == null && posX == null && posY == null
				&& posR == null && posF == null) {
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

		if (posR != null) {
			try {
				Float.parseFloat(posR);
			} catch (NumberFormatException e) {
				throw new PdfAsWebException(
						PdfAsParameterExtractor.PARAM_SIG_POS_R
								+ " has invalid value!", e);
			}
			sb.append("r:" + posR.trim() + ";");
		} else {
			sb.append("r:0;");
		}

		if (posF != null) {
			try {
				Float.parseFloat(posF);
			} catch (NumberFormatException e) {
				throw new PdfAsWebException(
						PdfAsParameterExtractor.PARAM_SIG_POS_F
								+ " has invalid value!", e);
			}
			sb.append("f:" + posF.trim() + ";");
		} else {
			sb.append("f:0;");
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

		logger.debug("Verifing Signature index: " + signIdx);

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

	public static List<VerifyResult> synchornousVerify(byte[] pdfData,
			int signIdx, SignatureVerificationLevel lvl) throws Exception {
		logger.debug("Verifing Signature index: " + signIdx);

		Configuration config = pdfAs.getConfiguration();

		ByteArrayDataSource dataSource = new ByteArrayDataSource(pdfData);

		VerifyParameter verifyParameter = PdfAsFactory.createVerifyParameter(
				config, dataSource);

		verifyParameter.setSignatureVerificationLevel(lvl);
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
		} else if (connector.equals("jks")) {
			signer = new PAdESSignerKeystore(
					WebConfiguration.getKeystoreFile(),
					WebConfiguration.getKeystoreAlias(),
					WebConfiguration.getKeystorePass(),
					WebConfiguration.getKeystoreKeyPass(),
					WebConfiguration.getKeystoreType());
		} else {
			throw new PdfAsWebException("Invalid connector (moa | jks)");
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
	public static PDFASSignResponse synchornousServerSignature(byte[] pdfData,
			PDFASSignParameters params) throws Exception {
		Configuration config = pdfAs.getConfiguration();

		// Generate Sign Parameter
		SignParameter signParameter = PdfAsFactory.createSignParameter(config,
				new ByteArrayDataSource(pdfData));

		// Get Connector

		IPlainSigner signer;
		if (params.getConnector().equals(Connector.MOA)) {
			if (!WebConfiguration.getMOASSEnabled()) {
				throw new PdfAsWebException("MOA connector disabled.");
			}
			signer = new PAdESSigner(new MOAConnector(config));
		} else if (params.getConnector().equals(Connector.JKS)) {
			if (!WebConfiguration.getKeystoreEnabled()) {
				throw new PdfAsWebException("JKS connector disabled.");
			}
			signer = new PAdESSignerKeystore(
					WebConfiguration.getKeystoreFile(),
					WebConfiguration.getKeystoreAlias(),
					WebConfiguration.getKeystorePass(),
					WebConfiguration.getKeystoreKeyPass(),
					WebConfiguration.getKeystoreType());
		} else {
			throw new PdfAsWebException("Invalid connector (moa | jks)");
		}

		signParameter.setPlainSigner(signer);

		// set Signature Profile (null use default ...)
		signParameter.setSignatureProfileId(params.getProfile());

		ByteArrayDataSink output = new ByteArrayDataSink();
		signParameter.setOutput(output);

		// set Signature Position
		signParameter.setSignaturePosition(params.getPosition());

		SignResult signResult = pdfAs.sign(signParameter);

		PDFASSignResponse signResponse = new PDFASSignResponse();
		signResponse.setSignedPDF(output.getData());
		
		PDFASVerificationResponse verResponse = new PDFASVerificationResponse();
		
		verResponse.setSignerCertificate(signResult.getSignerCertificate()
				.getEncoded());

		signResponse.setVerificationResponse(verResponse);
		
		return signResponse;
	}

	public static void startSignature(HttpServletRequest request,
			HttpServletResponse response, ServletContext context,
			byte[] pdfData, String connector, String position,
			String transactionId, String profile) throws Exception {

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

		logger.info("Setting TransactionID: " + transactionId);

		signParameter.setTransactionId(transactionId);

		IPlainSigner signer;
		if (connector.equals("bku") || connector.equals("onlinebku")
				|| connector.equals("mobilebku")) {
			BKUSLConnector conn = new BKUSLConnector(config);
			// conn.setBase64(true);
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
		signParameter.setSignatureProfileId(profile);

		ByteArrayDataSink dataSink = new ByteArrayDataSink();
		signParameter.setOutput(dataSink);

		// set Signature Position
		signParameter.setSignaturePosition(position);

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

	public static byte[] generateVisualBlock(String profile, int resolution)
			throws IOException, CertificateException, PdfAsException {
		X509Certificate cert = new X509Certificate(
				PdfAsHelper.class.getResourceAsStream("/qualified.cer"));
		Configuration config = pdfAs.getConfiguration();
		SignParameter parameter = PdfAsFactory
				.createSignParameter(config, null);
		parameter.setSignatureProfileId(profile);
		Image img = pdfAs.generateVisibleSignaturePreview(parameter, cert,
				resolution);

		if (img == null) {
			return null;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write((RenderedImage) img, "png", baos);
		baos.close();
		return baos.toByteArray();
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

		logger.debug("Got CMS Signature Response");

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
		// IPlainSigner plainSigner = (IPlainSigner) session
		// .getAttribute(PDF_SIGNER);

		String connector = (String) session.getAttribute(PDF_SL_INTERACTIVE);

		if (connector.equals("bku") || connector.equals("onlinebku")
				|| connector.equals("mobilebku")) {
			BKUSLConnector bkuSLConnector = (BKUSLConnector) session
					.getAttribute(PDF_SL_CONNECTOR);

			if (statusRequest.needCertificate()) {
				logger.debug("Needing Certificate from BKU");
				// build SL Request to read certificate
				InfoboxReadRequestType readCertificateRequest = bkuSLConnector
						.createInfoboxReadRequest(statusRequest
								.getSignParameter());

				JAXBElement<InfoboxReadRequestType> readRequest = of
						.createInfoboxReadRequest(readCertificateRequest);

				String url = generateDataURL(request, response);
				String slRequest = SLMarschaller.marshalToString(readRequest);
				String template = getTemplateSL();
				String locale = getLocale(request, response);
				template = template.replace("##BKU##",
						generateBKUURL(connector));
				template = template.replace("##XMLRequest##",
						StringEscapeUtils.escapeHtml4(slRequest));
				template = template.replace("##DataURL##", url);
				template = template.replace("##LOCALE##", locale);

				if (statusRequest.getSignParameter().getTransactionId() != null) {
					template = template.replace(
							"##ADDITIONAL##",
							"<input type=\"hidden\" name=\"TransactionId_\" value=\""
									+ StringEscapeUtils
											.escapeHtml4(statusRequest
													.getSignParameter()
													.getTransactionId())
									+ "\">");
				} else {
					template = template.replace("##ADDITIONAL##", "");
				}

				response.getWriter().write(template);
				// TODO: set content type of response!!
				response.setContentType("text/html");
				response.getWriter().close();
			} else if (statusRequest.needSignature()) {
				logger.debug("Needing Signature from BKU");
				// build SL Request for cms signature
				RequestPackage pack = bkuSLConnector.createCMSRequest(
						statusRequest.getSignatureData(),
						statusRequest.getSignatureDataByteRange(),
						statusRequest.getSignParameter());

				String slRequest = SLMarschaller
						.marshalToString(of
								.createCreateCMSSignatureRequest(pack
										.getRequestType()));

				logger.trace("SL Request: " + slRequest);

				response.setContentType("text/xml");
				response.getWriter().write(slRequest);
				response.getWriter().close();

			} else if (statusRequest.isReady()) {
				// TODO: store pdf document redirect to Finish URL
				logger.debug("Document ready!");

				SignResult result = pdfAs.finishSign(statusRequest);
				DataSink output = result.getOutputDocument();
				if (output instanceof ByteArrayDataSink) {
					ByteArrayDataSink byteDataSink = (ByteArrayDataSink) output;
					byte[] signedPdf = byteDataSink.getData();

					PDFASVerificationResponse verResponse = new PDFASVerificationResponse();
					List<VerifyResult> verResults = PdfAsHelper
							.synchornousVerify(signedPdf, -2,
									PdfAsHelper.getVerificationLevel(request));

					if (verResults.size() != 1) {
						throw new WebServiceException(
								"Document verification failed!");
					}
					VerifyResult verifyResult = verResults.get(0);

					verResponse.setCertificateCode(verifyResult
							.getCertificateCheck().getCode());
					verResponse.setValueCode(verifyResult.getValueCheckCode()
							.getCode());

					PdfAsHelper.setPDFASVerificationResponse(request, verResponse);
					PdfAsHelper.setSignedPdf(request, response, signedPdf);
					PdfAsHelper.gotoProvidePdf(context, request, response);

					String signerCert = Base64.encodeBase64String(result
							.getSignerCertificate().getEncoded());

					PdfAsHelper.setSignerCertificate(request, signerCert);
				} else {
					throw new PdfAsWebException("No Signature data available");
				}

			} else {
				throw new PdfAsWebException("Invalid state!");
			}
		} else {
			throw new PdfAsWebException("Invalid connector: " + connector);
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

	public static void setLocale(HttpServletRequest request,
			HttpServletResponse response, String locale) {
		HttpSession session = request.getSession();
		session.setAttribute(PDF_LOCALE, locale);
	}

	public static String getLocale(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute(PDF_LOCALE);
		return obj == null ? "DE" : obj.toString();
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
		logger.debug("[" + session.getId() + "]: Setting Error URL to: " + url);
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
		logger.debug("[" + session.getId() + "]: Setting Invoke URL to: " + url);
		session.setAttribute(PDF_INVOKE_URL, url);
	}

	public static String getInvokeURL(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute(PDF_INVOKE_URL);
		return obj == null ? null : obj.toString();
	}
	
	public static void setInvokeTarget(HttpServletRequest request,
			HttpServletResponse response, String url) {
		
		
		
		HttpSession session = request.getSession();
		session.setAttribute(PDF_INVOKE_TARGET, url);
		logger.debug("External Invoke TARGET: " + url);
	}

	public static String getInvokeTarget(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute(PDF_INVOKE_TARGET);
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
		logger.debug("Generated URL: " + dataURL);
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

	public static String generateUserEntryURL(String storeId) {
		String publicURL = WebConfiguration.getPublicURL();
		if (publicURL == null) {
			logger.error("To use this functionality "
					+ WebConfiguration.PUBLIC_URL
					+ " has to be configured in the web configuration");
			return null;
		}

		String baseURL = publicURL + PDF_USERENTRY_PAGE;
		try {
			return baseURL + "?" + UIEntryPointServlet.REQUEST_ID_PARAM + "="
					+ URLEncoder.encode(storeId, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.warn("Encoding not supported for URL encoding", e);
		}
		return baseURL + "?" + UIEntryPointServlet.REQUEST_ID_PARAM + "="
				+ storeId;
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

	public static void setSignerCertificate(HttpServletRequest request,
			String value) {
		HttpSession session = request.getSession();
		session.setAttribute(PDF_SIGNER_CERT, value);
	}

	public static String getSignerCertificate(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute(PDF_SIGNER_CERT);
		if (obj != null) {
			return obj.toString();
		}
		return null;
	}

	public static void setVerificationLevel(HttpServletRequest request,
			SignatureVerificationLevel lvl) {
		HttpSession session = request.getSession();
		session.setAttribute(PDF_VER_LEVEL, lvl);
	}

	public static SignatureVerificationLevel getVerificationLevel(
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute(PDF_VER_LEVEL);
		if (obj != null && obj instanceof SignatureVerificationLevel) {
			return (SignatureVerificationLevel) obj;
		}
		return SignatureVerificationLevel.INTEGRITY_ONLY_VERIFICATION;
	}
	
	public static void setPDFASVerificationResponse(HttpServletRequest request,
			PDFASVerificationResponse resp) {
		HttpSession session = request.getSession();
		session.setAttribute(PDF_VER_RESP, resp);
	}

	public static PDFASVerificationResponse getPDFASVerificationResponse(
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute(PDF_VER_RESP);
		if (obj != null && obj instanceof PDFASVerificationResponse) {
			return (PDFASVerificationResponse) obj;
		}
		return null;
	}

	public static void setVerificationResult(HttpServletRequest request,
			List<VerifyResult> value) {
		HttpSession session = request.getSession();
		session.setAttribute(VERIFICATION_RESULT, value);
	}

	@SuppressWarnings("unchecked")
	public static List<VerifyResult> getVerificationResult(
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute(VERIFICATION_RESULT);
		if (obj != null) {
			try {
				if (!(obj instanceof List<?>)) {
					logger.error("Invalid object type");
					return null;
				}
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

	public static String getVersion() {
		return PdfAsFactory.getVersion();
	}

	public static String getSCMRevision() {
		return PdfAsFactory.getSCMRevision();
	}
}
