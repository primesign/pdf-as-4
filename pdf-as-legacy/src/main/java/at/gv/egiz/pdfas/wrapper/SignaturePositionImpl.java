package at.gv.egiz.pdfas.wrapper;

import at.gv.egiz.pdfas.api.sign.pos.SignaturePosition;

public class SignaturePositionImpl implements SignaturePosition {

	private at.gv.egiz.pdfas.lib.api.SignaturePosition position;
	
	public SignaturePositionImpl(at.gv.egiz.pdfas.lib.api.SignaturePosition position) {
		this.position = position;
	}
	
	
	public int getPage() {
		return this.position.getPage();
	}

	public float getX() {
		return this.position.getX();
	}

	public float getY() {
		return this.position.getY();
	}

	public float getWidth() {
		return this.position.getWidth();
	}

	public float getHeight() {
		return this.position.getHeight();
	}

}
