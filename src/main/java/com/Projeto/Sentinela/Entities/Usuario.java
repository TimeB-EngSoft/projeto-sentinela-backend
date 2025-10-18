package com.Projeto.Sentinela.Entities;

public abstract class Usuario {
    private String nome;
    private String email;
    private String cargo;
    private String cpf;
    private Ente ente;

    public Usuario(String nome, String email, String cargo, String cpf, Ente ente) {
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
        this.cpf = cpf;
        this.ente = ente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Ente getEnte() {
        return ente;
    }

    public void setEnte(Ente ente) {
        this.ente = ente;
    }
}
