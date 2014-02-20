/*******************************************************************************
 * <copyright> Copyright 2014 by E-Government Innovation Center EGIZ, Graz, Austria </copyright>
 * PDF-AS has been contracted by the E-Government Innovation Center EGIZ, a
 * joint initiative of the Federal Chancellery Austria and Graz University of
 * Technology.
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * http://www.osor.eu/eupl/
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * 
 * This product combines work with different licenses. See the "NOTICE" text
 * file for details on the various modules and licenses.
 * The "NOTICE" text file is part of the distribution. Any derivative works
 * that you distribute must include a readable copy of the "NOTICE" text file.
 ******************************************************************************/
package com.lowagie.text.pdf;

import java.io.IOException;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PDFIOException;
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
              throw new PDFIOException("error.pdf.io.02", new NullPointerException("Image dictionary not found in document structure!"));
           }
        }
        else
        {
           throw new PDFIOException("error.pdf.io.02", new NullPointerException("Resource dictionary not found in document structure!"));
        }
     }
	
}
