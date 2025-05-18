package com.matias.physiocarepsp.utils;

import com.jcraft.jsch.*;

import java.io.ByteArrayInputStream;
import java.util.concurrent.CompletableFuture;

/**
 * Utility class for uploading files to an SFTP server.
 */
public class SftpUploader {

    /**
     * Uploads a file to the specified SFTP server.
     *
     * @param host the SFTP server host
     * @param port the SFTP server port
     * @param user the username for authentication
     * @param pwd the password for authentication
     * @param data the file data to upload
     * @param remoteName the name of the file on the remote server
     * @throws Exception if an error occurs during the upload process
     */
    public static void upload(String host, int port, String user, String pwd,
                              byte[] data, String remoteName) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, port);
        session.setPassword(pwd);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();

        // Verify if the directory exists, if not, create it
        String remoteDir = "/records";
        System.out.println("Attempting to create or access directory: " + remoteDir);
        try {
            sftp.cd(remoteDir);
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                try {
                    System.out.println("Directory does not exist. Attempting to create: " + remoteDir);
                    sftp.mkdir(remoteDir);
                    sftp.cd(remoteDir);
                } catch (SftpException mkdirException) {
                    if (mkdirException.id == ChannelSftp.SSH_FX_PERMISSION_DENIED) {
                        System.err.println("Error: Insufficient permissions to create directory " + remoteDir);
                        throw mkdirException;
                    } else {
                        throw mkdirException;
                    }
                }
            } else {
                throw e;
            }
        }

        // Upload the file
        sftp.put(new ByteArrayInputStream(data), remoteName);
        sftp.disconnect();
        session.disconnect();
    }

    /**
     * Asynchronously uploads a file to the specified SFTP server.
     *
     * @param host the SFTP server host
     * @param port the SFTP server port
     * @param user the username for authentication
     * @param pwd the password for authentication
     * @param data the file data to upload
     * @param remoteName the name of the file on the remote server
     * @return a CompletableFuture that resolves to true if the upload succeeds, or false if it fails
     */
    public static CompletableFuture<Boolean> uploadAsync(
            String host, int port, String user, String pwd,
            byte[] data, String remoteName) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                upload(host, port, user, pwd, data, remoteName);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }
}