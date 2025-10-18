package com.Projeto.Sentinela.Entities;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Ente")
public class UsuarioEnte extends UserAbstract {

    public UsuarioEnte(String nome, String email, String cargo, String cpf, Ente ente) {
        super(nome, email, cargo, cpf, ente);
    }

    public UsuarioEnte() {

    }

    public Denuncia emitirDenuncia(String report, String local, Integer numAfetados) {
        return new Denuncia(report, local, numAfetados);
    }
}
