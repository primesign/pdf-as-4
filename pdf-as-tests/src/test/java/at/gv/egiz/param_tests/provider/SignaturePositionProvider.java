package at.gv.egiz.param_tests.provider;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SignaturePositionProvider extends BaseSignatureDataProvider {

    /**
     * config-property value for "position.mode", if this mode is chosen, only
     * reference image is captured and no tests are performed
     */
    public static final String CAPTURE_REFERENCE = "capture_reference";
    /**
     * config-property key for the positioning-string, which has the same syntax
     * as in the CLI
     */
    public static final String POSITIONING_STRING = "position.positioning_string";
    /**
     * config-property key for the number of the page on which the signature is
     * expected to appear
     */
    public static final String SIG_PAGE_NUMBER = "position.page_number";
    /**
     * config-property key for the name of the reference image
     */
    public static final String REFERENCE_IMAGE = "position.reference_image";
    /**
     * config-property key for the areas which are ignored for image comparison
     */
    public static final String IGNORED_AREAS = "position.ignored_areas";
    /**
     * config-property key for mode to use for the test, it can either be
     * "capture_reference" or anything else
     */
    public static final String MODE = "position.mode";

    /**
     * This method extracts in addition to the standard parameters also
     * signature position test parameters from the config properties. The keys
     * for these properties are defined as string constants.
     */
    @Override
    protected Object[] extractDataFromConfig(File configFile,
            Properties rootProps, Properties configProps) {
        Object[] standardData = extractStandardDataFromConfig(configFile,
                rootProps, configProps);
        Object[] data = new Object[standardData.length + 5];
        int i;
        for (i = 0; i < standardData.length; i++) {
            data[i] = standardData[i];
        }
        data[i++] = getProperty(rootProps, configProps, POSITIONING_STRING);
        data[i++] = Integer.parseInt(getProperty(rootProps, configProps,
                SIG_PAGE_NUMBER));
        String ignoredAreaStringProp = getProperty(rootProps, configProps,
                IGNORED_AREAS);
        List<Rectangle> ignoredAreas = new ArrayList<Rectangle>();
        if (ignoredAreaStringProp != null) {
            String[] ignoredAreaStringSplit = ignoredAreaStringProp.split(";");
            for (String ignoredAreaString : ignoredAreaStringSplit) {
                Rectangle ignoredArea = parseIgnoredArea(ignoredAreaString);
                if (ignoredArea != null) {
                    ignoredAreas.add(ignoredArea);
                }
            }
        }
        data[i++] = ignoredAreas;
        data[i++] = getProperty(rootProps, configProps, REFERENCE_IMAGE);
        data[i++] = CAPTURE_REFERENCE.equals(getProperty(rootProps,
                configProps, MODE));
        return data;
    }

    /**
     * This parses one ignored area definition and returns a Rectangle-object
     * representing it. The definitions have the exact format
     * "<x>,<y>,<width>,<height>", with x and y specifying the coordinates of
     * the upper left corner of the area and width and height specifying the
     * size in pixels (width and height extends to the right and the bottom of
     * the image).
     * 
     * @param ignoredAreaString
     *            an ignored area definition
     * @return a rectangle representing the area
     */
    private Rectangle parseIgnoredArea(String ignoredAreaString) {
        String[] ignoredAreaStringSplit = ignoredAreaString.split(",");
        if (ignoredAreaStringSplit.length != 4)
            return null;
        int x = Integer.parseInt(ignoredAreaStringSplit[0]);
        int y = Integer.parseInt(ignoredAreaStringSplit[1]);
        int width = Integer.parseInt(ignoredAreaStringSplit[2]);
        int height = Integer.parseInt(ignoredAreaStringSplit[3]);
        return new Rectangle(x, y, width, height);
    }

    /**
     * This method checks if this provider supports a given test configuration.
     * In order to be supported the test type must be "position".
     */
    @Override
    protected boolean isSupportedTestType(Properties configProps) {
        return configProps.containsKey(TEST_TYPE)
                && configProps.get(TEST_TYPE).equals("position");

    }
}
