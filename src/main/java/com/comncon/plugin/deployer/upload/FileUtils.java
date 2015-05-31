package com.comncon.plugin.deployer.upload;

/**
 * @author Valery Kantor
 *         mailto: valery.kantor@gmail.com
 */
public class FileUtils {

    private FileUtils() {}

    public static String quote(final String src) {
        return "\"" + src + "\"";
    }

    public static String getDestinationDirectory(final String path) {
        if (path.endsWith("/")) {
            return path.substring(0, path.lastIndexOf("/"));
        }
        final int i = path.lastIndexOf("/");
        if (i == -1) {
            return isFile(path) ? null : path;
        }
        final String lastPart = path.substring(i + 1);
        if (isFile(lastPart)) {
            return path.substring(0, i);
        } else {
            return path;
        }
    }

    public static boolean isFile(final String path) {
        return path.contains(".");
    }

}
