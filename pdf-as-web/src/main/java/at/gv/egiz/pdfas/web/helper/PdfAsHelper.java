package at.gv.egiz.pdfas.web.helper;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBElement;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import at.gv.egiz.pdfas.lib.api.ByteArrayDataSource;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.StatusRequest;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.sigs.pades.PAdESSigner;
import at.gv.egiz.sl.CreateCMSSignatureRequestType;
import at.gv.egiz.sl.CreateCMSSignatureResponseType;
import at.gv.egiz.sl.InfoboxAssocArrayPairType;
import at.gv.egiz.sl.InfoboxReadRequestType;
import at.gv.egiz.sl.InfoboxReadResponseType;
import at.gv.egiz.sl.ObjectFactory;
import at.gv.egiz.sl.util.BKUSLConnector;
import at.gv.egiz.sl.util.SLMarschaller;

public class PdfAsHelper {

	private static final String PDF_CONFIG = "PDF_CONFIG";
	private static final String PDF_STATUS = "PDF_STATUS";
	private static final String PDF_SL_CONNECTOR = "PDF_SL_CONNECTOR";

	private static PdfAs pdfAs;
	private static ObjectFactory of = new ObjectFactory();

	static {
		pdfAs = PdfAsFactory.createPdfAs(new File("/home/afitzek/.pdfas"));
	}

	public static void startSignature(HttpServletRequest request,
			HttpServletResponse response, byte[] pdfData) throws Exception {

		HttpSession session = request.getSession();

		Configuration config = pdfAs.getConfiguration();
		session.setAttribute(PDF_CONFIG, config);
		BKUSLConnector bkuSLConnector = new BKUSLConnector(config);
		SignParameter signParameter = PdfAsFactory.createSignParameter(config,
				new ByteArrayDataSource(pdfData));
		signParameter.setPlainSigner(new PAdESSigner(bkuSLConnector));
		
		session.setAttribute(PDF_SL_CONNECTOR, bkuSLConnector);

		StatusRequest statusRequest = pdfAs.startSign(signParameter);
		session.setAttribute(PDF_STATUS, statusRequest);

		PdfAsHelper.process(request, response);
	}

	private static byte[] getCertificate(
			InfoboxReadResponseType infoboxReadResponseType) {
		byte[] data = null;
		if (infoboxReadResponseType.getAssocArrayData() != null) {
			List<InfoboxAssocArrayPairType> pairs = infoboxReadResponseType
					.getAssocArrayData().getPair();
			Iterator<InfoboxAssocArrayPairType> pairIterator = pairs.iterator();
			while(pairIterator.hasNext()) {
				InfoboxAssocArrayPairType pair = pairIterator.next();
				if(pair.getKey().equals("SecureSignatureKeypair")) {
					return pair.getBase64Content();
				}
			}
		}
		// SecureSignatureKeypair
		
		return data;
	}

	public static void injectCertificate(HttpServletRequest request,
			HttpServletResponse response, 
			InfoboxReadResponseType infoboxReadResponseType) throws Exception {
		
		HttpSession session = request.getSession();
		StatusRequest statusRequest = (StatusRequest)session.getAttribute(PDF_STATUS);
		
		statusRequest.setCertificate(getCertificate(infoboxReadResponseType));
		statusRequest = pdfAs.process(statusRequest);
		session.setAttribute(PDF_STATUS, statusRequest);
		
		PdfAsHelper.process(request, response);
	}

	public static void injectSignature(HttpServletRequest request,
			HttpServletResponse response,
			CreateCMSSignatureResponseType createCMSSignatureResponseType)
			throws Exception {

		HttpSession session = request.getSession();
		StatusRequest statusRequest = (StatusRequest) session
				.getAttribute(PDF_STATUS);

		statusRequest.setSigature(createCMSSignatureResponseType
				.getCMSSignature());
		statusRequest = pdfAs.process(statusRequest);
		session.setAttribute(PDF_STATUS, statusRequest);

		PdfAsHelper.process(request, response);
	}

	public static void process(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession();
		StatusRequest statusRequest = (StatusRequest) session
				.getAttribute(PDF_STATUS);
		BKUSLConnector bkuSLConnector = (BKUSLConnector) session
				.getAttribute(PDF_SL_CONNECTOR);
		Configuration config = (Configuration) session.getAttribute(PDF_CONFIG);

		if (statusRequest.needCertificate()) {
			// build SL Request to read certificate
			InfoboxReadRequestType readCertificateRequest = bkuSLConnector
					.createInfoboxReadRequest();

			JAXBElement<InfoboxReadRequestType> readRequest = of
					.createInfoboxReadRequest(readCertificateRequest);

			String url = request.getContextPath() + "/DataURL;jsessionid="
					+ session.getId();
			String fullurl = request.getScheme() + "://"
					+ request.getServerName() + ":" + request.getServerPort()
					+ url;
			String slRequest = SLMarschaller.marshalToString(readRequest);
			String template = getTemplateSL();
			template = template.replace("##BKU##",
					"http://127.0.0.1:3495/http-security-layer-request");
			template = template.replace("##XMLRequest##",
					StringEscapeUtils.escapeHtml4(slRequest));
			template = template.replace("##DataURL##", fullurl);
			response.getWriter().write(template);
			response.getWriter().close();
		} else if (statusRequest.needSignature()) {
			// build SL Request for cms signature
			CreateCMSSignatureRequestType createCMSSignatureRequestType = 
					bkuSLConnector.createCMSRequest(statusRequest.getSignatureData(), 
							statusRequest.getSignatureDataByteRange());
			
			String slRequest = SLMarschaller.marshalToString(of
						.createCreateCMSSignatureRequest(createCMSSignatureRequestType));
			
			response.setContentType("text/xml");
			response.getWriter().write(slRequest);
			response.getWriter().close();
			
		} else if (statusRequest.isReady()) {
			// TODO: store pdf document redirect to Finish URL
		} else {
			// TODO: invalid state
		}
	}

	private static String getTemplateSL() throws IOException {
		String xml = FileUtils.readFileToString(FileUtils
				.toFile(PdfAsHelper.class.getResource("/template_sl.html")));
		return xml;
	}

}
