package com.Projeto.Sentinela.Entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.ArrayList;

@Entity
@DiscriminatorValue("USUARIO_SECRETARIA")
public class UsuarioSecretaria extends UserAbstract{
    private ArrayList<Denuncia> denunciasRegistradas;
}
