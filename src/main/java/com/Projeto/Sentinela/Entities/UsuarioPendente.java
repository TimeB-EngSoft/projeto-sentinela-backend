package com.Projeto.Sentinela.Entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

@Getter
@Entity
@DiscriminatorValue("NaoValidado")
public class UsuarioPendente extends UserAbstract {
    private String justificativa;

    public UsuarioPendente() {}

    public UsuarioPendente(String nome, String email, String cargo, String cpf, Ente ente, String justificativa) {
        super(nome, email, cargo, cpf, ente);
        this.justificativa = justificativa;
    }


}
