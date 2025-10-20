package com.Projeto.Sentinela.Entities;

import com.Projeto.Sentinela.Enums.EnumCargo;
import com.Projeto.Sentinela.Enums.EnumUsuarioStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    private EnumCargo cargo;

    private String cpf;
    private String telefone;
    private String senha;
    private LocalDate dataNascimento;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;

    @Enumerated(EnumType.STRING)
    private EnumUsuarioStatus status;

    @OneToOne
    @JoinColumn(name = "id_instituicao", referencedColumnName = "id")
    private Instituicao instituicao;


    public UserAbstract() {

    }
}
