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
package at.gv.egiz.pdfas.common.utils;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * Created with IntelliJ IDEA.
 * User: afitzek
 * Date: 9/11/13
 * Time: 1:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class OgnlUtils {

    private static final Logger logger = LoggerFactory.getLogger(OgnlUtils.class);

    public static String resolvsOgnlExpression(String expression, OgnlContext ctx) {
        try {
			Object value = Ognl.getValue(expression, ctx);
			String valueString = value.toString();
			if(valueString.startsWith("[")) {
				valueString = valueString.substring(1);
			}
			if(valueString.endsWith("]")) {
				valueString = valueString.substring(0, valueString.length() - 1);
			}
			return valueString;

		} catch (OgnlException e) {
			logger.warn("OGNL resolver failed!", e);
		}
        return expression;
    }
}
