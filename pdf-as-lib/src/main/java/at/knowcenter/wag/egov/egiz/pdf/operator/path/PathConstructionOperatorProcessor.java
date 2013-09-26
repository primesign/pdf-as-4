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
package at.knowcenter.wag.egov.egiz.pdf.operator.path;

import at.knowcenter.wag.egov.egiz.pdf.PDFPage;
import org.apache.pdfbox.util.operator.OperatorProcessor;

import java.awt.geom.Point2D;

/**
 * Provides functions for path construction operators.
 *
 * @see "PDF 1.7 specification, Section 8.5.2 'Path Construction Operators'"
 * @author Datentechnik Innovation GmbH
 *
 */
public abstract class PathConstructionOperatorProcessor extends OperatorProcessor {

	public PathConstructionOperatorProcessor(PDFPage context) {
		setContext(context);
	}

	/**
	 * Transforms the given point from user space coordinates to device space coordinates based on the current
	 * transition matrix.
	 *
	 * @param x
	 *            The x axis value of the user space coordinates.
	 * @param y
	 *            The y axis value of the user space coordinates.
	 * @return The transformed point.
	 */
	public Point2D transform(double x, double y) {
		double[] position = { x, y };
		context.getGraphicsState().getCurrentTransformationMatrix().createAffineTransform()
				.transform(position, 0, position, 0, 1);
		return new Point2D.Double(position[0], position[1]);
	}

}
