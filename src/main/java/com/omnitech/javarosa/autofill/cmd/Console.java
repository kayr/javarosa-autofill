package com.omnitech.javarosa.autofill.cmd;

import com.omnitech.javarosa.autofill.api.IOUtils;
import com.omnitech.javarosa.autofill.submission.DataGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Console {

    private static final String USERNAME = "username",
            PASSWORD                     = "password",
            SERVER                       = "server",
            FORM_DEF                     = "form",
            DRY_RUN                      = "dryRun";

    private static final List<String> ALL_PROPERTIES = Arrays.asList(USERNAME, PASSWORD, SERVER, FORM_DEF);

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Please Provide Config Properties File With the following properties:");
            System.out.println(ALL_PROPERTIES);
            return;
        }


        Path path = Paths.get(args[0]);

        Properties properties = new Properties();
        properties.load(Files.newInputStream(path));

        asserPropertiesExist(properties);

        DataGenerator dataGenerator = new DataGenerator().setServerUrl(properties.getProperty(SERVER))
                                                         .setUsername(properties.getProperty(USERNAME))
                                                         .setPassword(properties.getProperty(PASSWORD))
                                                         .setFormDefXMl(readFile(properties, FORM_DEF))
                                                         .setDryRun(properties.getOrDefault(DRY_RUN, "true") == "true");

        dataGenerator.start();


    }

    private static void asserPropertiesExist(Properties properties) {
        ALL_PROPERTIES.forEach(p -> {
            if (!properties.containsKey(p)) throw new RuntimeException("Property Not Found: " + p);
        });
    }

    private static String readFile(Map properties, String string) throws IOException {

        String s = properties.get(string).toString();

        return IOUtils.getText(Files.newBufferedReader(Paths.get(s)));
    }
}
