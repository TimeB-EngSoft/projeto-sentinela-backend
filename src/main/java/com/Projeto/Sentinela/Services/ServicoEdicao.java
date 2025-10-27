package com.Projeto.Sentinela.Services;

import com.Projeto.Sentinela.DTOs.UpUserDTO;
import com.Projeto.Sentinela.Entities.Instituicao;
import com.Projeto.Sentinela.Entities.UserAbstract;
import com.Projeto.Sentinela.Repositories.InstituicaoRepository;
import com.Projeto.Sentinela.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ServicoEdicao {

    @Autowired
    private InstituicaoRepository instituicaoRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserAbstract atualizarUser(long idUser, UpUserDTO dto) {

        UserAbstract usuario = userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: id=" + idUser));

        // Atualiza email (verifica duplicidade)
        if (StringUtils.hasText(dto.getEmail()) && !dto.getEmail().equalsIgnoreCase(usuario.getEmail())) {
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new RuntimeException("E-mail já em uso por outro usuário.");
            }
            usuario.setEmail(dto.getEmail());
        }

        // Atualiza nome
        if (StringUtils.hasText(dto.getNome())) {
            usuario.setNome(dto.getNome());
        }

        // Atualiza telefone
        if (StringUtils.hasText(dto.getTelefone())) {
            usuario.setTelefone(dto.getTelefone());
        }

        // Atualiza CPF
        if (StringUtils.hasText(dto.getCpf())) {
            usuario.setCpf(dto.getCpf());
        }

        // Atualiza data de nascimento
        if (dto.getDataNascimento() != null) {
            usuario.setDataNascimento(LocalDate.parse(dto.getDataNascimento()));
        }

        // Atualiza status
        if (dto.getStatus() != null) {
            usuario.setStatus(dto.getStatus());
        }

        // Atualiza cargo
        if (dto.getCargo() != null) {
            usuario.setCargo(dto.getCargo());
        }

        // Atualiza instituição (por nome)
        if (StringUtils.hasText(dto.getInstituicaoNome())) {
            Instituicao inst = instituicaoRepository.findByNomeContainingIgnoreCase(dto.getInstituicaoNome());
            if (inst == null) {
                throw new RuntimeException("Instituição não encontrada: " + dto.getInstituicaoNome());
            }
            usuario.setInstituicao(inst);
        }

        // Atualiza a data de modificação
        usuario.setDataAtualizacao(LocalDateTime.now());

        return userRepository.save(usuario);
    }
}
