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
        ResourceBundle bundle = ResourceBundle.getBundle(messageResource);
        if(bundle == null) {
            logger.error("Failed to load resource bundle!!");
            Runtime.getRuntime().exit(-1);
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
