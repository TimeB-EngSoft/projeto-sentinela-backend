package com.Projeto.Sentinela.Model.DTOs;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class FiltroRelatorioDTO {
    private String tipoRelatorio; // geral, regional, temporal
    private LocalDate dataInicial;
    private LocalDate dataFinal;
    private String estado;
    private String status; // ativo, encerrado, etc.
    private String formato; // csv
}
