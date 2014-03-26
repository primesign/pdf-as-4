package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox;

import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

public class ImageObject {
	private PDXObjectImage image;
	private float size;
	
	public ImageObject(PDXObjectImage image, float size) {
		this.image = image;
		this.size = size;
	}

	public PDXObjectImage getImage() {
		return image;
	}

	public void setImage(PDXObjectImage image) {
		this.image = image;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}
	
	
}
