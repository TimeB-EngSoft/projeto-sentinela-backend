package com.Projeto.Sentinela.Controllers;

import com.Projeto.Sentinela.DTOs.DenunciaDTO;
import com.Projeto.Sentinela.Entities.Denuncia;
import com.Projeto.Sentinela.Services.ServicoDenuncias;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/denuncias")
public class ControladorDenuncias {

    @Autowired
    private ServicoDenuncias servicoDenuncia;

    @PatchMapping("/registrarexterna")
    public ResponseEntity<?> registrarDenuncia(@RequestBody DenunciaDTO dto) {
        try {
            Denuncia denuncia = servicoDenuncia.registrarDenunciaExterna(dto);
            return new ResponseEntity<>(denuncia, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erro ao registrar denúncia: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarDenuncia(@PathVariable Long id,
                                               @RequestBody DenunciaDTO dto) {

        try{
            Denuncia atualizado = servicoDenuncia.atualizarDenuncia(id, dto);
            return ResponseEntity.ok(atualizado);
        }catch (RuntimeException e) {
            if(e.getMessage().contains("Denúncia não presente no sistema")){
                return ResponseEntity.notFound().build();
            }
            // Para outros erros, retorne 400
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


}
