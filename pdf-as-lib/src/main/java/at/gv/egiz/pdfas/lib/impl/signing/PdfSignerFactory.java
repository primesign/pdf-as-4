package at.gv.egiz.pdfas.lib.impl.signing;

import at.gv.egiz.pdfas.lib.impl.signing.pdfbox.PADESPDFBOXSigner;

public class PdfSignerFactory {
    public static IPdfSigner createPdfSigner() {
        return new PADESPDFBOXSigner();
    }
}
