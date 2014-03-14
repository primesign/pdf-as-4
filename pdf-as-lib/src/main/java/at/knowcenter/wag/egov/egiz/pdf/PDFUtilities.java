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
/**
 * <copyright> Copyright 2006 by Know-Center, Graz, Austria </copyright>
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
 *
 * $Id: PDFUtilities.java,v 1.3 2006/10/31 08:09:33 wprinz Exp $
 */
package at.knowcenter.wag.egov.egiz.pdf;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;

import at.gv.egiz.pdfas.common.exceptions.PDFIOException;


/**
 * Contains useful helpers for accessing PDF documents.
 *
 * @author wprinz
 * @author mruhmer
 */
public abstract class PDFUtilities
{
    public static float calculatePageLength(PDDocument document, int page, float effectivePageHeight, /*int pagerotation,*/ boolean legacy32) throws PDFIOException {
	    //int last_page_id = document.getNumberOfPages();
	    List allPages = document.getDocumentCatalog().getAllPages();
	    PDPage pdpage = (PDPage) allPages.get(page);
	    //pdpage.setRotation(pagerotation);
	    return calculatePageLength(pdpage, effectivePageHeight, legacy32);
	}

    public static float calculatePageLength(PDPage page, float effectivePageHeight, boolean legacy32) throws PDFIOException
    {
        try{
            PDFPage my_page = new PDFPage(effectivePageHeight, legacy32);
            PDResources resources = page.findResources();
            COSStream stream = page.getContents().getStream();
            //List<PDThreadBead> articles = page.getThreadBeads();
            //my_page.processMyPage(page);
            my_page.processStream(page, resources, stream);
            return my_page.getMaxPageLength();
        }
        catch (IOException e)
        {
            throw new PDFIOException("error.pdf.stamp.11", e);
        }
    }

}
