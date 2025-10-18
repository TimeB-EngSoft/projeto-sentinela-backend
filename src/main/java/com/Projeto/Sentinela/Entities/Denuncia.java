package com.Projeto.Sentinela.Entities;

public class Denuncia {
    private String report;
    private String localizacao;
    private Integer numAfetados;

    public Denuncia(String report, String localizacao, Integer numAfetados) {
        this.report = report;
        this.localizacao = localizacao;
        this.numAfetados = numAfetados;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public Integer getNumAfetados() {
        return numAfetados;
    }

    public void setNumAfetados(Integer numAfetados) {
        this.numAfetados = numAfetados;
    }
}
