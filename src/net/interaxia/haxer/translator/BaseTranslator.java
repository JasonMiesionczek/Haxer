package net.interaxia.haxer.translator;

public class BaseTranslator {

    protected boolean isStartOfComment(String input) {
        return input.trim().startsWith("/*");

    }

    protected boolean isEndOfComment(String input) {
        return input.trim().endsWith("*/");

    }

    protected boolean isSingleLineComment(String input) {
        String temp = input.trim();
        return temp.startsWith("//") || (temp.startsWith("/*") && temp.endsWith("*/"));

    }

    protected String getLineWithoutComments(String line) {
        int idx = line.indexOf("//");
        if (idx >= 0) {
            return line.substring(0, idx);
        }

        return line;

    }
}
