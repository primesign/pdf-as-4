package at.gv.egiz.pdfas.web.servlets;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jose4j.base64url.Base64Url;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import at.gv.egiz.pdfas.lib.util.StreamUtils;
import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.helper.PdfAsHelper;
import at.gv.egiz.pdfas.web.sl20.JsonSecurityUtils;
import at.gv.egiz.pdfas.web.sl20.X509Utils;
import at.gv.egiz.sl20.data.VerificationResult;
import at.gv.egiz.sl20.exceptions.SL20Exception;
import at.gv.egiz.sl20.exceptions.SL20SecurityException;
import at.gv.egiz.sl20.exceptions.SLCommandoParserException;
import at.gv.egiz.sl20.utils.SL20Constants;
import at.gv.egiz.sl20.utils.SL20JSONExtractorUtils;

@MultipartConfig
public class SLDataURLServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory
			.getLogger(SLDataURLServlet.class);
	
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SLDataURLServlet() {
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
		
		JsonObject sl20ReqObj = null;	
		try {
			if(!PdfAsHelper.checkDataUrlAccess(request)) {
				throw new Exception("No valid dataURL access");
			}
			
			PdfAsHelper.setFromDataUrl(request);
						
			String sl20Result = request.getParameter(SL20Constants.PARAM_SL20_REQ_COMMAND_PARAM);
			if (StringUtils.isEmpty(sl20Result)) {
				//Workaround for SIC Handy-Signature, because it sends result in InputStream
				String isReqInput = StreamUtils.readStream(request.getInputStream(), "UTF-8");					
				if (StringUtils.isNotEmpty(isReqInput)) {
					logger.info("Use SIC Handy-Signature work-around!");
					sl20Result = isReqInput.substring("slcommand=".length());
					
				} else {					
					logger.info("NO SL2.0 commando or result FOUND.");
					throw new SL20Exception("sl20.04", null);
				}
				
			}

			logger.trace("Received SL2.0 result: " + sl20Result);		
			
			//parse SL2.0 command/result into JSON				
			try {
				JsonParser jsonParser = new JsonParser();
				JsonElement sl20Req = jsonParser.parse(Base64Url.decodeToUtf8String(sl20Result));
				sl20ReqObj = sl20Req.getAsJsonObject();
				
			} catch (JsonSyntaxException e) {
				logger.warn("SL2.0 command or result is NOT valid JSON.", e);
				logger.debug("SL2.0 msg: " + sl20Result);
				throw new SL20Exception("sl20.02", e);
				
			}
			
			//extract transactionId
			String transactionId = SL20JSONExtractorUtils.getStringValue(sl20ReqObj, SL20Constants.SL20_TRANSACTIONID, false);
			if (StringUtils.isNotEmpty(transactionId))
					request.setAttribute(PdfAsHelper.PDF_SESSION_PREFIX + SL20Constants.SL20_TRANSACTIONID, transactionId);
			
			
			//validate reqId with inResponseTo 
			String sl20ReqId = (String) request.getSession(false).getAttribute(PdfAsHelper.PDF_SESSION_PREFIX + SL20Constants.SL20_REQID);
			String inRespTo = SL20JSONExtractorUtils.getStringValue(sl20ReqObj, SL20Constants.SL20_INRESPTO, true);
			if (sl20ReqId == null || !sl20ReqId.equals(inRespTo)) {
				logger.info("SL20 'reqId': " + sl20ReqId + " does NOT match to 'inResponseTo':" + inRespTo);
				throw new SL20SecurityException("SL20 'reqId': " + sl20ReqId + " does NOT match to 'inResponseTo':" + inRespTo);
			}
			
			JsonSecurityUtils joseTools = JsonSecurityUtils.getInstance();
			if (!joseTools.isInitialized())
				joseTools = null;
			
			//validate signature
			VerificationResult payLoadContainer = SL20JSONExtractorUtils.extractSL20PayLoad(sl20ReqObj, joseTools, 
					WebConfiguration.isSL20SigningRequired());
			
			if ( (payLoadContainer.isValidSigned() == null || !payLoadContainer.isValidSigned())) {
				if (WebConfiguration.isSL20SigningRequired()) {
					logger.info("SL20 result from VDA was not valid signed");
					throw new SL20SecurityException("Signature on SL20 result NOT valid.");
					
				} else {
					logger.warn("SL20 result from VDA is NOT valid signed, but signatures-verification is DISABLED by configuration!");
					
				}				
			}
						
			//extract payloaf
			JsonObject payLoad = payLoadContainer.getPayload();
			
			//check response type
			if (SL20JSONExtractorUtils.getStringValue(
					payLoad, SL20Constants.SL20_COMMAND_CONTAINER_NAME, true)
						.equals(SL20Constants.SL20_COMMAND_IDENTIFIER_GETCERTIFICATE)) {
				logger.debug("Find " + SL20Constants.SL20_COMMAND_IDENTIFIER_GETCERTIFICATE + " result .... ");
								
				JsonElement getCertificateResult = SL20JSONExtractorUtils.extractSL20Result(
						payLoad, joseTools, 
						WebConfiguration.isSL20EncryptionRequired());

				//extract certificates												
				List<String> certsB64 = SL20JSONExtractorUtils.getListOfStringElements(getCertificateResult.getAsJsonObject(), 
						SL20Constants.SL20_COMMAND_PARAM_GETCERTIFICATE_RESULT_CERTIFICATE, 
						true);
				
				if (certsB64.isEmpty()) {
					logger.warn("SL20 'getCertificate' result contains NO certificate");
					throw new SLCommandoParserException();
					
				} else if (certsB64.size() == 1) {
					logger.debug("SL20 'getCertificate' result contains only one certificate");
					PdfAsHelper.injectCertificate(request, response, Base64.getDecoder().decode(certsB64.get(0)), getServletContext());	
					
				} else {
					logger.debug("SL20 'getCertificate' result contains more than one certificate. Certificates must be sorted ... ");
					List<X509Certificate> certs = new ArrayList<X509Certificate>();
					for (String certB64 : certsB64)
						certs.add(new iaik.x509.X509Certificate(Base64.getDecoder().decode(certB64)));
												
					List<X509Certificate> sortedCerts = X509Utils.sortCertificates(certs);
					logger.debug("Sorting of certificate completed. Select end-user certificate ... ");
					PdfAsHelper.injectCertificate(request, response, Base64.getDecoder().decode(sortedCerts.get(0).getEncoded()), getServletContext());
					
				}
				
			} else if (SL20JSONExtractorUtils.getStringValue(
					payLoad, SL20Constants.SL20_COMMAND_CONTAINER_NAME, true)
					.equals(SL20Constants.SL20_COMMAND_IDENTIFIER_CREATE_SIG_CADES)) {
				logger.debug("Find " + SL20Constants.SL20_COMMAND_IDENTIFIER_CREATE_SIG_CADES + " result .... ");
				
				JsonElement getCertificateResult = SL20JSONExtractorUtils.extractSL20Result(
						payLoad, joseTools, 
						WebConfiguration.isSL20EncryptionRequired());

				//extract CAdES signature
				String cadesSigB64 = SL20JSONExtractorUtils.getStringValue(
						getCertificateResult.getAsJsonObject(), 
						SL20Constants.SL20_COMMAND_PARAM_CREATE_SIG_CADES_RESULT_SIGNATURE, 
						true);
				
				if (StringUtils.isEmpty(cadesSigB64)) {
					logger.warn("SL20 'createCAdES' result contains NO signature");
					throw new SLCommandoParserException();
				}
				
				PdfAsHelper.injectSignature(request, response, Base64.getDecoder().decode(cadesSigB64), getServletContext());
				
			} else {
				logger.info("SL20 response is NOT a " + SL20Constants.SL20_COMMAND_IDENTIFIER_QUALIFIEDEID + " result");
				throw new SLCommandoParserException();
				
			}	
			
		} catch (Exception e) {
			logger.warn("Error in DataURL Servlet. " , e);
			PdfAsHelper.setSessionException(request, response, e.getMessage(),
					e);
			
			if (PdfAsHelper.getFromDataUrl(request)) {
				String errorUrl = PdfAsHelper.generateErrorURL(request, response);
				try {
					String transactionId = null;
					if (sl20ReqObj != null)
						transactionId = SL20JSONExtractorUtils.getStringValue(sl20ReqObj, SL20Constants.SL20_TRANSACTIONID, false);
					
					PdfAsHelper.buildSL20RedirectResponse(request, response, transactionId, errorUrl);
					
				} catch (SL20Exception e1) {
					logger.error("SL20 error-handling FAILED", e);
					response.sendError(500, "Internal Server Error.");
					
				}
				
			} else			
				PdfAsHelper.gotoError(getServletContext(), request, response);
		}
	}
}
