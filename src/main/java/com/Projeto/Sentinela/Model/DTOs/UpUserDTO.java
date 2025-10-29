package com.Projeto.Sentinela.Model.DTOs;

import com.Projeto.Sentinela.Model.Enums.EnumCargo;
import com.Projeto.Sentinela.Model.Enums.EnumUsuarioStatus;
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