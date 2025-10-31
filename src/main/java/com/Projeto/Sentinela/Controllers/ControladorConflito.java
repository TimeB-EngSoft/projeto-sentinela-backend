package com.Projeto.Sentinela.Controllers;

import com.Projeto.Sentinela.Model.DTOs.ConflitoDTO;
import com.Projeto.Sentinela.Model.Entities.Conflito;
import com.Projeto.Sentinela.Services.ServicoConflito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conflito")
public class ControladorConflito {

    @Autowired
    private ServicoConflito servicoConflito;

    @PostMapping("cadastroDireto")
    public ResponseEntity<?> cadastroConflitoDiretamente(@RequestBody ConflitoDTO dto){

        try{
            Conflito conflito = servicoConflito.cadastarConflitoDiretamente(dto);
            return new ResponseEntity<>(conflito, HttpStatus.CREATED);
        }catch(RuntimeException e){
            if(e.getMessage().equals("Conflito já cadastrado")){
                return ResponseEntity.unprocessableEntity().build();
            }else{
                return ResponseEntity.badRequest().build();
            }
        }

    }

}
