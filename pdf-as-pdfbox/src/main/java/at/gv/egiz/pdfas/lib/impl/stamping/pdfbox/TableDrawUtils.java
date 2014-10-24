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
package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.knowcenter.wag.egov.egiz.table.Entry;
import at.knowcenter.wag.egov.egiz.table.Style;

public class TableDrawUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(TableDrawUtils.class);

	public static final String TABLE_DEBUG = "debug.table";

	public static void drawTable(PDPage page,
			PDPageContentStream contentStream, float x, float y, float width,
			float height, PDFBoxTable abstractTable, PDDocument doc,
			boolean subtable, PDResources formResources,
			Map<String, ImageObject> images, ISettings settings)
			throws PdfAsException {

		logger.debug("Drawing Table: X {} Y {} WIDTH {} HEIGHT {} \n{}", x, y,
				width, height, abstractTable.getOrigTable().toString());

		abstractTable.getOrigTable().setWidth(width);

		drawTableBackground(page, contentStream, x, y, width, height,
				abstractTable, settings);

		drawBorder(page, contentStream, x, y, width, height, abstractTable,
				doc, subtable, settings);

		drawContent(page, contentStream, x, y, width, height, abstractTable,
				doc, subtable, formResources, images, settings);
	}

	public static void drawContent(PDPage page,
			PDPageContentStream contentStream, float x, float y, float width,
			float height, PDFBoxTable abstractTable, PDDocument doc,
			boolean subtable, PDResources formResources,
			Map<String, ImageObject> images, ISettings settings)
			throws PdfAsException {

		float contentx = x;
		float contenty = y + height;
		float padding = abstractTable.getPadding();
		float[] colsSizes = getColSizes(abstractTable);

		for (int i = 0; i < abstractTable.getRowCount(); i++) {
			ArrayList<Entry> row = abstractTable.getRow(i);
			for (int j = 0; j < row.size(); j++) {
				Entry cell = (Entry) row.get(j);

				// Cell only contains default values so table style is the primary style
				Style inherit_style = Style.doInherit(abstractTable.style, cell.getStyle());
				cell.setStyle(inherit_style);

				float colWidth = 0;//colWidths[j];

				int colsleft = cell.getColSpan();

				if (j + colsleft > colsSizes.length) {
					throw new PdfAsException(
							"Configuration is wrong. Cannot determine column width!");
				}

				for (int k = 0; k < colsleft; k++) {
					colWidth = colWidth + colsSizes[j + k];
				}
				
				drawDebugPadding(contentStream, contentx, contenty, padding,
						colWidth, abstractTable.getRowHeights()[i], settings);

				switch (cell.getType()) {
				case Entry.TYPE_CAPTION:
					drawCaption(page, contentStream, contentx, contenty,
							colWidth, abstractTable.getRowHeights()[i],
							padding, abstractTable, doc, cell, formResources, settings);
					break;
				case Entry.TYPE_VALUE:
					drawValue(page, contentStream, contentx, contenty,
							colWidth, abstractTable.getRowHeights()[i],
							padding, abstractTable, doc, cell, formResources, settings);
					break;
				case Entry.TYPE_IMAGE:
					drawImage(page, contentStream, contentx, contenty,
							colWidth, abstractTable.getRowHeights()[i],
							padding, abstractTable, doc, cell, formResources,
							images, settings);
					break;
				case Entry.TYPE_TABLE:

					PDFBoxTable tbl_value = (PDFBoxTable) cell.getValue();

					Style inherit_styletab = Style.doInherit(
							abstractTable.style, cell.getStyle());
					tbl_value.table.setStyle(inherit_styletab);

					drawTable(page, contentStream, contentx, contenty
							- abstractTable.getRowHeights()[i], colWidth,
							abstractTable.getRowHeights()[i], tbl_value, doc,
							true, formResources, images, settings);
					break;
				default:
					logger.warn("Unknown Cell entry type: " + cell.getType());
					break;
				}

				// Move content pointer
				contentx += colWidth;
				
				int span = cell.getColSpan() - 1;
				j += span;
			}

			// Move content pointer
			contenty -= abstractTable.getRowHeights()[i];
			contentx = x;
		}
	}

	private static void drawString(PDPage page,
			PDPageContentStream contentStream, float contentx, float contenty,
			float width, float height, float padding,
			PDFBoxTable abstractTable, PDDocument doc, Entry cell,
			float fontSize, float textHeight, String valign, String halign,
			String[] tlines, PDFont textFont, PDResources formResources,
			ISettings settings) throws PdfAsException {
		try {
			float ty = contenty - padding;
			float tx = contentx + padding;
			float innerHeight = height - (2 * padding);
			float innerWidth = width - (2 * padding);
			if (Style.BOTTOM.equals(valign)) {
				float bottom_offset = innerHeight - textHeight;
				ty -= bottom_offset;
			} else if (Style.MIDDLE.equals(valign)) {
				float bottom_offset = innerHeight - textHeight;
				bottom_offset = bottom_offset / 2.0f;
				ty -= bottom_offset;
			}

			// calculate the max with of the text content
			float maxWidth = 0;
			for (int k = 0; k < tlines.length; k++) {
				float lineWidth;
				// if (textFont instanceof PDType1Font) {
				lineWidth = textFont.getStringWidth(tlines[k]) / 1000.0f
						* fontSize;
				/*
				 * } else { float fwidth = textFont
				 * .getStringWidth("abcdefghijklmnopqrstuvwxyz ") / 1000.0f *
				 * fontSize; fwidth = fwidth / (float)
				 * "abcdefghijklmnopqrstuvwxyz" .length(); lineWidth =
				 * tlines[k].length() * fwidth; }
				 */
				if (maxWidth < lineWidth) {
					maxWidth = lineWidth;
				}
			}

			if (Style.CENTER.equals(halign)) {
				float offset = innerWidth - maxWidth;
				if (offset > 0) {
					offset = offset / 2.0f;
					tx += offset;
				}
			} else if (Style.RIGHT.equals(halign)) {
				float offset = innerWidth - maxWidth;
				if (offset > 0) {
					tx += offset;
				}
			}
			float ascent = textFont.getFontDescriptor().getAscent();
			float descent = textFont.getFontDescriptor().getDescent();
			
			ascent = ascent / 1000.0f * fontSize;
			descent = descent / 1000.0f * fontSize;
			
			//ty = ty + (descent * (-1));
			
			logger.debug("Text tx {} ty {} maxWidth {} textHeight {}", tx, ty,
					maxWidth, textHeight);
			logger.debug("Text ASCENT {} DESCENT {}", ascent, descent);

			logger.debug("Text TRANSFORMED ASCENT {} DESCENT {}", ascent, descent);
			
			drawDebugLineString(contentStream, tx, ty, maxWidth, textHeight, descent, settings);

			contentStream.beginText();

			if (formResources.getFonts().containsValue(textFont)) {
				String fontID = getFontID(textFont, formResources);
				logger.debug("Using Font: " + fontID);
				contentStream.appendRawCommands("/" + fontID + " " + fontSize
						+ " Tf\n");
			} else {
				contentStream.setFont(textFont, fontSize);
			}

			logger.debug("Writing: " + tx + " : " + (ty - fontSize + (descent * (-1))) + " as "
					+ cell.getType());
			contentStream.moveTextPositionByAmount(tx, (ty - fontSize + (descent * (-1))));

			contentStream.appendRawCommands(fontSize + " TL\n");
			for (int k = 0; k < tlines.length; k++) {
				contentStream.drawString(tlines[k]);
				if (k < tlines.length - 1) {
					contentStream.appendRawCommands("T*\n");
				}
			}

			contentStream.endText();

		} catch (IOException e) {
			logger.error("IO Exception", e);
			throw new PdfAsException("Error", e);
		}
	}

	public static void drawCaption(PDPage page,
			PDPageContentStream contentStream, float contentx, float contenty,
			float width, float height, float padding,
			PDFBoxTable abstractTable, PDDocument doc, Entry cell,
			PDResources formResources, ISettings settings)
			throws PdfAsException {

		logger.debug("Drawing Caption @ X: {} Y: {}", contentx, contenty);

		try {
			float fontSize = PDFBoxFont.defaultFontSize;
			PDFont textFont = PDFBoxFont.defaultFont;

			textFont = abstractTable.getFont().getFont(doc);
			fontSize = abstractTable.getFont().getFontSize();

			// get the cell Text
			String text = (String) cell.getValue();
			String[] tlines = text.split("\n");
			float textHeight = fontSize * tlines.length;

			Style cellStyle = cell.getStyle();
			String valign = cellStyle.getVAlign();
			String halign = cellStyle.getHAlign();

			drawString(page, contentStream, contentx, contenty, width, height,
					padding, abstractTable, doc, cell, fontSize, textHeight,
					valign, halign, tlines, textFont, formResources, settings);
		} catch (IOException e) {
			logger.error("IO Exception", e);
			throw new PdfAsException("Error", e);
		}
	}

	public static void drawValue(PDPage page,
			PDPageContentStream contentStream, float contentx, float contenty,
			float width, float height, float padding,
			PDFBoxTable abstractTable, PDDocument doc, Entry cell,
			PDResources formResources, ISettings settings)
			throws PdfAsException {

		logger.debug("Drawing Value @ X: {} Y: {}", contentx, contenty);

		try {
			float fontSize = PDFBoxFont.defaultFontSize;
			PDFont textFont = PDFBoxFont.defaultFont;

			textFont = abstractTable.getValueFont().getFont(doc);
			fontSize = abstractTable.getValueFont().getFontSize();

			// get the cell Text
			String text = (String) cell.getValue();
			String[] tlines = text.split("\n");
			float textHeight = fontSize * tlines.length;

			Style cellStyle = cell.getStyle();
			String valign = cellStyle.getValueVAlign();
			String halign = cellStyle.getValueHAlign();

			drawString(page, contentStream, contentx, contenty, width, height,
					padding, abstractTable, doc, cell, fontSize, textHeight,
					valign, halign, tlines, textFont, formResources, settings);
		} catch (IOException e) {
			logger.error("IO Exception", e);
			throw new PdfAsException("Error", e);
		}
	}

	public static void drawImage(PDPage page,
			PDPageContentStream contentStream, float contentx, float contenty,
			float width, float height, float padding,
			PDFBoxTable abstractTable, PDDocument doc, Entry cell,
			PDResources formResources, Map<String, ImageObject> images,
			ISettings settings) throws PdfAsException {
		try {
			float innerHeight = height;
			float innerWidth = width;
						
			String img_ref = (String) cell.getValue();
			if (!images.containsKey(img_ref)) {
				logger.error("Image not prepared! : " + img_ref);
				throw new PdfAsException("Image not prepared! : " + img_ref);
			}
			ImageObject image = images.get(img_ref);
			PDXObjectImage pdImage = image.getImage();

			float imgx = contentx;
			float hoffset = innerWidth - image.getWidth();
			if (cell.getStyle().getImageHAlign() != null
					&& cell.getStyle().getImageHAlign().equals(Style.LEFT)) {
				hoffset = hoffset / 2.0f;
				imgx += hoffset;
			} else if (cell.getStyle().getImageHAlign() != null
					&& cell.getStyle().getImageHAlign().equals(Style.RIGHT)) {
				imgx += hoffset;
			} else {
				hoffset = hoffset / 2.0f;
				imgx += hoffset;
			}

			float imgy = contenty;
			float voffset = innerHeight - image.getHeight();
			if (cell.getStyle().getImageVAlign() != null
					&& cell.getStyle().getImageVAlign().equals(Style.MIDDLE)) {
				voffset = voffset / 2.0f;
				imgy -= voffset;
			} else if (cell.getStyle().getImageVAlign() != null
					&& cell.getStyle().getImageVAlign().equals(Style.BOTTOM)) {
				imgy -= voffset;
			}

			drawDebugLine(contentStream, imgx, imgy, image.getWidth(),
					image.getHeight(), settings);

			// logger.debug("Image: " + imgx + " : " + (imgy -
			// image.getHeight()));
			contentStream.drawXObject(pdImage, imgx, imgy - image.getHeight(),
					image.getWidth(), image.getHeight());
		} catch (IOException e) {
			logger.error("IO Exception", e);
			throw new PdfAsException("Error", e);
		}

	}

	public static float[] getColSizes(PDFBoxTable abstractTable) {
		float[] origcolsSizes = abstractTable.getColsRelativeWith();
		int max_cols = abstractTable.getColCount();
		float[] colsSizes = new float[max_cols];
		if (origcolsSizes == null) {
			// set the column ratio for all columns to 1
			for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++) {
				colsSizes[cols_idx] = 1;
			}
		} else {
			// set the column ratio for all columns to 1
			for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++) {
				colsSizes[cols_idx] = origcolsSizes[cols_idx];
			}
		}

		// adapt
		float total = 0;

		for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++) {
			total += colsSizes[cols_idx];
		}

		for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++) {
			colsSizes[cols_idx] = (colsSizes[cols_idx] / total)
					* abstractTable.getWidth();
		}

		float sum = 0;

		for (int cols_idx = 0; cols_idx < colsSizes.length; cols_idx++) {
			sum += colsSizes[cols_idx];
		}

		logger.debug("Table Col Sizes SUM {} Table Width {}", sum,
				abstractTable.getWidth());
		logger.debug("Table Table Height {}", abstractTable.getHeight());

		return colsSizes;
	}

	public static void drawBorder(PDPage page,
			PDPageContentStream contentStream, float x, float y, float width,
			float height, PDFBoxTable abstractTable, PDDocument doc,
			boolean subtable, ISettings settings) throws PdfAsException {
		try {

			logger.debug("Drawing Table borders for "
					+ abstractTable.getOrigTable().getName());

			final int rows = abstractTable.getRowCount();
			float border = abstractTable.style.getBorder();
			float[] colsSizes = getColSizes(abstractTable);

			if (border > 0) {
				contentStream.setLineWidth(border);

				float x_from = x;
				float x_to = x + width;
				float y_from = y + height;
				float y_to = y + height;

				// draw first line
				logger.debug("ROW LINE: {} {} {} {}", x_from, y_from, x_to,
						y_from);
				contentStream.drawLine(x, y_from, x_to, y_from);

				// Draw all row borders
				for (int i = 0; i < rows; i++) {
					y_from -= abstractTable.getRowHeights()[i];

					// Draw row border!
					logger.debug("ROW LINE: {} {} {} {}", x_from, y_from, x_to,
							y_from);
					contentStream.drawLine(x, y_from, x_to, y_from);

				}

				// reset y for "line feed"
				y_from = y + height;
				y_to = y_from - abstractTable.getRowHeights()[0];

				// Draw all column borders
				for (int i = 0; i < rows; i++) {
					ArrayList<Entry> row = abstractTable.getRow(i);

					// reset x for "line feed"
					x_from = x;

					// draw first line
					logger.debug("COL LINE: {} {} {} {}", x_from, y_from,
							x_from, y_to);

					contentStream.drawLine(x_from, y_from, x_from, y_to);

					for (int j = 0; j < row.size(); j++) {
						Entry cell = (Entry) row.get(j);

						for (int k = 0; k < cell.getColSpan(); k++) {
							if (k + j < colsSizes.length) {
								x_from += colsSizes[k + j];
							}
						}
						logger.debug("COL LINE: {} {} {} {}", x_from, y_from,
								x_from, y_to);
						contentStream.drawLine(x_from, y_from, x_from, y_to);
					}

					if (i + 1 < rows) {
						y_from = y_to;
						y_to = y_from - abstractTable.getRowHeights()[i + 1];
					}
				}

			}
		} catch (Throwable e) {
			logger.warn("drawing table borders", e);
			throw new PdfAsException("drawing table borders", e);
		}
	}

	public static void drawTableBackground(PDPage page,
			PDPageContentStream contentStream, float x, float y, float width,
			float height, PDFBoxTable abstractTable, ISettings settings)
			throws PdfAsException {
		try {
			if (abstractTable.getBGColor() != null) {
				contentStream.setNonStrokingColor(abstractTable.getBGColor());
				contentStream.fillRect(x, y, abstractTable.getWidth(),
						abstractTable.getHeight());
				contentStream.setNonStrokingColor(Color.BLACK);
			}
		} catch (Throwable e) {
			logger.warn("drawing table borders", e);
			throw new PdfAsException("drawing table borders", e);
		}
	}

	private static void drawDebugLine(PDPageContentStream contentStream,
			float x, float y, float width, float height, ISettings settings) {
		if ("true".equals(settings.getValue(TABLE_DEBUG))) {
			try {
				contentStream.setStrokingColor(Color.RED);
				contentStream.drawLine(x, y, x + width, y);
				contentStream.setStrokingColor(Color.BLUE);
				contentStream.drawLine(x, y, x, y - height);
				contentStream.setStrokingColor(Color.GREEN);
				contentStream.drawLine(x + width, y, x + width, y - height);
				contentStream.setStrokingColor(Color.ORANGE);
				contentStream.drawLine(x, y - height, x + width, y - height);

				contentStream.setStrokingColor(Color.BLACK);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void drawDebugLineString(PDPageContentStream contentStream,
			float x, float y, float width, float height, float descent, ISettings settings) {
		if ("true".equals(settings.getValue(TABLE_DEBUG))) {
			try {
				contentStream.setStrokingColor(Color.RED);
				contentStream.drawLine(x, y, x + width, y);
				contentStream.setStrokingColor(Color.BLUE);
				contentStream.drawLine(x, y, x, y - height);
				contentStream.setStrokingColor(Color.GREEN);
				contentStream.drawLine(x + width, y, x + width, y - height);
				contentStream.setStrokingColor(Color.ORANGE);
				contentStream.drawLine(x, y - height, x + width, y - height);
				contentStream.setStrokingColor(Color.MAGENTA);
				contentStream.drawLine(x, y + (descent * (-1)) - height, x + width, y + (descent * (-1)) - height);

				contentStream.setStrokingColor(Color.BLACK);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	private static void drawDebugPadding(PDPageContentStream contentStream,
			float x, float y, float padding, float width, float height,
			ISettings settings) {
		if ("true".equals(settings.getValue(TABLE_DEBUG))) {
			try {
				contentStream.setStrokingColor(Color.RED);
				contentStream.drawLine(x, y, x + padding, y - padding);
				contentStream.drawLine(x + width, y, x + width - padding, y
						- padding);
				contentStream.drawLine(x + width, y - height, x + width
						- padding, y - height + padding);
				contentStream.drawLine(x, y - height, x + padding, y - height
						+ padding);
				contentStream.setStrokingColor(Color.BLACK);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	private static String getFontID(PDFont font, PDResources resources) {
		Iterator<java.util.Map.Entry<String, PDFont>> it = resources.getFonts()
				.entrySet().iterator();
		while (it.hasNext()) {
			java.util.Map.Entry<String, PDFont> entry = it.next();
			if (entry.getValue().equals(font)) {
				return entry.getKey();
			}
		}
		return "";
	}

}
