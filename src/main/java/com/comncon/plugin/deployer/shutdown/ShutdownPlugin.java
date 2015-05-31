package com.comncon.plugin.deployer.shutdown;

import com.comncon.plugin.deployer.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;

import java.text.MessageFormat;

/**
 * @author Valery Kantor
 *         mailto: valery.kantor@gmail.com
 */
@Mojo(name = "shutdown", defaultPhase = LifecyclePhase.PACKAGE)
public class ShutdownPlugin extends AbstractDeployerPlugin {

    @Parameter(alias = "delay", defaultValue = "10")
    private long delay;
    @Parameter(alias = "tomcat")
    private Tomcat tomcat;

    @Override
    protected void executeOnRemoteSession(RemoteSession session) throws MojoExecutionException {
        final Exec exec = sudo ? session.execAsSudo(password) : session.exec();
        try {
            if (tomcat != null) {
                final Tomcat.Version tomcatVersion = tomcat.getResolvedVersion();
                String stopTomcatCmd;
                if (!StringUtils.isEmpty(tomcat.getTomcatPath())) {
                    stopTomcatCmd = MessageFormat.format("{0}/bin/catalina.sh stop", tomcat.getTomcatPath());
                } else {
                    stopTomcatCmd = MessageFormat.format("service {0} stop", tomcatVersion.getService());
                }
                getLog().info("Execute command: " + stopTomcatCmd);
                exec.execute(stopTomcatCmd);
                getLog().info("Waiting " + delay + " seconds...");
                try {
                    Thread.sleep(delay * 1000L);
                } catch (InterruptedException e) {
                }

            }
        } catch (SshException e) {
            getLog().error("Unexpected SshException", e);
            throw new MojoExecutionException("Unexpected SshException", e);
        }

    }
}
