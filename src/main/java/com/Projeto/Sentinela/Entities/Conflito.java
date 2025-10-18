package com.Projeto.Sentinela.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Conflito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String report;
    private String localizacao;
    private Integer numAfetados;
    private String statusConflito;

    public Conflito(String report, String localizacao, Integer numAfetados, String statusConflito) {
        this.report = report;
        this.localizacao = localizacao;
        this.numAfetados = numAfetados;
        this.statusConflito = statusConflito;
    }

    public Conflito() {}
}
