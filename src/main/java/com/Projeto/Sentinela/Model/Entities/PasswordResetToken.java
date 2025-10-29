package com.Projeto.Sentinela.Model.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table (name = "PasswordTokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private LocalDateTime expiration;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserAbstract usuario;

    public PasswordResetToken() {
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiration);
    }


}
