package com.Projeto.Sentinela.Repositories;

import com.Projeto.Sentinela.Entities.Conflito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConflitoRepository extends JpaRepository<Conflito, Long> {

    List<Conflito> findByStatusConflito(String status);

    List<Conflito> findByNumAfetadosGreaterThan(Integer numAfetados);
}
