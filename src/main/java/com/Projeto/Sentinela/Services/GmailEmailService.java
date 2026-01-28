package com.Projeto.Sentinela.Services;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Properties;

@Service
@ConditionalOnProperty(name = "email.enabled", havingValue = "true")
public class GmailEmailService {

    @Value("${gmail.client.id}")
    private String clientId;

    @Value("${gmail.client.secret}")
    private String clientSecret;

    @Value("${gmail.refresh.token}")
    private String refreshToken;

    @Value("${gmail.user}")
    private String user;

    private Gmail buildGmailService() throws Exception {

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(JacksonFactory.getDefaultInstance())
                .setClientSecrets(clientId, clientSecret)
                .build();

        credential.setRefreshToken(refreshToken);
        credential.refreshToken();

        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        )
                .setApplicationName("Projeto Sentinela")
                .build();
    }

    public void enviarEmail(String destinatario, String assunto, String corpoHtml) {

        try {
            Gmail service = buildGmailService();

            Properties props = new Properties();
            Session session = Session.getInstance(props);

            MimeMessage email = new MimeMessage(session);
            email.setFrom(new InternetAddress(user, "Projeto Sentinela"));
            email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(destinatario));
            email.setSubject(assunto, "UTF-8");

            Multipart multipart = new MimeMultipart("related");

            // HTML
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(corpoHtml, "text/html; charset=UTF-8");
            multipart.addBodyPart(htmlPart);

            // LOGO INLINE (SEM QUEBRAR NO HEROKU)
            try {
                ClassPathResource resource = new ClassPathResource("static/images/ProjetoSentinelaLogo.png");

                try (InputStream is = resource.getInputStream()) {
                    byte[] imageBytes = is.readAllBytes();
                    DataSource ds = new ByteArrayDataSource(imageBytes, "image/png");

                    MimeBodyPart imagePart = new MimeBodyPart();
                    imagePart.setDataHandler(new DataHandler(ds));
                    imagePart.setHeader("Content-ID", "<logoSentinela>");
                    imagePart.setDisposition(MimeBodyPart.INLINE);

                    multipart.addBodyPart(imagePart);
                }

            } catch (Exception e) {
                System.out.println("⚠️ Logo não encontrada. Enviando email sem imagem.");
            }

            email.setContent(multipart);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);

            String encodedEmail = Base64.getUrlEncoder()
                    .encodeToString(buffer.toByteArray());

            Message message = new Message();
            message.setRaw(encodedEmail);

            service.users().messages().send(user, message).execute();

            System.out.println("✅ Email enviado para " + destinatario);

        } catch (Exception e) {
            e.printStackTrace();
            // NÃO deixa o erro derrubar a aplicação
            System.err.println("❌ Falha ao enviar email: " + e.getMessage());
        }
    }
}
