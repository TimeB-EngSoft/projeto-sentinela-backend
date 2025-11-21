package com.Projeto.Sentinela.Controllers;

import com.Projeto.Sentinela.Model.DTOs.ConflitoDTO;
import com.Projeto.Sentinela.Model.Entities.Conflito;
import com.Projeto.Sentinela.Model.Entities.UserAbstract;
import com.Projeto.Sentinela.Services.ServicoConflito;
import com.Projeto.Sentinela.Services.ServicoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"}, allowedHeaders = "*")
@RestController
@RequestMapping("/conflito")
public class ControladorConflito {

    @Autowired
    private ServicoConflito servicoConflito;

    @PostMapping("/cadastroDireto")
    public ResponseEntity<?> cadastroConflitoDiretamente(@RequestBody ConflitoDTO dto) {
        try {
            Conflito conflito = servicoConflito.cadastarConflitoDiretamente(dto);
            return new ResponseEntity<>(conflito, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Conflito já cadastrado")) {
                return ResponseEntity.unprocessableEntity().body(e.getMessage());
            } else {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
    }

    @GetMapping("/listarConflitos")
    public ResponseEntity<?> listarConflitos() {
        try {
            List<Conflito> conflitos = servicoConflito.listarConflitos();
            return ResponseEntity.ok(conflitos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            Conflito conflito = servicoConflito.buscarPorId(id);
            return ResponseEntity.ok(conflito);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarConflito(@PathVariable Long id, @RequestBody ConflitoDTO dto) {
        try {
            Conflito atualizado = servicoConflito.atualizarConflito(id, dto);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * endpoint responsável por filtrar os conflitos pelos seus enums
     * mapeado para receber requisições GET
     *
     * @param id corresponde ao ID do usuário que deseja filtrar.
     * @param filter corresponde à lista de filtros que se deseja impor;
     * @return retorna uma requisição HTTP com os DTOs correspondetes a todos os conflitos congruentes aos filtros
     * o uma mensagem de erro
     *
     * */
    @GetMapping("/{id}/filtrar")
    public ResponseEntity<?> filtrarConflitos(@PathVariable Long id, @RequestParam List<String> filter) {
        try{

            List<?> lista = servicoConflito.filtroConflitos(id, filter);
            return ResponseEntity.ok().body(lista);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
