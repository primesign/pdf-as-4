package at.gv.egiz.pdfas.lib.impl.pdfbox2.positioning;

import java.awt.Color;
import java.awt.Paint;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;

public class PositioningPageDrawer extends PageDrawer{

	public PositioningPageDrawer(PageDrawerParameters parameters)
			throws IOException {
		super(parameters);
		// TODO Auto-generated constructor stub
	}

	private static final Color POSCOLOR = new Color(234, 14, 184, 211);
	
	@Override
	protected Paint getPaint(PDColor color){
		return POSCOLOR;
	}
	
//	@Override
//	protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode,Vector displacement) throws IOException{
//		// bbox in EM -> user units
//        Shape bbox = new Rectangle2D.Float(0, 0, font.getWidth(code) / 1000, 1);
//        AffineTransform at = textRenderingMatrix.createAffineTransform();
//        bbox = at.createTransformedShape(bbox);
//        
//        // save
//        Graphics2D graphics = getGraphics();
//
//        // draw
//        graphics.setClip(graphics.getDeviceConfiguration().getBounds());
//        graphics.setColor(POSCOLOR);
//        graphics.setStroke(new BasicStroke(.5f));
//        graphics.draw(bbox);
//
//        // restore
//	}
//	
//	@Override
//    public void fillPath(int windingRule) throws IOException
//    {
//        // bbox in user units
//        Shape bbox = getLinePath().getBounds2D();
//        
//        // draw path (note that getLinePath() is now reset)
//        //super.fillPath(windingRule);
//        
//        // save
//        Graphics2D graphics = getGraphics();
//
//
//        // draw
//        graphics.setClip(graphics.getDeviceConfiguration().getBounds());
//        graphics.setColor(POSCOLOR);
//        graphics.setStroke(new BasicStroke(.5f));
//        graphics.draw(bbox);
//
//    }

	

}
