package com.Projeto.Sentinela.Model.DTOs;

import com.Projeto.Sentinela.Model.Enums.EnumFonte;
import com.Projeto.Sentinela.Model.Enums.EnumStatusDenuncia;
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
    private EnumStatusDenuncia statusDenuncia;

    private String cep;
    private String estado;
    private String municipio; // mapeado como 'cidade' no front, ajustaremos no service
    private String bairro;
    private String rua;
    private String numero; // Pode ser salvo no complemento ou num campo novo se desejar
    private String referencia;

    private Long instituicaoId; // Novo campo para vínculo

    // Fonte (Opcional, o front manda "USUARIO_INTERNO" se estiver logado)
    private EnumFonte fonteDenuncia;

}
