package com.Projeto.Sentinela.Model.DTOs;

import com.Projeto.Sentinela.Model.Enums.EnumStatusInstituicao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para retornar informações de uma instituição já cadastrada.
 * Usado em listagens e consultas.
 */
@Getter
@Setter
@AllArgsConstructor
public class InstituicaoResponseDTO {

    private Long id;
    private String nome;
    private String sigla;
    private String cnpj;
    private String telefone;
    private String email;
    private String areaAtuacao;
    private String descricao;
    private EnumStatusInstituicao status;
	private String gestorResponsavel;
	private int totalUsuarios;
	private int totalConflitos;
}
