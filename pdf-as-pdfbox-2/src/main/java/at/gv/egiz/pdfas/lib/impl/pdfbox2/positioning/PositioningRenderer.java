package at.gv.egiz.pdfas.lib.impl.pdfbox2.positioning;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;

public class PositioningRenderer extends PDFRenderer{

	public PositioningRenderer(PDDocument document) {
		super(document);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected PageDrawer createPageDrawer(PageDrawerParameters params) throws IOException{
		return new PositioningPageDrawer(params);
	}

}
