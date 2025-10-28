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


}
