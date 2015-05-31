package com.comncon.plugin.deployer;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Properties;

/**
 * @author Valery Kantor
 *         mailto: valery.kantor@gmail.com
 */
public class SessionFactory {

    public RemoteSession connectByPassword(String host, int port, String user, String password) throws SshException {
        final JSch jsch = new JSch();
        try {
            final Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            final Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            return new RemoteSession(session);
        } catch (JSchException e) {
            throw new SshException(e);
        }
    }

    public RemoteSession connectByPrivateKey(String host, int port, String user, String privateKeyPath) throws SshException {
        final JSch jsch = new JSch();
        try {
            jsch.addIdentity(privateKeyPath);
            final Session session = jsch.getSession(user, host, port);
            final Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            return new RemoteSession(session);
        } catch (JSchException e) {
            throw new SshException(e);
        }
    }
}
