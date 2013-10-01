package at.gv.egiz.pdfas.lib.impl.signing;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;

public interface IPdfSigner {

    void signPDF(PDFObject pdfObject,
                 RequestedSignature requestedSignature, IPlainSigner signer) throws PdfAsException;
}
