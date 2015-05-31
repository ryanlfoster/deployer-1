package com.comncon.plugin.deployer;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Valery Kantor
 *         mailto: valery.kantor@gmail.com
 */
public class Exec {
    private final Session session;
    private final String sudoPassword;

    public Exec(Session session, String sudoPassword) {
        this.session = session;
        this.sudoPassword = sudoPassword;
    }

    public void execute(String command) throws SshException {
        doExec(command);
    }

    /**
     * Executes a remote command
     *
     * @param command remote command
     * @return remote command exit status
     * @throws SshException
     */
    private int doExec(String command) throws SshException {
        final Channel channel = connect(command);
        try {
            final InputStream in = channel.getInputStream();
            final byte[] buf = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    final int i = in.read(buf, 0, 1024);
                    if (i < 0) break;
                    /*if (out != null) {
                        out.write(buf, 0, i);
                    }*/
                }
                if (channel.isClosed()) {
                    return channel.getExitStatus();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ee) {
                }
            }
        } catch (IOException e) {
            throw new SshException(e);
        } finally {
            /*if (out != null) {
                try {
                    out.flush();
                } catch (IOException e) {}
            }*/
            channel.disconnect();
        }
    }

    private Channel connect(final String command) throws SshException {
        return sudoPassword != null ? connectAsSudo(command, sudoPassword) : connectRaw(command);
    }

    private Channel connectRaw(final String command) throws SshException {
        try {
            final Channel channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            channel.connect();
            return channel;
        } catch (JSchException e) {
            throw new SshException(e);
        }
    }

    private Channel connectAsSudo(final String command, final String password) throws SshException {
        final Channel channel;
        try {
            channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand("sudo -S -p '' " + command);
            final OutputStream out = channel.getOutputStream();
            ((ChannelExec) channel).setErrStream(System.err);
            channel.connect();
            final byte[] passwordBytes = (password + "\n").getBytes("UTF-8");
            out.write(passwordBytes);
            out.flush();
            return channel;
        } catch (JSchException e) {
            throw new SshException(e);
        } catch (IOException e) {
            throw new SshException(e);
        }

    }
}
