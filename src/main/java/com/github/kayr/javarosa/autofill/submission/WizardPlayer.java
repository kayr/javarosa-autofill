package com.github.kayr.javarosa.autofill.submission;

import com.github.kayr.javarosa.autofill.api.AutoFillException;
import com.github.kayr.javarosa.autofill.api.FormUtils;
import com.github.kayr.javarosa.autofill.api.IOUtils;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.IFormElement;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WizardPlayer {

    private TextIO       textIO = TextIoFactory.getTextIO();
    private TextTerminal term   = textIO.getTextTerminal();

    private JavarosaSubmitter submitter = new JavarosaSubmitter();

    private List<JavarosaSubmitter.XForm> xForms;
    private JavarosaSubmitter.XForm       xForm;
    private Path                          generexPath;
    private Integer                       numberOfRecords;
    private Boolean                       dryRun;
    private String                        serverUrl;
    private String                        username;
    private String                        password;


    public void start() {

        boolean ccontinue = true;

        while (ccontinue) {
            try {
                doStart();

            } catch (Exception x) {
                term.println("Error Occurred Generating Data:" + x.getMessage());
                String message = getStackTrace(x);
                term.print(message);
            }

            ccontinue = textIO.newBooleanInputReader()
                              .read("Process Another Form");
        }

        System.exit(0);

    }

    private String getStackTrace(Exception x) {
        StringWriter w  = new StringWriter();
        PrintWriter  pw = new PrintWriter(w);
        x.printStackTrace(pw);
        return w.toString();
    }

    private void doStart() {
        runAll(this::__formList,
               this::__selectForm,
               this::__selectOrCreateGenerex,
               this::__getNumberOfItems,
               this::__getDryRun,
               this::__assertContinue);
    }

    private void __formList() {
        while (true) {


            serverUrl = textIO.newStringInputReader()
                              .withDefaultValue(submitter.getServerUrl())
                              .read("URL");

            username = textIO.newStringInputReader()
                             .withDefaultValue(submitter.getUsername())
                             .read("UserName");

            password = textIO.newStringInputReader()
                             .withDefaultValue(submitter.getPassword())
                             .read("Password");

            submitter.reInit();

            try {
                xForms = submitter.formList();
                return;
            } catch (Exception e) {
                term.println("Error Fetching Form List: " + e.getClass().getSimpleName() + ":" + e.getMessage());
            }
        }
    }


    private void __selectForm() {
        List<String> possibleValues = xForms.stream()
                                            .map(s -> String.format("%s(%s)", s.name, s.formID))
                                            .collect(Collectors.toList());

        String select_form = textIO.newStringInputReader()
                                   .withNumberedPossibleValues(possibleValues)
                                   .read("Select Form");

        int i = possibleValues.indexOf(select_form);

        xForm = xForms.get(i);

    }

    private void __selectOrCreateGenerex() {
        String option = textIO.newStringInputReader()
                              .withInlinePossibleValues("s", "c")
                              .read("Select(S) or Create(C) Generex File");

        if ("s".equalsIgnoreCase(option)) {

            selectGenerexFile();


        } else {
            createGenerexFile();


        }
    }

    private void __getNumberOfItems() {
        numberOfRecords = textIO.newIntInputReader()
                                .withMinVal(1)
                                .read("Enter number of Records");
    }


    private void __getDryRun() {
        dryRun = textIO.newBooleanInputReader()
                       .withDefaultValue(true)
                       .read("Dry Run?");

    }

    private void __assertContinue() {

        boolean doContinue = true;
        if (!dryRun) {
            doContinue = textIO.newBooleanInputReader()
                               .read("All done. Am About to start submitting to the server is this ok?");
        }

        if (doContinue) {
            try {
                startSubmission();
            } catch (IOException e) {
                IOUtils.sneakyThrow(e);
            }
        }

    }

    private void startSubmission() throws IOException {
        DataGenerator dataGenerator = new DataGenerator().setServerUrl(serverUrl)
                                                         .setUsername(username)
                                                         .setPassword(password)
                                                         .setFormDefXMl(getXform())
                                                         .setNumberOfItems(numberOfRecords)
                                                         .setDryRun(dryRun)
                                                         .setDataListener((i, s) -> {
                                                             term.println("Processed " + i);
                                                             Console.saveData(i, s, generexPath);
                                                         });

        dataGenerator.start();


    }


    private String getXform() throws IOException {
        configSubmitter();
        return submitter.pullXfom(xForm);
    }

    private void configSubmitter() {
        submitter.setServerUrl(serverUrl);
        submitter.setPassword(password);
        submitter.setUsername(username);
        submitter.reInit();
    }


    private void selectGenerexFile() {
        String filePath = textIO.newStringInputReader()
                                .withValueChecker((val, itemName) -> {
                                    if (!Paths.get(val).toFile().exists()) {
                                        return Collections.singletonList("File Does Not Exist");
                                    }

                                    return Collections.emptyList();
                                })
                                .read("Enter File Path");

        this.generexPath = Paths.get(filePath);
    }

    private void createGenerexFile() {
        String filePath = textIO.newStringInputReader()
                                .withValueChecker((val, itemName) -> {
                                    if (Paths.get(appendExtension(val)).toFile().exists()) {
                                        return Collections.singletonList("File Already Exists");
                                    }
                                    return Collections.emptyList();
                                })
                                .read("Enter File Path/Name");

        try {
            filePath = appendExtension(filePath);
            Path path = Paths.get(filePath);

            boolean newFile = path.toFile().createNewFile();

            if (newFile) {
                term.println("File created: " + path.toAbsolutePath());
                this.generexPath = path;
                populateGenerex();
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(this.generexPath.toFile());
                    term.println("#####################################");
                    term.println("Opening File : " + path.toAbsolutePath());
                    term.println("Please fill add your generator expression there before you continue ");
                    term.println("#####################################");
                }
            } else {
                throw new AutoFillException("Failed to Create File: " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            IOUtils.sneakyThrow(e);
        }
    }

    private String appendExtension(String filePath) {
        return filePath + ".generex.properties";
    }

    private void populateGenerex() throws IOException {
        FormDef formDef = FormUtils.parseFromText(getXform());

        List<IFormElement> children = FormUtils.getChildren(formDef);

        List<String> bindVariables = children.stream()
                                             .filter(c -> c.getBind() != null && c.getBind().getReference().toString().lastIndexOf('/') != 0)
                                             .map(e -> "#" + e.getLabelInnerText() + "\n" + FormUtils.resolveVariable(e))
                                             .collect(Collectors.toList());

        String properties = String.join("=\n", bindVariables) + "=";

        Files.write(generexPath, properties.getBytes(StandardCharsets.UTF_8));


    }


    private void runAll(Runnable... runnable) {
        for (Runnable r : runnable) {
            r.run();
        }
    }


}
