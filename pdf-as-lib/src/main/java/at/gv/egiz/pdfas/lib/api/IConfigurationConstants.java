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
package at.gv.egiz.pdfas.lib.api;

public interface IConfigurationConstants {

	public static final String TRUE = "true";
	public static final String FALSE = "false";
	
	public static final String SIG_OBJECT = "sig_obj";
	public static final String TYPE = "type";
	public static final String TABLE = "table";
	public static final String MAIN = "main";
	public static final String POS = "pos";
	public static final String DEFAULT = "default";
	public static final String SEPERATOR = ".";

	
	public static final String LEGACY_POSITIONING = ".legacy.pos";
	
	public static final String PLACEHOLDER_SEARCH_ENABLED = "enable_placeholder_search";
	public static final String DEFAULT_SIGNATURE_PROFILE = SIG_OBJECT + SEPERATOR + TYPE + SEPERATOR + DEFAULT;
}
