package com.Projeto.Sentinela.Repositories;

import com.Projeto.Sentinela.Entities.Conflito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConflitoRepository extends JpaRepository<Conflito, Long> {

}
