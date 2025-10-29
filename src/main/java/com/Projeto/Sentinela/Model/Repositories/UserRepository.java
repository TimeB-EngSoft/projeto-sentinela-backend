package com.Projeto.Sentinela.Model.Repositories;

import com.Projeto.Sentinela.Model.Entities.UserAbstract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserAbstract, Long> {

    Optional<UserAbstract> findByCpf(String cpf);

    Optional<UserAbstract> findByEmail(String email);

    UserAbstract findUserAbstractByEmail(String email);

}
