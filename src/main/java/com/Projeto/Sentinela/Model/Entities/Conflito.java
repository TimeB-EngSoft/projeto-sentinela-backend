package com.Projeto.Sentinela.Model.Entities;

import com.Projeto.Sentinela.Model.Enums.EnumFonte;
import com.Projeto.Sentinela.Model.Enums.EnumPrioridade;
import com.Projeto.Sentinela.Model.Enums.EnumStatusConflito;
import com.Projeto.Sentinela.Model.Enums.EnumTipoDeDenuncia;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table (name = "Conflitos")
public class Conflito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EnumFonte fonteDenuncia;
    private String tituloConflito;
    @Enumerated(EnumType.STRING)
    private EnumTipoDeDenuncia tipoConflito;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private String descricaoConflito;
    private String parteReclamante;
    private String parteReclamada;
    private String gruposVulneraveis;
    @Enumerated(EnumType.STRING)
    private EnumStatusConflito status;
    @Enumerated(EnumType.STRING)
    private EnumPrioridade prioridade;

    @ManyToOne
    @JoinColumn(name = "id_Instituição", referencedColumnName = "id")
    private Instituicao instituicao;

    @OneToOne
    @JoinColumn(name = "id_Denuncia_Origem", referencedColumnName = "id")
    private Denuncia denunciaOrigem;

    public Conflito() {}

    public void atualizarStatus(EnumStatusConflito novoStatus) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("O status do conflito não pode ser nulo.");
        }

        if (this.status == EnumStatusConflito.CANCELADO && novoStatus == EnumStatusConflito.ATIVO) {
            throw new IllegalStateException("Não é permitido reabrir um conflito cancelado dessa forma.");
        }

        this.status = novoStatus;

        if (novoStatus == EnumStatusConflito.CANCELADO) {
            this.dataFim = LocalDateTime.now();
        }
    }

    public void definirPrioridade(EnumPrioridade novaPrioridade) {
        if (novaPrioridade == null) {
            throw new IllegalArgumentException("A prioridade do conflito não pode ser nula.");
        }

        this.prioridade = novaPrioridade;
    }
}

