package com.Projeto.Sentinela.Entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("GestorSecretaria")
public class GestorSecretaria extends UserAbstract {

    public GestorSecretaria(String nome, String email, String cargo, String cpf, Instituicao instituicao) {
        super(nome, email, cargo, cpf, instituicao);
    }

    public GestorSecretaria() {

    }

    //Metodos só funcionarão apos criar a package/classes de repositorios

    public UserAbstract cadastrarUser(String nome, String email, String cargo, String cpf, Instituicao instituicao) {
        return new UsuarioSecretaria(nome,email,cargo,cpf, instituicao);
    }

    public Instituicao cadastrarEnte(String areaAtuacao, String descricao) {
        return new Instituicao(areaAtuacao,descricao);
    }

    public Boolean descadastrarEnte(Instituicao instituicao) {
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
