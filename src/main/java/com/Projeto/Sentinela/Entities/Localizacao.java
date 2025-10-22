package com.Projeto.Sentinela.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
@Table (name = "Localizacoes")
public class Localizacao {

    @Id
    private String cep;

    private String estado;
    private String municipio;
    private Double latitude;
    private Double longitude;
    private String complemento;

    public void CalcularLatitudeLongitude(){}

}
