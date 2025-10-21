package com.Projeto.Sentinela.Entities;

import com.Projeto.Sentinela.Enums.EnumStatusInstituicao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "validacao", discriminatorType = DiscriminatorType.INTEGER)
public class Instituicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String sigla;
    private String cnpj;
    private String telefone;
    private String email;
    private String areaAtuacao;
    private String descricao;

    @Column(name = "validacao", insertable = false, updatable = false)
    private Integer validacao;

    @Enumerated(EnumType.STRING)
    private EnumStatusInstituicao status;

    @OneToOne
    @JoinColumn(name = "cep_localizacao", referencedColumnName = "cep")
    private Localizacao localizacao;

    private LocalDateTime dataCadastro;

    public Instituicao() {}

    public int getNumUsuarios(){
        return 1;
        //implementação futura, com captação do número a partir do repositorio de usuarios
    }
    public boolean associarGestor(){
        return true;
    }
}
