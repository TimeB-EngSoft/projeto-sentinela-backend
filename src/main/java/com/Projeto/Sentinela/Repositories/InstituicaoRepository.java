package com.Projeto.Sentinela.Repositories;

import com.Projeto.Sentinela.Entities.Instituicao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstituicaoRepository extends JpaRepository<Instituicao, Long> {

    @org.springframework.data.jpa.repository.Query("SELECT i.id FROM Instituicao i WHERE i.nome = :nome")
    Long findIdByNome(String nome);

    Instituicao findByNomeContainingIgnoreCase(String nome);




}
