package net.interaxia.haxer.translator;

/**
 * Created by IntelliJ IDEA.
 * User: Atmospherian
 * Date: Jul 16, 2009
 * Time: 8:19:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class BaseTranslator {

    protected boolean isStartOfComment(String input) {
        if (input.trim().startsWith("/*"))
            return true;

        return false;
    }

    protected boolean isEndOfComment(String input) {
        if (input.trim().endsWith("*/"))
            return true;

        return false;
    }

    protected boolean isSingleLineComment(String input) {
        String temp = input.trim();
        if (temp.startsWith("//") || (temp.startsWith("/*") && temp.endsWith("*/")))
            return true;

        return false;
    }

    protected String getLineWithoutComments(String line) {
        int idx = line.indexOf("//");
        if (idx >= 0) {
            return line.substring(0, idx);
        }

        return line;

    }
}
