package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox;

import java.io.IOException;

import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFVisualObject;
import at.knowcenter.wag.egov.egiz.table.Table;

public class PdfBoxVisualObject implements IPDFVisualObject {

	private Table abstractTable;
	private PDFBoxTable table;
	private float width;
	private float x;
	private float y;
	private int page;
	private ISettings settings;

	public PdfBoxVisualObject(Table table, ISettings settings)
			throws IOException {
		this.abstractTable = table;
		this.table = new PDFBoxTable(table, null, settings);
		this.settings = settings;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public void fixWidth() {
		try {
			table = new PDFBoxTable(abstractTable, null, this.width,  settings);
		} catch (IOException e) {
			// should not occur
			e.printStackTrace();
		}
	}

	public float getHeight() {
		return table.getHeight();
	}

	public float getWidth() {
		return table.getWidth();
	}

	public void setXPos(float x) {
		this.x = x;
	}

	public void setYPos(float y) {
		this.y = y;
	}
	
	public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public PDFBoxTable getTable() {
		return this.table;
	}
}
