package net.interaxia.haxer.scanner;

import net.interaxia.haxer.HaxeFile;
import net.interaxia.haxer.api.AllTypes;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Inspector {
    private File inputFile;
    private HaxeFile outputFile;

    public Inspector(File f) {
        inputFile = f;
        outputFile = new HaxeFile(f);
    }

    public File getInputFile() {
        return inputFile;
    }

    public void inspect() {
        try {
            FileReader reader = new FileReader(inputFile);
            BufferedReader breader = new BufferedReader(reader);
            String line;
            while ((line = breader.readLine()) != null) {
                outputFile.getLines().add(line);
            }
        }
        catch (FileNotFoundException fnf) {
            System.out.println(fnf.getMessage());
        }
        catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }

        detectPackage();
        detectClasses();
        detectInterfaces();

    }

    private void detectClasses() {
        Pattern p = Pattern.compile("public\\s+class\\s+(\\S+)");
        for (String line : outputFile.getLines()) {
            Matcher matcher = p.matcher(line);
            if (!matcher.find()) continue;
            AllTypes.getInstance().addType(outputFile.getPkg(), matcher.group(1));
        }
    }

    private void detectInterfaces() {
        Pattern p = Pattern.compile("public\\s+interface\\s+(\\S+)");
        for (String line : outputFile.getLines()) {
            Matcher matcher = p.matcher(line);
            if (!matcher.find()) continue;
            AllTypes.getInstance().addType(outputFile.getPkg(), matcher.group(1));
        }
    }

    private void detectPackage() {
        Pattern p = Pattern.compile("^[^a-z]*package\\s+(\\S+)\\b\\s*[{]*");
        for (String line : outputFile.getLines()) {
            Matcher matcher = p.matcher(line);
            if (!matcher.find()) continue;
            outputFile.setPkg(matcher.group(1));
        }
    }

    public HaxeFile getOutputFile() {
        return outputFile;
    }
}
