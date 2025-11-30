package com.Projeto.Sentinela.Controllers;

import com.Projeto.Sentinela.Model.DTOs.InstituicaoDTO;
import com.Projeto.Sentinela.Model.DTOs.UpInstituicaoDTO;
import com.Projeto.Sentinela.Model.Entities.Instituicao;
import com.Projeto.Sentinela.Services.ServicoInstituicao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gerenciar as requisições relacionadas a instituições.
 */
 @CrossOrigin(origins = {
    "http://127.0.0.1:5500", 
    "http://localhost:5500", 
    "https://projeto-sentinela-frontend.vercel.app" 
}, allowedHeaders = "*")
@RestController
@RequestMapping("/instituicoes")
public class ControladorInstituicao {

    @Autowired
    private ServicoInstituicao servicoInstituicao;

    /**
     * Endpoint para cadastrar uma nova instituição.
     * Mapeado para receber requisições POST em /instituicoes/cadastrar.
     *
     * @param instituicaoDTO Os dados da instituição enviados no corpo da requisição.
     * @return Uma resposta HTTP com a instituição criada e o status 201 (CREATED)
     * ou uma mensagem de erro com o status 400 (BAD REQUEST).
     */
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarInstituicao(@RequestBody InstituicaoDTO instituicaoDTO) {
        try {
            Instituicao instituicaoSalva = servicoInstituicao.cadastrarInstituicao(instituicaoDTO);
            return new ResponseEntity<>(instituicaoSalva, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Captura exceções da camada de serviço (ex: nome duplicado) e retorna um erro claro.
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    /**
     * Endpoint para atualizar os dados de uma instituição existente.
     * Mapeado para receber requisições PATCH em /instituicoes/{id}.
     *
     * @param id O ID da instituição a ser editada, vindo da URL.
     * @param instituicaoDTO Os dados a serem alterados, vindos do corpo da requisição.
     * @return Uma resposta HTTP com a instituição atualizada ou uma mensagem de erro.
     */
    @PatchMapping("/{id}/atualizar")
    public ResponseEntity<?> atualizarInstituicao(
            @PathVariable Long id,
            @RequestBody UpInstituicaoDTO instituicaoDTO) {
        try {
            Instituicao instituicaoAtualizada = servicoInstituicao.atualizarInstituicao(id, instituicaoDTO);
            return ResponseEntity.ok(instituicaoAtualizada);
        } catch (RuntimeException e) {
            // Captura exceções (ex: instituição não encontrada) e retorna um erro claro.
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     *
     * @param id id da instituição que se deseja buscar.
     * @param tipo tipo de usuário que se deseja buscar. Este pode assumir o valor "all" que retorna todos os tipos de usuário
     * @return uma resposta HTTP com uma list de usuários de uma instituição
     * */

    @GetMapping("/{id}/listUsers")
    public ResponseEntity<?> listarUsers(@PathVariable Long id, @RequestParam String tipo){

        try{
            List<?> a =servicoInstituicao.listarUsuarios(id,tipo);
            return ResponseEntity.ok(a);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarInstituicoes() {
        try {
            var instituicoes = servicoInstituicao.listarTodasInstituicoes();

            if (instituicoes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("Nenhuma instituição cadastrada.");
            }

            return ResponseEntity.ok(instituicoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao listar instituições: " + e.getMessage());
        }
    }

}