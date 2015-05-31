package com.comncon.plugin.deployer;

import java.text.MessageFormat;

/**
 * @author Valery Kantor
 *         mailto: valery.kantor@gmail.com
 */
public class ConnectionTest {

    public static void main(String[] args) {
        final SessionFactory sessionFactory = new SessionFactory();
        RemoteSession remoteSession = null;
        try {
            remoteSession = sessionFactory.connectByPrivateKey("52.25.108.201", 22, args[0], args[1]);
            final String stopTomcatCmd = MessageFormat.format("{0}/bin/catalina.sh stop", "/opt/tomcat");
        } catch (SshException e) {
            e.printStackTrace();
        } finally {
            if (remoteSession != null) {
                remoteSession.close();
            }
        }
    }
}
