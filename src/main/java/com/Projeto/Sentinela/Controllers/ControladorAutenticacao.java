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
    private ServicoAutenticacao emailService;

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
            emailService.solicitarRecuperarSenha(email);
            return ResponseEntity.ok("E-mail de recuperação enviado (se o e-mail existir no sistema).");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao solicitar recuperação: " + e.getMessage());
        }
    }

    @PostMapping("/redefinir")
    public ResponseEntity<String> redefinirSenha(@RequestParam String token, @RequestParam String novaSenha) {
        try {
            emailService.redefinirSenha(token, novaSenha);
            return ResponseEntity.ok("Senha redefinida com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao redefinir senha: " + e.getMessage());
        }
    }

}
