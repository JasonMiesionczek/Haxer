package net.interaxia.haxer.translator;

import net.interaxia.haxer.HaxeFile;
import net.interaxia.haxer.api.AllTypes;
import net.interaxia.haxer.api.ObjectType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        boolean withinComment = false;
        int packagePos = -1;
        List<String> newLines = new ArrayList<String>();
        for (int i = 0; i < file.getLines().size(); i++) {
            String line = file.getLines().get(i);

            if (isSingleLineComment(line)) {
                newLines.add(line);
                continue;
            }

            if (isStartOfComment(line)) {
                withinComment = true;
                newLines.add(line);
                continue;
            }

            if (withinComment && isEndOfComment(line)) {
                withinComment = false;
                newLines.add(line);
                continue;
            }

            if (withinComment) {
                newLines.add(line);
                continue;
            }

            if (isImport(line))
                continue;

            String line2 = locateAndConvertPackage(line);
            if (line2 != line) {
                packagePos = i;
            }
            line = line2;

            line = detectAndConvertTypes(line);

            newLines.add(line);
            //System.out.println(line);
        }

        if (packagePos >= 0) {
            for (String imp : file.getImports()) {
                newLines.add(packagePos + 1, "\timport " + imp + ";");
            }
        }

        file.setLines(newLines);
    }

    private boolean isStartOfComment(String input) {
        if (input.trim().startsWith("/*"))
            return true;

        return false;
    }

    private boolean isEndOfComment(String input) {
        if (input.trim().endsWith("*/"))
            return true;

        return false;
    }

    private boolean isSingleLineComment(String input) {
        String temp = input.trim();
        if (temp.startsWith("//") || (temp.startsWith("/*") && temp.endsWith("*/")))
            return true;

        return false;
    }

    private String getLineWithoutComments(String line) {
        int idx = line.indexOf("//");
        if (idx >= 0) {
            return line.substring(0, idx);
        }

        return line;

    }

    private String locateAndConvertPackage(String line) {
        String orig = getLineWithoutComments(line);
        String temp = orig;

        if (temp.contains("package " + file.getPkg())) {
            if (temp.trim().endsWith("{")) {
                temp = temp.replace("{", "").trim().concat(";");
                return line.replace(orig, temp);
            }
        }

        return line;
    }

    private boolean isImport(String line) {
        String orig = getLineWithoutComments(line);
        String temp = orig;

        if (temp.trim().startsWith("import ")) {
            return true;
        }

        return false;
    }

    private String detectAndConvertTypes(String line) {
        // remove comments from end of line, store original version so that we can use it replace
        // the code in the input line, thus preserving the comments in the output string
        String orig = getLineWithoutComments(line);
        String temp = orig;

        for (ObjectType type : AllTypes.getInstance().getAllTypes()) {
            String t = type.getTypeName();

            Pattern p = Pattern.compile("\\b(" + t + ")\\b");
            Matcher m = p.matcher(temp);
            if (m.find()) {
                if (!type.isBasicType())
                    file.addImport(t);
                temp = m.replaceAll(type.getNormalizedTypeName());

            }
        }

        return line.replace(orig, temp);
    }
}
