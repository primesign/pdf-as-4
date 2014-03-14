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
 * $Id: PDFPage.java,v 1.5 2006/10/31 08:09:33 wprinz Exp $
 */
package at.knowcenter.wag.egov.egiz.pdf;

import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectForm;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.PDFOperator;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;
import org.apache.pdfbox.util.operator.OperatorProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.knowcenter.wag.egov.egiz.pdf.operator.path.construction.ClosePath;
import at.knowcenter.wag.egov.egiz.pdf.operator.path.construction.CurveTo;
import at.knowcenter.wag.egov.egiz.pdf.operator.path.construction.CurveToReplicateFinalPoint;
import at.knowcenter.wag.egov.egiz.pdf.operator.path.construction.CurveToReplicateInitialPoint;
import at.knowcenter.wag.egov.egiz.pdf.operator.path.construction.LineTo;
import at.knowcenter.wag.egov.egiz.pdf.operator.path.construction.MoveTo;
import at.knowcenter.wag.egov.egiz.pdf.operator.path.painting.CloseAndStrokePath;
import at.knowcenter.wag.egov.egiz.pdf.operator.path.painting.CloseFillEvenOddAndStrokePath;
import at.knowcenter.wag.egov.egiz.pdf.operator.path.painting.CloseFillNonZeroAndStrokePath;
import at.knowcenter.wag.egov.egiz.pdf.operator.path.painting.EndPath;
import at.knowcenter.wag.egov.egiz.pdf.operator.path.painting.FillEvenOddAndStrokePath;
import at.knowcenter.wag.egov.egiz.pdf.operator.path.painting.FillNonZeroAndStrokePath;
import at.knowcenter.wag.egov.egiz.pdf.operator.path.painting.FillPathEvenOddRule;
import at.knowcenter.wag.egov.egiz.pdf.operator.path.painting.FillPathNonZeroWindingNumberRule;
import at.knowcenter.wag.egov.egiz.pdf.operator.path.painting.StrokePath;

/**
 * PDFPage is an inner class that is used to calculate the page length of a PDF
 * Document page. It extends the PDFTextStripper class and implement one
 * interested method: {@link at.knowcenter.wag.egov.egiz.pdf.PDFPage#showCharacter(TextPosition)}<br>
 * This method is called when processing the FileStream. By calling the method
 * {@link org.apache.pdfbox.util.PDFStreamEngine#processStream(org.apache.pdfbox.pdmodel.PDPage, org.apache.pdfbox.pdmodel.PDResources, org.pdfbox.cos.COSStream)}
 * the implemented method showCharacter is called.
 * 
 * @author wlackner
 * @see PDFTextStripper
 */
public class PDFPage extends PDFTextStripper {
	/**
	 * The logger definition.
	 */
    private static final Logger logger = LoggerFactory.getLogger(PDFPage.class);

	/**
	 * The maximum (lowest) y position of a character.
	 */
	protected float max_character_ypos = Float.NEGATIVE_INFINITY;

	/**
	 * The maximum (lowest y position of an image.
	 */
	protected float max_image_ypos = Float.NEGATIVE_INFINITY;

	/**
	 * The effective page height.
	 */
	protected float effectivePageHeight;

	/**
	 * The path currently being constructed.
	 */
	private GeneralPath currentPath = new GeneralPath();

	/**
	 * The lowest position of a drawn path (originating from top).
	 */
	private float maxPathRelatedYPositionFromTop = Float.NEGATIVE_INFINITY;

	/**
	 * Constructor.
	 * 
	 * @param effectivePageHeight
	 *            The height of the page to be evaluated. PDF elements outside
	 *            this height will not be considered.
	 * 
	 * @throws java.io.IOException
	 */
	public PDFPage(float effectivePageHeight, boolean legacy32) throws IOException {
		super();

		this.effectivePageHeight = effectivePageHeight;

		OperatorProcessor newInvoke = new MyInvoke(this);
		newInvoke.setContext(this);
        this.registerOperatorProcessor("Do", newInvoke);
        
		if (!legacy32) {
			registerCustomPathOperators();
		}
	}

	/**
	 * Registers operators responsible for path construction and painting in
	 * order to fix auto positioning on pages with path elements.
	 * 
	 * @author Datentechnik Innovation GmbH
	 */
	private void registerCustomPathOperators() {

		// *** path construction

        this.registerOperatorProcessor("m", new MoveTo(this));
        this.registerOperatorProcessor("l", new LineTo(this));
        this.registerOperatorProcessor("c", new CurveTo(this));
        this.registerOperatorProcessor("y", new CurveToReplicateFinalPoint(this));
        this.registerOperatorProcessor("v", new CurveToReplicateInitialPoint(this));
        this.registerOperatorProcessor("h", new ClosePath(this));

		// *** path painting

		// "S": stroke path
        this.registerOperatorProcessor("S", new StrokePath(this));
        this.registerOperatorProcessor("s", new CloseAndStrokePath(this));
        this.registerOperatorProcessor("f", new FillPathNonZeroWindingNumberRule(this));
        this.registerOperatorProcessor("F", new FillPathNonZeroWindingNumberRule(this));
        this.registerOperatorProcessor("f*", new FillPathEvenOddRule(this));
        this.registerOperatorProcessor("b", new CloseFillNonZeroAndStrokePath(this));
        this.registerOperatorProcessor("B", new FillNonZeroAndStrokePath(this));
        this.registerOperatorProcessor("b*", new CloseFillEvenOddAndStrokePath(this));
        this.registerOperatorProcessor("B*", new FillEvenOddAndStrokePath(this));
        this.registerOperatorProcessor("n", new EndPath(this));

		// Note: The graphic context
		// (org.pdfbox.pdmodel.graphics.PDGraphicsState) of the underlying
		// pdfbox library does
		// not yet support clipping. This prevents feasible usage of clipping
		// operators (W, W*).
		// operators.put("W", new ...(this));
		// operators.put("W*", new ...(this));

	}

	/**
	 * Returns the path currently being constructed.
	 * 
	 * @return The path currently being constructed.
	 */
	public GeneralPath getCurrentPath() {
		return currentPath;
	}

	/**
	 * Sets the current path.
	 * 
	 * @param currentPath
	 *            The new current path.
	 */
	public void setCurrentPath(GeneralPath currentPath) {
		this.currentPath = currentPath;
	}

	/**
	 * Registers a rectangle that bounds the path currently being drawn.
	 * 
	 * @param bounds
	 *            A rectangle depicting the bounds (coordinates originating from
	 *            bottom left).
	 * @author Datentechnik Innovation GmbH
	 */
	public void registerPathBounds(Rectangle bounds) {
		if (!bounds.isEmpty()) {
			logger.debug("Registering path bounds: " + bounds);

			// vertical start of rectangle (counting from top of page)
			float upperBoundYPositionFromTop;

			// vertical end of rectangle (counting from top of page)
			// this depicts the current end of path-related page content
			float lowerBoundYPositionFromTop;

			PDRectangle boundaryBox = this.getCurrentPage().findMediaBox();
			float pageHeight;

			switch (this.getCurrentPage().findRotation()) {
			case 90: // CW
				pageHeight = boundaryBox.getWidth();
				upperBoundYPositionFromTop = (float) bounds.getMinX();
				lowerBoundYPositionFromTop = (float) bounds.getMaxX();
				break;
			case 180:
				pageHeight = boundaryBox.getHeight();
				upperBoundYPositionFromTop = (float) bounds.getMinY();
				lowerBoundYPositionFromTop = (float) bounds.getMaxY();
				break;
			case 270: // CCW
				pageHeight = boundaryBox.getWidth();
				upperBoundYPositionFromTop = pageHeight
						- (float) bounds.getMaxX();
				lowerBoundYPositionFromTop = pageHeight
						- (float) bounds.getMinX();
				break;
			default:
				pageHeight = boundaryBox.getHeight();
				upperBoundYPositionFromTop = pageHeight
						- (float) bounds.getMaxY();
				lowerBoundYPositionFromTop = pageHeight
						- (float) bounds.getMinY();
				break;
			}

			// new maximum ?
			if (lowerBoundYPositionFromTop > maxPathRelatedYPositionFromTop) {
				// Is the rectangle (at least partly) located above the footer
				// line?
				// (effective page height := page height - footer line)
				if (upperBoundYPositionFromTop <= effectivePageHeight) {
					// yes: update current end of path-related page content
					maxPathRelatedYPositionFromTop = lowerBoundYPositionFromTop;
					logger.trace("New max path related y position (from top): "
							+ maxPathRelatedYPositionFromTop);
				} else {
					// no: rectangle is fully located below the footer line ->
					// ignore
					logger.trace("Ignoring path bound below the footer line.");
				}
			}
		}
	}

	protected void processOperator(PDFOperator operator, List<COSBase> arguments)
			throws IOException {
		logger.trace("operator = " + operator);
		super.processOperator(operator, arguments);
	}

	@Override
	protected void processTextPosition(TextPosition text) {
		showCharacter(text);
	}
	
	// exthex
	/**
	 * A method provided as an event interface to allow a subclass to perform
	 * some specific functionality when a character needs to be displayed. This
	 * method is used to calculate the latest position of a text in the page.
	 * Sorry for this missinterpretation of the method, but it is the only way
	 * to do this (provided by PDFBox)!!!
	 * 
	 * @param text
	 *            the character to be displayed -> calculate there y position.
	 */
	protected void showCharacter(TextPosition text) {
		float current_y = text.getY();
		final String character = text.getCharacter();

		int pageRotation = this.getCurrentPage().findRotation();
		// logger_.debug("PageRotation = " + pageRotation);
		if (pageRotation == 0) {
			current_y = text.getY();
		}
		if (pageRotation == 90) {
			current_y = text.getX();
		}
		if (pageRotation == 180) {
			float page_height = this.getCurrentPage().findMediaBox().getHeight();
			current_y = page_height - text.getY();
		}
		if (pageRotation == 270) {
			float page_height = this.getCurrentPage().findMediaBox().getHeight();
			current_y = page_height - text.getX();
		}

		if (current_y > this.effectivePageHeight) {
			// logger_.debug("character is below footer_line. footer_line = " +
			// this.footer_line + ", text.character=" + character + ", y=" +
			// current_y);
			return;
		}

		// store ypos of the char if it is not empty
		if (!character.equals(" ") && current_y > this.max_character_ypos) {
			this.max_character_ypos = current_y;
		}

	}

	// use this funtion getting an unsorted text output
	// public void showString(byte[] string) {
	// logger_.debug(new String(string));
	// }

	/**
	 * Returns the calculated page length.
	 * 
	 * @return the max page length value
	 */
	public float getMaxPageLength() {
		if (logger.isDebugEnabled()) {
			logger.debug("Determining page content length: text="
					+ max_character_ypos + ", image=" + max_image_ypos
					+ ", path=" + maxPathRelatedYPositionFromTop);
		}
		return NumberUtils.max(max_character_ypos, max_image_ypos,
                maxPathRelatedYPositionFromTop);
	}

	public class MyInvoke extends OperatorProcessor {

        private PDFPage mypage;

        public MyInvoke(PDFPage page) {
            this.mypage = page;
        }

		public void process(PDFOperator operator, List<COSBase> arguments)
				throws IOException {
			COSName name = (COSName) arguments.get(0);

			// PDResources res = context.getResources();

			Map<String, PDXObject> xobjects = context.getXObjects();
			PDXObject xobject = xobjects.get(name.getName());

			PDStream stream = xobject.getPDStream();
			COSStream cos_stream = stream.getStream();

			COSName subtype = (COSName) cos_stream
					.getDictionaryObject(COSName.SUBTYPE);
			if (subtype.equals(COSName.IMAGE)) {
				logger.debug("XObject Image");

				Matrix ctm = context.getGraphicsState()
						.getCurrentTransformationMatrix();
                logger.debug("ctm = " + ctm);

				Pos[] coordinates = new Pos[] { new Pos(0, 0, 1),
						new Pos(1, 0, 1), new Pos(0, 1, 1), new Pos(1, 1, 1) };

				Pos[] transformed_coordinates = transtormCoordinates(
						coordinates, ctm);

				/**********************************************************
				 * pdf-as fix: calculating min and max point of an image to look
				 * where the signature should be placed fix solves problems with
				 * footer and images and placement of the signature in an image
				 * only pdf document
				 **********************************************************/

				float actual_lowest_point = Float.NaN;
				float actual_starting_point = Float.NaN;

				int pageRotation = this.mypage.getCurrentPage().findRotation();
				logger.debug("PageRotation = " + pageRotation);
				if (pageRotation == 0) {
					float min_y = findMinY(transformed_coordinates);
                    logger.debug("min_y = " + min_y);
					float page_height = this.mypage.getCurrentPage().findMediaBox().getHeight();
                    logger.debug("page_height = " + page_height);

					actual_lowest_point = page_height - min_y;
					actual_starting_point = page_height
							- findMaxY(transformed_coordinates);
				}
				if (pageRotation == 90) {
					float max_x = findMaxX(transformed_coordinates);
                    logger.debug("max_x = " + max_x);
					float page_width = this.mypage.getCurrentPage().findMediaBox().getWidth();
                    logger.debug("page_width = " + page_width);

					actual_lowest_point = max_x;
					actual_starting_point = findMinX(transformed_coordinates);
				}
				if (pageRotation == 180) {
					float min_y = findMinY(transformed_coordinates);
                    logger.debug("min_y = " + min_y);
					float page_height = this.mypage.getCurrentPage().findMediaBox().getHeight();
					actual_lowest_point = page_height
							- findMaxY(transformed_coordinates);
					actual_starting_point = page_height - min_y;
				}
				if (pageRotation == 270) {
					float min_x = findMinX(transformed_coordinates);
                    logger.debug("min_x = " + min_x);

					float page_width = this.mypage.getCurrentPage().findMediaBox().getWidth();
                    logger.debug("page_width = " + page_width);

					actual_lowest_point = page_width - min_x;
					actual_starting_point = page_width
							- findMaxX(transformed_coordinates);
				}

                logger.debug("actual_lowest_point = " + actual_lowest_point);

				if (actual_lowest_point > PDFPage.this.effectivePageHeight
						&& actual_starting_point > PDFPage.this.effectivePageHeight) {
                    logger.debug("image is below footer_line");
					return;
				}

				if (actual_lowest_point > PDFPage.this.max_image_ypos) {
					PDFPage.this.max_image_ypos = actual_lowest_point;
				}

				return;
			}

			if (xobject instanceof PDXObjectForm) {
				PDXObjectForm form = (PDXObjectForm) xobject;
				COSStream invoke = (COSStream) form.getCOSObject();
				PDResources pdResources = form.getResources();
				PDPage page = context.getCurrentPage();
				if (pdResources == null) {
					pdResources = page.findResources();
				}

				getContext().processSubStream(page, pdResources, invoke);
			}
		}
	}

	public static Pos[] transtormCoordinates(Pos[] coordinates, Matrix m) {
		Pos[] transformed = new Pos[coordinates.length];
		for (int i = 0; i < coordinates.length; i++) {
			transformed[i] = transtormCoordinate(coordinates[i], m);
		}
		return transformed;
	}

	public static Pos transtormCoordinate(Pos pos, Matrix m) {
		Pos transformed = new Pos();
		transformed.x = pos.x * m.getValue(0, 0) + pos.y * m.getValue(1, 0)
				+ pos.z * m.getValue(2, 0);
		transformed.y = pos.x * m.getValue(0, 1) + pos.y * m.getValue(1, 1)
				+ pos.z * m.getValue(2, 1);
		transformed.z = pos.x * m.getValue(0, 2) + pos.y * m.getValue(1, 2)
				+ pos.z * m.getValue(2, 2);

        logger.debug(" transformed " + pos + " --> " + transformed);
		return transformed;
	}

	public static float findMinY(Pos[] coordinates) {
		float min = Float.POSITIVE_INFINITY;
		for (int i = 0; i < coordinates.length; i++) {
			if (coordinates[i].y < min) {
				min = coordinates[i].y;
			}
		}
		return min;
	}

	public static float findMaxY(Pos[] coordinates) {
		float max = 0;
		for (int i = 0; i < coordinates.length; i++) {
			if (coordinates[i].y > max) {
				max = coordinates[i].y;
			}
		}
		return max;
	}

	public static float findMaxX(Pos[] coordinates) {
		float max = Float.NEGATIVE_INFINITY;
		for (int i = 0; i < coordinates.length; i++) {
			if (coordinates[i].x > max) {
				max = coordinates[i].x;
			}
		}
		return max;
	}

	public static float findMinX(Pos[] coordinates) {
		float min = Float.POSITIVE_INFINITY;
		for (int i = 0; i < coordinates.length; i++) {
			if (coordinates[i].x < min) {
				min = coordinates[i].x;
			}
		}
		return min;
	}

}
