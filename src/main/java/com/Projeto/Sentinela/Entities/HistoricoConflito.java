package com.Projeto.Sentinela.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class HistoricoConflito {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_conflito", referencedColumnName = "id")
    private Conflito conflito;

    @ManyToOne
    @JoinColumn(name = "id_usuario_responsavel", referencedColumnName = "id")
    private UserAbstract usuarioResponsavel;

    private String acao;

    private LocalDateTime dataRegistro;

    @Column(length = 1000)
    private String observacao;


    public HistoricoConflito() {
        this.dataRegistro = LocalDateTime.now();
    }

    public HistoricoConflito(Conflito conflito, UserAbstract usuarioResponsavel,
                             String acao, String observacao) {
        this.conflito = conflito;
        this.usuarioResponsavel = usuarioResponsavel;
        this.acao = acao;
        this.observacao = observacao;
        this.dataRegistro = LocalDateTime.now();
    }



    public void registrar() {
        //implementação futura
    }


    public void consultar() {
        //implementação futura
    }
}
