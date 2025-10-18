package com.Projeto.Sentinela.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

@Entity
public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    private String report;
    private String localizacao;
    private Integer numAfetados;

    public Denuncia() {}

    public Denuncia(String report, String localizacao, Integer numAfetados) {
        this.report = report;
        this.localizacao = localizacao;
        this.numAfetados = numAfetados;
    }

}
