package com.Projeto.Sentinela.Model.DTOs;

import com.Projeto.Sentinela.Model.Enums.EnumCargo;
import com.Projeto.Sentinela.Model.Enums.EnumUsuarioStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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