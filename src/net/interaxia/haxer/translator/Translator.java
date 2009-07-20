package net.interaxia.haxer.translator;

import net.interaxia.haxer.HaxeFile;
import net.interaxia.haxer.api.AllTypes;
import net.interaxia.haxer.api.ObjectType;
import net.interaxia.haxer.api.Property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translator extends BaseTranslator {
    private HaxeFile file;
    private List<Pattern> forPatterns;
    private HashMap<String, Property> propertyMap;
    private int classPos = -1;

    public Translator(HaxeFile file) {
        this.file = file;
        propertyMap = new HashMap<String, Property>();
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
            if (!line2.equals(line)) {
                packagePos = i;
                if (nextLine.trim().startsWith("{")) {
                    i++;
                    newLines.add(line2);
                    continue;
                }
            }
            line = line2;

            line = detectAndConvertTypes(line);
            String line3 = detectAndConvertClass(line);
            if (!line3.equals(line)) {
                classPos = newLines.size() + 1;
            }
            line = line3;

            line = detectAndConvertConstructor(line);
            line = detectAndConvertForLoop(line);
            line = addSemiColon(line, nextLine);
            line = detectPropertySetter(line);
            line = detectPropertyGetter(line);
            newLines.add(line);

        }

        if (classPos >= 0) {
            for (String key : propertyMap.keySet()) {
                newLines.add(classPos + 1, "\t\t" + propertyMap.get(key).toString());
            }
        }

        if (packagePos >= 0) {
            for (String imp : file.getImports()) {
                newLines.add(packagePos + 1, "\timport " + imp + ";");
            }
        }

        file.setLines(removeLastCurlyBrace(newLines));
    }

    private String detectPropertySetter(String line) {
        String orig = getLineWithoutComments(line);
        String temp = orig;

        Pattern p = Pattern.compile("function\\s+set\\s+(\\w+)\\((\\S*)\\)");
        Matcher m = p.matcher(temp);
        if (m.find()) {
            String propName = m.group(1);
            String objType = m.group(2).split(":")[1];
            Property prop = new Property(propName);
            prop.setObjectType(objType);
            String newFunctionName = propName + "Setter";
            temp = temp.replace("function set " + propName, "function " + newFunctionName);
            prop.setSetterFunction(newFunctionName);
            propertyMap.put(propName, prop);
        }

        return line.replace(orig, temp);
    }

    private String detectPropertyGetter(String line) {
        String orig = getLineWithoutComments(line);
        String temp = orig;

        Pattern p = Pattern.compile("function\\s+get\\s+(\\w+)\\(\\S*\\)\\s*:\\s*(\\S+)");
        Matcher m = p.matcher(temp);

        if (m.find()) {
            String propName = m.group(1);
            String objType = m.group(2);
            String newFunctionName = propName + "Getter";
            temp = temp.replace("function get " + propName, "function " + newFunctionName);
            if (propertyMap.containsKey(propName)) {
                Property prop = propertyMap.get(propName);
                prop.setGetterFunction(newFunctionName);
                propertyMap.put(propName, prop);
            } else {
                Property prop = new Property(propName);
                prop.setGetterFunction(newFunctionName);
                prop.setObjectType(objType);
                propertyMap.put(propName, prop);
            }
        }

        return line.replace(orig, temp);
    }

    private List<String> removeLastCurlyBrace(List<String> input) {
        int lastBrace = 0;
        for (int i = input.size() - 1; i > 0; i--) {
            String line = input.get(i);
            if (line.trim().endsWith("}")) {
                lastBrace = i;
                break;
            }
        }

        if (lastBrace > 0) {
            input.remove(lastBrace);
        }

        return input;
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

    /**
     * Converts the format of the for loop into the ... syntax
     *
     * @param line the current line being processed
     * @return the modified line
     */
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
     * @param line the current line being processed
     * @return the modified line
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
     * @param line the current line being processed
     * @return the modified line
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
     * @param line the current line being processed
     * @return the modified line
     */
    private boolean isImport(String line) {
        String orig = getLineWithoutComments(line);

        return orig.trim().startsWith("import ");

    }

    /**
     * Iterates through all the known types searching for them within each line.
     * If a match is found, and the type is not a basic type, it is added to the import list. The type is then converted
     * to its normalized format (uppercase first letter).
     *
     * @param line the current line being processed
     * @return the modified line
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
