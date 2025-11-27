package com.Projeto.Sentinela.Model.Repositories;

import com.Projeto.Sentinela.Model.Entities.Instituicao;
import com.Projeto.Sentinela.Model.Entities.UserAbstract;
import com.Projeto.Sentinela.Model.Enums.EnumCargo;
import com.Projeto.Sentinela.Model.Enums.EnumUsuarioStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserAbstract, Long> {

    Optional<UserAbstract> findByCpf(String cpf);

    Optional<UserAbstract> findByEmail(String email);

    UserAbstract findUserAbstractByEmail(String email);

    // Métod para buscar todos os usuários de uma instituição (para desativação em cascata)
    List<UserAbstract> findByInstituicao(Instituicao instituicao);

    // Métod para verificar se já existe um gestor ativo na instituição
    Optional<UserAbstract> findByInstituicaoAndCargoAndStatus(Instituicao instituicao, EnumCargo cargo, EnumUsuarioStatus status);

    // Busca usuários por status e filtro opcional de instituição (já implementado na resposta anterior)
    @Query("SELECT u FROM UserAbstract u WHERE u.status = :status AND (:instNome IS NULL OR LOWER(u.instituicao.nome) = LOWER(:instNome))")
    List<UserAbstract> findByStatusAndInstituicaoOptional(@Param("status") EnumUsuarioStatus status, @Param("instNome") String instNome);

    // Conta usuários agrupando por ID da instituição (Uma única consulta para todas)
    @Query("SELECT u.instituicao.id, COUNT(u) FROM UserAbstract u WHERE u.instituicao IS NOT NULL GROUP BY u.instituicao.id")
    List<Object[]> countTotalUsuariosPorInstituicao();

    // Busca nomes dos gestores ativos agrupados por ID da instituição
    @Query("SELECT u.instituicao.id, u.nome FROM UserAbstract u WHERE u.cargo = 'GESTOR_INSTITUICAO' AND u.status = 'ATIVO'")
    List<Object[]> findGestoresAtivosMap();
}
