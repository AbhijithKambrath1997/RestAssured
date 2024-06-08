package utilities.FileReaders;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {

    Properties properties;

    public Properties getProperties(String propertiesPath) {
        try {
            FileInputStream fileInputStream = new FileInputStream(propertiesPath);
            properties = new Properties();
            properties.load(fileInputStream);
        } catch (FileNotFoundException e) {
            e.fillInStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}
