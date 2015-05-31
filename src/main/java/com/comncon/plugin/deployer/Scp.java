package com.comncon.plugin.deployer;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Scp {
    private final Session session;

    public Scp(final Session session) {
        this.session = session;
    }

    public void transfer(final File file, final String destination) throws SshException {
        final Channel channel;
        final ChannelTunnel tunnel;
        try {
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("scp -t " + quote(destination));
            tunnel = ChannelTunnel.connect(channel);
        } catch (JSchException e) {
            throw new SshException("Unable to open exec channel", e);
        }

        try {
            final String command = "C0644 " + file.length() + " " + file.getName() + "\n";
            tunnel.send(command);

            final InputStream fis = new FileInputStream(file);
            try {
                final byte[] buf = new byte[1024];
                while (true) {
                    final int len = fis.read(buf, 0, buf.length);
                    if (len <= 0) break;
                    tunnel.write(buf, 0, len); //out.flush();
                }
            } finally {
                fis.close();
            }

            tunnel.write(new byte[]{0});
            tunnel.commit();
        } catch (IOException e) {
            throw new SshException(e);
        } finally {
            tunnel.close();
            channel.disconnect();
        }
    }

    private static String quote(final String src) {
        return "\"" + src + "\"";
    }
}
