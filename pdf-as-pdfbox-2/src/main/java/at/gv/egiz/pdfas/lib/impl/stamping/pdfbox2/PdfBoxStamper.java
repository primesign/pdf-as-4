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
package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox2;

import java.io.IOException;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsWrappedIOException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.impl.pdfbox2.PDFBOXObject;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFStamper;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFVisualObject;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;
import at.knowcenter.wag.egov.egiz.pdf.PositioningInstruction;
import at.knowcenter.wag.egov.egiz.table.Table;

public class PdfBoxStamper implements IPDFStamper {

//	private static final Logger logger = LoggerFactory.getLogger(PdfBoxStamper.class);

//	private PDFTemplateBuilder pdfBuilder;

	public PdfBoxStamper() {
//		this.pdfBuilder = new PDVisibleSigBuilder();
	}
	
	public IPDFVisualObject createVisualPDFObject(PDFObject pdf, Table table) throws IOException {
		try {
			PDFBOXObject pdfboxObject = (PDFBOXObject)pdf;
			return new PdfBoxVisualObject(table, pdf.getStatus().getSettings(), pdfboxObject);
		} catch (PdfAsException e) {
			throw new PdfAsWrappedIOException(e);
		}
	}

	public byte[] writeVisualObject(IPDFVisualObject visualObject,
			PositioningInstruction positioningInstruction, byte[] pdfData,
			String placeholderName) throws PdfAsException {
		return null;
	}

	public void setSettings(ISettings settings) {
		// not needed currently
	}

}
