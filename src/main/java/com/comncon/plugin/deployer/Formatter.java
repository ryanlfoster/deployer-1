package com.comncon.plugin.deployer;

public class Formatter {
    public static String formatTime(final long ms) {
        if (ms < 1000) {
            return ms + "ms";
        } else if (ms < 5 * 60000) {
            return (ms / 1000) + "sec";
        } else {
            return (ms / 60000) + "min";
        }
    }

    public static String formatSize(final long bytes) {
        if (bytes < 1024) {
            return bytes + "b";
        } else if (bytes < 5 * 1024 * 1024) {
            return (bytes / 1024) + "Kb";
        } else {
            return (bytes / 1024 / 1024) + "Mb";
        }
    }

    public static String formatSpeed(final long bytes, final long ms) {
        return (bytes * 1000 * 8 / Math.max(ms, 1) / 1024) + "Kbit/s";
    }
}
