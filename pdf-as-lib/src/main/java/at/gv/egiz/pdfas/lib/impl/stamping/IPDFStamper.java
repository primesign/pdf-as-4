package at.gv.egiz.pdfas.lib.impl.stamping;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;
import at.knowcenter.wag.egov.egiz.pdf.PositioningInstruction;
import at.knowcenter.wag.egov.egiz.table.Table;

public interface IPDFStamper {
    public IPDFVisualObject createVisualPDFObject(PDFObject pdf, Table table);
    public byte[] writeVisualObject(IPDFVisualObject visualObject, PositioningInstruction positioningInstruction,
                                    byte[] pdfData) throws PdfAsException;
    
    public void setSettings(ISettings settings);
}
