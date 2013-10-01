package at.gv.egiz.pdfas.common.utils;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return expression;
    }
}
