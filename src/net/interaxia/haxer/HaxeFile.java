package net.interaxia.haxer;

import net.interaxia.haxer.api.AllTypes;
import net.interaxia.haxer.api.ObjectType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HaxeFile {
    private List<String> lines;
    private List<String> imports;
    private String pkg;
    private String fileName;

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

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public List<String> getImports() {
        return imports;
    }

    public String getFileName() {

        return fileName;
    }

    public HaxeFile(File source) {
        lines = new ArrayList<String>();
        imports = new ArrayList<String>();
        fileName = source.getName();

    }
}
