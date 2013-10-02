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

import java.io.IOException;
import java.io.OutputStream;

/**
 * Output document data sink.
 * 
 * <p>
 * Actually, the DataSink can be seen as a factory for creating OutputStreams
 * with mime type and character encoding provided. This allows the API user to
 * decide how data is to be stored (e.g. in a file, in a byte array, etc.).
 * </p>
 * 
 * @author wprinz
 */
public interface DataSink
{
  /**
   * Creates an OutputStream for binary data.
   * 
   * <p>
   * Note that the stream may be written only once. Creating another stream
   * overwrites the existing one.
   * </p>
   * 
   * @param mimeType
   *          The mime type of the output data.
   * @return Returns the created output stream.
   * @throws IOException
   *           Thrown if the stream cannot be created.
   */
  public OutputStream createOutputStream(String mimeType) throws IOException;

  /**
   * Creates an OutputStream for character data.
   * 
   * <p>
   * This is basically the same as {@link #createOutputStream(String)}, but
   * allows to specify the character encoding.
   * </p>
   * 
   * @param mimeType
   *          The mime type of the output data.
   * @param characterEncoding
   *          The character encoding of the data.
   * @return Returns the created output stream.
   * @throws IOException
   *           Thrown if the stream cannot be created.
   */
  public OutputStream createOutputStream(String mimeType, String characterEncoding) throws IOException;

  /**
   * Returns the mime type of the data stream.
   * 
   * <p>
   * This is only valid after a stream has been created.
   * </p>
   * 
   * @return Returns the mime type of the data stream.
   */
  public String getMimeType();

  /**
   * Returns the character encoding of the data stream.
   * 
   * <p>
   * This is only valid after a stream has been created. Null means that no
   * character encoding was specified for the data (e.g. if the data is binary).
   * </p>
   * 
   * @return Returns the character encoding of the data stream.
   */
  public String getCharacterEncoding();
}
