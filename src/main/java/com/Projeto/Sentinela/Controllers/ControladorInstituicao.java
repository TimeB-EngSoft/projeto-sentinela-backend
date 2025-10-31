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

    @GetMapping("/{id}/listarUsers")
    public ResponseEntity<?> listarUsuarios(@PathVariable Long id){

        try{
            List<?> a =servicoInstituicao.listarUsuarios(id);
            return ResponseEntity.ok(a);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}