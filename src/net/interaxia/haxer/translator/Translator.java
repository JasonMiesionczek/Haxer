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
public class Translator extends BaseTranslator {
    private HaxeFile file;
    private List<Pattern> forPatterns;

    public Translator(HaxeFile file) {
        this.file = file;
    }

    /**
     * Processes every line of the input file, translating the ActionScript 3 code into haXe code.
     */
    public void translate() {
        forPatterns = new ArrayList<Pattern>();
        forPatterns.add(Pattern.compile("for\\s*\\(var\\s+(.+):\\w+\\s*=\\s*(\\w+);\\s*\\w+\\s*[<=>]+\\s*(\\S+);\\s*\\w+\\+\\+\\)\\s*"));
        forPatterns.add(Pattern.compile("for\\s*\\(\\s*(\\w+)\\s*[=]\\s*(\\S+);\\s*\\w+\\s*[<=>]+\\s*(\\w+);\\s*\\S+\\s*\\)"));
        boolean withinComment = false;
        int packagePos = -1;
        List<String> newLines = new ArrayList<String>();
        for (int i = 0; i < file.getLines().size(); i++) {
            String line = file.getLines().get(i);
            String nextLine = null;
            try {
                nextLine = file.getLines().get(i + 1);
            } catch (IndexOutOfBoundsException oob) {
                // no need to do anything here. default value set above is acceptable. 
            }

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
                if (nextLine.trim().startsWith("{")) {
                    i++;
                    newLines.add(line2);
                    continue;
                }
            }
            line = line2;

            line = detectAndConvertTypes(line);
            line = detectAndConvertClass(line);
            line = detectAndConvertConstructor(line);
            line = detectAndConvertForLoop(line);
            line = addSemiColon(line, nextLine);
            newLines.add(line);
        }

        if (packagePos >= 0) {
            for (String imp : file.getImports()) {
                newLines.add(packagePos + 1, "\timport " + imp + ";");
            }
        }

        file.setLines(removeLastCurlyBrace(newLines));
    }

    private List<String> removeLastCurlyBrace(List<String> input) {
        List<String> temp = input;
        int lastBrace = 0;
        for (int i = temp.size() - 1; i > 0; i--) {
            String line = temp.get(i);
            if (line.trim().endsWith("}")) {
                lastBrace = i;
                break;
            }
        }

        if (lastBrace > 0) {
            temp.remove(lastBrace);
        }

        return temp;
    }


    private String addSemiColon(String line, String nextLine) {
        String orig = getLineWithoutComments(line);
        String temp = orig;

        if (nextLine != null && nextLine.trim().startsWith("{"))
            return line;

        if (temp.trim().endsWith("{") ||
                temp.trim().endsWith("}") ||
                temp.trim().endsWith(";") ||
                temp.trim().endsWith(":") ||
                (temp.trim().endsWith(")") && nextLine != null && nextLine.trim().startsWith("{")) ||
                temp.matches("^\\s*$") ||
                temp.isEmpty())
            return line;


        temp = temp.concat(";");

        return line.replace(orig, temp);
    }

    private String detectAndConvertForLoop(String line) {
        String orig = getLineWithoutComments(line);
        String temp = orig;

        for (Pattern p : forPatterns) {
            Matcher m = p.matcher(temp);
            if (m.find()) {
                String variable = m.group(1);
                String min = m.group(2);
                String max = m.group(3);
                temp = m.replaceAll("for (" + variable + " in " + min + "..." + max + ") ");
            }
        }

        return line.replace(orig, temp);
    }


    /**
     * Detects any classes defined in the current file and removes the preceding 'public' accessor if its present.
     *
     * @param line the current line being processed
     * @return the modified line
     */
    private String detectAndConvertClass(String line) {
        String orig = getLineWithoutComments(line);
        String temp = orig;


        if (temp.contains("class ")) {
            if (temp.trim().startsWith("public ")) {
                temp = temp.replace("public ", "");
            }
        }

        return line.replace(orig, temp);
    }

    /**
     * Detects the constructor for the current class and converts its name to 'new'
     *
     * @param line
     * @return
     */
    private String detectAndConvertConstructor(String line) {
        String orig = getLineWithoutComments(line);
        String temp = orig;

        for (ObjectType t : AllTypes.getInstance().getAllTypes()) {
            String name = t.getNormalizedTypeName();
            Pattern p = Pattern.compile("function\\s+" + name + "\\s*\\(");
            Matcher m = p.matcher(temp);
            if (m.find()) {
                temp = temp.replace(name, "new");
            }
        }

        return line.replace(orig, temp);
    }

    /**
     * Finds the package definition and converts it from the beginning of a block to a single statement.
     *
     * @param line
     * @return
     */
    private String locateAndConvertPackage(String line) {
        String orig = getLineWithoutComments(line);
        String temp = orig;

        if (temp.contains("package " + file.getPkg())) {
            if (temp.trim().endsWith("{")) {
                temp = temp.replace("{", "").trim().concat(";");
                return line.replace(orig, temp);
            } else {
                temp = temp.trim().concat(";");
                return line.replace(orig, temp);
            }
        }

        return line;
    }

    /**
     * Detects if the current line is an import statement so that the translator can skip adding it to the newLines list
     *
     * @param line
     * @return
     */
    private boolean isImport(String line) {
        String orig = getLineWithoutComments(line);
        String temp = orig;

        if (temp.trim().startsWith("import ")) {
            return true;
        }

        return false;
    }

    /**
     * Iterates through all the known types searching for them within each line.
     * If a match is found, and the type is not a basic type, it is added to the import list. The type is then converted
     * to its normalized format (uppercase first letter).
     *
     * @param line
     * @return
     */
    private String detectAndConvertTypes(String line) {
        // remove comments from end of line, store original version so that we can use it replace
        // the code in the input line, thus preserving the comments in the output string
        String orig = getLineWithoutComments(line);
        String temp = orig;

        for (ObjectType type : AllTypes.getInstance().getAllTypes()) {
            String t = type.getTypeName();

            // search for the type, checking for uniquness 
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
