package com.Projeto.Sentinela.Entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("0")
public class InstituicaoPendente extends Instituicao {
    private String justificativa;

    public InstituicaoPendente(String areaAtuacao, String descricao, String justificativa) {
        super(areaAtuacao, descricao);
        this.justificativa = justificativa;
    }
    public InstituicaoPendente() {}
}
