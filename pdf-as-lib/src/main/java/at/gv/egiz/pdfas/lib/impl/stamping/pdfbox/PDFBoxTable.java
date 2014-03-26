package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.knowcenter.wag.egov.egiz.table.Entry;
import at.knowcenter.wag.egov.egiz.table.Style;
import at.knowcenter.wag.egov.egiz.table.Table;

public class PDFBoxTable {

	private static final Logger logger = LoggerFactory
			.getLogger(PDFBoxTable.class);

	Table table;
	Style style;
	PDFont font;
	PDFont valueFont;

	float padding;
	int positionX = 0;
	int positionY = 0;
	float tableWidth;
	float tableHeight;
	float fontSize;

	float[] rowHeights;
	float[] colWidths;

	private void initializeStyle(Table abstractTable, PDFBoxTable parent)
			throws IOException {
		this.table = abstractTable;

		if (abstractTable.getStyle() != null) {
			style = abstractTable.getStyle();
		}

		if (style == null && parent != null) {
			style = parent.style;
		}

		if (style == null) {
			throw new IOException("Failed to determine Table style");
		}

		String fontString = style.getFont();
		font = PDType1Font.COURIER;

		String vfontString = style.getValueFont();
		valueFont = font;

		padding = style.getPadding();
		fontSize = 5f;
	}

	public PDFBoxTable(Table abstractTable, PDFBoxTable parent, float fixSize)
			throws IOException {
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

	public PDFBoxTable(Table abstractTable, PDFBoxTable parent)
			throws IOException {
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
			ArrayList row = (ArrayList) this.table.getRows().get(i);
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
			ArrayList row = (ArrayList) this.table.getRows().get(i);
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
			String string = (String) cell.getValue();
			if (isValue) {
				c = valueFont;
			} else {
				c = font;
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
			return 80.f;
		case Entry.TYPE_TABLE:
			PDFBoxTable pdfBoxTable = null;
			if (cell.getValue() instanceof Table) {
				pdfBoxTable = new PDFBoxTable((Table) cell.getValue(), this);
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
					//if (cLine + subword.length() > maxline) {
						lines.add(cLineValue.trim());
						cLineValue = "";
						cLine = 0;
					//}
					cLineValue += subword + " ";
					cLine += subword.length();
				}
			} else {
				if (cLine + word.length() > maxline && 
						cLineValue.length() != 0) {
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

	private float getCellHeight(Entry cell, float width) throws IOException {
		boolean isValue = true;
		switch (cell.getType()) {
		case Entry.TYPE_CAPTION:
			isValue = false;
		case Entry.TYPE_VALUE:
			PDFont c = null;
			String string = (String) cell.getValue();
			if (isValue) {
				c = valueFont;
			} else {
				c = font;
			}

			float fwidth = c.getFontDescriptor().getFontBoundingBox()
					.getWidth()
					/ 1000 * fontSize;

			int maxcharcount = (int) ((width - padding * 2) / fwidth) - 1;
			logger.info("Max {} chars per line!", maxcharcount);
			float fheight = c.getFontDescriptor().getFontBoundingBox()
					.getHeight()
					/ 1000 * fontSize;

			String[] lines = breakString(string, maxcharcount);
			cell.setValue(concatLines(lines));
			return fheight * lines.length;
		case Entry.TYPE_IMAGE:
			return width;
		case Entry.TYPE_TABLE:
			PDFBoxTable pdfBoxTable = null;
			if (cell.getValue() instanceof Table) {
				pdfBoxTable = new PDFBoxTable((Table) cell.getValue(), this,
						width - padding);
				cell.setValue(pdfBoxTable);
			} else if (cell.getValue() instanceof PDFBoxTable) {
				pdfBoxTable = (PDFBoxTable) cell.getValue();
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
			String string = (String) cell.getValue();
			if (isValue) {
				c = valueFont;
			} else {
				c = font;
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
			return 80.f;
		case Entry.TYPE_TABLE:
			PDFBoxTable pdfBoxTable = null;
			if (cell.getValue() instanceof Table) {
				pdfBoxTable = new PDFBoxTable((Table) cell.getValue(), this);
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
		logger.info("=====================================================================");
		logger.info("Information about: " + this.table.getName());
		logger.info("\tDimensions: {} x {} (W x H)", this.tableWidth, this.tableHeight);
		logger.info("\tPadding: {}", padding);
		logger.info("\t================================");
		logger.info("\tRow Heights:");
		for (int i = 0; i < rowHeights.length; i++) {
			logger.info("\t[{}] : {}", i, this.rowHeights[i]);
		}
		logger.info("\t================================");
		logger.info("\tCol Widths:");
		for (int i = 0; i < colWidths.length; i++) {
			logger.info("\t[{}] : {}", i, this.colWidths[i]);
		}
		logger.info("=====================================================================");
	}
	
	public ArrayList getRow(int i) {
		return (ArrayList) this.table.getRows().get(i);
	}
}
