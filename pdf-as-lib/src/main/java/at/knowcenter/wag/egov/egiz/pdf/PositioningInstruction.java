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
 * $Id:  $
 */
package at.knowcenter.wag.egov.egiz.pdf;

/**
 * The positioning instruction holds information of where to place the signature
 * block.
 * 
 * <p>
 * This instruction is given to the PDF writer in order to place the signature.
 * </p>
 * 
 * @author wprinz
 */
public class PositioningInstruction
{

  /**
   * Tells, if a new plain page should be appended.
   * 
   * <p>
   * This command is executed before the signature block is positioned according
   * to page, x and y.
   * </p>
   */
  protected boolean make_new_page = false;

  /**
   * The number of the page on which the signature block is to be placed. If
   * specified to make a new page, the number of this newly created page can be
   * used here as well.
   */
  protected int page = 0;

  /**
   * The x coordinate where the upper left corner of the signature block should
   * be placed.
   */
  protected float x = 0.0f;

  /**
   * The y coordinate where the upper left corner of the signature block should
   * be placed.
   */
  protected float y = 0.0f;
  
  /**
   * The rotation of the signature block
   */
  protected float rotation = 0.0f;

  /**
   * 
   * @param make_new_page
   *          Tells, if a new plain page should be appended. This command is
   *          executed before the signature block is positioned according to
   *          page, x and y.
   * @param page
   *          The number of the page on which the signature block is to be
   *          placed. If specified to make a new page, the number of this newly
   *          created page can be used here as well.
   * @param x
   *          The x coordinate where the upper left corner of the signature
   *          block should be placed.
   * @param y
   *          The y coordinate where the upper left corner of the signature
   *          block should be placed.
   */
  public PositioningInstruction(boolean make_new_page, int page, float x, float y, float rotation)
  {
    this.make_new_page = make_new_page;
    this.page = page;
    this.x = x;
    this.y = y;
    this.rotation = rotation;
  }

  /**
   * Tells, if a new plain page should be appended to the document.
   * 
   * @return Returns true, if a new plain page should be appended.
   */
  public boolean isMakeNewPage()
  {
    return this.make_new_page;
  }

  /**
   * Returns the page on which the signature is to be printed.
   * 
   * @return Returns the page on which the signature is to be printed.
   */
  public int getPage()
  {
    return this.page;
  }


  /**
   * Returns the x coordinate where the upper left corner of the signature block
   * should be placed.
   * 
   * @return Returns the x coordinate where the upper left corner of the
   *         signature block should be placed.
   */
  public float getX()
  {
    return this.x;
  }

  /**
   * Returns the y coordinate where the upper left corner of the signature block
   * should be placed.
   * 
   * @return Returns the y coordinate where the upper left corner of the
   *         signature block should be placed.
   */
  public float getY()
  {
    return this.y;
  }
  
  public float getRotation()
  {
    return this.rotation;
  }

  
  public void setRotation(float rotation)
  {
    this.rotation += rotation;
  }

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (make_new_page ? 1231 : 1237);
		result = prime * result + page;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(rotation);
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PositioningInstruction))
			return false;
		PositioningInstruction other = (PositioningInstruction) obj;
		if (make_new_page != other.make_new_page)
			return false;
		if (page != other.page)
			return false;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		if (Float.floatToIntBits(rotation) != Float.floatToIntBits(other.rotation))
			return false;
		return true;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("PositioningInstruction [page=");
		buffer.append(page);
		buffer.append(", make_new_page=");
		buffer.append(make_new_page);
		buffer.append(", x=");
		buffer.append(x);
		buffer.append(", y=");
		buffer.append(y);
		buffer.append(", r=");
		buffer.append(rotation);
		buffer.append("]");
		return buffer.toString();
	}

}
