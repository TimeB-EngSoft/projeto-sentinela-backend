package com.Projeto.Sentinela.Services;

import com.Projeto.Sentinela.Model.DTOs.UpUserDTO;
import com.Projeto.Sentinela.Model.Entities.*;
import com.Projeto.Sentinela.Model.Enums.EnumCargo;
import com.Projeto.Sentinela.Model.Enums.EnumUsuarioStatus;
import com.Projeto.Sentinela.Model.Repositories.InstituicaoRepository;
import com.Projeto.Sentinela.Model.Repositories.PasswordResetTokenRepository;
import com.Projeto.Sentinela.Model.Repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Example;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ServicoUser {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private InstituicaoRepository instituicaoRepository;

    /*
    * permite que sejam passados parâmetros na forma de string, case-insensitive, para qualquer um dos enuns de user
    * */
    public  Enum<?> enumConverter(String tipo){
        try{
            EnumCargo c = EnumCargo.valueOf(tipo.toUpperCase());
            return c;
        }catch(IllegalArgumentException e){
            try{
                EnumUsuarioStatus s = EnumUsuarioStatus.valueOf(tipo.toUpperCase());
                return s;
            }catch(IllegalArgumentException ex){
                throw new RuntimeException("Tipo passado não corresponde aos existentes");
            }
        }
    }

    public List<UpUserDTO> listUserByStatus(String status){

        if(enumConverter(status) instanceof EnumUsuarioStatus){
            EnumUsuarioStatus eu = (EnumUsuarioStatus) enumConverter(status);
            UserAbstract u = new UsuarioInstituicao();
            u.setStatus(eu);
            UserAbstract g = new GestorInstituicao();
            g.setStatus(eu);
            UserAbstract gs = new GestorSecretaria();
            gs.setStatus(eu);
            UserAbstract us = new UsuarioSecretaria();
            us.setStatus(eu);


            Example<UserAbstract> ex1 = Example.of(u);
            Example<UserAbstract> ex2 = Example.of(g);
            Example<UserAbstract> ex3 = Example.of(gs);
            Example<UserAbstract> ex4 = Example.of(us);
            List<UserAbstract> list = userRepository.findAll(ex1);
            list.addAll(userRepository.findAll(ex2));
            list.addAll(userRepository.findAll(ex3));
            list.addAll(userRepository.findAll(ex4));
            if(list.isEmpty()){
                throw new RuntimeException("Não há usuários com este status");
            }

            List<UpUserDTO> listDTO = list.stream().map(user -> new UpUserDTO(
                    user.getNome(),
                    user.getEmail(),
                    user.getTelefone(),
                    Optional.ofNullable(user.getDataNascimento())
                            .map(LocalDate::toString)
                            .orElse(null),
                    user.getCpf(),
                    user.getCargo(),
                    user.getStatus(),
                    Optional.ofNullable(user.getInstituicao())
                            .map(Instituicao::getNome)
                            .orElse(null)
            )).toList();
            return listDTO;
        }else{
            throw new RuntimeException("Argumento inválido");
        }

    }

    @Transactional
    public UserAbstract atualizarUser(long idUser, UpUserDTO dto) {

        UserAbstract usuario = userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: id=" + idUser));

        // Atualiza email (verifica duplicidade)
        if (StringUtils.hasText(dto.getEmail()) && !dto.getEmail().equalsIgnoreCase(usuario.getEmail())) {
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new RuntimeException("E-mail já em uso por outro usuário.");
            }
            usuario.setEmail(dto.getEmail());
        }

        // Atualiza nome
        if (StringUtils.hasText(dto.getNome())) {
            usuario.setNome(dto.getNome());
        }

        // Atualiza telefone
        if (StringUtils.hasText(dto.getTelefone())) {
            usuario.setTelefone(dto.getTelefone());
        }

        // Atualiza CPF
        if (StringUtils.hasText(dto.getCpf())) {
            usuario.setCpf(dto.getCpf());
        }

        // Atualiza data de nascimento
        if (dto.getDataNascimento() != null) {
            usuario.setDataNascimento(LocalDate.parse(dto.getDataNascimento()));
        }

        // Atualiza status
        if (dto.getStatus() != null) {
            usuario.setStatus(dto.getStatus());
        }

        // Atualiza cargo
        if (dto.getCargo() != null) {
            usuario.setCargo(dto.getCargo());
        }

        // Atualiza instituição (por nome)
        if (StringUtils.hasText(dto.getInstituicaoNome())) {
            Instituicao inst = instituicaoRepository.findByNomeContainingIgnoreCase(dto.getInstituicaoNome());
            if (inst == null) {
                throw new RuntimeException("Instituição não encontrada: " + dto.getInstituicaoNome());
            }
            usuario.setInstituicao(inst);
        }

        // Atualiza a data de modificação
        usuario.setDataAtualizacao(LocalDateTime.now());

        return userRepository.save(usuario);
    }

    public UserAbstract getData (long id){
        UserAbstract a = userRepository.findById(id).orElseThrow(()-> new RuntimeException("Usuário não encontrado"));
        return a;
    }


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

        String link = "http://projeto-sentinela-frontend.s3-website-sa-east-1.amazonaws.com/user/redefinir?token=" + token;
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
        System.out.println("🔍 Procurando instituição com nome: " + instituicao);


        Instituicao instituicaoEncontrada = instituicaoRepository.findByNomeContainingIgnoreCase(instituicao);

        if (instituicaoEncontrada == null) {
            throw new RuntimeException("Instituição não encontrada: " + instituicao);
        }

        usuario.setInstituicao(instituicaoEncontrada);
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

        if (user.getStatus() != EnumUsuarioStatus.PENDENTE) {
            throw new RuntimeException("Usuário ainda não foi aprovado pelo gestor.");
        }

        user.setSenha(senha);
        user.setTelefone(telefone);
        user.setDataNascimento(LocalDate.parse(dataNascimento));
        user.setCpf(cpf);
        user.setStatus(EnumUsuarioStatus.ATIVO);
        user.setDataAtualizacao(LocalDateTime.now());

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
	
	public void atualizarSenha(Long id, String senhaAtual, String novaSenha) {
    UserAbstract user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    if (!user.getSenha().equals(senhaAtual)) {
        throw new RuntimeException("Senha atual incorreta");
    }

    user.setSenha(novaSenha);
    user.setDataAtualizacao(LocalDateTime.now());
    userRepository.save(user);
	}

    @Transactional
    public void aprovarOuRecusarCadastro(Long id, boolean aprovado) {
        UserAbstract user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (user.getStatus() != EnumUsuarioStatus.PENDENTE) {
            throw new RuntimeException("Usuário já foi analisado anteriormente.");
        }

        if (!aprovado) {
            user.setStatus(EnumUsuarioStatus.INATIVO);
            user.setDataAtualizacao(LocalDateTime.now());
            userRepository.save(user);
            return;
        }

        // Caso aprovado: gera token e envia e-mail
        user.setStatus(EnumUsuarioStatus.PENDENTE);
        user.setDataAtualizacao(LocalDateTime.now());
        userRepository.save(user);

        String token = UUID.randomUUID().toString();

        PasswordResetToken confirmToken = new PasswordResetToken();
        confirmToken.setToken(token);
        confirmToken.setUsuario(user);
        confirmToken.setExpiration(LocalDateTime.now().plusDays(2)); // expira em 48h
        tokenRepository.save(confirmToken);

        String link = "http://projeto-sentinela-frontend.s3-website-sa-east-1.amazonaws.com/user/completarcadastro?token=" + token;

        enviarEmailAprovacao(user.getEmail(), user.getNome(), link);
    }

    private void enviarEmailAprovacao(String destinatario, String nome, String link) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject("✅ Cadastro Aprovado - Projeto Sentinela");

            // 🔹 Carrega o HTML do template
            String corpoHtml;
            try (InputStream inputStream = getClass().getResourceAsStream("/templates/email/email-cadastro.html")) {
                if (inputStream == null) {
                    throw new RuntimeException("Template de e-mail não encontrado em /templates/email/email-cadastro.html");
                }

                corpoHtml = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                corpoHtml = corpoHtml.replace("${userName}", nome);
                corpoHtml = corpoHtml.replace("${redirectUrl}", link);
            }

            helper.setText(corpoHtml, true);

            // 🔹 Adiciona logo inline
            ClassPathResource logo = new ClassPathResource("static/images/ProjetoSentinelaLogo.png");
            if (logo.exists()) {
                helper.addInline("logoSentinela", logo);
            } else {
                System.err.println("⚠️ Logo não encontrada no caminho: static/images/ProjetoSentinelaLogo.png");
            }

            mailSender.send(mensagem);
            System.out.println("✅ E-mail de aprovação enviado para " + destinatario);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar e-mail de aprovação: " + e.getMessage());
        }
    }


}
