package com.Projeto.Sentinela.Controllers;

import com.Projeto.Sentinela.Model.DTOs.FiltroRelatorioDTO;
import com.Projeto.Sentinela.Services.ServicoRelatorios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"}, allowedHeaders = "*")
@RestController
@RequestMapping("/relatorios")
public class ControladorRelatorios {

    @Autowired
    private ServicoRelatorios servicoRelatorios;

    @PostMapping("/gerar")
    public ResponseEntity<byte[]> gerarRelatorio(@RequestBody FiltroRelatorioDTO filtro) {
        byte[] dados = servicoRelatorios.gerarRelatorioCsv(filtro);

        String filename = "relatorio_sentinela_" + System.currentTimeMillis() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(dados);
    }
}

