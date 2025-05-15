package com.matias.physiocarepsp.utils;

import com.jcraft.jsch.*;

import java.io.ByteArrayInputStream;

public class SftpUploader {
    public static void upload(String host, int port, String user, String pwd,
                              byte[] data, String remoteName) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, port);
        session.setPassword(pwd);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();
        sftp.cd("/records");
        sftp.put(new ByteArrayInputStream(data), remoteName);
        sftp.disconnect();
        session.disconnect();
    }
}
