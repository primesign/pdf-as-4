package at.gv.egiz.pdfas.web.servlets;

import at.gv.egiz.pdfas.api.ws.PDFASSignParameters;
import at.gv.egiz.pdfas.api.ws.PDFASSignResponse;
import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.exception.PdfAsWebException;
import at.gv.egiz.pdfas.web.filter.UserAgentFilter;
import at.gv.egiz.pdfas.web.helper.DigestHelper;
import at.gv.egiz.pdfas.web.helper.JSONStartResponse;
import at.gv.egiz.pdfas.web.helper.PdfAsHelper;
import at.gv.egiz.pdfas.web.stats.StatisticEvent;
import at.gv.egiz.pdfas.web.stats.StatisticFrontend;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andreas Fitzek on 6/23/16.
 */
public class JSONAPIServlet extends HttpServlet {

    private static final String JSON_PROFILE = "profile";
    private static final String JSON_POSITION = "position";
    private static final String JSON_CONNECTOR = "connector";
    private static final String JSON_REQUEST_ID = "reqID";
    private static final String JSON_INPUT = "input";
    private static final String JSON_OUTPUT = "output";
    private static final String JSON_OUTPUT_SIG = "verifySignature";
    private static final String JSON_OUTPUT_CER = "verifyCertificate";
    private static final String JSON_DATAURL = "dataUrl";
    private static final String JSON_BKUURL = "bkuUrl";
    private static final String JSON_SLREQUEST = "slRequest";
    private static final String JSON_SBP = "sbp";
    private static final Logger logger = LoggerFactory.getLogger(JSONAPIServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if(!WebConfiguration.isJSONAPIEnabled()) {
            logger.info("Access to JSON API, but JSON API is disabled. Returning 404 error.");
            resp.sendError(404);
            return;
        }

        String jsonString = IOUtils.toString(req.getInputStream(), "UTF-8");

        logger.debug("Reading json String {}", jsonString);

        JSONObject jsonObject = new JSONObject(jsonString);

        logger.debug("JSON parsed: {}", jsonObject.toString());

        process(req, resp, jsonObject);
    }

    protected void process(HttpServletRequest request,
                           HttpServletResponse response,
                           JSONObject jsonObject) throws ServletException, IOException {

        JSONObject jsonResponse = new JSONObject();



        String profile = jsonObject.has(JSON_PROFILE) ? jsonObject.getString(JSON_PROFILE) : null;
        String position = jsonObject.has(JSON_POSITION) ? jsonObject.getString(JSON_POSITION) : null;
        String connector = jsonObject.getString(JSON_CONNECTOR);
        String input = jsonObject.getString(JSON_INPUT);
        String requestID = jsonObject.has(JSON_REQUEST_ID) ? jsonObject.getString(JSON_REQUEST_ID) : null;

        if(input == null) {
            throw new ServletException(
                    "Invalid input value!");
        }

        byte[] inputDocument = Base64.decodeBase64(input);

        StatisticEvent statisticEvent = new StatisticEvent();
        statisticEvent.setSource(StatisticEvent.Source.JSON);
        statisticEvent.setOperation(StatisticEvent.Operation.SIGN);
        statisticEvent.setUserAgent(UserAgentFilter.getUserAgent());
        statisticEvent.setStartNow();

        try {

            if(connector == null) {
                throw new ServletException(
                        "Invalid connector value!");
            }

            PDFASSignParameters.Connector connectorEnum = null;

            if(PDFASSignParameters.Connector.MOA.equalsName(connector)) {
                connectorEnum = PDFASSignParameters.Connector.MOA;
            } else if(PDFASSignParameters.Connector.JKS.equalsName(connector)) {
                connectorEnum = PDFASSignParameters.Connector.JKS;
            } else if(PDFASSignParameters.Connector.BKU.equalsName(connector)) {
                connectorEnum = PDFASSignParameters.Connector.BKU;
            } else if(PDFASSignParameters.Connector.MOBILEBKU.equalsName(connector)) {
                connectorEnum = PDFASSignParameters.Connector.MOBILEBKU;
            } else if(PDFASSignParameters.Connector.ONLINEBKU.equalsName(connector)) {
                connectorEnum = PDFASSignParameters.Connector.ONLINEBKU;
            } else if(PDFASSignParameters.Connector.SECLAYER20.equalsName(connector)) {
                connectorEnum = PDFASSignParameters.Connector.SECLAYER20;
            } 

            if(connectorEnum == null) {
                throw new ServletException(
                        "Invalid connector value!");
            }

            // TODO: check connector is enabled!

            statisticEvent.setFilesize(inputDocument.length);
            statisticEvent.setProfileId(profile);
            statisticEvent.setDevice(connector);

            PDFASSignParameters parameters = new PDFASSignParameters();
            parameters.setConnector(connectorEnum);
            parameters.setPosition(position);
            parameters.setProfile(profile);

            Map<String, String> signatureBlockParametersMap = new HashMap<>();
            try {
                JSONArray jsonArray = jsonObject.getJSONArray(JSON_SBP);
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String s = jsonArray.getString(0);
                        if (!s.contains("=")) {//TODO or pass as map?
                            throw new Exception("Invalid parameter: " + s);
                        }
                        String[] values = s.split("=", 2);
                        signatureBlockParametersMap.put(values[0], values[1]);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            if (PDFASSignParameters.Connector.MOA.equals(connectorEnum)
                    || PDFASSignParameters.Connector.JKS.equals(connectorEnum)) {
                // Plain server based signatures!!
                PDFASSignResponse pdfasSignResponse = PdfAsHelper.synchronousServerSignature(
                        inputDocument, parameters, signatureBlockParametersMap);

                VerifyResult verifyResult = null;

                List<VerifyResult> verResults = PdfAsHelper
                           .synchronousVerify(
                                    pdfasSignResponse.getSignedPDF(),
                                    -1,
                                    VerifyParameter.SignatureVerificationLevel.INTEGRITY_ONLY_VERIFICATION,
                                    null);

                if (verResults.size() != 1) {
                   throw new ServletException(
                          "Document verification failed!");
                }
                verifyResult = verResults.get(0);

                if(verifyResult.getValueCheckCode().getCode() == 0) {
                    statisticEvent.setStatus(StatisticEvent.Status.OK);
                    statisticEvent.setEndNow();
                    statisticEvent.setTimestampNow();
                    StatisticFrontend.getInstance().storeEvent(statisticEvent);
                    statisticEvent.setLogged(true);
                } else {
                    statisticEvent.setStatus(StatisticEvent.Status.ERROR);
                    statisticEvent.setErrorCode(verifyResult.getValueCheckCode().getCode());
                    statisticEvent.setEndNow();
                    statisticEvent.setTimestampNow();
                    StatisticFrontend.getInstance().storeEvent(statisticEvent);
                    statisticEvent.setLogged(true);
                }

                jsonResponse.put(JSON_OUTPUT, Base64.encodeBase64String(pdfasSignResponse.getSignedPDF()));
                jsonResponse.put(JSON_OUTPUT_SIG, verifyResult.getValueCheckCode().getCode());
                jsonResponse.put(JSON_OUTPUT_CER, verifyResult.getCertificateCheck().getCode());

            } else {

                PdfAsHelper.setStatisticEvent(request, response, statisticEvent);
                PdfAsHelper.setVerificationLevel(request,
                        VerifyParameter.SignatureVerificationLevel.INTEGRITY_ONLY_VERIFICATION);

                String pdfDataHash = DigestHelper.getHexEncodedHash(inputDocument);

                PdfAsHelper.setSignatureDataHash(request, pdfDataHash);
                logger.debug("Storing signatures data hash: " + pdfDataHash);

                logger.debug("Starting signature creation with: " + connector);

                // start asynchronous signature creation

                    if (PDFASSignParameters.Connector.BKU.equals(connectorEnum)) {
                        if (WebConfiguration.getLocalBKUURL() == null) {
                            throw new PdfAsWebException(
                                    "Invalid connector bku is not supported");
                        }
                    }

                    if (PDFASSignParameters.Connector.ONLINEBKU.equals(connectorEnum)) {
                        if (WebConfiguration.getLocalBKUURL() == null) {
                            throw new PdfAsWebException(
                                    "Invalid connector onlinebku is not supported");
                        }
                    }

                    if (PDFASSignParameters.Connector.MOBILEBKU.equals(connectorEnum)) {
                        if (WebConfiguration.getLocalBKUURL() == null) {
                            throw new PdfAsWebException(
                                    "Invalid connector mobilebku is not supported");
                        }
                    }
                    
                    if (PDFASSignParameters.Connector.SECLAYER20.equals(connectorEnum)) {
                        if (WebConfiguration.getSecurityLayer20URL() == null) {
                            throw new PdfAsWebException(
                                    "Invalid connector mobilebku is not supported");
                        }
                    }


                    PdfAsHelper.startSignatureJson(request, response, getServletContext(),
                            inputDocument, connectorEnum.toString(),
                            position,
                            null,
                            profile, null,
                            null);

                JSONStartResponse jsonStartResponse = PdfAsHelper.startJsonProcess(request, response, getServletContext());

                if(jsonStartResponse == null) {
                    throw new PdfAsWebException(
                            "Invalid configuration for json API");
                }

                jsonResponse.put(JSON_DATAURL, jsonStartResponse.getUrl());
                jsonResponse.put(JSON_BKUURL, jsonStartResponse.getBkuURL());
                jsonResponse.put(JSON_SLREQUEST, jsonStartResponse.getSlRequest());
            }

            response.setContentType("application/json");
            IOUtils.write(jsonResponse.toString(), response.getOutputStream(), "UTF-8");

        } catch (Throwable e) {

            statisticEvent.setStatus(StatisticEvent.Status.ERROR);
            statisticEvent.setException(e);
            if(e instanceof PDFASError) {
                statisticEvent.setErrorCode(((PDFASError)e).getCode());
            }
            statisticEvent.setEndNow();
            statisticEvent.setTimestampNow();
            StatisticFrontend.getInstance().storeEvent(statisticEvent);
            statisticEvent.setLogged(true);

            logger.warn("Error in JSON Service", e);
            if (e.getCause() != null) {
                throw new ServletException(e.getCause().getMessage());
            } else {
                throw new ServletException(e.getMessage());
            }
        } finally {
            logger.debug("Done JSON Sign Request");
        }
    }
}
