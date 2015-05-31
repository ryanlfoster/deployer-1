package com.comncon.plugin.deployer.upload;

import com.comncon.plugin.deployer.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.comncon.plugin.deployer.upload.FileUtils.getDestinationDirectory;
import static com.comncon.plugin.deployer.upload.FileUtils.quote;
import static java.text.MessageFormat.format;

/**
 * @author Valery Kantor
 *         mailto: valery.kantor@gmail.com
 */
@Mojo(name = "upload", defaultPhase = LifecyclePhase.PACKAGE)
public class UploadPlugin extends AbstractDeployerPlugin {

    @Parameter(alias = "items", required = true)
    private List<Item> items;

    @Override
    protected void validate() throws MojoExecutionException {
        for (final Item item : items) {
            if (!item.getSource().exists()) {
                getLog().error("File to copy does not exist: " + item.getSource().getAbsolutePath());
                throw new MojoExecutionException("File not found: " + item.getSource().getAbsolutePath());
            }
        }
    }


    @Override
    protected void executeOnRemoteSession(RemoteSession session) throws MojoExecutionException {
        try {
            final String temporaryFolder = "~/temp";
            final Exec exec = session.exec();
            final String cmdRm = format("rm -rf {0}", quote(temporaryFolder));
            getLog().info("Execute: " + cmdRm);
            exec.execute(cmdRm);

            final String cmdMkdir = format("mkdir -p {0}", quote(temporaryFolder));
            getLog().info("Execute: " + cmdMkdir);
            exec.execute(cmdMkdir);

            final Map<Item, String> temporaryPathMapping = new HashMap<Item, String>();
            final Scp scp = session.scp();
            getLog().debug("Upload " + items.size() + " to remote server");
            final long st = System.currentTimeMillis();
            long totalUploadedSize = 0;

            for (final Item item : items) {
                final String temporaryPath = temporaryFolder + "/" + item.getSource().getName();
                final long fileSizeBytes = item.getSource().length();
                getLog().info("SCP: " + item.getSource().getAbsolutePath() + " -> " + temporaryPath + " (size = " + Formatter.formatSize(fileSizeBytes) + ")");
                final long t1 = System.currentTimeMillis();
                scp.transfer(item.getSource(), temporaryPath);
                final long uploadTime = System.currentTimeMillis() - t1;
                getLog().debug("File " + item.getSource().getAbsolutePath() + " is uploaded to remote server in " + Formatter.formatTime(uploadTime) + " (Speed = " + Formatter.formatSpeed(fileSizeBytes, uploadTime) + ")");
                temporaryPathMapping.put(item, temporaryPath);
                totalUploadedSize += fileSizeBytes;
            }

            final long totalUploadTime = System.currentTimeMillis() - st;
            getLog().debug("All " + items.size() + " are uploaded to remote server in " + Formatter.formatTime(totalUploadTime) + " seconds (Speed = " + Formatter.formatSpeed(totalUploadedSize, totalUploadTime) + ")");

            final Exec sudoExec = sudo ? session.execAsSudo(password) : session.exec();
            for (final Item item : items) {
                if (item.getCleanup() != null && !item.getCleanup().isEmpty()) {
                    final String cmdCleanup = format("rm -rf {0}", quote(item.getCleanup()));
                    getLog().info("Execute: " + cmdCleanup);
                    sudoExec.execute(cmdCleanup);
                }

                final String destinationDirectory = getDestinationDirectory(item.getDestination());
                if (destinationDirectory != null) {
                    final String cmdMkdirDest = format("mkdir -p {0}", quote(destinationDirectory));
                    getLog().info("Execute: " + cmdMkdirDest);
                    sudoExec.execute(cmdMkdirDest);
                }
                final String temporaryPath = temporaryPathMapping.get(item);
                final String cmdMv = format("mv -f {0} {1}", quote(temporaryPath), quote(item.getDestination()));
                getLog().info("Execute: " + cmdMv);
                sudoExec.execute(cmdMv);
            }

        } catch (SshException e) {
            getLog().error("Unexpected SshException", e);
            throw new MojoExecutionException("Unexpected SshException", e);
        }
    }


}
