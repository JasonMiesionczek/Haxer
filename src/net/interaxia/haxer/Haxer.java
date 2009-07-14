package net.interaxia.haxer;

import net.interaxia.haxer.scanner.Scanner;
import net.interaxia.haxer.scanner.Inspector;
import net.interaxia.haxer.api.AllTypes;
import net.interaxia.haxer.api.ObjectType;
import net.interaxia.haxer.translator.Translator;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Atmospherian
 * Date: Jul 14, 2009
 * Time: 8:15:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class Haxer {
    public static void main(String[] args){
        Scanner scanner= new Scanner();
        scanner.scan(new File(args[0]));
        List<File> files = scanner.getFilesToConvert();
        AllTypes.getInstance();
        List<HaxeFile> haxeFiles = new ArrayList<HaxeFile>();
        for (File f: files) {
            Inspector i = new Inspector(f);
            i.inspect();
            haxeFiles.add(i.getOutputFile());
        }

        for (HaxeFile hf: haxeFiles) {
            Translator t = new Translator(hf);
            t.translate();
        }
    }
}
