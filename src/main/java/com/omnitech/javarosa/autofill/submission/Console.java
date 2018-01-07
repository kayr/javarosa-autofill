package com.omnitech.javarosa.autofill.submission;

import com.omnitech.javarosa.autofill.api.AutoFillException;
import com.omnitech.javarosa.autofill.api.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
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
            NUMBER_OF_ITEMS              = "numberOfItems",
            DRY_RUN                      = "dryRun";

    private static final List<String> ALL_PROPERTIES = Arrays.asList(USERNAME, PASSWORD, SERVER, FORM_DEF, NUMBER_OF_ITEMS);

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Please Provide Config Properties File With the following properties:");
            System.out.println(ALL_PROPERTIES);
            return;
        }


        Path path = Paths.get(args[0]);

        Properties properties = new Properties();
        properties.load(Files.newInputStream(path));

        assertPropertiesExist(properties,ALL_PROPERTIES);

        DataGenerator dataGenerator = new DataGenerator().setServerUrl(properties.getProperty(SERVER))
                                                         .setUsername(properties.getProperty(USERNAME))
                                                         .setPassword(properties.getProperty(PASSWORD))
                                                         .setFormDefXMl(readFile(properties, FORM_DEF))
                                                         .setNumberOfItems(Integer.parseInt(properties.getProperty(NUMBER_OF_ITEMS)))
                                                         .setDryRun(properties.getOrDefault(DRY_RUN, "true").equals("true"))
                                                         .setDataListener((i, s) -> saveData(i, s, path));

        dataGenerator.start();


    }

    static private void saveData(Integer iteration, String data, Path path) {
        Path submitDataPath = path.resolveSibling("__submit_data");

        if (!submitDataPath.toFile().exists()) {
            if (!submitDataPath.toFile().mkdirs()) {
                throw new AutoFillException("Failed to create out xml path: " + submitDataPath);
            }
        }

        Path dataPath = submitDataPath.resolve(StringUtils.leftPad(iteration.toString(), 4, '0') + ".xml");

        try {
            Files.write(dataPath, data.getBytes(Charset.defaultCharset()));
        } catch (IOException e) {
            throw new AutoFillException(e);
        }


    }


    private static void assertPropertiesExist(Properties properties,List<String> propertyList) {
        propertyList.forEach(p -> {
            if (!properties.containsKey(p)) throw new RuntimeException("Property Not Found: " + p);
        });
    }

    private static String readFile(Map properties, String string) throws IOException {

        String s = properties.get(string).toString();

        return IOUtils.getText(Files.newBufferedReader(Paths.get(s)));
    }
}