package com.comncon.plugin.deployer.upload;

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * @author Valery Kantor
 *         mailto: valery.kantor@gmail.com
 */
public class Item {
    @Parameter(alias = "source", required = true)
    private File source;
    @Parameter(alias = "destination", required = true)
    private String destination;
    @Parameter(alias = "cleanup", required = false)
    private String cleanup;

    public File getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getCleanup() {
        return cleanup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (!destination.equals(item.destination)) return false;
        if (!source.equals(item.source)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + destination.hashCode();
        return result;
    }
}
