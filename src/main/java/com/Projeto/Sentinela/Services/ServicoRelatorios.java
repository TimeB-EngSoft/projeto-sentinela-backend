package com.Projeto.Sentinela.Services;

import com.Projeto.Sentinela.Model.DTOs.FiltroRelatorioDTO;
import com.Projeto.Sentinela.Model.Entities.Conflito;
import com.Projeto.Sentinela.Model.Entities.Denuncia;
import com.Projeto.Sentinela.Model.Repositories.ConflitoRepository;
import com.Projeto.Sentinela.Model.Repositories.DenunciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicoRelatorios {

    @Autowired
    private ConflitoRepository conflitoRepository;

    @Autowired
    private DenunciaRepository denunciaRepository;

    public byte[] gerarRelatorioCsv(FiltroRelatorioDTO filtro) {
        StringBuilder csv = new StringBuilder();
        csv.append("Tipo;ID;Título;Data;Status;Prioridade/Tipo;Local\n");

        // 1. Buscar e filtrar Conflitos
        List<Conflito> conflitos = conflitoRepository.findAll();
        // (Idealmente usar Specification ou Query personalizada com os filtros do DTO)

        for (Conflito c : conflitos) {
            // Aplica filtro simples de data se fornecido
            if (filtro.getDataInicial() != null && c.getDataInicio().toLocalDate().isBefore(filtro.getDataInicial())) continue;
            if (filtro.getDataFinal() != null && c.getDataInicio().toLocalDate().isAfter(filtro.getDataFinal())) continue;

            csv.append(String.format("CONFLITO;%d;%s;%s;%s;%s;%s\n",
                    c.getId(),
                    escapeCsv(c.getTituloConflito()),
                    c.getDataInicio().format(DateTimeFormatter.ISO_DATE),
                    c.getStatus(),
                    c.getPrioridade(),
                    escapeCsv(c.getLocalizacao() != null ? c.getLocalizacao().getMunicipio() : "-")
            ));
        }

        // 2. Buscar e filtrar Denúncias
        List<Denuncia> denuncias = denunciaRepository.findAll();

        for (Denuncia d : denuncias) {
            if (filtro.getDataInicial() != null && d.getDataOcorrido().toLocalDate().isBefore(filtro.getDataInicial())) continue;

            csv.append(String.format("DENUNCIA;%d;%s;%s;%s;%s;%s\n",
                    d.getId(),
                    escapeCsv(d.getTituloDenuncia()),
                    d.getDataOcorrido().format(DateTimeFormatter.ISO_DATE),
                    d.getStatus(),
                    d.getTipoDenuncia(),
                    escapeCsv(d.getLocalizacao() != null ? d.getLocalizacao().getMunicipio() : "-")
            ));
        }

        return csv.toString().getBytes();
    }

    private String escapeCsv(String data) {
        if (data == null) return "";
        return "\"" + data.replace("\"", "\"\"") + "\"";
    }
}
