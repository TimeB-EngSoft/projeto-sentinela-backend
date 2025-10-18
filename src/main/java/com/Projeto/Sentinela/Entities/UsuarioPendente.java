package com.Projeto.Sentinela.Entities;

public class UsuarioPendente extends Usuario{
    private String justificativa;

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public UsuarioPendente(String nome, String email, String cargo, String cpf, Ente ente, String justificativa) {
        super(nome, email, cargo, cpf, ente);
        this.justificativa = justificativa;


    }


}
