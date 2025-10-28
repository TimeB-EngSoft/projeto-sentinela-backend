package com.Projeto.Sentinela.DTOs;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) para receber os dados de cadastro de uma nova instituição.
 * Define a estrutura de dados que o cliente deve enviar na requisição.
 */
@Getter
@Setter
public class InstituicaoDTO {

    private String nome;
    private String sigla;
    private String cnpj;
    private String telefone;
    private String email;
    private String areaAtuacao;
    private String descricao;
}