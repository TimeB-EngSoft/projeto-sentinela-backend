package com.Projeto.Sentinela.Repositories;

import com.Projeto.Sentinela.Entities.Denuncia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {

    List<Denuncia> findByLocalizacaoContainingIgnoreCase(String localizacao);

    List<Denuncia> findByNumAfetadosGreaterThan(Integer numAfetados);
}
