package hr.algebra.mangaapp.util;

import hr.algebra.mangaapp.exception.ConfigurationException;
import hr.algebra.mangaapp.model.config.DatabaseConfig;
import javafx.geometry.Dimension2D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public final class XmlConfigUtils {

    private static final String CONFIG_PATH = "/hr/algebra/mangaapp/config.xml";

    private XmlConfigUtils() {
    }

    public static DatabaseConfig loadDatabaseConfig() {
        try {
            Document document = loadConfigDocument();

            Element databaseElement = (Element) document
                    .getElementsByTagName("database")
                    .item(0);

            if (databaseElement == null) {
                throw new ConfigurationException("Missing <database> element in config.xml");
            }

            String url = getTagValue(databaseElement, "url");
            String username = getTagValue(databaseElement, "username");
            String password = getTagValue(databaseElement, "password");

            return new DatabaseConfig(url, username, password);

        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException("Error while reading database configuration", e);
        }
    }

    public static Dimension2D getSmallScreen() {
        return getScreenSize("smallScreen");
    }

    public static Dimension2D getBigScreen() {
        return getScreenSize("bigScreen");
    }

    private static Dimension2D getScreenSize(String screenSizeTagName) {
        try {
            Document document = loadConfigDocument();

            Element screenSizesElement = (Element) document
                    .getElementsByTagName("screenSizes")
                    .item(0);

            if (screenSizesElement == null) {
                throw new ConfigurationException("Missing <screenSizes> element in config.xml");
            }

            Element screenSizeElement = (Element) screenSizesElement
                    .getElementsByTagName(screenSizeTagName)
                    .item(0);

            if (screenSizeElement == null) {
                throw new ConfigurationException("Missing <" + screenSizeTagName + "> element in config.xml");
            }

            double width = getPositiveDoubleTagValue(screenSizeElement, "width");
            double height = getPositiveDoubleTagValue(screenSizeElement, "height");

            return new Dimension2D(width, height);

        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException("Error while reading screen size configuration", e);
        }
    }

    private static Document loadConfigDocument() {
        try (InputStream inputStream = XmlConfigUtils.class.getResourceAsStream(CONFIG_PATH)) {

            if (inputStream == null) {
                throw new ConfigurationException("Config file not found: " + CONFIG_PATH);
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            return document;

        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException("Error while reading config.xml", e);
        }
    }

    private static double getPositiveDoubleTagValue(Element parent, String tagName) {
        String value = getTagValue(parent, tagName);

        try {
            double parsedValue = Double.parseDouble(value);

            if (parsedValue <= 0) {
                throw new ConfigurationException(
                        "Configuration value <" + tagName + "> must be greater than zero"
                );
            }

            return parsedValue;

        } catch (NumberFormatException e) {
            throw new ConfigurationException(
                    "Configuration value <" + tagName + "> must be a number",
                    e
            );
        }
    }

    private static String getTagValue(Element parent, String tagName) {
        Element element = (Element) parent.getElementsByTagName(tagName).item(0);

        if (element == null) {
            throw new ConfigurationException("Missing <" + tagName + "> element in config.xml");
        }

        String value = element.getTextContent().trim();

        if (value.isBlank()) {
            throw new ConfigurationException("Configuration value <" + tagName + "> cannot be empty");
        }

        return value;
    }
}
