package com.Projeto.Sentinela.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "validacao", discriminatorType = DiscriminatorType.INTEGER)
public class Ente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String areaAtuacao;
    private String descricao;
    private Integer validacao;

    public Ente(String areaAtuacao, String descricao) {
        this.areaAtuacao = areaAtuacao;
        this.descricao = descricao;
    }
    public Ente() {}

}
