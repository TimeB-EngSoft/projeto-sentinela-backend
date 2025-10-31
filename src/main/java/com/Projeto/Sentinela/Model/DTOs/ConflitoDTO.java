package com.Projeto.Sentinela.Model.DTOs;


import com.Projeto.Sentinela.Model.Entities.Denuncia;
import com.Projeto.Sentinela.Model.Entities.Instituicao;
import com.Projeto.Sentinela.Model.Enums.EnumFonte;
import com.Projeto.Sentinela.Model.Enums.EnumPrioridade;
import com.Projeto.Sentinela.Model.Enums.EnumStatusConflito;
import com.Projeto.Sentinela.Model.Enums.EnumTipoDeDenuncia;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ConflitoDTO {

    private Long id;
    private EnumFonte fonteDenuncia;
    private String tituloConflito;
    private EnumTipoDeDenuncia tipoConflito;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private String descricaoConflito;
    private String parteReclamante;
    private String parteReclamada;
    private String gruposVulneraveis;
    private EnumStatusConflito status;
    private EnumPrioridade prioridade;
    private Instituicao instituicao;
    private Denuncia denunciaOrigem;

}
