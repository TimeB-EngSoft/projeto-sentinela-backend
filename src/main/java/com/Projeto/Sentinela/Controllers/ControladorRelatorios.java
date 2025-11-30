package com.Projeto.Sentinela.Controllers;

import com.Projeto.Sentinela.Model.DTOs.FiltroRelatorioDTO;
import com.Projeto.Sentinela.Services.ServicoRelatorios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {
    "http://127.0.0.1:5500", 
    "http://localhost:5500", 
    "https://projeto-sentinela-frontend.vercel.app" 
}, allowedHeaders = "*")
@RestController
@RequestMapping("/relatorios")
public class ControladorRelatorios {

    @Autowired
    private ServicoRelatorios servicoRelatorios;

    @PostMapping("/gerar")
    public ResponseEntity<byte[]> gerarRelatorio(@RequestBody FiltroRelatorioDTO filtro) {

        // Captura o e-mail enviado pelo front-end no DTO
        String usuario = filtro.getEmailUsuario();

        // Fallback caso venha nulo
        if (usuario == null || usuario.trim().isEmpty()) {
            usuario = "Usuario Nao Identificado";
        }

        byte[] dados = servicoRelatorios.gerarRelatorio(filtro, usuario);
        String formato = filtro.getFormato() != null ? filtro.getFormato().toUpperCase() : "CSV";

        String filename = "relatoriosentinela" + System.currentTimeMillis();
        MediaType mediaType;

        if ("PDF".equals(formato)) {
            filename += ".pdf";
            mediaType = MediaType.APPLICATION_PDF;
        } else if ("XLSX".equals(formato)) {
            filename += ".xlsx";
            mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        } else {
            filename += ".csv";
            mediaType = MediaType.parseMediaType("text/csv");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(mediaType)
                .body(dados);
    }
}