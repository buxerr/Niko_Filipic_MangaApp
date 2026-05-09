package hr.algebra.mangaapp.xml;

import hr.algebra.mangaapp.model.User;
import hr.algebra.mangaapp.model.enums.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class ActionXmlLogService {

    private static final Logger log = LoggerFactory.getLogger(ActionXmlLogService.class);
    private static final Path ACTION_LOG_PATH = Path.of("logs", "action-log.xml");
    private static final Object LOCK = new Object();

    private ActionXmlLogService() {
    }

    public static void log(User user, String actionType, String details) {
        if (user == null) {
            log("unknown", null, actionType, details);
            return;
        }

        log(user.getUsername(), user.getRole(), actionType, details);
    }

    public static void log(String username, UserRole role, String actionType, String details) {
        synchronized (LOCK) {
            try {
                Files.createDirectories(ACTION_LOG_PATH.getParent());

                Document document = loadOrCreateDocument();
                Element root = document.getDocumentElement();

                Element actionElement = document.createElement("action");
                actionElement.setAttribute("id", UUID.randomUUID().toString());
                actionElement.setAttribute(
                        "timestamp",
                        OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                );

                appendTextElement(document, actionElement, "username", safeValue(username));
                appendTextElement(document, actionElement, "role", role != null ? role.name() : "UNKNOWN");
                appendTextElement(document, actionElement, "type", safeValue(actionType));
                appendTextElement(document, actionElement, "details", safeValue(details));

                root.appendChild(actionElement);
                writeDocument(document);

            } catch (Exception e) {
                log.warn("Failed to write XML action log", e);
            }
        }
    }

    private static Document loadOrCreateDocument() throws Exception {
        if (Files.exists(ACTION_LOG_PATH) && Files.size(ACTION_LOG_PATH) > 0) {
            DocumentBuilder builder = createDocumentBuilder();
            Document document = builder.parse(ACTION_LOG_PATH.toFile());
            document.getDocumentElement().normalize();

            return document;
        }

        DocumentBuilder builder = createDocumentBuilder();
        Document document = builder.newDocument();
        Element root = document.createElement("actionLog");
        document.appendChild(root);

        return document;
    }

    private static DocumentBuilder createDocumentBuilder() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

        return factory.newDocumentBuilder();
    }

    private static void appendTextElement(
            Document document,
            Element parent,
            String name,
            String value
    ) {
        Element element = document.createElement(name);
        element.setTextContent(value);
        parent.appendChild(element);
    }

    private static void writeDocument(Document document) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(
                new DOMSource(document),
                new StreamResult(ACTION_LOG_PATH.toFile())
        );
    }

    private static String safeValue(String value) {
        return value == null || value.isBlank() ? "N/A" : value.trim();
    }
}
