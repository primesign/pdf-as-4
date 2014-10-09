package at.gv.egiz.pdfas.web.servlets;

import javax.servlet.ServletConfig;
import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.web.ws.PDFASSigningImpl;
import at.gv.egiz.pdfas.web.ws.PDFASVerificationImpl;

public class SoapServiceServlet extends CXFNonSpringServlet {

	private static final Logger logger = LoggerFactory
			.getLogger(SoapServiceServlet.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8903883276191902043L;

	@Override
	protected void loadBus(ServletConfig sc) {
		super.loadBus(sc);

		// You could add the endpoint publish codes here
        Bus bus = this.getBus();
        BusFactory.setDefaultBus(bus);
        Endpoint signEp = Endpoint.publish("/wssign", new PDFASSigningImpl());
        /*
         * SOAPBinding signBinding = (SOAPBinding)signEp.getBinding();
        signBinding.setMTOMEnabled(true);
        */
        
        Endpoint verifyEp = Endpoint.publish("/wsverify", new PDFASVerificationImpl());
        /*
        SOAPBinding verifyBinding = (SOAPBinding)verifyEp.getBinding();
        verifyBinding.setMTOMEnabled(true);
        */
        
	}
}
