package com.Projeto.Sentinela.Model.Repositories;

import com.Projeto.Sentinela.Model.Entities.PasswordResetToken;
import com.Projeto.Sentinela.Model.Entities.UserAbstract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUsuario(UserAbstract usuario);
}
