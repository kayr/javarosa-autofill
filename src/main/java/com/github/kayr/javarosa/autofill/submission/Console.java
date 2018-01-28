package com.github.kayr.javarosa.autofill.submission;

import com.github.kayr.javarosa.autofill.api.AutoFillException;
import com.github.kayr.javarosa.autofill.api.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Console {

    private static final String USERNAME = "username",
            PASSWORD                     = "password",
            SERVER                       = "server",
            FORM_DEF                     = "form",
            NUMBER_OF_ITEMS              = "numberOfItems",
            DRY_RUN                      = "dryRun",
            GENEREX_FILE                 = "generex";

    private static final List<String> ALL_PROPERTIES = Arrays.asList(USERNAME, PASSWORD, SERVER, FORM_DEF, NUMBER_OF_ITEMS);

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            startWizard();
        } else if (args.length == 1) {
            processPropertiesFile(args[0]);
        }


    }


    private static void startWizard() {

        new WizardPlayer().start();
    }

    private static void processPropertiesFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        Properties properties = FileUtil.loadProperties(path);

        assertPropertiesExist(properties, ALL_PROPERTIES);

        DataGenerator dataGenerator = new DataGenerator().setServerUrl(properties.getProperty(SERVER))
                                                         .setUsername(properties.getProperty(USERNAME))
                                                         .setPassword(properties.getProperty(PASSWORD))
                                                         .setFormDefXMl(readFile(properties, FORM_DEF))
                                                         .setNumberOfItems(Integer.parseInt(properties.getProperty(NUMBER_OF_ITEMS)))
                                                         .setDryRun(properties.getOrDefault(DRY_RUN, "true").equals("true"))
                                                         .setDataListener((i, s) -> saveData(i, s, path));

        String generexFile = properties.getProperty(GENEREX_FILE);

        if (generexFile != null) {
            Map<String, String> propes = new HashMap<>();

            Path generexPath = Paths.get(generexFile);

            Properties generexProperties = FileUtil.loadProperties(generexPath);
            generexProperties.keySet().forEach(s -> propes.put(s.toString(), generexProperties.getProperty(s.toString())));
            dataGenerator.setGenerexMap(propes);
        }


        dataGenerator.start();
    }

    public static void saveData(Integer iteration, String data, Path path) {
        String fileName       = path.getFileName().toString() + "__submit_data";
        Path   submitDataPath = path.resolveSibling(fileName);

        if (!submitDataPath.toFile().exists()) {
            if (!submitDataPath.toFile().mkdirs()) {
                throw new AutoFillException("Failed to create out xml path: " + submitDataPath);
            }
        }

        Path dataPath = submitDataPath.resolve(StringUtils.leftPad(iteration.toString(), 4, '0') + ".xml");

        try {
            Files.write(dataPath, data.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new AutoFillException(e);
        }


    }


    private static void assertPropertiesExist(Properties properties, List<String> propertyList) {
        propertyList.forEach(p -> {
            if (!properties.containsKey(p)) throw new RuntimeException("Property Not Found: " + p);
        });
    }

    private static String readFile(Map properties, String string) throws IOException {

        String s = properties.get(string).toString();

        return IOUtils.getText(Files.newBufferedReader(Paths.get(s)));
    }
}
