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
 */
package at.gv.egiz.pdfas.api.sign.pos.page;

import java.io.Serializable;

/**
 * The page is selected absolutely by giving the page number directly.
 * 
 * @author wprinz
 */
public class AbsolutePageAlgorithm extends PageAlgorithm implements Serializable
{
  /**
    * 
    */
   private static final long serialVersionUID = 1L;

  /**
   * The page.
   */
  protected int page = -1;

  /**
   * Constructor.
   * 
   * @param page
   *          The page.
   */
  public AbsolutePageAlgorithm(int page)
  {
    this.page = page;
  }

  /**
   * @return the page
   */
  public int getPage()
  {
    return this.page;
  }

}
