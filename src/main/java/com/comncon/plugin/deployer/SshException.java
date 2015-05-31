package com.comncon.plugin.deployer;

/**
 * @author Valery Kantor
 *         mailto: valery.kantor@gmail.com
 */
public class SshException extends Exception {
    public SshException() {
    }

    public SshException(String message) {
        super(message);
    }

    public SshException(String message, Throwable cause) {
        super(message, cause);
    }

    public SshException(Throwable cause) {
        super(cause);
    }
}
