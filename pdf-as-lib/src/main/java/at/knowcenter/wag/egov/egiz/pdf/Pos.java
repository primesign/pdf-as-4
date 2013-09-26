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
 * $Id: Pos.java,v 1.1 2006/08/25 17:10:08 wprinz Exp $
 */
package at.knowcenter.wag.egov.egiz.pdf;

/**
 * Encapsulation of a position on a PDF page.
 * 
 * @author wprinz
 */
public class Pos
{

  public float x;

  public float y;

  public float z;

  /**
   * Default constructor.
   */
  public Pos()
  {
  }

  /**
   * Constructor that sets the coordinates.
   * @param xx
   * @param yy
   * @param zz
   */
  public Pos(float xx, float yy, float zz)
  {
    this.x = xx;
    this.y = yy;
    this.z = zz;
  }

  /**
   * @see Object#toString()
   */
  public String toString()
  {
    return "(" + this.x + "," + this.y + "," + this.z + ")";
  }

}
