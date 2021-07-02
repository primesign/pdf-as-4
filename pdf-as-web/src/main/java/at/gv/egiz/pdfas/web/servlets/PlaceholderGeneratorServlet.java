package at.gv.egiz.pdfas.web.servlets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.WriterException;

import at.gv.egiz.pdfas.lib.impl.placeholder.PlaceholderExtractorConstants;
import at.gv.egiz.pdfas.lib.impl.placeholder.SignaturePlaceholderData;
import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.helper.QRCodeGenerator;

public class PlaceholderGeneratorServlet extends HttpServlet implements PlaceholderExtractorConstants {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5854130802422496977L;

	public static final String PARAM_ID = "id";
	public static final String PARAM_PROFILE = "profile";
	public static final String PARAM_WIDTH = "w";
	public static final String PARAM_HEIGHT = "h";
	public static final String PARAM_BORDER = "b";
	
	public static final int MAX_SIZE = 1024;
	
	private static final Logger logger = LoggerFactory
			.getLogger(ExternSignServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doProcess(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doProcess(req, resp);
	}
	
	protected void doProcess(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		if(!WebConfiguration.isQRPlaceholderGenerator()) {
			logger.info("QR Placeholder is disabled by configuration (see {})" + WebConfiguration.PLACEHOLDER_GENERATOR_ENABLED);
			resp.sendError(HttpStatus.SC_NOT_FOUND);
			return;
		}
		
		String baseImage = "/img/PLATZHALTER-SIG.png";
		
		String id = req.getParameter(PARAM_ID);
		String profile = req.getParameter(PARAM_PROFILE);
		
		String buildString = QR_PLACEHOLDER_IDENTIFIER;
		
		String filename = "placeholder";
		
		if(id != null && !id.isEmpty()) {
			id = id.replaceAll("[^0-9]", "");
			if(id != null && !id.isEmpty()) {
				buildString = buildString + ";" + SignaturePlaceholderData.ID_KEY + "=" + id;
				filename = filename + "_" + id;
			}
		}
		
		if(profile != null && !profile.isEmpty()) {
			buildString = buildString + ";" + SignaturePlaceholderData.PROFILE_KEY + "=" + profile;
			
			if(profile.endsWith("_EN")) {
				baseImage = "/img/PLACEHOLDER-SIG_EN.png";
				filename = filename + "_en";
			} else {
				filename = filename + "_de";
			}
		}
		
		filename = filename + ".png";
		
		logger.info("generating qr placeholder for '{}' as {}", buildString, filename);
		
		//if(id != null || profile != null) {
			// We need to generate the image
		// default values set for pdf-as wai on buergerkarte.at
		int height = 60;
		int width = 300;
		int border = 2;
		
		if(req.getParameter(PARAM_HEIGHT) != null) {
			try {
				height = Integer.parseInt(req.getParameter(PARAM_HEIGHT));
			} catch(NumberFormatException e) {
				logger.info("Parameter {} has to be a number.", PARAM_HEIGHT);
				resp.sendError(HttpStatus.SC_NOT_ACCEPTABLE);
			}
		}
		
		if(req.getParameter(PARAM_WIDTH) != null) {
			try {
				width = Integer.parseInt(req.getParameter(PARAM_WIDTH));
			} catch(NumberFormatException e) {
				logger.info("Parameter {} has to be a number.", PARAM_WIDTH);
				resp.sendError(HttpStatus.SC_NOT_ACCEPTABLE);
			}
		}
		
		if(req.getParameter(PARAM_BORDER) != null) {
			try {
				border = Integer.parseInt(req.getParameter(PARAM_BORDER));
			} catch(NumberFormatException e) {
				logger.info("Parameter {} has to be a number.", PARAM_BORDER);
				resp.sendError(HttpStatus.SC_NOT_ACCEPTABLE);
			}
		}
		
		int qrSize = height - ( 2 * border);
		
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(baseImage);
			if(is == null) {
				logger.warn("Cannot open resource {} to generator QR placeholder", baseImage);
				resp.sendError(HttpStatus.SC_NOT_FOUND);
				return;
			}
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			// generate QR code
			try {
				QRCodeGenerator.generateQRCode(buildString, baos, qrSize);
			} catch (WriterException e) {
				logger.warn("Failed to generate QR Code for placeholder generationg", e);
				resp.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR);
				return;
			}
			
			baos.close();
			BufferedImage qr = ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
			
			BufferedImage off_Image =
					  new BufferedImage(width, height,
					                    BufferedImage.TYPE_INT_ARGB);
			
			Graphics g = off_Image.getGraphics();
			g.setColor(new Color(255,255,255,1));
			g.fillRect(0, 0, width, height);
			g.setColor(Color.WHITE);
			g.fillRect(border, border, width - (2 * border), height - (2 * border));
			//g.drawImage(base, 0, 0, 250, 98, 0, 0, base.getWidth(), base.getHeight(), null);
			g.drawImage(qr, border, border, qrSize + border, qrSize + border, 0, 0, qr.getWidth(), qr.getHeight(), null);
			
			//g.(x, y, width, height);
			Font writeFont = new Font(Font.SANS_SERIF, Font.BOLD, 10);
			g.setFont(writeFont);
			g.setColor(Color.BLACK);
			
			int lineSpace = 14 + 4;
			
			int textHeight = 3 * lineSpace;
			
			if(id != null && !id.isEmpty()) {
				textHeight = 4 * lineSpace;
			}
			
			int start = (height - textHeight) / 2; 
			
			if(profile != null && profile.endsWith("_EN")) {
				
				g.drawString("placeholder for the", qrSize + ( 3 * border), start + lineSpace);
				g.drawString("electronic signature", qrSize + ( 3 * border), start + (2 * lineSpace));
			} else {
				g.drawString("Platzhalter fuer die", qrSize + ( 3 * border), start + lineSpace);
				g.drawString("elektronische Signatur", qrSize + ( 3 * border), start + (2 * lineSpace));
			}
			if(id != null && !id.isEmpty()) {
				
				Font nrFont = new Font(Font.SANS_SERIF, Font.BOLD | Font.ITALIC, 10);
				g.setFont(nrFont);
				
				g.drawString("NR: " + id, qrSize + ( 3 * border), start + (3 * lineSpace));
			}
			
			logger.info("serving qr placeholder for '{}'", buildString);
			resp.setContentType("image/png");
			resp.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
			resp.setHeader("Pragma", "no-cache");
			resp.setHeader ("Content-Disposition", "attachment; filename=\""+filename+"\""); 

			ImageIO.write(off_Image, "PNG", resp.getOutputStream());
			return;
		/*} else {
			// just use the template
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(baseImage);
			if(is == null) {
				logger.warn("Cannot open resource {} to generator QR placeholder", baseImage);
				resp.sendError(HttpStatus.SC_NOT_FOUND);
				return;
			} else {
				logger.info("serving default qr placeholder");
				resp.setContentType("image/png");
				resp.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
				resp.setHeader("Pragma", "no-cache");
				IOUtils.copy(is, resp.getOutputStream());
				return;
			}
		}*/
	}
	
}
