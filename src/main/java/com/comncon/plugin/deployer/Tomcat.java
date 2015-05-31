package com.comncon.plugin.deployer;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author Valery Kantor
 *         mailto: valery.kantor@gmail.com
 */
public class Tomcat {
    private final int DEFAULT_PORT = 8080;
    private static final Version DEFAULT_VERSION = Version.TOMCAT_8;

    @Parameter(alias = "version")
    private String version;
    @Parameter(alias = "tomcatPath")
    private String tomcatPath;

    public Version getResolvedVersion() throws MojoExecutionException {
        if (StringUtils.isEmpty(version)) {
            return DEFAULT_VERSION;
        } else if ("tomcat7".equals(version)) {
            return Version.TOMCAT_7;
        } else if ("tomcat6".equals(version)) {
            return Version.TOMCAT_6;
        } else {
            throw new MojoExecutionException("Unsupported tomcat version: " + version);
        }
    }

    public String getTomcatPath() {
        return tomcatPath;
    }

    public enum Version {
        TOMCAT_6("tomcat6", "/var/lib/tomcat6"),
        TOMCAT_7("tomcat7", "/var/lib/tomcat6"),
        TOMCAT_8("tomcat8", "/var/lib/tomcat6");

        private final String service;
        private final String directory;

        private Version(String service, String directory) {
            this.service = service;
            this.directory = directory;
        }

        public String getService() {
            return service;
        }

        public String getDirectory() {
            return directory;
        }
    }
}
