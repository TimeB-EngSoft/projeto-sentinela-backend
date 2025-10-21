package com.Projeto.Sentinela.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private LocalDateTime expiration;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UserAbstract usuario;

    public PasswordResetToken() {
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiration);
    }


}
