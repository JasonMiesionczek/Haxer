package net.interaxia.haxer;

import net.interaxia.haxer.api.AllTypes;
import net.interaxia.haxer.api.ObjectType;
import net.interaxia.haxer.scanner.Inspector;
import net.interaxia.haxer.scanner.Scanner;
import net.interaxia.haxer.translator.Translator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Atmospherian
 * Date: Jul 14, 2009
 * Time: 8:15:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class Haxer {
    public static void main(String[] args) {
        String inputPath = null;
        String outputPath = null;
        if (argsAreOk(args)) {
            inputPath = args[0];
            outputPath = args[1];
        }

        System.out.println(inputPath);
        System.out.println(outputPath);
        long processStart = System.currentTimeMillis();
        Scanner scanner = new Scanner();
        scanner.scan(new File(inputPath));
        List<File> files = scanner.getFilesToConvert();
        AllTypes.getInstance();
        List<HaxeFile> haxeFiles = new ArrayList<HaxeFile>();
        for (File f : files) {
            Inspector i = new Inspector(f);
            i.inspect();
            haxeFiles.add(i.getOutputFile());
        }

        for (HaxeFile hf : haxeFiles) {
            Translator t = new Translator(hf);
            t.translate();
            saveOutputFile(outputPath, hf);
        }
        long processEnd = System.currentTimeMillis();
        long totalTime = processEnd - processStart;
        System.out.println(haxeFiles.size() + " files translated in " + totalTime + "ms (" + totalTime / 1000 + "s)");
    }

    private static void saveOutputFile(String rootFolder, HaxeFile file) {
        String destinationPath = rootFolder + File.separator + file.getPkg().replace(".", File.separator);
        File root = new File(destinationPath);
        if (!root.exists()) {
            root.mkdirs();
        }

        String fileName = file.getFileName().replace(".as", "");
        ObjectType shortName = AllTypes.getInstance().getTypeByShortName(fileName);
        if (shortName != null) {
            fileName = shortName.getNormalizedTypeName() + ".hx";
        } else {
            fileName = fileName + ".hx";
        }

        try {
            String finalFile = destinationPath + File.separator + fileName;
            System.out.println("Saving: " + finalFile);
            FileWriter writer = new FileWriter(finalFile, false);
            for (String line : file.getLines()) {
                writer.write(line + "\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    private static boolean argsAreOk(String[] args) {
        if (args.length < 2) {
            System.out.println("Wrong number of arguments received.\nUSAGE: haxer <input path> <output path>");
            return false;
        }

        return true;
    }
}
