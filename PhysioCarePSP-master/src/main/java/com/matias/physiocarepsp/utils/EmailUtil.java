package com.matias.physiocarepsp.utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.util.Properties;

public class EmailUtil {
    private final Session session;

    public EmailUtil(String host, int port, String user, String pass) {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));
        props.put("mail.smtp.auth", "true");
        session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });
    }

    public void send(String to, String subject, String text, byte[] attachment, String name) throws Exception {
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("noreply@fisiocare.com"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject);

        MimeBodyPart txt = new MimeBodyPart();
        txt.setText(text);

        MimeBodyPart file = new MimeBodyPart();
        file.setFileName(name);
        file.setContent(attachment, "application/pdf");

        Multipart mp = new MimeMultipart();
        mp.addBodyPart(txt);
        mp.addBodyPart(file);
        msg.setContent(mp);
        Transport.send(msg);
    }
}
