package at.gv.egiz.pdfas.stmp.itext;

import at.gv.egiz.pdfas.lib.impl.stamping.IPDFVisualObject;
import at.knowcenter.wag.egov.egiz.pdf.Pos;
import com.lowagie.text.pdf.PdfPTable;

public class ITextVisualObject implements IPDFVisualObject {

    private PdfPTable table;
    private float x;
    private float y;
    private int page;

    public  ITextVisualObject(PdfPTable table) {
        this.table = table;
    }

    public void setWidth(float width) {
        table.setTotalWidth(width);
    }

    public void fixWidth() {
        table.setLockedWidth(true);
    }

    public float getHeight() {
        return this.table.getTotalHeight();
    }

    public float getWidth() {
        return this.table.getTotalWidth();
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

    public PdfPTable getTable() {
        return table;
    }
}
