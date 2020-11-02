package at.gv.egiz.pdfas.lib.impl.configuration;

import java.util.Properties;

public class PlaceholderWebConfiguration {

    protected static Properties properties = new Properties();

    //todo properties not cleaned
    public static void setValue(String key, String value)
    {
        properties.clear();
        properties.setProperty(key,value);
    }
    public static String getValue(String key)
    {
        return properties.getProperty(key);
    }

    public static void clear () {
        properties.clear();
    }

}
