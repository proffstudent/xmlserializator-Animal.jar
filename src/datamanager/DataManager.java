package datamanager;

import com.company.People;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

/**
 * Created by root on 10.02.17.
 */
public class DataManager {

    private static String ATTRIBUTE_NAME_FIELD_NAME = "id";
    private static String ATTRIBUTE_NAME_FIELD_TYPE = "type";
    private static String ATTRIBUTE_NAME_FIELD_VALUE = "value";

    public static Document serialize(Object obj, Document document) throws IllegalAccessException {
        final Field[] fields;
        final Class oclass = obj.getClass();
        final Element person;
        Element personInfo;

        fields = oclass.getDeclaredFields();

        person = document.createElement("Object");
        person.setAttribute(ATTRIBUTE_NAME_FIELD_TYPE, oclass.getName());
        document.setXmlStandalone(false);
        document.appendChild(person);
        for (Field field : fields) {
            field.setAccessible(true);
            personInfo = document.createElement("field");
            personInfo.setAttribute(ATTRIBUTE_NAME_FIELD_TYPE,
                    field.getType().getSimpleName());
            personInfo.setAttribute(ATTRIBUTE_NAME_FIELD_NAME, field.getName());
            personInfo.setAttribute(ATTRIBUTE_NAME_FIELD_VALUE, field.get(obj).toString());
            person.appendChild(personInfo);
        }
        return document;
    }
    public static void serialize(Object people) throws Exception {
        String value;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();

        Document doc = impl.createDocument(null, null, null);

        Element element = doc.createElement("Object");
        element.setAttribute("type", people.getClass().getSimpleName());
        for (Field field : people.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Element element1 = doc.createElement("fields");
            element1.setAttribute("type", field.getType().getSimpleName());
            element1.setAttribute("id", field.getName());
            element1.setAttribute("value", value = (field.get(people) !=null ? field.get(people).toString() : "myValue"));
            element.appendChild(element1);
        }
        doc.appendChild(element);

        StreamResult result = new StreamResult(new File("test3.xml"));
        DOMSource source = new DOMSource(doc);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
        StreamResult consoleResult = new StreamResult(System.out);
    }

    public static void serializeCollection(Collection<People> collection) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();

        Document doc = impl.createDocument(null, null, null);

        Element rootElement = doc.createElement("Collection");
        rootElement.setAttribute("type", People.class.getSimpleName());
        for (People people : collection) {
            Element element = element = doc.createElement("Object");
            element.setAttribute("type", People.class.getSimpleName());
            for (Field field : people.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Element element1 = doc.createElement("fields");
                element1.setAttribute("type", field.getType().getSimpleName());
                element1.setAttribute("id", field.getName());
                element1.setAttribute("value", field.get(people).toString());
                element.appendChild(element1);
            }
            rootElement.appendChild(element);
        }
        doc.appendChild(rootElement);

        StreamResult result = new StreamResult(new File("testCollection.xml"));
        DOMSource source = new DOMSource(doc);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
    }


    public static People deserialize(String path) throws Exception {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(path));

        Class cls = Class.forName("com.company.People");
        People people = (People) cls.newInstance();

        Field[] fields = people.getClass().getDeclaredFields();
        Map<String,Field> fieldsMap = new HashMap<>();
        for (Field field : fields) {
            field.setAccessible(true);
            fieldsMap.put(field.getName(),field);
        }

        NodeList nodeList = doc.getElementsByTagName("Object").item(0).getChildNodes();
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() !=  Node.TEXT_NODE) {
                list.add(nodeList.item(i));
            }
        }

        for (int i = 0; i < list.size(); i++) {
            String id    = list.get(i).getAttributes().getNamedItem("id").getNodeValue();
            String type  = list.get(i).getAttributes().getNamedItem("type").getNodeValue();
            String value = list.get(i).getAttributes().getNamedItem("value").getNodeValue();
            switch (type) {
                case "String" :
                    fieldsMap.get(id).set(people,value);
                    break;
                case "int":
                    fieldsMap.get(id).set(people,Integer.parseInt(value));
                    break;
                case "double":
                    fieldsMap.get(id).set(people,Double.parseDouble(value));
                    break;
                default:
                    throw new Exception("Неизвестный тип");
            }
        }

        return people;
    }


}



