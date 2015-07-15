package com.comncon.plugin.deployer.startup;

import com.comncon.plugin.deployer.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.text.MessageFormat;

/**
 * @author Valery Kantor
 *         mailto: valery.kantor@gmail.com
 */
@Mojo(name = "startup", defaultPhase = LifecyclePhase.PACKAGE)
public class StartupPlugin extends AbstractDeployerPlugin {

    @Parameter(alias = "tomcat")
    protected Tomcat tomcat;

    @Override
    protected void executeOnRemoteSession(RemoteSession session) throws MojoExecutionException {
        final Exec exec = sudo ? session.execAsSudo(password) : session.exec();
        try {
            if (tomcat != null) {
                final Tomcat.Version tomcatVersion = tomcat.getResolvedVersion();
                String startTomcatCmd;
                /*if (!StringUtils.isEmpty(tomcat.getTomcatPath())) {
                    startTomcatCmd = MessageFormat.format("{0}/bin/catalina.sh start", tomcat.getTomcatPath());
                } else {
                    startTomcatCmd = MessageFormat.format("service {0} start", tomcatVersion.getService());
                }*/

                startTomcatCmd = MessageFormat.format("service {0} start", tomcatVersion.getService());
                getLog().info("Execute command: " + startTomcatCmd);
                exec.execute(startTomcatCmd);
                getLog().info("Waiting for server startup");
            }
        } catch (SshException e) {
            getLog().error("Unexpected SshException", e);
            throw new  MojoExecutionException(e.getMessage(), e);
        }
    }
}
