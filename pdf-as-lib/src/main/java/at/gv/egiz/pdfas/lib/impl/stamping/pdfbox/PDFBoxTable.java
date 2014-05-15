package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox;

import java.awt.Color;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsWrappedIOException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.common.utils.StringUtils;
import at.knowcenter.wag.egov.egiz.table.Entry;
import at.knowcenter.wag.egov.egiz.table.Style;
import at.knowcenter.wag.egov.egiz.table.Table;

public class PDFBoxTable {

	private static final Logger logger = LoggerFactory
			.getLogger(PDFBoxTable.class);

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

	float[] rowHeights;
	float[] colWidths;

	private void normalizeContent(Table abstractTable) throws PdfAsException {
		try {
			int rows = abstractTable.getRows().size();
			for (int i = 0; i < rows; i++) {
				ArrayList<Entry> row = this.table.getRows().get(i);
				for (int j = 0; j < row.size(); j++) {
					Entry cell = (Entry) row.get(j);

					switch (cell.getType()) {
					case Entry.TYPE_CAPTION:
					case Entry.TYPE_VALUE:
						String value = (String) cell.getValue();
						cell.setValue(StringUtils
								.convertStringToPDFFormat(value));
						break;
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			throw new PdfAsException("Unsupported Encoding", e);
		}
	}

	private void initializeStyle(Table abstractTable, PDFBoxTable parent)
			throws IOException {
		this.table = abstractTable;
		try {
			normalizeContent(abstractTable);
		} catch(PdfAsException e) {
			throw new PdfAsWrappedIOException(e);
		}
		if (abstractTable.getStyle() != null) {
			style = abstractTable.getStyle();
		}

		if (style == null && parent != null) {
			style = parent.style;
		}

		if (style == null) {
			throw new IOException("Failed to determine Table style, for table " + abstractTable.getName());
		}

		String fontString = style.getFont();

		String vfontString = style.getValueFont();
		
		if(fontString == null || vfontString == null) {
			
		}
		
		if (parent != null && style == parent.style) {
			font = parent.getFont();

			valueFont = parent.getValueFont();
		} else {
			if(fontString == null && parent != null && parent.style != null) {
				fontString = parent.style.getFont();
			} else if(fontString == null) {
				throw new IOException("Failed to determine Table font style, for table " + abstractTable.getName());
			}
			
			font = new PDFBoxFont(fontString, settings);
			
			if(vfontString == null && parent != null && parent.style != null) {
				vfontString = parent.style.getValueFont();
			} else if(fontString == null) {
				throw new IOException("Failed to determine value Table font style, for table " + abstractTable.getName());
			}
			
			valueFont = new PDFBoxFont(vfontString, settings);
		}
		padding = style.getPadding();

		bgColor = style.getBgColor();
	}

	public PDFBoxTable(Table abstractTable, PDFBoxTable parent, float fixSize,
			ISettings settings) throws IOException {
		this.settings = settings;
		initializeStyle(abstractTable, parent);
		float[] relativSizes = abstractTable.getColsRelativeWith();
		colWidths = new float[relativSizes.length];
		float totalrel = 0;

		for (int i = 0; i < relativSizes.length; i++) {
			totalrel += relativSizes[i];
		}

		float unit = (fixSize / totalrel);

		for (int i = 0; i < relativSizes.length; i++) {

			colWidths[i] = unit * relativSizes[i];
		}

		calculateHeightsBasedOnWidths();
	}

	public PDFBoxTable(Table abstractTable, PDFBoxTable parent,
			ISettings settings) throws IOException {
		this.settings = settings;
		initializeStyle(abstractTable, parent);
		this.calculateWidthHeight();
	}

	private void calculateHeightsBasedOnWidths() throws IOException {
		int rows = this.table.getRows().size();
		rowHeights = new float[rows];

		for (int i = 0; i < rows; i++) {
			rowHeights[i] = 0;
		}

		for (int i = 0; i < rows; i++) {
			ArrayList<Entry> row = this.table.getRows().get(i);
			for (int j = 0; j < row.size(); j++) {
				Entry cell = (Entry) row.get(j);

				float cellheight = getCellHeight(cell, colWidths[j]);

				if (rowHeights[i] < cellheight) {
					rowHeights[i] = cellheight;
				}

				logger.debug("ROW: {} COL: {} Width: {} Height: {}", i, j,
						cellheight);

				int span = cell.getColSpan() - 1;
				j += span;
			}
		}

		calcTotals();
	}

	private void calculateWidthHeight() throws IOException {
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
				Entry cell = (Entry) row.get(j);
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
			this.tableHeight += rowHeights[i] + padding * 2;
		}

		this.tableWidth = 0;

		for (int i = 0; i < colWidths.length; i++) {
			this.tableWidth += colWidths[i] + padding * 2;
		}
	}

	private float getCellWidth(Entry cell) throws IOException {
		boolean isValue = true;
		switch (cell.getType()) {
		case Entry.TYPE_CAPTION:
			isValue = false;
		case Entry.TYPE_VALUE:
			PDFont c = null;
			float fontSize;
			String string = (String) cell.getValue();
			if (isValue) {
				c = valueFont.getFont(null);
				fontSize = valueFont.getFontSize();
			} else {
				c = font.getFont(null);
				fontSize = font.getFontSize();
			}
			if (string == null) {
				string = "";
				cell.setValue(string);
			}
			if (string.contains("\n")) {
				float maxWidth = 0;
				String[] lines = string.split("\n");
				for (int i = 0; i < lines.length; i++) {
					float w = c.getStringWidth(lines[i]) / 1000 * fontSize;
					if (maxWidth < w) {
						maxWidth = w;
					}
				}
				return maxWidth;
			} else {
				return c.getStringWidth(string) / 1000 * fontSize;
			}
		case Entry.TYPE_IMAGE:
			if(style != null && style.getImageScaleToFit() != null) {
				return style.getImageScaleToFit().getWidth();
			}
			return 80.f;
		case Entry.TYPE_TABLE:
			PDFBoxTable pdfBoxTable = null;
			if (cell.getValue() instanceof Table) {
				pdfBoxTable = new PDFBoxTable((Table) cell.getValue(), this,
						this.settings);
				cell.setValue(pdfBoxTable);
			} else if (cell.getValue() instanceof PDFBoxTable) {
				pdfBoxTable = (PDFBoxTable) cell.getValue();
			} else {
				throw new IOException("Failed to build PDFBox Table");
			}
			return pdfBoxTable.getWidth();
		default:
			logger.warn("Invalid Cell Entry Type: " + cell.getType());
			// return font.getFontDescriptor().getFontBoundingBox().getHeight()
			// / 1000 * fontSize;
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

	private String[] breakString(String value, int maxline) {
		String[] words = value.split(" ");
		List<String> lines = new ArrayList<String>();
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

	private float getCellHeight(Entry cell, float width) throws IOException {
		boolean isValue = true;
		switch (cell.getType()) {
		case Entry.TYPE_CAPTION:
			isValue = false;
		case Entry.TYPE_VALUE:
			PDFont c = null;
			float fontSize;
			String string = (String) cell.getValue();
			if (isValue) {
				c = valueFont.getFont(null);
				fontSize = valueFont.getFontSize();
			} else {
				c = font.getFont(null);
				fontSize = font.getFontSize();
			}

			float fwidth;
			if (c instanceof PDType1Font) {
				fwidth = c.getFontDescriptor().getFontBoundingBox().getWidth()
						/ 1000.0f * fontSize;
			} else {
				fwidth = c.getStringWidth("abcdefghijklmnopqrstuvwxyz ") / 1000.0f * fontSize;
				fwidth = fwidth / (float)"abcdefghijklmnopqrstuvwxyz".length();
			}

			logger.debug("Font Width: {}", fwidth);
			int maxcharcount = (int) ((width - padding * 2) / fwidth) - 1;
			logger.debug("Max {} chars per line!", maxcharcount);
			float fheight = c.getFontDescriptor().getFontBoundingBox()
					.getHeight()
					/ 1000 * fontSize;

			String[] lines = breakString(string, maxcharcount);
			cell.setValue(concatLines(lines));
			return fheight * lines.length;
		case Entry.TYPE_IMAGE:
			if(style != null && style.getImageScaleToFit() != null) {
				if( style.getImageScaleToFit().getHeight() < width) {
					return style.getImageScaleToFit().getHeight();
				}
			}
			return width;
		case Entry.TYPE_TABLE:
			PDFBoxTable pdfBoxTable = null;
			if (cell.getValue() instanceof Table) {
				pdfBoxTable = new PDFBoxTable((Table) cell.getValue(), this,
						width - padding, this.settings);
				cell.setValue(pdfBoxTable);
			} else if (cell.getValue() instanceof PDFBoxTable) {
				// recreate here beacuse of fixed width!
				pdfBoxTable = (PDFBoxTable) cell.getValue();
				pdfBoxTable = new PDFBoxTable(pdfBoxTable.table, this, width
						- padding, this.settings);
				cell.setValue(pdfBoxTable);
			} else {
				throw new IOException("Failed to build PDFBox Table");
			}
			return pdfBoxTable.getHeight() - padding;
		default:
			logger.warn("Invalid Cell Entry Type: " + cell.getType());
		}
		return 0;
	}

	private float getCellHeight(Entry cell) throws IOException {
		boolean isValue = true;
		switch (cell.getType()) {
		case Entry.TYPE_CAPTION:
			isValue = false;
		case Entry.TYPE_VALUE:
			PDFont c = null;
			float fontSize;
			String string = (String) cell.getValue();
			if (isValue) {
				c = valueFont.getFont(null);
				fontSize = valueFont.getFontSize();
			} else {
				c = font.getFont(null);
				fontSize = font.getFontSize();
			}
			float fheight = c.getFontDescriptor().getFontBoundingBox()
					.getHeight()
					/ 1000 * fontSize;
			if (string.contains("\n")) {
				String[] lines = string.split("\n");

				return fheight * lines.length;
			} else {
				return fheight;
			}
		case Entry.TYPE_IMAGE:
			if(style != null && style.getImageScaleToFit() != null) {
				return style.getImageScaleToFit().getHeight();
			}
			return 80.f;
		case Entry.TYPE_TABLE:
			PDFBoxTable pdfBoxTable = null;
			if (cell.getValue() instanceof Table) {
				pdfBoxTable = new PDFBoxTable((Table) cell.getValue(), this,
						this.settings);
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

	public float[] getRowHeights() {
		return rowHeights;
	}

	public int getRowCount() {
		return this.table.getRows().size();
	}

	public int getColCount() {
		return this.table.getColsRelativeWith().length;
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
