package at.gv.egiz.pdfas.lib.api;

public interface SignaturePosition {
	  /**
	   * Returns the page on which the signature was placed.
	   * 
	   * @return Returns the page on which the signature was placed.
	   */
	  public int getPage();

	  /**
	   * Returns the x position.
	   * 
	   * @return Returns the x position.
	   */
	  public float getX();

	  /**
	   * Returns the y position.
	   * 
	   * @return Returns the y position.
	   */
	  public float getY();

	  /**
	   * Returns the width of the signature.
	   * 
	   * @return Returns the width of the signature.
	   */
	  public float getWidth();

	  /**
	   * Returns the height of the signature.
	   * 
	   * @return Returns the height of the signature.
	   */
	  public float getHeight();
}
