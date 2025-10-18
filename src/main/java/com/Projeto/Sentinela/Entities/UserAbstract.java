package com.Projeto.Sentinela.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cargo", discriminatorType = DiscriminatorType.STRING)
public abstract class UserAbstract {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String cargo;
    private String cpf;
    @ManyToOne
    @JoinColumn(name = "ente_id")
    private Ente ente;


    public UserAbstract(String nome, String email, String cargo, String cpf, Ente ente) {
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
        this.cpf = cpf;
        this.ente = ente;
    }

    public UserAbstract() {

    }
}
