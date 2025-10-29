package com.Projeto.Sentinela.Model.DTOs;

import com.Projeto.Sentinela.Model.Enums.EnumTipoDeDenuncia;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DenunciaDTO {

    private String nomeDenunciante;
    private String emailDenunciante;
    private String telefoneDenunciante;
    private String cpfDenunciante;

    private EnumTipoDeDenuncia tipoDenuncia; // usado se o front enviar enum direto
    private String tipoDenunciaTexto;        // fallback textual ("Desmatamento")

    private String tituloDenuncia;
    private String descricaoDenuncia;
    private String descricaoPartesEnvolvidas;
    private LocalDateTime dataOcorrido;
}
