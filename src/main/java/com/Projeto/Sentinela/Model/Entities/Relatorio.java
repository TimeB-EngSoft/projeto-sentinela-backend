package com.Projeto.Sentinela.Model.Entities;

import com.Projeto.Sentinela.Model.Enums.EnumFormato;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table (name = "Relatorios")
public class Relatorio {

    @Id
    @GeneratedValue
    private UUID id;

    private String titulo;

    @Enumerated(EnumType.STRING)
    private EnumFormato formato;

    private LocalDateTime dataGeracao;

    private Boolean agendado;

    public Relatorio() {
        this.dataGeracao = LocalDateTime.now();
        this.agendado = false;
    }

    public Relatorio(String titulo, EnumFormato formato) {
        this.titulo = titulo;
        this.formato = formato;
        this.dataGeracao = LocalDateTime.now();
        this.agendado = false;
    }


    public void gerar() {
        this.dataGeracao = LocalDateTime.now();
        //implementação futura
    }

    public void exportarCSV() {
        if (formato != EnumFormato.CSV) {
            throw new IllegalStateException("O formato do relatório não é CSV. Formato atual: " + formato);
        }
        //implementação futura
    }

    public void exportarPDF() {
        if (formato != EnumFormato.PDF) {
            throw new IllegalStateException("O formato do relatório não é PDF. Formato atual: " + formato);
        }
        //implementação futura
        }

    public void agendar() {
        this.agendado = true;
        //implementação futura
    }

    public void salvarFiltrosFavoritos() {
        //implementação futura
    }

}
