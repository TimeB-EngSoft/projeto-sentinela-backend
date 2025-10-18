package com.Projeto.Sentinela.Entities;

public class Conflito {

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

    public String getStatusConflito() {
        return statusConflito;
    }

    public void setStatusConflito(String statusConflito) {
        this.statusConflito = statusConflito;
    }
}
