package com.Projeto.Sentinela.Model.DTOs;

import com.Projeto.Sentinela.Model.Enums.EnumStatusInstituicao;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) para receber os dados de atualização de uma instituição.
 * Contém apenas os campos que são permitidos para alteração.
 */
@Getter
@Setter
public class UpInstituicaoDTO {

    private String nome;
    private String sigla;
    private String cnpj;
    private String telefone;
    private String email;
    private String areaAtuacao;
    private String descricao;
    private EnumStatusInstituicao status; // Permite que o status seja alterado
}