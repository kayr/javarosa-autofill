package com.github.kayr.javarosa.autofill.submission;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Copied from SO
 * https://stackoverflow.com/questions/1155107/is-there-a-cross-platform-java-method-to-remove-filename-special-chars
 */
public class FileUtil {

    //removing tab-9,new line-10,\r-13, space - 32
    private final static int[] illegalChars = {34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47};

    static {
        Arrays.sort(illegalChars);
    }

    public static String cleanFileName(String badFileName) {
        return removeIllegalChars(badFileName).replaceAll("\\s+", "_").toLowerCase();
    }

    private static String removeIllegalChars(String badFileName) {
        StringBuilder cleanName = new StringBuilder();
        for (int i = 0; i < badFileName.length(); i++) {
            int c = (int) badFileName.charAt(i);
            if (Arrays.binarySearch(illegalChars, c) < 0) {
                cleanName.append((char) c);
            }
        }
        return cleanName.toString();
    }


    static String alternativePath(String path, String extension) {
        String newPath = path;
        int    value   = 1;
        while (Paths.get(appendExtension(newPath, extension)).toFile().exists()) {
            newPath = path + "_" + StringUtils.leftPad("" + value, 2, "0");
            value = value + 1;
        }
        return newPath;
    }

    public static String appendExtension(String filePath) {
        return appendExtension(filePath, ".generex.properties");
    }

    public static String appendExtension(String filePath, String extension) {
        if (filePath.endsWith(extension)) {
            return filePath;
        } else {
            return filePath + extension;
        }
    }

}