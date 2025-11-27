package com.Projeto.Sentinela.Model.Repositories;

import com.Projeto.Sentinela.Model.Entities.Instituicao;
import com.Projeto.Sentinela.Model.Entities.UserAbstract;
import com.Projeto.Sentinela.Model.Enums.EnumCargo;
import com.Projeto.Sentinela.Model.Enums.EnumUsuarioStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserAbstract, Long> {

    Optional<UserAbstract> findByCpf(String cpf);

    Optional<UserAbstract> findByEmail(String email);

    UserAbstract findUserAbstractByEmail(String email);

    // Novo métod para buscar todos os usuários de uma instituição (para desativação em cascata)
    List<UserAbstract> findByInstituicao(Instituicao instituicao);

    // Novo métod para verificar se já existe um gestor ativo na instituição
    Optional<UserAbstract> findByInstituicaoAndCargoAndStatus(Instituicao instituicao, EnumCargo cargo, EnumUsuarioStatus status);

}
