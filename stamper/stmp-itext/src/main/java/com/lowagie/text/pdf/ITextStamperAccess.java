package com.lowagie.text.pdf;

import java.io.IOException;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.stmp.itext.ITextStamper;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;

public class ITextStamperAccess {

	private static final Logger logger = LoggerFactory.getLogger(ITextStamperAccess.class);
	
	public static void replacePlaceholder(PdfStamper stamper, int pageNr, String placeholderName) 
    		throws BadElementException, MalformedURLException, IOException, 
    		BadPdfFormatException, PdfAsException {
        Image img = Image.getInstance(ITextStamper.class.getResource("/placeholder/empty.jpg"));
        PdfImage pImg = new PdfImage(img, "Imwurscht", null);
        PdfStamperImp stamperImp = (PdfStamperImp)stamper.getWriter();
        PdfIndirectObject ind = stamperImp.addToBody(pImg);

        PdfDictionary resources = stamper.getReader().getPageN(pageNr).getAsDict(PdfName.RESOURCES);
        if (ind != null && resources != null)
        {
           PdfDictionary xobjDict = resources.getAsDict(PdfName.XOBJECT);
           if (xobjDict != null)
           {
              xobjDict.put(new PdfName(placeholderName), ind.getIndirectReference());
              stamperImp.markUsed(resources);
           }
           else
           {
              throw new PdfAsException("failed to write PDF", new NullPointerException("Image dictionary not found in document structure!"));
           }
        }
        else
        {
           throw new PdfAsException("failed to write PDF", new NullPointerException("Resource dictionary not found in document structure!"));
        }
     }
	
}
