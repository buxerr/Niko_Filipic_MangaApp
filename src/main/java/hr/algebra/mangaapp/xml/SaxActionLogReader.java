package hr.algebra.mangaapp.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

public class SaxActionLogReader {

    private static final Path ACTION_LOG_PATH = Path.of("logs", "action-log.xml");

    //Starting method
    public Map<String, Long> countActionsByType() {
        if (!Files.isRegularFile(ACTION_LOG_PATH)) {
            return Map.of();
        }

        try {
            ActionTypeCounterHandler handler = new ActionTypeCounterHandler();
            SAXParser parser = createParser();

            parser.parse(ACTION_LOG_PATH.toFile(), handler);

            return handler.getActionCounts();

        } catch (Exception e) {
            throw new IllegalStateException("Error while reading XML action log with SAX", e);
        }
    }

    //Creates a SAXParser with safety features
    private SAXParser createParser() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        return factory.newSAXParser();
    }

    //SAX Handler, passed to SAX
    private static class ActionTypeCounterHandler extends DefaultHandler {

        private final Map<String, Long> actionCounts = new TreeMap<>();
        private final StringBuilder currentText = new StringBuilder();

        private boolean readingType;

        //Reads opening tags, if the opening tag is not "type" it does nothing, otherwise sets the read flag to true
        @Override
        public void startElement(
                String uri,
                String localName,
                String qName,
                Attributes attributes
        ) {
            if ("type".equals(qName)) {
                readingType = true;
                currentText.setLength(0);
            }
        }

        //Reads the text inside the tags, returns text as char field. If the read flag is true, it adds the text to the currentText stringBuilder
        @Override
        public void characters(char[] ch, int start, int length) {
            if (readingType) {
                currentText.append(ch, start, length);
            }
        }

        //Reads closing tags, if the closing tag is "type", it gets the text from currentText, trims it and if it's not blank, it adds it to the actionCounts map. Then resets the flag and currentText
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("type".equals(qName)) {
                String actionType = currentText.toString().trim();

                if (!actionType.isBlank()) {
                    actionCounts.merge(actionType, 1L, Long::sum);
                }

                readingType = false;
                currentText.setLength(0);
            }
        }

        //Returns an unmodifiable copy of the actionCounts map
        private Map<String, Long> getActionCounts() {
            return Map.copyOf(actionCounts);
        }
    }
}
