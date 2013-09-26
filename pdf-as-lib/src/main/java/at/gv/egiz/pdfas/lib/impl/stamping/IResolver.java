package at.gv.egiz.pdfas.lib.impl.stamping;

import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;

/**
 * Created with IntelliJ IDEA.
 * User: afitzek
 * Date: 9/11/13
 * Time: 1:44 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IResolver {
    public String resolve(String key, String value, SignatureProfileSettings settings);
}
