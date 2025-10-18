package com.Projeto.Sentinela.Entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("SuperAdmin")
@Getter
@Setter
public class SuperAdmin extends UserAbstract {

//Metodos só funcionarão apos criar a package/classes de repositorios

    public GestorSecretaria cadastrarGestor(String nome, String email, String cargo, String cpf, Ente ente){
        return new GestorSecretaria(nome, email, cargo, cpf, ente);
    }

    public UserAbstract cadastrarUser(String nome, String email, String cargo, String cpf, Ente ente) {
        if(cargo.equalsIgnoreCase("secretaria")){
            return new UsuarioSecretaria(nome,email,cargo,cpf,ente);
        }
        return new UsuarioEnte(nome, email, cargo, cpf, ente);
    }

    public Ente cadastrarEnte(String areaAtuacao, String descricao) {
        return new Ente(areaAtuacao,descricao);
    }

    public Boolean descadastrarEnte(Ente ente) {
        return true;
    }

    public Boolean descadastrarUser(UserAbstract usuario) {
        return true;
    }

    public boolean validarRequest(UsuarioPendente usuarioPendente) {
        return true;
    }

    public boolean validarDenuncia(Denuncia denuncia) {
        return true;
    }

    public Conflito cadastrarConflito(String report, String localizacao, Integer numAfetados, String statusConflito) {
        return new Conflito(report,localizacao,numAfetados,statusConflito);
    }

    public boolean descadastrarConflito(Conflito conflito) {
        return true;
    }
}
