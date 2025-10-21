package com.Projeto.Sentinela.Services;

import com.Projeto.Sentinela.Entities.PasswordResetToken;
import com.Projeto.Sentinela.Entities.UserAbstract;
import com.Projeto.Sentinela.Repositories.PasswordResetTokenRepository;
import com.Projeto.Sentinela.Repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ServicoAutenticacao {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    public void solicitarRecuperarSenha(String email) throws MessagingException {
        Optional<UserAbstract> user = userRepository.findByEmail(email);

        if(user.isEmpty()){
            throw new RuntimeException("Usuário não encontrado para o email " + email);
        }

        UserAbstract userAbstract = user.get();

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new  PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUsuario(userAbstract);
        resetToken.setExpiration(LocalDateTime.now().plusMinutes(20));

        tokenRepository.save(resetToken);

        String link = "http://localhost:8080/autenticacao/redefinir?token="  + token; //Modificar link posteriormente
        enviarEmail(userAbstract.getEmail(), link);
    }

    public void enviarEmail(String destinatario, String link) {
        try{
            MimeMessage mensagem = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

        helper.setTo(destinatario);
        helper.setSubject("🔒 Redefinição de Senha - Projeto Sentinela");

        String corpoHtml = """
                <div style="font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: auto; border: 1px solid #ddd; border-radius: 8px; padding: 20px;">
                                    <h2 style="color: #111;">Recuperação de senha</h2>
                                    <p>Olá! Recebemos uma solicitação para redefinir sua senha no <strong>Projeto Sentinela</strong>.</p>
                                    <p>Clique no botão abaixo para criar uma nova senha:</p>
                                    <div style="text-align: center; margin: 30px 0;">
                                        <a href="%s" style="background-color: #007BFF; color: white; padding: 12px 20px; text-decoration: none; border-radius: 6px; font-weight: bold;">
                                            Redefinir Senha
                                        </a>
                                    </div>
                                    <p>Se você não solicitou essa alteração, pode ignorar este e-mail com segurança.</p>
                                    <p style="font-size: 12px; color: #666;">Este link expira em 15 minutos.</p>
                                    <hr>
                                    <p style="font-size: 12px; text-align: center; color: #888;">Equipe Projeto Sentinela</p>
                                </div>""".formatted(link);


        helper.setText(corpoHtml, true);
        mailSender.send(mensagem);
        } catch (MessagingException e){
            throw new RuntimeException("Erro ao enviar e-mail de recuperação de senha", e);
        }
    }

    public void redefinirSenha(String token, String novaSenha){
        PasswordResetToken resetToken = tokenRepository.findByToken(token).orElseThrow(()  -> new RuntimeException("Token inválido"));

        if(resetToken.isExpired()){
            throw new RuntimeException("Token Expirado");
        }

        UserAbstract user = resetToken.getUsuario();
        user.setSenha(novaSenha);
        userRepository.save(user);
    }

}
