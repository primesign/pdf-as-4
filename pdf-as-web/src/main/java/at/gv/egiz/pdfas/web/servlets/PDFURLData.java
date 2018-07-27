package at.gv.egiz.pdfas.web.servlets;

import at.gv.egiz.pdfas.api.ws.PDFASVerificationResponse;
import at.gv.egiz.pdfas.common.exceptions.PDFIOException;
import at.gv.egiz.pdfas.common.utils.PDFUtils;
import at.gv.egiz.pdfas.lib.api.StatusRequest;
import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.helper.PdfAsHelper;
import at.gv.egiz.pdfas.web.helper.PdfAsParameterExtractor;
import at.gv.egiz.pdfas.web.stats.StatisticEvent;
import at.gv.egiz.pdfas.web.stats.StatisticFrontend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;

public class PDFURLData extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String PDF_STATUS = "PDF_STATUS";


    private static final Logger logger = LoggerFactory.getLogger(PDFData.class);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public PDFURLData() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        try {
            this.process(request, response);
        } catch (PDFIOException e) {
            response.sendError(500, "file cannot be transfered");
        }
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
                           HttpServletResponse response) throws ServletException, IOException, PDFIOException {

        HttpSession session = request.getSession();
        StatusRequest statusRequest = (StatusRequest) session
                .getAttribute(PDF_STATUS);

        byte[] nonSignedData = statusRequest.getSignatureData();

        if (nonSignedData != null) {

            byte[] blackoutnonSignedData = PDFUtils.blackOutSignature(nonSignedData, statusRequest.getSignatureDataByteRange());


            response.setContentType("application/pdf");
            OutputStream os = response.getOutputStream();
            os.write(blackoutnonSignedData);
            os.close();

        } else {
            PdfAsHelper.setSessionException(request, response,
                    "todo", null);
            PdfAsHelper.gotoError(getServletContext(), request, response);
            response.sendError(500, '');
        }
    }
}