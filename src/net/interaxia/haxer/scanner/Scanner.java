package net.interaxia.haxer.scanner;

import java.util.List;
import java.util.ArrayList;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Atmospherian
 * Date: Jul 14, 2009
 * Time: 8:17:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class Scanner {
    private List<File> filesToConvert;

    public Scanner() {
        filesToConvert = new ArrayList<File>();
    }

    public List<File> getFilesToConvert() {
        return filesToConvert;
    }

    public void scan(File rootDir) {
        File[] children = rootDir.listFiles();
        for (File f : children) {
            if (f.isFile()) {
                if (f.getName().endsWith(".as"))
                    filesToConvert.add(f);
            }

            if (f.isDirectory())
                scan(f);
        }
    }

}
