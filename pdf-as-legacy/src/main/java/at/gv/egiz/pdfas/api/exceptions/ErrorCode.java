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
 * Contains constants for the error codes.
 * 
 * <p>
 * In Java 1.5 this would be an enum.
 * </p>
 * 
 * @author wprinz
 */
public final class ErrorCode
{
  public static final int EXTERNAL_ERROR = 0;
  public static final int UNKNOWN_ERROR = 6;
  public static final int OUT_OF_MEMORY_ERROR = 7;

  public static final int SETTING_NOT_FOUND = 100;
  public static final int SETTINGS_EXCEPTION = 101;
  public static final int KZ_SETTING_NOT_FOUND = 102;
  public static final int NO_EMBEDABLE_TTF_CONFIGURED_FOR_PDFA = 103;
  public static final int INVALID_SIGNATURE_LAYOUT_IMPL_CONFIGURED = 104;
  public static final int MISSING_HEADER_SERVER_USER_AGENT = 105;
  public static final int CIRCULAR_INCLUDE_INSTRUCTION_DETECTED = 106;
  public static final int UNABLE_TO_LOAD_DEFAULT_CONFIG = 107;

  public static final int DOCUMENT_CANNOT_BE_READ = 201;
  public static final int TEXT_EXTRACTION_EXCEPTION = 202;
  public static final int CANNOT_WRITE_PDF = 205;
  public static final int DOCUMENT_NOT_SIGNED = 206;
  public static final int SIGNATURE_TYPES_EXCEPTION = 223;
  public static final int FONT_NOT_FOUND = 230;
  public static final int DOCUMENT_IS_PROTECTED = 231;
  public static final int INVALID_SIGNATURE_DICTIONARY = 232;
//23.11.2010 changed by exthex - added error code for failed extraction
  public static final int SIGNATURE_PLACEHOLDER_EXTRACTION_FAILED = 233;
  
	/**
	 * Error code for {@code SignatureException}s occurring when trying to sign with a certain signature profile that
	 * is not allowed to be used for signature, e.g. because ist has been set to
	 * <p/>
	 * {@code sig_obj.types.<PROFILE_ID> = verify_only}
	 * @author Datentechnik Innovation GmbH
	 */
  public static final int SIGNATURE_PROFILE_IS_NOT_ALLOWED_FOR_SIGNATURE = 234;
  
  public static final int INVALID_SIGNATURE_POSITION = 224;
  public static final int NO_TEXTUAL_CONTENT = 251;

  public static final int SIGNATURE_COULDNT_BE_CREATED = 300;
  public static final int SIGNED_TEXT_EMPTY = 301;
  public static final int PROFILE_NOT_DEFINED = 302;
  public static final int SERIAL_NUMBER_INVALID = 303;
  public static final int SIG_CERTIFICATE_CANNOT_BE_READ = 304;
  public static final int PROFILE_NOT_USABLE_FOR_TEXT = 305;
  
  public static final int COULDNT_VERIFY = 310;
  
  public static final int CERTIFICATE_NOT_FOUND = 313;
  public static final int NOT_SEMANTICALLY_EQUAL = 314;

  public static final int MODIFIED_AFTER_SIGNATION = 316;
  public static final int NON_BINARY_SIGNATURES_PRESENT = 317;  
  
  public static final int UNSUPPORTED_REPLACES_NAME = 318;
  public static final int UNSUPPORTED_SIGNATURE = 319;

  public static final int DETACHED_SIGNATURE_NOT_SUPPORTED = 370;
  
  public static final int SIGNATURE_VERIFICATION_NOT_SUPPORTED = 371;  
  public static final int INVALID_SIGNING_TIME = 372;

  public static final int BKU_NOT_SUPPORTED = 373;
  
  public static final int WEB_EXCEPTION = 330;
  public static final int UNABLE_TO_RECEIVE_SUITABLE_RESPONSE = 340;

  
  public static final int NORMALIZER_EXCEPTION = 400;
  
  public static final int SESSION_EXPIRED = 600;
  
  public static final int PLACEHOLDER_EXCEPTION = 700;
  public static final int CAPTION_NOT_FOUND_EXCEPTION = 701;

  public static final int UNABLE_TO_PARSE_ID = 800;
  public static final int CORRECTOR_EXCEPTION = 801;
  public static final int EXTERNAL_CORRECTOR_TIMEOUT_REACHED = 802;  

  public static final int WRAPPED_ERROR_CODE = 998;
  public static final int FUNCTION_NOT_AVAILABLE = 999;
}
