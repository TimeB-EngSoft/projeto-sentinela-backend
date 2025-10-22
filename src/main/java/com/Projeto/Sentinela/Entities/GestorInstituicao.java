package com.Projeto.Sentinela.Entities;

import com.Projeto.Sentinela.Enums.EnumCargo;
import com.Projeto.Sentinela.Enums.EnumStatusDenuncia;
import com.Projeto.Sentinela.Enums.EnumUsuarioStatus;
import com.Projeto.Sentinela.Repositories.InstituicaoRepository;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.util.ArrayList;

@Entity
@DiscriminatorValue("GESTOR_DA_INSTITUICAO")
public class GestorInstituicao extends UserAbstract {

    @Transient
    InstituicaoRepository instituicaoRepository;

    private ArrayList<Conflito> conflitosRegistrados;


    public Boolean aprovarUsuario(UsuarioInstituicao user, String instituicao){
        if(user == null){
            throw new IllegalArgumentException("O usuario deve existir para ser aprovado");
        }
        user.setInstituicao(instituicaoRepository.findByNomeContainingIgnoreCase(instituicao));
        user.setStatus(EnumUsuarioStatus.ATIVO);
        if(instituicao.equalsIgnoreCase("Secretaria")){
            throw new IllegalArgumentException("Você não tem permissão para aceitar usuários da secretaria");
        }
        user.setCargo(EnumCargo.USUARIO_INSTITUICAO);
        return true;
    }

    public Boolean validarDenuncia(Denuncia den){
        if(den == null){
            throw new IllegalArgumentException("A denuncia deve existir para ser aprovada");
        }
        den.setStatus(EnumStatusDenuncia.APROVADA);
        return true;
        //a criação do conflito pode ser criada nos controllers, para não deixar nas classes básicas
    }

    public GestorInstituicao() {
    }

}
