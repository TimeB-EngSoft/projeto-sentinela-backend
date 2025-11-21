package com.Projeto.Sentinela.Model.Entities;

import com.Projeto.Sentinela.Model.Enums.EnumFonte;
import com.Projeto.Sentinela.Model.Enums.EnumStatusDenuncia;
import com.Projeto.Sentinela.Model.Enums.EnumTipoDeDenuncia;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
@Setter
@Getter

@Entity
@Table (name = "Denuncias")
public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeDenunciante;
    private String emailDenunciante;
    private String telefoneDenunciante;
    private String cpfDenunciante;
    @Enumerated(EnumType.STRING)
    private EnumFonte fonteDenuncia;
    private String tituloDenuncia;
    @Enumerated(EnumType.STRING)
    private EnumTipoDeDenuncia tipoDenuncia;
    private LocalDateTime dataOcorrido;
    private String descricaoDenuncia;
    private String descricaoPartesEnvolvidas;
    @Enumerated(EnumType.STRING)
    private EnumStatusDenuncia status;
    @ManyToOne
    @JoinColumn(name = "id_instituicao", referencedColumnName = "id", nullable = true)
    private Instituicao instituicao; // Nova vinculação
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cep_localizacao", referencedColumnName = "cep")
    private Localizacao localizacao;


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
