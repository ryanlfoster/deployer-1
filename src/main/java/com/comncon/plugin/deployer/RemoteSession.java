package com.comncon.plugin.deployer;

import com.jcraft.jsch.Session;

/**
 * @author Valery Kantor
 *         mailto: valery.kantor@gmail.com
 */
public class RemoteSession {
    private final Session session;

    public RemoteSession(Session session) {
        this.session = session;
    }

    public Scp scp() {
        return new Scp(session);
    }

    public Exec exec() {
        return new Exec(session, null);
    }

    public Exec execAsSudo(String password) {
        return new Exec(session, password);
    }

    public void close() {
        session.disconnect();
    }
}
