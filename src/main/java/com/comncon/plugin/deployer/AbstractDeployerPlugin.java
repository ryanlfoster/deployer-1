package com.comncon.plugin.deployer;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author Valery Kantor
 *         mailto: valery.kantor@gmail.com
 */
public abstract class AbstractDeployerPlugin extends AbstractMojo {

    @Parameter(alias = "host", required = true)
    private String host;
    @Parameter(alias = "port", defaultValue = "22")
    private int port;
    @Parameter(alias = "user", required = true)
    protected String user;
    @Parameter(alias = "password")
    protected String password;
    @Parameter(alias = "privateKeyPath")
    private String privateKeyPath;
    @Parameter(alias = "sudo", defaultValue = "true")
    protected boolean sudo;
    @Parameter(alias = "skip", defaultValue = "false")
    protected boolean skip;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Skip execution!");
            return;
        }

        final SessionFactory sessionFactory = new SessionFactory();
        final RemoteSession session;
        try {
            getLog().debug("Connect: " + user + "@" + host + ":" + port);
            if (StringUtils.isEmpty(privateKeyPath)) {
                session = sessionFactory.connectByPassword(host, port, user, password);
            } else {
                session = sessionFactory.connectByPrivateKey(host, port, user, privateKeyPath);
            }
        } catch (SshException e) {
            throw new MojoExecutionException("Unable to connect to remote server");
        }

        try {
            executeOnRemoteSession(session);
        } finally {
            getLog().debug("Disconnect from: " + host + ":" + port);
            session.close();
        }
    }

    protected void validate() throws MojoExecutionException {
    }

    protected abstract void executeOnRemoteSession(final RemoteSession session) throws MojoExecutionException;
}
