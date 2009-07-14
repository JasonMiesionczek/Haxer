package net.interaxia.haxer.translator;

import net.interaxia.haxer.HaxeFile;
import net.interaxia.haxer.api.AllTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Atmospherian
 * Date: Jul 14, 2009
 * Time: 4:59:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class Translator {
    private HaxeFile file;

    public Translator(HaxeFile file) {
        this.file = file;
    }

    public void translate() {
        List<String> newLines = new ArrayList<String>();
        for (int i = 0; i < file.getLines().size(); i++) {
            String line = file.getLines().get(i);

            line = detectTypes(line);

            newLines.add(line);
        }
    }

    private String detectTypes(String line) {
        String temp = line;

        for (String t: AllTypes.getInstance().getAllShortNames()) {
            if (temp.contains(t)) {
                file.addImport(t);    
            }
        }

        return temp;
    }
}
