package com.comncon.plugin.deployer;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ChannelTunnel {
    private final InputStream in;
    private final OutputStream out;

    private ChannelTunnel(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
    }

    public static ChannelTunnel connect(final Channel channel) throws SshException {
        try {
            final InputStream in = channel.getInputStream();
            final OutputStream out = channel.getOutputStream();
            channel.connect();
            if (!checkAck(in)) {
                throw new SshException("Invalid acknowledgement received");
            }
            return new ChannelTunnel(in, out);
        } catch (IOException e) {
            throw new SshException(e);
        } catch (JSchException e) {
            throw new SshException(e);
        }
    }

    public void send(final String data) throws SshException {
        try {
            out.write(data.getBytes("UTF-8"));
            out.flush();
            if (!checkAck(in)) {
                throw new SshException("Invalid acknowledgement received");
            }
        } catch (IOException e) {
            throw new SshException(e);
        }
    }

    public void write(final byte[] data, final int off, final int length) throws SshException {
        try {
            out.write(data, off, length);
        } catch (IOException e) {
            throw new SshException(e);
        }
    }

    public void write(final byte[] data) throws SshException {
        try {
            out.write(data);
        } catch (IOException e) {
            throw new SshException(e);
        }
    }

    public void commit() throws SshException {
        try {
            out.flush();
            if (!checkAck(in)) {
                throw new SshException("Invalid acknowledgement received");
            }
        } catch (IOException e) {
            throw new SshException(e);
        }
    }

    public void close() throws SshException {
        try {
            out.close();
        } catch (IOException e) {
            throw new SshException(e);
        }
    }

    private static boolean checkAck(final InputStream in) throws IOException {
        return readAck(in) == 0;
    }

    private static int readAck(final InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) return b;
        if (b == -1) return b;

        if (b == 1 || b == 2) {
            final StringBuilder sb = new StringBuilder();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            }
            while (c != '\n');
            if (b == 1) { // error
                System.out.print(sb.toString());
            }
            if (b == 2) { // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }
}
