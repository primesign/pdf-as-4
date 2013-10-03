package at.gv.egiz.pdfas.lib.impl.signing;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.impl.signing.sig_interface.PDFASSignatureInterface;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;

public interface IPdfSigner {

    void signPDF(PDFObject pdfObject,
                 RequestedSignature requestedSignature, PDFASSignatureInterface signer) throws PdfAsException;
}
