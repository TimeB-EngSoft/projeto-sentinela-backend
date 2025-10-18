package com.Projeto.Sentinela.Entities;

public class EntePendente extends Ente{
    private String justificativa;

    public EntePendente(String areaAtuacao, String descricao, String justificativa) {
        super(areaAtuacao, descricao);
        this.justificativa = justificativa;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }
}
