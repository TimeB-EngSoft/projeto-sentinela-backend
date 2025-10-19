package com.Projeto.Sentinela.Repositories;

import com.Projeto.Sentinela.Entities.Ente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnteRepository extends JpaRepository<Ente, Long> {

    List<Ente> findByAreaAtuacaoContainingIgnoreCase(String areaAtuacao);

    List<Ente> findByDescricaoContainingIgnoreCase(String descricao);

    List<Ente> findByValidacao(Integer validacao);

}
