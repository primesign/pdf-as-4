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
package at.knowcenter.wag.egov.egiz.pdfbox2.pdf;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import at.gv.egiz.pdfas.lib.impl.pdfbox2.positioning.PositioningRenderer;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFVisualObject;

public abstract class PDFUtilities {

	public static Color MAGIC_COLOR = new Color(152,254,52);// green-ish background
	
	public static float getMaxYPosition(
			PDDocument pdfDataSource, int page, IPDFVisualObject pdfTable, float signatureMarginVertical, float footer_line) throws IOException {
		long t0 = System.currentTimeMillis();
		PositioningRenderer renderer = new PositioningRenderer(pdfDataSource);
		//BufferedImage bim = renderer.renderImage(page);
		long t1 = System.currentTimeMillis();
		int width = (int) pdfDataSource.getPage(page).getCropBox().getWidth();
		int height = (int) pdfDataSource.getPage(page).getCropBox().getHeight();
		BufferedImage bim = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		long t2 = System.currentTimeMillis();
		Graphics2D graphics = bim.createGraphics();
		long t3 = System.currentTimeMillis();
//		graphics.setPaint(MAGIC_COLOR);
//		graphics.fillRect(0, 0, width, height);
		graphics.setBackground(MAGIC_COLOR);
        
		renderer.renderPageToGraphics(page, graphics);
		long t4 = System.currentTimeMillis();
		Color bgColor = MAGIC_COLOR;
		
		if(true){ //only used if background color should be determined automatically
			bgColor = determineBackgroundColor(bim);
		}
		long t5 = System.currentTimeMillis();
		int yCoord = bim.getHeight() - 1 - (int)footer_line;

		for(int row = yCoord; row >= 0; row--){
			for(int col = 0; col < bim.getWidth(); col++){
				int val = bim.getRGB(col, row);
				if(val != bgColor.getRGB()){
					yCoord = row;
					row=-1;
					break;
				}
			}
		}
		long t6 = System.currentTimeMillis();
		
		System.out.println("new Renderer: "+ (t1-t0));
		System.out.println("new BI: "+ (t2-t1));
		System.out.println("Create Graphics: "+ (t3-t2));
		System.out.println("Render to Graphics: "+ (t4-t3));
		System.out.println("Determined bg color: "+ (t5-t4));
		System.out.println("Calc y: "+ (t6-t5));

//		for(int i=0; i < bim.getWidth(); i++){
//			bim.setRGB(i, yCoord, 255);
//		}
//
		ImageIOUtil.writeImage(bim, "/home/cmaierhofer/temp/bufferer.png", 72);

		return yCoord;
	}
	
//	public static float getFreeTablePosition(
//			PDDocument pdfDataSource, int page, IPDFVisualObject pdfTable, float signatureMarginVertical) throws IOException {
//		
//		float table_height = pdfTable.getHeight();
//		
//		PDFRenderer renderer = new PDFRenderer(pdfDataSource);
//
//		BufferedImage bim = renderer.renderImage(page);
//
//		Color bgColor = determineBackgroundColor(bim);
//		float posY = bim.getHeight();
//		
//		for(int row=0; row<bim.getHeight();row++){
//			boolean backgroundOnly = true;
//			int countFreeRows = 0;
//			for(int c = row; c<bim.getHeight();c++){
//				countFreeRows++;
//				for(int col = 0; col < bim.getWidth(); col++){
//					int val = bim.getRGB(col, c);
//					if(val != bgColor){//end of bg
//						backgroundOnly = false;
//						row = c;
//						break;
//					}
//				}
//				if(!backgroundOnly){
//					break;
//				}else{
//					if(countFreeRows >= table_height+signatureMarginVertical){
//						posY = row;
//						row=bim.getHeight();
//						break;
//					}
//				}
//			}
//		}
//
//		if(posY == -1)
//			return Float.NEGATIVE_INFINITY;
//		return posY;
//	}

	public static Color determineBackgroundColor(BufferedImage bim){
		
		int inset = 5;//px
		
		int pixelUpLeft = bim.getRGB(inset,inset);
		int pixelUpRight = bim.getRGB(bim.getWidth()-inset,inset);
		int pixelDownLeft = bim.getRGB(inset, bim.getHeight()-inset);
		int pixelDownRight = bim.getRGB(bim.getWidth()-inset, bim.getHeight()-inset);
		
		HashMap<Integer, Integer> stats = new HashMap<Integer, Integer>();
		stats.put(pixelUpLeft, 0);
		stats.put(pixelUpRight, 0);
		stats.put(pixelDownLeft, 0);
		stats.put(pixelDownRight, 0);
		
		stats.put(pixelUpLeft, stats.get(pixelUpLeft)+1);
		stats.put(pixelUpRight, stats.get(pixelUpRight)+1);
		stats.put(pixelDownLeft, stats.get(pixelDownLeft)+1);
		stats.put(pixelDownRight, stats.get(pixelDownRight)+1);

		int bgValue = -1;
		int cnt =0;
		for(int key:stats.keySet()){
			if(stats.get(key) > cnt){
				cnt = stats.get(key);
				bgValue = key;
			}		
		}
		
		return new Color(bgValue);
	}
	
}
