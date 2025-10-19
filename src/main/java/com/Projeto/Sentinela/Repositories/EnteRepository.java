package com.Projeto.Sentinela.Repositories;

import com.Projeto.Sentinela.Entities.Instituicao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnteRepository extends JpaRepository<Instituicao, Long> {

    List<Instituicao> findByAreaAtuacaoContainingIgnoreCase(String areaAtuacao);

    List<Instituicao> findByDescricaoContainingIgnoreCase(String descricao);

    List<Instituicao> findByValidacao(Integer validacao);

}
