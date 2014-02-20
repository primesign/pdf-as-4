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
package at.gv.egiz.pdfas.common.messages;

import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageResolver {
	private static final String messageResource = "resources.messages.common";
    private static final String missingMsg = "Please add message ";

    private static final Logger logger = LoggerFactory.getLogger(MessageResolver.class);

    private static ResourceBundle bundle;

    static {
        bundle = ResourceBundle.getBundle(messageResource);
        if(bundle == null) {
            logger.error("Failed to load resource bundle!!");
            System.err.println("Failed to load resource bundle!!");
            //Runtime.getRuntime().exit(-1);
        }
    }

    public static void forceLocale(Locale locale) {
        bundle = ResourceBundle.getBundle(messageResource, locale);
    }

    public static String resolveMessage(String msgId) {
        if(bundle == null) {
            return missingMsg + msgId;
        }
        if(bundle.containsKey(msgId)) {
            String value = bundle.getString(msgId);
            if(value == null) {
                return missingMsg + msgId;
            }
            return value;
        }
        return missingMsg + msgId;
    }
}
