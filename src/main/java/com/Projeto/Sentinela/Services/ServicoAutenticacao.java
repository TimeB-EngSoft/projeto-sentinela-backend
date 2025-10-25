package com.Projeto.Sentinela.Services;

import com.Projeto.Sentinela.Entities.*;
import com.Projeto.Sentinela.Enums.EnumCargo;
import com.Projeto.Sentinela.Enums.EnumUsuarioStatus;
import com.Projeto.Sentinela.Repositories.InstituicaoRepository;
import com.Projeto.Sentinela.Repositories.PasswordResetTokenRepository;
import com.Projeto.Sentinela.Repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
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
    @Autowired
    private InstituicaoRepository instituicaoRepository;

    @Transactional
    public void solicitarRecuperarSenha(String email) {
        Optional<UserAbstract> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado para o email: " + email);
        }

        UserAbstract userAbstract = user.get();

        tokenRepository.deleteByUsuario(userAbstract);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUsuario(userAbstract);
        resetToken.setExpiration(LocalDateTime.now().plusMinutes(20));

        tokenRepository.save(resetToken);

        String link = "http://localhost:8080/autenticacao/redefinir?token=" + token;
        enviarEmail(userAbstract.getEmail(), link, token);
    }

    public void enviarEmail(String destinatario, String link, String token) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            UserAbstract user = userRepository.findUserAbstractByEmail(destinatario);
            helper.setTo(destinatario);
            helper.setSubject("🔒 Redefinição de Senha - Projeto Sentinela");

            String corpoHtml = carregarTemplateEmail(user.getNome(), link, token);
            helper.setText(corpoHtml, true);

            ClassPathResource logo = new ClassPathResource("static/images/ProjetoSentinelaLogo.png");
            if (logo.exists()) {
                helper.addInline("logoSentinela", logo);
            } else {
                System.err.println("⚠️ Logo não encontrada no caminho: static/images/ProjetoSentinelaLogo.png");
            }

            mailSender.send(mensagem);
            System.out.println("✅ E-mail de recuperação enviado com sucesso para " + destinatario);

        } catch (MessagingException e) {
            System.err.println("❌ Erro ao enviar e-mail: " + e.getMessage());
            throw new RuntimeException("Erro ao enviar e-mail de recuperação de senha", e);
        }
    }

    private String carregarTemplateEmail(String nome, String link, String token) {
        try (InputStream inputStream = getClass().getResourceAsStream("/templates/email/email-recuperacao.html")) {

            if (inputStream == null) {
                throw new RuntimeException("Template de e-mail não encontrado em /templates/email/email-recuperacao.html");
            }

            String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            template = template.replace("${userName}", nome);
            template = template.replace("${redirectUrl}", link);
            template = template.replace("${token}", token);

            System.out.println("✅ Template de e-mail carregado com sucesso!");
            return template;

        } catch (IOException e) {
            System.err.println("❌ Erro ao ler template de e-mail: " + e.getMessage());
            throw new RuntimeException("Erro ao ler template de e-mail", e);
        }
    }

    @Transactional
    public void redefinirSenha(String token, String novaSenha) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Token expirado. Solicite uma nova recuperação de senha.");
        }

        UserAbstract user = resetToken.getUsuario();
        user.setSenha(novaSenha);
        userRepository.save(user);

        tokenRepository.delete(resetToken);
        System.out.println("✅ Senha redefinida com sucesso para o usuário: " + user.getEmail());
    }

    public void cadastroParcial(String nome, String email, String instituicao, String cargo, String justificativa) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado.");
        }

        UserAbstract usuario = null;
        switch (cargo){ // dar uma olhada depois no nome dos cases, por questao de estetica!
            case "Gestor Secretaria": usuario = new GestorSecretaria();
            break;
            case "Gestor Instituicao": usuario = new GestorInstituicao();
            break;
            case "Usuario Secretaria": usuario = new UsuarioSecretaria();
            break;
            case "Usuario Instituicao": usuario = new UsuarioInstituicao();
            break;
            default: break;

        }

        if (usuario == null) {
            throw new IllegalArgumentException("O usuário precisa existir para ser cadastrado");
        }
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setInstituicao(instituicaoRepository.findByNomeContainingIgnoreCase(instituicao));
        usuario.setCargo(EnumCargo.valueOf(
                cargo.toUpperCase()
                        .replace(" DA ", " ") // remove o “DA” antes de virar underscore
                        .replace(" DE ", " ") // opcional, cobre casos tipo “Gestor de Secretaria”
                        .replace(" ", "_")
        ));

        usuario.setJustificativa(justificativa);
        usuario.setStatus(EnumUsuarioStatus.PENDENTE);
        usuario.setDataCadastro(LocalDateTime.now());

        userRepository.save(usuario);
    }

    public void cadastroCompleto(String email, String senha, String telefone, String dataNascimento, String cpf) {
        UserAbstract user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (user.getStatus() != EnumUsuarioStatus.ATIVO) {  // ao gestor aceitar o usuario, o status dele passa automaticamente para ATIVO (lembrar de implementar no futuro)
            throw new RuntimeException("O cadastro ainda não foi aprovado.");
        }

        user.setSenha(senha);
        user.setTelefone(telefone);
        user.setDataNascimento(LocalDate.parse(dataNascimento));
        user.setDataAtualizacao(LocalDateTime.now());
        user.setCpf(cpf);

        userRepository.save(user);
    }

    public UserAbstract login(String email, String senha) {
        UserAbstract usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (usuario.getStatus() != EnumUsuarioStatus.ATIVO) {
            throw new RuntimeException("O cadastro ainda não foi aprovado ou está inativo.");
        }

        if (!usuario.getSenha().equals(senha)) {
            throw new RuntimeException("Senha incorreta.");
        }

        userRepository.save(usuario);

        return usuario;
    }

    public void logout(String email) {
        Optional<UserAbstract> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            UserAbstract user = userOpt.get();
            userRepository.save(user);
            System.out.println("✅ Logout registrado para: " + email);
        } else {
            System.out.println("⚠️ Logout solicitado para e-mail não encontrado: " + email);
        }
    }

    public boolean validarToken(String token) {
        // Busca o token no repositório específico de tokens.
        Optional<PasswordResetToken> resetTokenOpt = tokenRepository.findByToken(token);

        // Se o token não for encontrado, ele é inválido.
        if (resetTokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = resetTokenOpt.get();

        // Verifica se o token expirou. Se sim, é inválido.
        if (resetToken.isExpired()) {
            return false;
        }

        // Se encontrou e não expirou, o token é válido.
        return true;
    }

}
