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
package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox2;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsWrappedIOException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.common.utils.ImageUtils;
import at.gv.egiz.pdfas.common.utils.PDFTextNormalizationUtils;
import at.gv.egiz.pdfas.common.utils.StringUtils;
import at.gv.egiz.pdfas.lib.impl.pdfbox2.PDFBOXObject;
import at.knowcenter.wag.egov.egiz.table.Entry;
import at.knowcenter.wag.egov.egiz.table.Style;
import at.knowcenter.wag.egov.egiz.table.Table;

public class PDFBoxTable {

	private static final Logger logger = LoggerFactory
			.getLogger(PDFBoxTable.class);

	private static final String NBSPACE = '\u00A0'+"";

	Table table;
	Style style;
	PDFBoxFont font;
	PDFBoxFont valueFont;
	ISettings settings;

	float padding;
	int positionX = 0;
	int positionY = 0;
	float tableWidth;
	float tableHeight;
	Color bgColor;

	boolean[] addPadding;
	float[] rowHeights;
	float[] colWidths;

	PDDocument originalDoc;

	PDFBOXObject pdfBoxObject;

	private void normalizeContent(Table abstractTable) throws PdfAsException {
		int rows = abstractTable.getRows().size();
		for (int i = 0; i < rows; i++) {
			ArrayList<Entry> row = this.table.getRows().get(i);
			for (int j = 0; j < row.size(); j++) {
				Entry cell = row.get(j);

				switch (cell.getType()) {
				case Entry.TYPE_CAPTION:
				case Entry.TYPE_VALUE:
					String value = (String) cell.getValue();

					String checkedOrNormalizedValue = null;
					PDFont pdFont;
					if (valueFont != null && (pdFont = valueFont.getFont()) != null) {
						try {
							// try to encode value using the provided font
							pdFont.getStringWidth(value);
							checkedOrNormalizedValue = value;
						} catch (IllegalArgumentException | IOException e) {
							logger.debug("Font '{}' does not support a character of the value '{}': {}", pdFont.getName(), value, String.valueOf(e));
						}
					}
					if (checkedOrNormalizedValue == null) {
						// when encoding failed or no font was declared -> normalize value in order to meet ansi encoding
						checkedOrNormalizedValue = PDFTextNormalizationUtils.normalizeText(value, WinAnsiEncoding.INSTANCE::contains); 
					}
					cell.setValue(checkedOrNormalizedValue);
					
					break;
				}
			}
		}
	}

	private void initializeStyle(Table abstractTable, PDFBoxTable parent,
			PDFBOXObject pdfBoxObject) throws IOException {
		this.table = abstractTable;

		if (parent != null) {
			style = Style.doInherit(abstractTable.getStyle(), parent.style);
		} else {
			style = abstractTable.getStyle();
		}

		if (style == null) {
			throw new IOException("Failed to determine Table style, for table "
					+ abstractTable.getName());
		}

		String fontString = style.getFont();

		String vfontString = style.getValueFont();
		
		// FIXME: Fix this bogus code section

		if (parent != null && style == parent.style) {
			font = parent.getFont();

			valueFont = parent.getValueFont();
		} else {
			if (fontString == null && parent != null && parent.style != null) {
				fontString = parent.style.getFont();
			} else if (fontString == null) {
				throw new IOException(
						"Failed to determine Table font style, for table "
								+ abstractTable.getName());
			}

			font = new PDFBoxFont(fontString, settings, pdfBoxObject);

			if (vfontString == null && parent != null && parent.style != null) {
				vfontString = parent.style.getValueFont();
			} else if (vfontString == null) {
				throw new IOException(
						"Failed to determine value Table font style, for table "
								+ abstractTable.getName());
			}

			valueFont = new PDFBoxFont(vfontString, settings, pdfBoxObject);
		}
		padding = style.getPadding();

		bgColor = style.getBgColor();

		try {
			normalizeContent(abstractTable);
		} catch (PdfAsException e) {
			throw new PdfAsWrappedIOException(e);
		}
	}

	public PDFBoxTable(Table abstractTable, PDFBoxTable parent, float fixSize,
			ISettings settings, PDFBOXObject pdfBoxObject) throws IOException,
			PdfAsException {
		this.settings = settings;
		this.pdfBoxObject = pdfBoxObject;
		this.originalDoc = pdfBoxObject.getDocument();
		initializeStyle(abstractTable, parent, pdfBoxObject);
		float[] relativSizes = abstractTable.getColsRelativeWith();
		if (relativSizes != null) {
			colWidths = new float[relativSizes.length];
			float totalrel = 0;

			for (int i = 0; i < relativSizes.length; i++) {
				totalrel += relativSizes[i];
			}

			float unit = (fixSize / totalrel);

			for (int i = 0; i < relativSizes.length; i++) {

				colWidths[i] = unit * relativSizes[i];
			}
		} else {
			colWidths = new float[abstractTable.getMaxCols()];
			float totalrel = abstractTable.getMaxCols();
			float unit = (fixSize / totalrel);
			for (int i = 0; i < colWidths.length; i++) {

				colWidths[i] = unit;
			}
		}
		calculateHeightsBasedOnWidths();

		logger.debug("Generating Table with fixed With {} got width {}",
				fixSize, getWidth());
	}

	public PDFBoxTable(Table abstractTable, PDFBoxTable parent,
			ISettings settings, PDFBOXObject pdfBoxObject) throws IOException,
			PdfAsException {
		this.settings = settings;
		this.pdfBoxObject = pdfBoxObject;
		this.originalDoc = pdfBoxObject.getDocument();
		initializeStyle(abstractTable, parent, pdfBoxObject);
		this.calculateWidthHeight();
	}

	private void calculateHeightsBasedOnWidths() throws IOException,
			PdfAsException {
		int rows = this.table.getRows().size();
		rowHeights = new float[rows];
		addPadding = new boolean[rows];

		for (int i = 0; i < rows; i++) {
			rowHeights[i] = 0;
		}

		for (int i = 0; i < rows; i++) {
			ArrayList<Entry> row = this.table.getRows().get(i);
			for (int j = 0; j < row.size(); j++) {
				Entry cell = row.get(j);

				float colWidth = 0;// colWidths[j];

				int colsleft = cell.getColSpan();

				if (j + colsleft > colWidths.length) {
					throw new IOException(
							"Configuration is wrong. Cannot determine column width!");
				}

				for (int k = 0; k < colsleft; k++) {
					colWidth = colWidth + colWidths[j + k];
				}

				float cellheight = getCellHeight(cell, colWidth);

				if (rowHeights[i] < cellheight) {
					rowHeights[i] = cellheight;
				}

				logger.debug("ROW: {} COL: {} Width: {} Height: {}", i, j,
						colWidth, cellheight);

				int span = cell.getColSpan() - 1;
				j += span;
			}
		}

		calcTotals();
	}

	private void calculateWidthHeight() throws IOException, PdfAsException {
		int cols = this.table.getMaxCols();
		colWidths = new float[cols];

		for (int i = 0; i < cols; i++) {
			colWidths[i] = 0;
		}

		int rows = this.table.getRows().size();
		rowHeights = new float[rows];

		for (int i = 0; i < rows; i++) {
			rowHeights[i] = 0;
		}

		for (int i = 0; i < rows; i++) {
			ArrayList<Entry> row = this.table.getRows().get(i);
			for (int j = 0; j < row.size(); j++) {
				Entry cell = row.get(j);
				float cellWidth = getCellWidth(cell);

				if (colWidths[j] < cellWidth) {
					colWidths[j] = cellWidth;
				}

				float cellheight = getCellHeight(cell);

				if (rowHeights[i] < cellheight) {
					rowHeights[i] = cellheight;
				}

				logger.debug("ROW: {} COL: {} Width: {} Height: {}", i, j,
						cellWidth, cellheight);

				int span = cell.getColSpan() - 1;
				j += span;
			}
		}

		calcTotals();
	}

	private void calcTotals() {

		this.tableHeight = 0;

		for (int i = 0; i < rowHeights.length; i++) {
			this.tableHeight += rowHeights[i];
		}

		// Post Process heights for inner Tables ...
		for (int i = 0; i < rowHeights.length; i++) {
			ArrayList<Entry> row = this.table.getRows().get(i);
			for (int j = 0; j < row.size(); j++) {
				Entry cell = row.get(j);
				if (cell.getType() == Entry.TYPE_TABLE) {
					PDFBoxTable tbl = (PDFBoxTable) cell.getValue();
					if (rowHeights[i] != tbl.getHeight()) {
						tbl.setHeight(rowHeights[i]);
					}
				}
			}
		}

		this.tableWidth = 0;

		for (int i = 0; i < colWidths.length; i++) {
			this.tableWidth += colWidths[i];
		}
	}

	private float getCellWidth(Entry cell) throws IOException, PdfAsException {
		boolean isValue = true;
		switch (cell.getType()) {
		case Entry.TYPE_CAPTION:
			isValue = false;
		case Entry.TYPE_VALUE:
			PDFont c = null;
			float fontSize;
			String string = (String) cell.getValue();
			if (isValue) {
				c = valueFont.getFont();//null
				fontSize = valueFont.getFontSize();
			} else {
				c = font.getFont();//null
				fontSize = font.getFontSize();
			}
			if (string == null) {
				string = "";
				cell.setValue(string);
			}
			if (string.contains(NBSPACE) || string.contains("\n")) {
				float maxWidth = 0;
				string = string.replace(NBSPACE, " ");
				String[] lines = string.split("\n");

				for (int i = 0; i < lines.length; i++) {
					float w = c.getStringWidth(lines[i]) / 1000 * fontSize;
					if (maxWidth < w) {
						maxWidth = w;
					}
				}
			}
			else{
				return c.getStringWidth(string) / 1000 * fontSize;
			}
		case Entry.TYPE_IMAGE:
			if (style != null && style.getImageScaleToFit() != null) {
				return style.getImageScaleToFit().getWidth();
			}
			return 80.f;
		case Entry.TYPE_TABLE:
			PDFBoxTable pdfBoxTable = null;
			if (cell.getValue() instanceof Table) {
				pdfBoxTable = new PDFBoxTable((Table) cell.getValue(), this,
						this.settings, pdfBoxObject);
				cell.setValue(pdfBoxTable);
			} else if (cell.getValue() instanceof PDFBoxTable) {
				pdfBoxTable = (PDFBoxTable) cell.getValue();
			} else {
				throw new IOException("Failed to build PDFBox Table");
			}
			return pdfBoxTable.getWidth();
		default:
			logger.warn("Invalid Cell Entry Type: " + cell.getType());
		}
		return 0;
	}

	private String concatLines(String[] lines) {
		String v = "";
		for (int i = 0; i < lines.length; i++) {
			v += lines[i];
			if (i + 1 < lines.length) {
				v += "\n";
			}
		}
		return v;
	}

	private String[] breakString(String value, float maxwidth, PDFont font,
			float fontSize) throws IOException {
		String[] words = value.split(" ");
		List<String> lines = new ArrayList<>();
		String cLineValue = "";
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			String[] lineBreaks = word.split("\n");
			if (lineBreaks.length > 1) {
				for (int j = 0; j < lineBreaks.length; j++) {
					String subword = lineBreaks[j];
					// if (cLine + subword.length() > maxline) {
					if (j == 0 && word.startsWith("\n")) {
						lines.add(cLineValue.trim());
						cLineValue = "";
					} else if (j != 0) {
						lines.add(cLineValue.trim());
						cLineValue = "";
					}
					// }
					String tmpLine = cLineValue + subword;
					tmpLine = tmpLine.replace(NBSPACE, " ");

					float size = font.getStringWidth(tmpLine) / 1000.0f
							* fontSize;
					if (size > maxwidth && cLineValue.length() != 0) {
						lines.add(cLineValue.trim());
						cLineValue = "";
					}
					cLineValue += subword + " ";
				}
			} else {
				String tmpLine = cLineValue + word;
				tmpLine = tmpLine.replace(NBSPACE, " ");

				float size = font.getStringWidth(tmpLine) / 1000.0f * fontSize;
				if (size > maxwidth && cLineValue.length() != 0) {
					lines.add(cLineValue.trim());
					cLineValue = "";
				}
				cLineValue += word + " ";
			}
		}
		lines.add(cLineValue.trim());
		for(int i=0;i<lines.size();i++){
			lines.set(i, lines.get(i).replace(NBSPACE, " "));
		}
		return lines.toArray(new String[0]);
	}

	private String[] breakString(String value, int maxline) {
		String[] words = value.split(" ");
		List<String> lines = new ArrayList<>();
		int cLine = 0;
		String cLineValue = "";
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			String[] lineBreaks = word.split("\n");
			if (lineBreaks.length > 1) {
				for (int j = 0; j < lineBreaks.length; j++) {
					String subword = lineBreaks[j];
					// if (cLine + subword.length() > maxline) {
					lines.add(cLineValue.trim());
					cLineValue = "";
					cLine = 0;
					// }
					cLineValue += subword + " ";
					cLine += subword.length();
				}
			} else {
				if (cLine + word.length() > maxline && cLineValue.length() != 0) {
					lines.add(cLineValue.trim());
					cLineValue = "";
					cLine = 0;
				}
				cLineValue += word + " ";
				cLine += word.length();
			}
		}
		lines.add(cLineValue.trim());
		return lines.toArray(new String[0]);
	}

	// private String[] breakString(String value, PDFont f, float maxwidth)
	// throws IOException {
	// String[] words = value.split(" ");
	// List<String> lines = new ArrayList<String>();
	// int cLine = 0;
	// String cLineValue = "";
	// for (int i = 0; i < words.length; i++) {
	// String word = words[i];
	// String[] lineBreaks = word.split("\n");
	// if (lineBreaks.length > 1) {
	// for (int j = 0; j < lineBreaks.length; j++) {
	// String subword = lineBreaks[j];
	// // if (cLine + subword.length() > maxline) {
	// lines.add(cLineValue.trim());
	// cLineValue = "";
	// cLine = 0;
	// // }
	// cLineValue += subword + " ";
	// cLine += subword.length();
	// }
	// } else {
	// if (f.getStringWidth(cLineValue + word) > maxwidth && cLineValue.length()
	// != 0) {
	// lines.add(cLineValue.trim());
	// cLineValue = "";
	// cLine = 0;
	// }
	// cLineValue += word + " ";
	// cLine += word.length();
	// }
	// }
	// lines.add(cLineValue.trim());
	// return lines.toArray(new String[0]);
	// }

	private float[] getStringHeights(String[] lines, PDFont c, float fontSize) {
		float[] heights = new float[lines.length];
		for (int i = 0; i < lines.length; i++) {
			float maxLineHeight = 0;
			try {
				byte[] linebytes = StringUtils.applyWinAnsiEncoding(lines[i]);
				for (int j = 0; j < linebytes.length; j++) {
					float he = c.getHeight(linebytes[j]) / 1000
							* fontSize;
					if (he > maxLineHeight) {
						maxLineHeight = he;
					}
				}
			} catch (UnsupportedEncodingException e) {
				logger.warn("failed to determine String height", e);
				maxLineHeight = c.getFontDescriptor().getCapHeight() / 1000
						* fontSize;
			} catch (IOException e) {
				logger.warn("failed to determine String height", e);
				maxLineHeight = c.getFontDescriptor().getCapHeight() / 1000
						* fontSize;
			}

			heights[i] = maxLineHeight;
		}

		return heights;
	}

	private float getCellHeight(Entry cell, float width) throws IOException,
			PdfAsException {
		boolean isValue = true;
		switch (cell.getType()) {
		case Entry.TYPE_CAPTION:
			isValue = false;
		case Entry.TYPE_VALUE:
			PDFont c = null;
			float fontSize;
			String string = (String) cell.getValue();
			if (isValue) {
				c = valueFont.getFont();//null
				fontSize = valueFont.getFontSize();
			} else {
				c = font.getFont();//null
				fontSize = font.getFontSize();
			}

			String[] lines = breakString(string, (width - padding * 2.0f), c,
					fontSize);
			cell.setValue(concatLines(lines));
			float[] heights = getStringHeights(lines, c, fontSize);
			return fontSize * heights.length + padding * 2;
		case Entry.TYPE_IMAGE:
			String imageFile = (String) cell.getValue();
			if (style != null && style.getImageScaleToFit() != null) {
				// if (style.getImageScaleToFit().getHeight() < width) {
				return style.getImageScaleToFit().getHeight() + padding * 2;
				// }
			}
			Dimension dim = ImageUtils.getImageDimensions(imageFile, settings);
			float wfactor = (float) ((width - padding * 2.0f) / dim.getWidth());
			float scaleFactor = wfactor;
			float iheight = (float) Math
					.floor(scaleFactor * dim.getHeight());
			//if (dim.getHeight() > 80.0f) {
			//	return width + padding * 2;
			//}
			return iheight + padding * 2;
		case Entry.TYPE_TABLE:
			PDFBoxTable pdfBoxTable = null;
			if (cell.getValue() instanceof Table) {
				pdfBoxTable = new PDFBoxTable((Table) cell.getValue(), this,
						width, this.settings, this.pdfBoxObject);
				cell.setValue(pdfBoxTable);
			} else if (cell.getValue() instanceof PDFBoxTable) {
				// recreate here beacuse of fixed width!
				pdfBoxTable = (PDFBoxTable) cell.getValue();
				pdfBoxTable = new PDFBoxTable(pdfBoxTable.table, this, width,
						this.settings, this.pdfBoxObject);
				cell.setValue(pdfBoxTable);
			} else {
				throw new IOException("Failed to build PDFBox Table");
			}
			return pdfBoxTable.getHeight();
		default:
			logger.warn("Invalid Cell Entry Type: " + cell.getType());
		}
		return 0;
	}

	private float getCellHeight(Entry cell) throws IOException, PdfAsException {
		boolean isValue = true;
		switch (cell.getType()) {
		case Entry.TYPE_CAPTION:
			isValue = false;
		case Entry.TYPE_VALUE:
			PDFont c = null;
			float fontSize;
			String string = (String) cell.getValue();
			if (isValue) {
				c = valueFont.getFont();//null
				fontSize = valueFont.getFontSize();
			} else {
				c = font.getFont();//null
				fontSize = font.getFontSize();
			}

			if (string.contains("\n")) {
				String[] lines = string.split("\n");

				return fontSize * lines.length + padding * 2;
			} else {
				return fontSize + padding * 2;
			}
		case Entry.TYPE_IMAGE:
			String imageFile = (String) cell.getValue();
			if (style != null && style.getImageScaleToFit() != null) {
				return style.getImageScaleToFit().getHeight() + padding * 2;
			}
			Dimension dim = ImageUtils.getImageDimensions(imageFile, settings);
			if (dim.getHeight() > 80.0f) {
				return 80.0f + padding * 2;
			}
			return (float) dim.getHeight() + padding * 2;

		case Entry.TYPE_TABLE:
			PDFBoxTable pdfBoxTable = null;
			if (cell.getValue() instanceof Table) {
				pdfBoxTable = new PDFBoxTable((Table) cell.getValue(), this,
						this.settings, pdfBoxObject);
				cell.setValue(pdfBoxTable);
			} else if (cell.getValue() instanceof PDFBoxTable) {
				pdfBoxTable = (PDFBoxTable) cell.getValue();
			} else {
				throw new IOException("Failed to build PDFBox Table");
			}
			return pdfBoxTable.getHeight();
		default:
			logger.warn("Invalid Cell Entry Type: " + cell.getType());
		}
		return 0;
	}

	public int getX() {
		return this.positionX;
	}

	public int getY() {
		return this.positionY;
	}

	public float getWidth() {
		return tableWidth;
	}

	public float getHeight() {
		return tableHeight;
	}

	public void setHeight(float height) {
		float diff = height - this.getHeight();
		if (diff > 0) {
			this.rowHeights[rowHeights.length - 1] += diff;
			calcTotals();
		} else {
			logger.warn("Table cannot be this small!");
		}
	}

	public float[] getRowHeights() {
		return rowHeights;
	}

	public int getRowCount() {
		return this.table.getRows().size();
	}

	public int getColCount() {
		return this.table.getMaxCols();// .getColsRelativeWith().length;
	}

	public float[] getColsRelativeWith() {
		return this.table.getColsRelativeWith();
	}

	public float getPadding() {
		return this.padding;
	}

	public void dumpTable() {
		logger.debug("=====================================================================");
		logger.debug("Information about: " + this.table.getName());
		logger.debug("\tDimensions: {} x {} (W x H)", this.tableWidth,
				this.tableHeight);
		logger.debug("\tPadding: {}", padding);
		logger.debug("\t================================");
		logger.debug("\tRow Heights:");
		for (int i = 0; i < rowHeights.length; i++) {
			logger.debug("\t[{}] : {}", i, this.rowHeights[i]);
		}
		logger.debug("\t================================");
		logger.debug("\tCol Widths:");
		for (int i = 0; i < colWidths.length; i++) {
			logger.debug("\t[{}] : {}", i, this.colWidths[i]);
		}
		logger.debug("\t================================");
		logger.debug("\tTable:");
		logger.debug("\t" + this.table.toString());
		logger.debug("=====================================================================");
	}

	public Table getOrigTable() {
		return this.table;
	}

	public ArrayList<Entry> getRow(int i) {
		return this.table.getRows().get(i);
	}

	public PDFBoxFont getFont() {
		return font;
	}

	public PDFBoxFont getValueFont() {
		return valueFont;
	}

	public Color getBGColor() {
		return this.bgColor;
	}
}
