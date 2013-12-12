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
package at.gv.egiz.pdfas.api.exceptions;

/**
 * This exception is the base for all PDF-AS exceptions.
 * 
 * <p>
 * Every PDF-AS Exception has an error code.
 * </p>
 * 
 * @author wprinz
 */
@Deprecated
public class PdfAsException extends Exception
{
  /**
   * The error code.
   */
  protected int errorCode = -1;

  /**
   * Constructor.
   * 
   * @param errorCode
   *          The error code.
   * @param message
   *          The detail message.
   */
  public PdfAsException(int errorCode, String message)
  {
    super(message);

    this.errorCode = errorCode;
  }

  /**
   * Constructor.
   * 
   * @param errorCode
   *          The error code.
   * @param message
   *          The detail message.
   * @param cause
   *          The cause.
   */
  public PdfAsException(int errorCode, String message, Throwable cause)
  {
    super(message, cause);

    this.errorCode = errorCode;
  }

  /**
   * Constructor.
   * 
   * @param errorCode
   *          The error code.
   * @param cause
   *          The cause.
   */
  public PdfAsException(int errorCode, Throwable cause)
  {
    super(cause);

    this.errorCode = errorCode;
  }

  /**
   * Returns the error code of this exception.
   * 
   * @return Returns the error code of this exception.
   */
  public int getErrorCode()
  {
    return this.errorCode;
  }
}
