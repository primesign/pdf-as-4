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
package at.gv.egiz.pdfas.api.io;

import java.io.InputStream;

/**
 * Input document data source.
 * 
 * <p>
 * This allows the holder of the data to decide how the data is to be stored (e.g. in a File or in a byte array).
 * </p>
 * 
 * @author wprinz
 * 
 */
public interface DataSource
{
  /**
   * Creates a new InputStream that allows to read out the document's binary
   * data from the beginning.
   * 
   * @return Returns the InputStream with the binary data.
   */
  public InputStream createInputStream();

  /**
   * Returns the length (number of bytes) of the stream.
   * 
   * @return Returns the length (number of bytes) of the stream.
   */
  public int getLength();

  /**
   * Returns the data of this DataSource as a byte array for random read only access.
   * 
   * <p>
   * Calling this method indicates that you need a byte array for random
   * <strong>read only</strong> access. The DataSource implementation should of
   * course cache this byte array to avoid too much memory usage.
   * </p>
   * <p>
   * Performance analysis has shown that the libraries internally convert the
   * streams to byte arrays and that file system access is very slow.
   * </p>
   * <p>
   * Never write to this byte array!
   * </p>
   * 
   * @return Returns the data of this DataSource as a byte array for random read only access.
   */
  public byte[] getAsByteArray();

  /**
   * Returns the mime type of the data.
   * 
   * @return Returns the mime type of the data.
   */
  public String getMimeType();

  /**
   * Returns the character encoding of the data.
   * 
   * <p>
   * This makes only sense for character based mime types.
   * </p>
   * 
   * @return Returns the character encoding of the data or null if no encoding
   *         is applicable (e.g. if the data is binary).
   */
  public String getCharacterEncoding();

}
