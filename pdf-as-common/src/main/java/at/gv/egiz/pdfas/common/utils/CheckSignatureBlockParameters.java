package at.gv.egiz.pdfas.common.utils;

import at.gv.egiz.pdfas.common.settings.DefaultSignatureProfileSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CheckSignatureBlockParameters {

  private static final Logger logger = LoggerFactory
      .getLogger(CheckSignatureBlockParameters.class);
  public static boolean checkSignatureBlockParameterMapIsValid(Map<String, String> map, String keyRegex,
                                                               String valueRegex) {

    logger.trace("regex1:"+keyRegex+", regex1:"+valueRegex);
    if(keyRegex == null || keyRegex.length() == 0) {
      keyRegex = DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_KEY_REGEX;
    }
    if(valueRegex == null || valueRegex.length() == 0) {
      valueRegex = DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX;
    }
    logger.trace("regex2:"+keyRegex+", regex2:"+valueRegex);
  for(String key : map.keySet()){
    if(isValid(key, keyRegex) == false) {
      logger.error("Invalid key:"+key+", regex:"+keyRegex);
      return false;
    }
    if(isValid(map.get(key), valueRegex) == false) {
      logger.error("Invalid value:"+map.get(key)+", regex:"+valueRegex);
      return false;
    }
  }

    return true;
  }

  public static boolean isValid(String s, String regex) {
    return s.matches(regex);
  }

}
