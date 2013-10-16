package at.gv.egiz.pdfas.lib.impl;

import at.gv.egiz.pdfas.lib.api.SignaturePosition;

public class SignaturePositionImpl implements SignaturePosition {

	protected int page;
	protected float x;
	protected float y;
	protected float width;
	protected float height;
	
	
	
	public void setPage(int page) {
		this.page = page;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public int getPage() {
		return page;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

}
