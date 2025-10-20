package com.Projeto.Sentinela.Entities;

import com.Projeto.Sentinela.Enums.EnumCargo;
import com.Projeto.Sentinela.Repositories.InstituicaoRepository;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Secretaria")
public class Secretaria extends GestorSecretaria {

    InstituicaoRepository instituicaoRepository;

    public Boolean aprovarGestorSecretaria(Usuario user) {
        if(user == null){
            throw new IllegalArgumentException("O usuario deve existir para ser aprovado");
        }
        user.setInstituicao(instituicaoRepository.findByNomeContainingIgnoreCase("Secretaria"));
        user.setCargo(EnumCargo.GESTOR_DA_SECRETARIA);
        return true;
    }
}
