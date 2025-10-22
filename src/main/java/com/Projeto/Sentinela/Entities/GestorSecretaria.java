package com.Projeto.Sentinela.Entities;

import com.Projeto.Sentinela.Enums.EnumCargo;
import com.Projeto.Sentinela.Enums.EnumStatusDenuncia;
import com.Projeto.Sentinela.Enums.EnumStatusInstituicao;
import com.Projeto.Sentinela.Enums.EnumUsuarioStatus;
import com.Projeto.Sentinela.Repositories.InstituicaoRepository;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.util.ArrayList;

@Entity
@DiscriminatorValue("GESTOR_DA_SECRETARIA")
public class GestorSecretaria extends UserAbstract {

    @Transient
    InstituicaoRepository instituicaoRepository;

    private ArrayList<Conflito> conflitosRegistrados;

    public Boolean aprovarGestorInstituicao(UsuarioInstituicao user, String nomeDaInstituicao) {
        if(user == null){
            throw new IllegalArgumentException("O usuário deve existir para ser aprovado");
        }
        user.setCargo(EnumCargo.valueOf("GestorInstituicao"));
        user.setInstituicao(instituicaoRepository.findByNomeContainingIgnoreCase(nomeDaInstituicao));
        return true;
    }

    public Boolean aprovarUsuario(UsuarioInstituicao user, String instituicao){
        if(user == null){
            throw new IllegalArgumentException("O usuario deve existir para ser aprovado");
        }
        user.setInstituicao(instituicaoRepository.findByNomeContainingIgnoreCase(instituicao));
        user.setStatus(EnumUsuarioStatus.ATIVO);
        if(instituicao.equalsIgnoreCase("Secretaria")){
            user.setCargo(EnumCargo.USUARIO_SECRETARIA);
        }
        user.setCargo(EnumCargo.USUARIO_INSTITUICAO);
        return true;
    }

    public Boolean aprovarInstituicao(Instituicao inst){
        if(inst == null){
            throw new IllegalArgumentException("A instituicao deve existir para ser aprovada");
        }
        inst.setStatus(EnumStatusInstituicao.ATIVO);
        return true;
    }

    public Boolean aprovarDenuncia(Denuncia den){
        if(den == null){
            throw new IllegalArgumentException("A denuncia deve existir para ser aprovada");
        }
        den.setStatus(EnumStatusDenuncia.APROVADA);
        return true;
        //a criação do conflito pode ser criada nos controllers, para não deixar nas classes básicas
    }

    public GestorSecretaria() {}



}
