package com.Projeto.Sentinela.Controllers;

import com.Projeto.Sentinela.Services.ServicoAutenticacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class ControladorAutenticacao {

    @Autowired
    private ServicoAutenticacao servicoAutenticacao;


    @PostMapping("/cadastrar-parcial")
    public ResponseEntity<String> cadastrarParcial(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String instituicao,
            @RequestParam String cargo,
            @RequestParam String justificativa) {
        try {
            servicoAutenticacao.cadastroParcial(nome, email, instituicao, cargo, justificativa);
            return ResponseEntity.ok("Solicitação enviada para análise.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao solicitar cadastro: " + e.getMessage());
        }
    }

    @PostMapping("/cadastrar-completo")
    public ResponseEntity<String> cadastrarCompleto(
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String telefone,
            @RequestParam String dataNascimento,
            @RequestParam String cpf)
    {
        try {
            servicoAutenticacao.cadastroCompleto(email, senha, telefone, dataNascimento, cpf);
            return ResponseEntity.ok("Cadastro completo realizado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao completar cadastro: " + e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<String> efetuarLogin(@RequestParam String email, @RequestParam String senha) {
        return ResponseEntity.ok("Login efetuado para o e-mail: " + email);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout efetuado com sucesso");
    }

    @PostMapping("/recuperar")
    public ResponseEntity<String> recuperarSenha(@RequestParam String email) {
        try {
            servicoAutenticacao.solicitarRecuperarSenha(email);
            return ResponseEntity.ok("E-mail de recuperação enviado (se o e-mail existir no sistema).");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao solicitar recuperação: " + e.getMessage());
        }
    }

    @PostMapping("/redefinir")
    public ResponseEntity<String> redefinirSenha(@RequestParam String token, @RequestParam String novaSenha) {
        try {
            servicoAutenticacao.redefinirSenha(token, novaSenha);
            return ResponseEntity.ok("Senha redefinida com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao redefinir senha: " + e.getMessage());
        }
    }

}
