package com.Projeto.Sentinela.Model.Repositories;

import com.Projeto.Sentinela.Model.Entities.Instituicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório para a entidade Instituicao.
 * Estende JpaRepository para fornecer operações CRUD (Criar, Ler, Atualizar, Deletar)
 * e outras funcionalidades de acesso a dados prontas para uso.
 */
@Repository
public interface InstituicaoRepository extends JpaRepository<Instituicao, Long> {

    /**
     * Busca uma instituição pelo nome, ignorando diferenças entre maiúsculas e minúsculas.
     * O Spring Data JPA cria a implementação deste métoddo automaticamente com base no nome.
     *
     * @param nome O nome da instituição a ser procurado.
     * @return um objeto Instituicao se encontrado, caso contrário, null.
     */
    Instituicao findByNomeContainingIgnoreCase(String nome);
}