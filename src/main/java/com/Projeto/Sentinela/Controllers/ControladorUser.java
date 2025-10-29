package com.Projeto.Sentinela.Controllers;

import com.Projeto.Sentinela.DTOs.UpUserDTO;
import com.Projeto.Sentinela.Entities.UserAbstract;
import java.util.HashMap;
import java.util.Map;

import com.Projeto.Sentinela.Services.ServicoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class ControladorUser {

    @Autowired
    private ServicoUser servicoUser;


    @PatchMapping("/{id}/atualizar")
    public ResponseEntity<UserAbstract> atualizarUsuario(
            @PathVariable Long id,
            @RequestBody UpUserDTO dto) {
        try {
            UserAbstract atualizado = servicoUser.atualizarUser(id, dto);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}/informacoes")
    public ResponseEntity<?> getData(@PathVariable long id){
        try{
            UserAbstract a = servicoUser.getData(id);
            return ResponseEntity.ok(a);
        }catch(RuntimeException e){
            // Para "usuário não encontrado", retorne 404
            if(e.getMessage().contains("Usuário não encontrado")){
                return ResponseEntity.notFound().build();
            }
            // Para outros erros, retorne 400
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/cadastrar-parcial")
    public ResponseEntity<String> cadastrarParcial(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String instituicao,
            @RequestParam String cargo,
            @RequestParam String justificativa) {
        try {
            servicoUser.cadastroParcial(nome, email, instituicao, cargo, justificativa);
            return ResponseEntity.ok("Solicitação enviada para análise.");
        } catch (Exception e) {
            if(e.getMessage().equals("E-mail já cadastrado.")){
                return ResponseEntity.notFound().build();
            }
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
            servicoUser.cadastroCompleto(email, senha, telefone, dataNascimento, cpf);
            return ResponseEntity.ok("Cadastro completo realizado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao completar cadastro: " + e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> efetuarLogin(@RequestParam String email, @RequestParam String senha) {
        try {
            UserAbstract usuario = servicoUser.login(email, senha);

            String mensagem = String.format(
                    "Login efetuado com sucesso! Bem-vindo, %s (%s).",
                    usuario.getNome(),
                    usuario.getCargo()
            );

            return ResponseEntity.ok(mensagem);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erro ao efetuar login: " + e.getMessage());
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam(required = false) String email) {
        if (email != null) {
            servicoUser.logout(email);
        }
        return ResponseEntity.ok("Logout efetuado com sucesso!");
    }

    @PostMapping("/validar-token")
    public ResponseEntity<String> validarToken(@RequestParam String token) {
        try {
            boolean isTokenValido = servicoUser.validarToken(token);

            if (isTokenValido) {
                return ResponseEntity.ok("Token é válido.");
            } else {
                return ResponseEntity.badRequest().body("Token inválido ou expirado.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao validar token: " + e.getMessage());
        }
    }

    @PostMapping("/recuperar")
    public ResponseEntity<String> recuperarSenha(@RequestParam String email) {
        try {
            servicoUser.solicitarRecuperarSenha(email);
            return ResponseEntity.ok("E-mail de recuperação enviado (se o e-mail existir no sistema).");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao solicitar recuperação: " + e.getMessage());
        }
    }

    @PostMapping("/redefinir")
    public ResponseEntity<String> redefinirSenha(@RequestParam String token, @RequestParam String novaSenha) {
        try {
            servicoUser.redefinirSenha(token, novaSenha);
            return ResponseEntity.ok("Senha redefinida com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao redefinir senha: " + e.getMessage());
        }
    }

}
