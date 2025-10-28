package com.Projeto.Sentinela.Controllers;


import com.Projeto.Sentinela.DTOs.UpUserDTO;
import com.Projeto.Sentinela.Entities.UserAbstract;
import com.Projeto.Sentinela.Services.ServicoEdicao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/edit")
public class ControladorEdicao {

    @Autowired
    private ServicoEdicao servicoEdicao;

    @PatchMapping("/{id}/user")
    public ResponseEntity<UserAbstract> atualizarUsuario(
            @PathVariable Long id,
            @RequestBody UpUserDTO dto) {
        try {
            UserAbstract atualizado = servicoEdicao.atualizarUser(id, dto);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}/user")
    public ResponseEntity<UserAbstract> getData(@PathVariable long id ){
        try{
            UserAbstract a = servicoEdicao.getData(id);
            return ResponseEntity.ok(a);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(null);
        }
    }

}
