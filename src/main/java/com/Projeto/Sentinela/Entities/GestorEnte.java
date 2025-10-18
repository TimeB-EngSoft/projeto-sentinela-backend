package com.Projeto.Sentinela.Entities;
//terminar dps, não entendi muito bem os metodos (matheus)
public class GestorEnte extends Usuario {
    public GestorEnte(String nome, String email, String cargo, String cpf, Ente ente) {
        super(nome, email, cargo, cpf, ente);
    }

    public Boolean cadastrarUser(){
        return true;
    }
}
