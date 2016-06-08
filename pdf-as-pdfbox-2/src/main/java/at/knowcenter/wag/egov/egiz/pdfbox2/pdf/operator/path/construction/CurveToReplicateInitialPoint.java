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
 * Append a cubic Bezier curve to the current path. The curve shall extend from the current point to the point (x3, y3),
 * using the current point and (x2, y2) as the Bezier control points (see 8.5.2.2, "Cubic Bezier Curves"). The new
 * current point shall be (x3, y3).
 *
 * @see "PDF 1.7 specification, Section 8.5.2 'Path Construction Operators'"
 * @author PdfBox, modified by Datentechnik Innovation GmbH
 */
public class CurveToReplicateInitialPoint extends PathConstructionOperatorProcessor {

	private Log log = LogFactory.getLog(getClass());

	public CurveToReplicateInitialPoint(PDFPage context) {
		super(context);
	}

	@Override
	public void process(Operator operator, List<COSBase> operands) throws IOException {
		try {
			PDFPage pdfPage = (PDFPage) context;

			COSNumber x2 = (COSNumber) operands.get(0);
			COSNumber y2 = (COSNumber) operands.get(1);
			COSNumber x3 = (COSNumber) operands.get(2);
			COSNumber y3 = (COSNumber) operands.get(3);

	        Point2D currentPoint = pdfPage.getCurrentPath().getCurrentPoint();
			Point2D p2 = transform(x2.doubleValue(), y2.doubleValue());
			Point2D p3 = transform(x3.doubleValue(), y3.doubleValue());

			pdfPage.getCurrentPath().curveTo(
					(float)currentPoint.getX(), (float)currentPoint.getY(),
					(float) p2.getX(), (float) p2.getY(),
					(float) p3.getX(), (float) p3.getY()
			);

			if (log.isTraceEnabled()) {
				log.trace("Appending cubic Bezier curve with x2:" + p2.getX() + ",y2:" + p2.getY() + ", x3:"
						+ p3.getX() + ",y3:" + p3.getY() + ", using current point x:" + currentPoint.getX() + ",y:"
						+ currentPoint.getY());
			}
		} catch (Exception e) {
			log.warn("Error processing operator 'v'.", e);
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
