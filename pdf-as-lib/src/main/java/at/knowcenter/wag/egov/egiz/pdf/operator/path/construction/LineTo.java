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
 */
package at.knowcenter.wag.egov.egiz.pdf.operator.path.construction;

import at.knowcenter.wag.egov.egiz.pdf.PDFPage;
import at.knowcenter.wag.egov.egiz.pdf.operator.path.PathConstructionOperatorProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.util.PDFOperator;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;

/**
 * Append a straight line segment from the current point to the point (x, y). The new current point shall be (x, y).
 *
 * @see "PDF 1.7 specification, Section 8.5.2 'Path Construction Operators'"
 * @author PdfBox, modified by Datentechnik Innovation GmbH
 */
public class LineTo extends PathConstructionOperatorProcessor {

	private Log log = LogFactory.getLog(getClass());

	public LineTo(PDFPage context) {
		super(context);
	}

	@Override
	public void process(PDFOperator operator, List operands) throws IOException {
		try {
			PDFPage pdfPage = (PDFPage) context;

			COSNumber x = (COSNumber) operands.get(0);
			COSNumber y = (COSNumber) operands.get(1);
			Point2D p = transform(x.doubleValue(), y.doubleValue());

			pdfPage.getCurrentPath().lineTo((float) p.getX(), (float) p.getY());

			if (log.isTraceEnabled()) {
				log.trace("Adding line to x:" + p.getX() + ",y:" + p.getY());
			}
		} catch (Exception e) {
			log.warn("Error processing operator 'l'.", e);
		}
	}

}
