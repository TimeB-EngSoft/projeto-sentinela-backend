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
@Table (name = "Usuarios")
public abstract class UserAbstract {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "cargo", insertable = false, updatable = false)
    private EnumCargo cargo;

    private String cpf;
    private String telefone;
    private String senha;
    private LocalDate dataNascimento;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;
    private String justificativa;

    @Enumerated(EnumType.STRING)
    private EnumUsuarioStatus status;

    @ManyToOne
    @JoinColumn(name = "id_instituicao", referencedColumnName = "id")
    private Instituicao instituicao;


    public UserAbstract() {

    }
}
