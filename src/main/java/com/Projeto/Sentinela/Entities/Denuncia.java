package com.Projeto.Sentinela.Entities;

import com.Projeto.Sentinela.Enums.EnumFonte;
import com.Projeto.Sentinela.Enums.EnumStatusDenuncia;
import com.Projeto.Sentinela.Enums.EnumTipoDeDenuncia;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter

@Entity
public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeDenunciante;
    private String emailDenunciante;
    private String telefoneDenunciante;
    @Enumerated(EnumType.STRING)
    private EnumFonte fonteDenuncia;
    private String tituloDenuncia;
    @Enumerated(EnumType.STRING)
    private EnumTipoDeDenuncia tipoDenuncia;
    private LocalDateTime dataOcorrido;
    private String descricaoDenuncia;
    private String parteReclamante;
    private String parteReclamada;
    private String gruposVulneraveisAfetados;
    @Enumerated(EnumType.STRING)
    private EnumStatusDenuncia status;
    private int quantidadePessoasAfetadas;



    public boolean vincularInstituicao(){
        return true;
        //implementação futura, baseada na localização

    }
    public void serAprovada(Boolean aprovada){
        if(aprovada == null){
            throw new IllegalArgumentException("A aprovação não pode ser nula");
        }
        if(aprovada){
            this.status = EnumStatusDenuncia.APROVADA;
        }
        else{
            this.status = EnumStatusDenuncia.ARQUIVADA;
        }
    }


}
