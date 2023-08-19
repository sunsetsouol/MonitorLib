package com.qgstudio;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {
    public static String apiKey;
    static {
        try {
            Properties properties = new Properties();
            InputStream resourceAsStream = PropertiesUtils.class.getClassLoader().getResourceAsStream("Monitor.properties");
            properties.load(resourceAsStream);
            apiKey = properties.getProperty("apiKey");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
