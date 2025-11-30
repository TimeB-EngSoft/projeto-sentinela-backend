package com.Projeto.Sentinela.Services;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Properties;

@Service
public class GmailEmailService {
    @Value("${gmail.client.id}")
    private String clientId;
    @Value("${gmail.client.secret}")
    private String clientSecret;
    @Value("${gmail.refresh.token}")
    private String refreshToken;
    @Value("${gmail.user}")
    private String user;

    private Gmail buildGmailService() throws GeneralSecurityException, IOException {
        GoogleClientSecrets secrets = new GoogleClientSecrets()
                .setInstalled(new GoogleClientSecrets.Details()
                        .setClientId(clientId)
                        .setClientSecret(clientSecret));

        Credential credential = new GoogleCredential.Builder()
                .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(JacksonFactory.getDefaultInstance())
                .setClientSecrets(secrets)
                .build()
                .setRefreshToken(refreshToken);

        credential.refreshToken();

        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("Projeto Sentinela")
                .build();
    }

    /**
     * Envia e-mail com HTML e imagem da logo embutida (Inline).
     */
    public void enviarEmail(String destinatario, String assunto, String corpoHtml) {
        try {
            Gmail service = buildGmailService();

            Properties props = new Properties();
            Session session = Session.getInstance(props);
            MimeMessage email = new MimeMessage(session);

            email.setFrom(new InternetAddress(user, "Projeto Sentinela"));
            email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(destinatario));
            email.setSubject(assunto, "UTF-8");

            // Cria a parte Multipart "related" para suportar imagens inline
            Multipart multipart = new MimeMultipart("related");

            // 1. Parte do HTML
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(corpoHtml, "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);

            // 2. Parte da Imagem (Logo)
            // Busca a imagem em src/main/resources/static/images/ProjetoSentinelaLogo.png
            try {
                ClassPathResource imageResource = new ClassPathResource("static/images/ProjetoSentinelaLogo.png");
                File imageFile = imageResource.getFile();

                MimeBodyPart imagePart = new MimeBodyPart();
                DataSource fds = new FileDataSource(imageFile);
                imagePart.setDataHandler(new DataHandler(fds));
                imagePart.setHeader("Content-ID", "<logoSentinela>"); // O ID usado no HTML: cid:logoSentinela
                imagePart.setDisposition(MimeBodyPart.INLINE);
                multipart.addBodyPart(imagePart);
            } catch (Exception e) {
                System.err.println("⚠️ Aviso: Logo não encontrada ou erro ao anexar. O e-mail será enviado sem logo. " + e.getMessage());
            }

            email.setContent(multipart);

            // Codifica e envia
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);
            String encoded = Base64.getUrlEncoder().encodeToString(buffer.toByteArray());

            Message message = new Message();
            message.setRaw(encoded);

            service.users().messages().send(user, message).execute();
            System.out.println("✅ E-mail enviado com sucesso para: " + destinatario);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao enviar e-mail via Gmail API: " + e.getMessage(), e);
        }
    }
}