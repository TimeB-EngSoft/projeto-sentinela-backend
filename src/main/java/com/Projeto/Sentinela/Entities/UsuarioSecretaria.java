package com.Projeto.Sentinela.Entities;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Secretaria")
public class UsuarioSecretaria extends UserAbstract {
    public UsuarioSecretaria(String nome, String email, String cargo, String cpf, Ente ente) {
        super(nome, email, cargo, cpf, ente);
    }

    public UsuarioSecretaria() {}

    public Denuncia emitirDenuncia(String report, String local, Integer numAfetados) {
        return new Denuncia(report, local, numAfetados);
    }
}
