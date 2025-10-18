package com.Projeto.Sentinela.Entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("0")
public class EntePendente extends Ente{
    private String justificativa;

    public EntePendente(String areaAtuacao, String descricao, String justificativa) {
        super(areaAtuacao, descricao);
        this.justificativa = justificativa;
    }
    public EntePendente() {}
}
