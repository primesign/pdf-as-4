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
package at.gv.egiz.pdfas.common.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.settings.ISettings;

public class ImageUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(ImageUtils.class);

	public static BufferedImage removeAlphaChannel(BufferedImage src) {
		//if (src.getColorModel().hasAlpha()) {
			BufferedImage image = new BufferedImage(src.getWidth(),
					src.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.setComposite(AlphaComposite.Src);
			g.drawImage(src, 0, 0, null);
			g.dispose();
			return image;
		//}
		//return src;
		/*
		 * BufferedImage rgbImage = new BufferedImage(src.getWidth(),
		 * src.getHeight(), BufferedImage.TYPE_3BYTE_BGR); for (int x = 0; x <
		 * src.getWidth(); ++x) { for (int y = 0; y < src.getHeight(); ++y) {
		 * rgbImage.setRGB(x, y, src.getRGB(x, y) & 0xFFFFFF); } } return
		 * rgbImage;
		 */
	}

	public static BufferedImage convertRGBAToIndexed(BufferedImage src) {
		BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(),
				BufferedImage.TYPE_BYTE_INDEXED);
		Graphics g = dest.getGraphics();
		g.setColor(new Color(231, 20, 189));
		g.fillRect(0, 0, dest.getWidth(), dest.getHeight()); // fill with a
																// hideous color
																// and make it
																// transparent
		dest = makeTransparent(dest, 0, 0);
		dest.createGraphics().drawImage(src, 0, 0, null);
		return dest;
	}

	public static BufferedImage makeTransparent(BufferedImage image, int x,
			int y) {
		ColorModel cm = image.getColorModel();
		if (!(cm instanceof IndexColorModel))
			return image; // sorry...
		IndexColorModel icm = (IndexColorModel) cm;
		WritableRaster raster = image.getRaster();
		int pixel = raster.getSample(x, y, 0); // pixel is offset in ICM's
												// palette
		int size = icm.getMapSize();
		byte[] reds = new byte[size];
		byte[] greens = new byte[size];
		byte[] blues = new byte[size];
		icm.getReds(reds);
		icm.getGreens(greens);
		icm.getBlues(blues);
		IndexColorModel icm2 = new IndexColorModel(8, size, reds, greens,
				blues, pixel);
		return new BufferedImage(icm2, raster, image.isAlphaPremultiplied(),
				null);
	}

	public static Dimension getImageDimensions(InputStream is)
			throws IOException {
		ImageInputStream in = ImageIO.createImageInputStream(is);
		try {
			final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			if (readers.hasNext()) {
				ImageReader reader = readers.next();
				try {
					reader.setInput(in);
					return new Dimension(reader.getWidth(0),
							reader.getHeight(0));
				} finally {
					reader.dispose();
				}
			}
			throw new IOException("Failed to read Image file");
		} finally {
			if (in != null)
				in.close();
		}
	}

	public static File getImageFile(String imageFile, ISettings settings)
			throws PdfAsException, IOException {
		File img_file = new File(imageFile);
		if (!img_file.isAbsolute()) {
			logger.debug("Image file declaration is relative. Prepending path of resources directory.");
			logger.debug("Image Location: " + settings.getWorkingDirectory()
					+ File.separator + imageFile);
			img_file = new File(settings.getWorkingDirectory() + File.separator
					+ imageFile);
		} else {
			logger.debug("Image file declaration is absolute. Skipping file relocation.");
		}

		if (!img_file.exists()) {
			logger.debug("Image file \"" + img_file.getCanonicalPath()
					+ "\" doesn't exist.");
			throw new PdfAsException("error.pdf.stamp.04");
		}

		return img_file;
	}

	public static Dimension getImageDimensions(String imageValue,
			ISettings settings) throws PdfAsException, IOException {
		InputStream is = getImageInputStream(imageValue, settings);
		try {
			return getImageDimensions(is);
		} catch (Throwable e) {
			throw new PdfAsException("error.pdf.stamp.04", e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	public static InputStream getImageInputStream(String imageValue,
			ISettings settings) throws PdfAsException, IOException {
		InputStream is = null;
		try {
			File img_file = ImageUtils.getImageFile(imageValue, settings);

			if (!img_file.exists()) {
				throw new PdfAsException("error.pdf.stamp.04");
			}

			is = new FileInputStream(img_file);
		} catch (PdfAsException e) {
			try {
				is = new ByteArrayInputStream(Base64.decodeBase64(imageValue));
			} catch (Throwable e1) {
				// Ignore value is not base 64!
				logger.debug("Value is not base64: ", e1);
				// rethrow e
				throw e;
			}
		} catch (IOException e) {
			try {
				is = new ByteArrayInputStream(Base64.decodeBase64(imageValue));
			} catch (Throwable e1) {
				// Ignore value is not base 64!
				logger.debug("Value is not base64: ", e1);
				// rethrow e
				throw e;
			}
		}
		return is;
	}

	public static BufferedImage getImage(String imageValue, ISettings settings)
			throws PdfAsException, IOException {
		InputStream is = getImageInputStream(imageValue, settings);
		try {
			return ImageIO.read(is);
		} catch (Throwable e) {
			throw new PdfAsException("error.pdf.stamp.04", e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
}
