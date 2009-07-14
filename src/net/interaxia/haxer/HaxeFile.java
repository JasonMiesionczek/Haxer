package net.interaxia.haxer;

import net.interaxia.haxer.api.ObjectType;
import net.interaxia.haxer.api.AllTypes;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Atmospherian
 * Date: Jul 14, 2009
 * Time: 10:32:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class HaxeFile {
    private List<String> lines;
    private List<String> imports;
    private String pkg;

    public void setPkg(String pkg) {
        this.pkg = pkg.toLowerCase();
    }

    public List<String> getLines() {

        return lines;
    }

    public String getPkg() {
        return pkg;
    }

    public void addImport(String shortName) {
        ObjectType importType = AllTypes.getInstance().getTypeByShortName(shortName);
        if (!imports.contains(importType.toString())) {
            imports.add(importType.toString());
        }
    }

    public HaxeFile() {
        lines = new ArrayList<String>();
        imports = new ArrayList<String>();
    }
}
