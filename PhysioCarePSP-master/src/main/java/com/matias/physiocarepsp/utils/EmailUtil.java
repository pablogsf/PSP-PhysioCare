package com.matias.physiocarepsp.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Properties;

public class EmailUtil {
    private final Session session;
    private static final NetHttpTransport HTTP_TRANSPORT =
            new com.google.api.client.http.javanet.NetHttpTransport();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/client_secret_174567140746-khnp8cb6mcq2olc7vn4iepa5pan7ejhq.apps.googleusercontent.com.json";
    private static final String APPLICATION_NAME = "PhysioCarePSP";
    private static final JsonFactory JSON_FACTORY =
            GsonFactory.getDefaultInstance();

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

    /**
     * Create a message from an email.
     */
    public static com.google.api.services.gmail.model.Message createMessageWithEmail(
            MimeMessage email) throws MessagingException, java.io.IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = java.util.Base64.getUrlEncoder()
                .encodeToString(bytes);
        com.google.api.services.gmail.model.Message message = new com.google.api.services.gmail.model.Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     * Send an email from the user's mailbox to its recipient.
     */
    public static void sendMessage(
            Gmail service, String userId, MimeMessage emailContent)
            throws MessagingException, java.io.IOException {
        com.google.api.services.gmail.model.Message message = createMessageWithEmail(emailContent);
        message = service.users().messages()
                .send(userId, message).execute();
        System.out.println("Message id: " + message.getId());
        System.out.println("Email sent successfully.");
    }


    public static MimeMessage createEmailWithAttachment(String to,
                                                        String from,
                                                        String subject,
                                                        String bodyText,
                                                        String fileDir)
            throws MessagingException, IOException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);

        // Crear el cuerpo del correo
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(bodyText, "utf-8");

        // Adjuntar archivo
        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.setDataHandler(new jakarta.activation.DataHandler(
                new jakarta.activation.FileDataSource(fileDir)));
        attachmentPart.setFileName(new java.io.File(fileDir).getName());

        // Crear estructura MIME
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);

        email.setContent(multipart, "multipart/mixed");

        return email;
    }

    /**
     * Create Credential object.
     */
    public static Credential getCredentials(
            final NetHttpTransport HTTP_TRANSPORT) throws Exception {
        // Load client secrets.
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY,
                        new InputStreamReader(
                                new FileInputStream(CREDENTIALS_FILE_PATH)));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
                        Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM))
                        .setDataStoreFactory(new FileDataStoreFactory(
                                new java.io.File(TOKENS_DIRECTORY_PATH)))
                        .setAccessType("offline")
                        .build();
        LocalServerReceiver receiver =
                new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver)
                .authorize("user");
    }

    public static Gmail getService() throws Exception {
        Gmail service =
                new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                        getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();
        return service;
    }
}
