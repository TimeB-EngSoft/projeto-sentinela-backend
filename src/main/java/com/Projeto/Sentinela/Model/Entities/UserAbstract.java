package com.Projeto.Sentinela.Model.Entities;

import com.Projeto.Sentinela.Model.Enums.EnumCargo;
import com.Projeto.Sentinela.Model.Enums.EnumUsuarioStatus;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
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

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "tipo"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = GestorSecretaria.class, name = "GestorSecretaria"),
    @JsonSubTypes.Type(value = UsuarioSecretaria.class, name = "UsuarioSecretaria"),
    @JsonSubTypes.Type(value = GestorInstituicao.class, name = "GestorInstituicao"),
    @JsonSubTypes.Type(value = UsuarioInstituicao.class, name = "UsuarioInstituicao")
})
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
