package com.Projeto.Sentinela.DTOs;

import com.Projeto.Sentinela.Enums.EnumCargo;
import com.Projeto.Sentinela.Enums.EnumUsuarioStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpUserDTO {
    private String nome;
    private String email;
    private String telefone;
    private String dataNascimento; // yyyy-MM-dd
    private String cpf;
    private EnumCargo cargo;
    private EnumUsuarioStatus status;
    private String instituicaoNome;

}