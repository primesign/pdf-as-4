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
 */
package at.knowcenter.wag.egov.egiz.pdfbox2.pdf.operator.path.construction;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;

import at.knowcenter.wag.egov.egiz.pdfbox2.pdf.PDFPage;
import at.knowcenter.wag.egov.egiz.pdfbox2.pdf.operator.path.PathConstructionOperatorProcessor;

/**
 * Begin a new subpath by moving the current point to coordinates (x, y), omitting any connecting line segment. If the
 * previous path construction operator in the current path was also m, the new m overrides it; no vestige of the
 * previous m operation remains in the path.
 *
 * @see "PDF 1.7 specification, Section 8.5.2 'Path Construction Operators'"
 * @author PdfBox, modified by Datentechnik Innovation GmbH
 */
public class MoveTo  extends PathConstructionOperatorProcessor{

	public MoveTo(PDFPage context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private Log log = LogFactory.getLog(getClass());

	public void process(Operator operator, List<COSBase> operands) throws IOException {
		try {
			PDFPage pdfPage = (PDFPage) context;

			COSNumber x = (COSNumber) operands.get(0);
			COSNumber y = (COSNumber) operands.get(1);
			Point2D p = transform(x.doubleValue(), y.doubleValue());

			pdfPage.getCurrentPath().moveTo((float) p.getX(), (float) p.getY());

			if (log.isTraceEnabled()) {
				log.trace("Moving current path to x:" + p.getX() + ",y:" + p.getY());
			}
		} catch (Exception e) {
			log.warn("Error processing operator 'm'.", e);
		}
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
