package net.interaxia.haxer.api;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Atmospherian
 * Date: Jul 14, 2009
 * Time: 10:06:09 AM
 * To change this template use File | Settings | File Templates.
 */
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
        for (ObjectType o: allTypes) {
            temp.add(o.getTypeName());

        }

        return temp;
    }

    public ObjectType getTypeByShortName(String shortName) {
        for (ObjectType t: allTypes) {
            if (t.getTypeName().equals(shortName))
                return t;
        }

        return null;
    }

    private AllTypes() {
        allTypes = new ArrayList<ObjectType>();
        readFlashAPI();

    }

    public void addType(String pkgName, String typeName) {
        ObjectType otype = new ObjectType(pkgName, typeName);
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
            for (int i = 0; i < nodes.getLength(); i++ ) {
                Node n = nodes.item(i);
                if (n.getNodeType() != Node.ELEMENT_NODE) continue;
                Element e = (Element)n;
                String pkgName = e.getAttribute("name");
                NodeList types = e.getElementsByTagName("type");
                for (int j = 0; j < types.getLength(); j++) {
                    Element t = (Element)types.item(j);
                    String typeName = ((Node)(t.getChildNodes().item(0))).getNodeValue();
                    addType(pkgName, typeName);

                }
                
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
