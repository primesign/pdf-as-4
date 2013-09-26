package at.gv.egiz.pdfas.lib.impl.stamping;

public interface IPDFVisualObject {
    public void setWidth(float width);
    public void fixWidth();
    public float getHeight();
    public float getWidth();
    public void setXPos(float x);
    public void setYPos(float x);
    public int getPage();
    public void setPage(int page);
}
