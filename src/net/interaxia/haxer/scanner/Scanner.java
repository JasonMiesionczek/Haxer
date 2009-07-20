package net.interaxia.haxer.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
