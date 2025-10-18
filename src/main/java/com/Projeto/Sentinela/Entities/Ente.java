package com.Projeto.Sentinela.Entities;

public class Ente {
    private String areaAtuacao;
    private String descricao;

    public Ente(String areaAtuacao, String descricao) {
        this.areaAtuacao = areaAtuacao;
        this.descricao = descricao;
    }

    public String getAreaAtuacao() {
        return areaAtuacao;
    }

    public void setAreaAtuacao(String areaAtuacao) {
        this.areaAtuacao = areaAtuacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
