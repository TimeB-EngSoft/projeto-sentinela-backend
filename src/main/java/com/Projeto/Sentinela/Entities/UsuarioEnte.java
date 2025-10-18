package com.Projeto.Sentinela.Entities;

public class UsuarioEnte extends Usuario {

    public UsuarioEnte(String nome, String email, String cargo, String cpf, Ente ente) {
        super(nome, email, cargo, cpf, ente);
    }

    public Denuncia emitirDenuncia(String report, String local, Integer numAfetados) {
        return new Denuncia(report, local, numAfetados);
    }
}
