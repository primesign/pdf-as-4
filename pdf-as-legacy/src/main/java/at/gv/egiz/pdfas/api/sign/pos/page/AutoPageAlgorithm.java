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
 * The page for placing the signature is selected automatically.
 * 
 * <p>
 * The algorithm first tries to place the signature on the free space of the
 * last page (considering the footer). If there is not enough space on the last
 * page, a new page is appended and the signature is placed there.
 * </p>
 * 
 * @author wprinz
 */
public class AutoPageAlgorithm extends PageAlgorithm implements Serializable
{

   /**
    * 
    */
   private static final long serialVersionUID = 1L;
// empty
}
