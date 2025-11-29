package com.Projeto.Sentinela.Model.Repositories;

import com.Projeto.Sentinela.Model.Entities.LogAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {
    // Busca logs recentes ordenados
    List<LogAuditoria> findAllByOrderByDataHoraDesc();

    // Contagem para os widgets
    long countByDataHoraAfter(LocalDateTime data);
    long countByNivel(com.Projeto.Sentinela.Model.Enums.EnumNivelAuditoria nivel);

    // Busca os usuários mais ativos (Top 5)
    @Query(value = "SELECT usuario, COUNT(*) as total FROM logs_auditoria GROUP BY usuario ORDER BY total DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findTopActiveUsers();

    // Busca ações mais frequentes (Top 5)
    @Query(value = "SELECT acao, COUNT(*) as total FROM logs_auditoria GROUP BY acao ORDER BY total DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findTopActions();
}
