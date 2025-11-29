package com.Projeto.Sentinela.Controllers;

import com.Projeto.Sentinela.Model.Entities.LogAuditoria;
import com.Projeto.Sentinela.Services.ServicoAuditoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"}, allowedHeaders = "*")
@RestController
@RequestMapping("/auditoria")
public class ControladorAuditoria {

    @Autowired
    private ServicoAuditoria servicoAuditoria;

    @GetMapping("/logs")
    public ResponseEntity<List<LogAuditoria>> listarLogs() {
        return ResponseEntity.ok(servicoAuditoria.listarLogs());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> obterEstatisticas() {
        return ResponseEntity.ok(servicoAuditoria.obterEstatisticas());
    }

    // Endpoint para o front registrar logs (ex: tentativa de login falha no front, erros de JS, etc)
    // Normalmente o log Ã© gerado internamente no backend, mas isso ajuda em logs de interface
    @PostMapping("/registrar")
    public ResponseEntity<Void> registrarLogExterno(@RequestBody LogAuditoria log) {
        servicoAuditoria.registrarLog(
                log.getUsuario(), log.getAcao(), log.getModulo(),
                log.getDetalhes(), log.getNivel(), log.getIp()
        );
        return ResponseEntity.ok().build();
    }
}