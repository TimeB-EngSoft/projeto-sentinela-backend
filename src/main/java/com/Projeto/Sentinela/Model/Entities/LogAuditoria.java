package com.Projeto.Sentinela.Model.Entities;

import com.Projeto.Sentinela.Model.Enums.EnumNivelAuditoria;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "LogsAuditoria")
public class LogAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataHora;
    private String usuario; // Email ou Nome do usuário que realizou a ação
    private String acao;    // Ex: "Login", "Aprovação"
    private String modulo;  // Ex: "Autenticação", "Conflitos"
    private String detalhes;
    private String ip;

    @Enumerated(EnumType.STRING)
    private EnumNivelAuditoria nivel;

    public LogAuditoria() {
        this.dataHora = LocalDateTime.now();
    }

    public LogAuditoria(String usuario, String acao, String modulo, String detalhes, EnumNivelAuditoria nivel, String ip) {
        this.dataHora = LocalDateTime.now();
        this.usuario = usuario;
        this.acao = acao;
        this.modulo = modulo;
        this.detalhes = detalhes;
        this.nivel = nivel;
        this.ip = ip;
    }
}