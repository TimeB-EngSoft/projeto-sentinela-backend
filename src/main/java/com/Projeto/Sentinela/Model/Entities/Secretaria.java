package com.Projeto.Sentinela.Model.Entities;

import com.Projeto.Sentinela.Model.Enums.EnumCargo;
import com.Projeto.Sentinela.Model.Repositories.InstituicaoRepository;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

@Entity
@DiscriminatorValue("SECRETARIA")
public class Secretaria extends GestorSecretaria {

    @Transient
    InstituicaoRepository instituicaoRepository;

    public Boolean aprovarGestorSecretaria(UsuarioInstituicao user) {
        if(user == null){
            throw new IllegalArgumentException("O usuario deve existir para ser aprovado");
        }
        user.setInstituicao(instituicaoRepository.findByNomeContainingIgnoreCase("Secretaria"));
        user.setCargo(EnumCargo.GESTOR_SECRETARIA);
        return true;
    }
}
