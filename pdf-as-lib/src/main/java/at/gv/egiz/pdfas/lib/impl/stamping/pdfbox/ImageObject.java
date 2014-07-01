package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox;

import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

public class ImageObject {
	private PDXObjectImage image;
	private float width;
	private float height;
	
	public ImageObject(PDXObjectImage image, float width, float height) {
		this.image = image;
		this.width = width;
		this.height = height;
	}

	public PDXObjectImage getImage() {
		return image;
	}

	public void setImage(PDXObjectImage image) {
		this.image = image;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

}
