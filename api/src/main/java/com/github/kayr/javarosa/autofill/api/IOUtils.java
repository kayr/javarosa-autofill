package com.github.kayr.javarosa.autofill.api;

import com.github.kayr.javarosa.autofill.api.functions.FnRandomSelectFromFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Methods copied from groovy
 */
public class IOUtils {

    private static Logger LOG = Logger.getLogger(IOUtils.class.getName());

    public static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream answer = new ByteArrayOutputStream();
        // reading the content of the file within a byte buffer
        byte[] byteBuffer = new byte[8192];
        int    nbByteRead /* = 0*/;
        try {
            while ((nbByteRead = is.read(byteBuffer)) != -1) {
                // appends buffer
                answer.write(byteBuffer, 0, nbByteRead);
            }
        } finally {
            closeWithWarning(is);
        }
        return answer.toByteArray();
    }


    /**
     * Read the content of this InputStream and return it as a String.
     * The stream is closed before this method returns.
     *
     * @param is an input stream
     * @return the text from that URL
     * @throws IOException if an IOException occurs.
     * @since 1.0
     */
    public static String getText(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        return getText(reader);
    }


    /**
     * Read the content of this InputStream using specified charset and return
     * it as a String.  The stream is closed before this method returns.
     *
     * @param is      an input stream
     * @param charset opens the stream with a specified charset
     * @return the text from that URL
     * @throws IOException if an IOException occurs.
     * @since 1.0
     */
    public static String getText(InputStream is, String charset) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
        return getText(reader);
    }

    /**
     * Read the content of the Reader and return it as a String.  The reader
     * is closed before this method returns.
     *
     * @param reader a Reader whose content we want to read
     * @return a String containing the content of the buffered reader
     * @throws IOException if an IOException occurs.
     * @see #getText(java.io.BufferedReader)
     * @since 1.0
     */
    public static String getText(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        return getText(bufferedReader);
    }

    /**
     * Read the content of the BufferedReader and return it as a String.
     * The BufferedReader is closed afterwards.
     *
     * @param reader a BufferedReader whose content we want to read
     * @return a String containing the content of the buffered reader
     * @throws IOException if an IOException occurs.
     * @since 1.0
     */
    public static String getText(BufferedReader reader) throws IOException {
        StringBuilder answer = new StringBuilder();
        // reading the content of the file within a char buffer
        // allow to keep the correct line endings
        char[] charBuffer = new char[8192];
        int    nbCharRead /* = 0*/;
        try {
            while ((nbCharRead = reader.read(charBuffer)) != -1) {
                // appends buffer
                answer.append(charBuffer, 0, nbCharRead);
            }
            Reader temp = reader;
            reader = null;
            temp.close();
        } finally {
            closeWithWarning(reader);
        }
        return answer.toString();
    }

    /**
     * Close the Closeable. Logging a warning if any problems occur.
     *
     * @param c the thing to close
     */
    public static void closeWithWarning(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                LOG.warning("Caught exception during close(): " + e);
            }
        }
    }

    /**
     * Close the Closeable. Ignore any problems that might occur.
     *
     * @param c the thing to close
     */
    public static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                /* ignore */
            }
        }
    }

    public static List<String> loadFile(String filePath) throws IOException {
        String basePathEnv  = System.getenv(FnRandomSelectFromFile.AUTO_FILL_BASE_PATH);
        String basePathProp = System.getProperty(FnRandomSelectFromFile.AUTO_FILL_BASE_PATH);
        String basePath     = basePathEnv != null ? basePathEnv : basePathProp;

        Path path = basePath != null ? Paths.get(basePath, filePath) : Paths.get(filePath);
        return Files.lines(path).collect(Collectors.toList());
    }

//    @SuppressWarnings({"UnusedReturnValue", "unchecked"})
//    public static <T extends Throwable> T sneakyThrow(Throwable t) throws T {
//        throw (T) t;
//    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable, R> R sneakyThrow(Throwable t) throws T {

        throw (T) t; // ( ͡° ͜ʖ ͡°)

    }
}
