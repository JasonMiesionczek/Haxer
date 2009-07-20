package net.interaxia.haxer.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class AllTypes {
    private static AllTypes _instance;
    private List<ObjectType> allTypes;

    public static AllTypes getInstance() {
        if (_instance == null)
            _instance = new AllTypes();

        return _instance;
    }

    public List<ObjectType> getAllTypes() {
        return allTypes;
    }

    public List<String> getAllShortNames() {
        List<String> temp = new ArrayList<String>();
        for (ObjectType o : allTypes) {
            temp.add(o.getTypeName());

        }

        return temp;
    }

    public ObjectType getTypeByShortName(String shortName) {
        for (ObjectType t : allTypes) {
            if (t.getTypeName().equals(shortName))
                return t;
        }

        return null;
    }

    private AllTypes() {
        allTypes = new ArrayList<ObjectType>();
        readFlashAPI();
        addBasicType("void", "Void");
        addBasicType("int", "Int");
        addBasicType("Number", "Float");
        addBasicType("Boolean", "Bool");
        addBasicType("Array", "Array<Dynamic>");

    }

    private void addBasicType(String asName, String haxeName) {
        ObjectType otype = new ObjectType("", asName);
        otype.setBasicType(true);
        otype.setNormalizedTypeName(haxeName);
        allTypes.add(otype);
    }

    public void addType(String pkgName, String typeName) {
        ObjectType otype = new ObjectType(pkgName, typeName);
        otype.setBasicType(false);
        allTypes.add(otype);
    }

    private void readFlashAPI() {
        try {
            File file = new File("FlashAPI.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName("package");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                if (n.getNodeType() != Node.ELEMENT_NODE) continue;
                Element e = (Element) n;
                String pkgName = e.getAttribute("name");
                NodeList types = e.getElementsByTagName("type");
                for (int j = 0; j < types.getLength(); j++) {
                    Element t = (Element) types.item(j);
                    String typeName = t.getChildNodes().item(0).getNodeValue();
                    addType(pkgName, typeName);

                }

            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
