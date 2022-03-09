package at.gv.egiz.pdfas.lib.test.mains;

import at.gv.egiz.pdfas.common.settings.DefaultSignatureProfileSettings;
import at.gv.egiz.pdfas.common.utils.CheckSignatureBlockParameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
@RunWith(JUnit4.class)
public class SignatureBlockParameterTest {

  @Test
  public void testKeyInvalid() {
    assertFalse(checkValid( "aaaaaaaaaaaaaaaaaaaaa" , "^([A-za-z]){1,20}$"));
    assertFalse(checkValid( "" , "^([A-za-z]){1,20}$"));
    assertFalse(checkValid( "a9" , "^([A-za-z]){1,20}$"));
  }

  @Test
  public void testKeyValid() {
    assertTrue(checkValid( "aaa" +"aa", "^([A-za-z]){1,20}$"));
    assertTrue(checkValid( "aaa" , "^([A-za-z]){1,20}$"));
    assertTrue(checkValid( "aaaaaaaaaaaaaaaaaaaa", "^([A-za-z]){1,20}$"));
    assertTrue(checkValid( "AA", "^([A-za-z]){1,20}$"));
  }

  @Test
  public void testValueValid() {
    assertTrue(checkValid( "aaa" +"aa", "^([\\p{Print}]){1,100}$"));
    assertTrue(checkValid( "aaa" , "^([\\p{Print}]){1,100}$"));
    assertTrue(checkValid( "a!\"$%&/()[]=?aa-_,;.:[]|{}" , "^([\\p{Print}]){1,100}$"));
//    assertTrue(checkValid( "a!\"§$%&/()=?aa" , "^([\\p{Print}]){1,100}$"));
    assertTrue(checkValid( "aa!%&/()=?a" , "^([\\p{Print}]){1,100}$"));
    assertTrue(checkValid( "a{\"a!%&/()=?a" , "^([\\p{Print}]){1,100}$"));
    assertTrue(checkValid( "BB" , "^([\\p{Print}]){1,100}$"));
    assertTrue(checkValid( "BB " , "^([\\p{Print}]){1,100}$"));
  }

  @Test
  public void testValueValidWithDefaultRegex() {
    assertTrue(checkValid( "aaa" +"aa", DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid( "aaa" , DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid( "a!\"$%&/()[]=?aa-_,;.:[]|{}" , DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
//    assertTrue(checkValid( "a!\"§$%&/()=?aa" , DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid( "aa!%&/()=?a" , DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid( "a{\"a!%&/()=?a" , DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid( "BB" , DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid( "BB " , DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid("! €%&/()?`$\"§", DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid("€", DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid("$", DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid("! €\"§$%&/()=?`", DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid("#", DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid("-_", DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid("'", DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid("abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!#-_ß?+#*",
        DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
  }

  @Test
  public void testValueInvalid() {
    assertFalse(checkValid((char) 13 +"aaa" +"aa", "^([\\p{Print}]){1,100}$"));
    assertFalse(checkValid((char) 13 +"", "^([\\p{Print}]){1,100}$"));
    assertFalse(checkValid( "aaa" +(char) 13, "^([\\p{Print}]){1,100}$"));
    assertFalse(checkValid("", "^([\\p{Print}]){1,100}$"));
    assertFalse(checkValid("a", "^([\\p{Print}]){2,100}$"));
    assertFalse(checkValid("aaa"+(char) 13 +"aa", "^([\\p{Print}]){1,100}$"));
  }

  @Test
  public void testUmlauteValid() {
    assertTrue(checkValid( "ä", DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid( "ö", DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid( "ü", DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid( "Ä", DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid( "Ö", DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid( "Ü", DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid( "ÄÖÜöäüjhsbdjej", DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid( "aauacnhuisdc Ä Ü Ö aaxsa ö aÜaÖa", DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
    assertTrue(checkValid( "NMS Güssing", DefaultSignatureProfileSettings.SIG_BLOCK_PARAMETER_DEFAULT_VALUE_REGEX));
  }

  public boolean checkValid(String s, String regex) {
    return CheckSignatureBlockParameters.isValid(s, regex);
  }
}
