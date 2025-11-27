package com.Projeto.Sentinela.Services;

import com.Projeto.Sentinela.Model.DTOs.*;
import com.Projeto.Sentinela.Model.Entities.*;
import com.Projeto.Sentinela.Model.Enums.EnumCargo;
import com.Projeto.Sentinela.Model.Enums.EnumStatusInstituicao;
import com.Projeto.Sentinela.Model.Enums.EnumUsuarioStatus;
import com.Projeto.Sentinela.Model.Repositories.InstituicaoRepository;
import com.Projeto.Sentinela.Model.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Serviço responsável pela lógica de negócio relacionada às instituições.
 */
@Service
public class ServicoInstituicao {

    @Autowired
    private InstituicaoRepository instituicaoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServicoUser servicoUser;

    /**
     * Cadastra uma nova instituição no sistema.
     *
     * @param dto Os dados da instituição a ser cadastrada.
     * @return A entidade Instituicao que foi salva no banco de dados.
     * @throws RuntimeException se já existir uma instituição com o mesmo nome.
     */
    @Transactional
    public Instituicao cadastrarInstituicao(InstituicaoDTO dto) {
        // Verifica se já existe uma instituição com o mesmo nome.
        if (instituicaoRepository.findByNomeContainingIgnoreCase(dto.getNome()) != null) {
            throw new RuntimeException("Já existe uma instituição cadastrada com o nome: " + dto.getNome());
        }

        // Converte os dados do DTO para a entidade Instituicao.
        Instituicao novaInstituicao = new Instituicao();
        novaInstituicao.setNome(dto.getNome());
        novaInstituicao.setSigla(dto.getSigla());
        novaInstituicao.setCnpj(dto.getCnpj());
        novaInstituicao.setTelefone(dto.getTelefone());
        novaInstituicao.setEmail(dto.getEmail());
        novaInstituicao.setAreaAtuacao(dto.getAreaAtuacao());
        novaInstituicao.setDescricao(dto.getDescricao());

        // Define valores padrão antes de salvar.
        novaInstituicao.setDataCadastro(LocalDateTime.now());
        novaInstituicao.setStatus(EnumStatusInstituicao.PENDENTE); // Toda nova instituição começa como pendente.

        // Persistência: Chama o repositório para salvar a entidade no banco de dados.
        return instituicaoRepository.save(novaInstituicao);
    }


    /**
     * Atualiza os dados de uma instituição existente com base no ID.
     *
     * @param id O ID da instituição a ser atualizada.
     * @param dto Os novos dados para a instituição.
     * @return A entidade Instituicao atualizada e salva no banco.
     * @throws RuntimeException se a instituição não for encontrada.
     */
    @Transactional
    public Instituicao atualizarInstituicao(long id, UpInstituicaoDTO dto) {
        // Busca a instituição no banco de dados. Se não encontrar, lança uma exceção.
        Instituicao instituicao = instituicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada com o ID: " + id));

        // Atualiza cada campo apenas se um novo valor foi fornecido no DTO.
        // A função StringUtils.hasText() verifica se a String não é nula e não está vazia.

        if (StringUtils.hasText(dto.getNome())) {
            instituicao.setNome(dto.getNome());
        }
        if (StringUtils.hasText(dto.getSigla())) {
            instituicao.setSigla(dto.getSigla());
        }
        if (StringUtils.hasText(dto.getCnpj())) {
            instituicao.setCnpj(dto.getCnpj());
        }
        if (StringUtils.hasText(dto.getTelefone())) {
            instituicao.setTelefone(dto.getTelefone());
        }
        if (StringUtils.hasText(dto.getEmail())) {
            instituicao.setEmail(dto.getEmail());
        }
        if (StringUtils.hasText(dto.getAreaAtuacao())) {
            instituicao.setAreaAtuacao(dto.getAreaAtuacao());
        }
        if (StringUtils.hasText(dto.getDescricao())) {
            instituicao.setDescricao(dto.getDescricao());
        }
        // Lógica de Status e Cascata
        if (dto.getStatus() != null) {
            // Desativar todos os usuarios pertencentes a instituicao
            if (dto.getStatus() == EnumStatusInstituicao.INATIVO && instituicao.getStatus() != EnumStatusInstituicao.INATIVO) {
                desativarUsuariosDaInstituicao(instituicao);
            }
            // Reativar todos os usuarios pertencentes a instituicao
            else if (dto.getStatus() == EnumStatusInstituicao.ATIVO && instituicao.getStatus() == EnumStatusInstituicao.INATIVO) {
                reativarUsuariosDaInstituicao(instituicao);
            }
            instituicao.setStatus(dto.getStatus());
        }

        // Salva a entidade atualizada no banco de dados.
        return instituicaoRepository.save(instituicao);
    }

    private void desativarUsuariosDaInstituicao(Instituicao instituicao) {
        List<UserAbstract> usuarios = userRepository.findByInstituicao(instituicao);
        for (UserAbstract user : usuarios) {
            if (user.getStatus() == EnumUsuarioStatus.ATIVO) {
                user.setStatus(EnumUsuarioStatus.INATIVO);
                user.setDataAtualizacao(LocalDateTime.now());
                userRepository.save(user);
            }
        }
    }

    private void reativarUsuariosDaInstituicao(Instituicao instituicao) {
        List<UserAbstract> usuarios = userRepository.findByInstituicao(instituicao);
        for (UserAbstract user : usuarios) {
            // Reativa apenas quem estava INATIVO (evita reativar bloqueados/pendentes indevidamente se não for a regra)
            if (user.getStatus() == EnumUsuarioStatus.INATIVO) {
                user.setStatus(EnumUsuarioStatus.ATIVO);
                user.setDataAtualizacao(LocalDateTime.now());
                userRepository.save(user);
            }
        }
    }


    public List<UpUserDTO> listarUsuarios(long id, String tipo) {

        Instituicao i = instituicaoRepository.findById(id).orElseThrow(() -> new RuntimeException("Intituição não presente"));

        if (tipo.equalsIgnoreCase("all")) {
            UserAbstract g = new GestorInstituicao();
            g.setInstituicao(i);
            UserAbstract u = new UsuarioInstituicao();
            u.setInstituicao(i);

            Example<UserAbstract> e1 = Example.of(g);
            Example<UserAbstract> e2 = Example.of(u);

            List<UserAbstract> lista = userRepository.findAll(e1);
            lista.addAll(userRepository.findAll(e2));
            if (lista.isEmpty()) {
                throw new RuntimeException("Nenhum usuario encontrado");
            }

            List<UpUserDTO> listDTO = lista.stream().map(user -> {
                UpUserDTO dto = new UpUserDTO();
                dto.setId(user.getId()); // <-- Adiciona o ID
                dto.setNome(user.getNome());
                dto.setEmail(user.getEmail());
                dto.setTelefone(user.getTelefone());
                dto.setDataNascimento(Optional.ofNullable(user.getDataNascimento())
                        .map(LocalDate::toString)
                        .orElse(null));
                dto.setCpf(user.getCpf());
                dto.setCargo(user.getCargo());
                dto.setStatus(user.getStatus());
                dto.setInstituicaoNome(Optional.ofNullable(user.getInstituicao()) // <-- Mais seguro
                        .map(Instituicao::getNome)
                        .orElse(null));
                return dto;
            }).toList();

            return listDTO;
        }

        try {
            if (servicoUser.enumConverter(tipo) instanceof EnumCargo) {
                EnumCargo c = (EnumCargo) servicoUser.enumConverter(tipo);

                // Instanciação de usuários para servir como exemplo para query no BD
                UserAbstract g = new GestorInstituicao();
                g.setInstituicao(i);
                g.setCargo(c);

                Example<UserAbstract> e1 = Example.of(g);

                List<UserAbstract> lista = userRepository.findAll(e1);

                if (lista.isEmpty()) {
                    throw new RuntimeException("Nenhum usuario encontrado");
                }

                List<UpUserDTO> listDTO = lista.stream().map(user -> {
                    UpUserDTO dto = new UpUserDTO();
                    dto.setId(user.getId()); // <-- Adiciona o ID
                    dto.setNome(user.getNome());
                    dto.setEmail(user.getEmail());
                    dto.setTelefone(user.getTelefone());
                    dto.setDataNascimento(Optional.ofNullable(user.getDataNascimento())
                            .map(LocalDate::toString)
                            .orElse(null));
                    dto.setCpf(user.getCpf());
                    dto.setCargo(user.getCargo());
                    dto.setStatus(user.getStatus());
                    dto.setInstituicaoNome(Optional.ofNullable(user.getInstituicao()) // <-- Mais seguro
                            .map(Instituicao::getNome)
                            .orElse(null));
                    return dto;
                }).toList();

                return listDTO;
            } else {
                throw new RuntimeException("Parametro não corresponde a um cargo");
            }
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public List<InstituicaoResponseDTO> listarTodasInstituicoes() {
        // Busca todas as instituições (Query 1)
        List<Instituicao> instituicoes = instituicaoRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"));

        if (instituicoes.isEmpty()) return List.of();

        // Busca contagem de usuários agrupada por ID da instituição (Query 2)
        // Retorna lista de arrays [ID_INST, COUNT]
        Map<Long, Long> contagemUsuarios = userRepository.countTotalUsuariosPorInstituicao().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        // Busca nomes dos gestores ativos agrupados por ID (Query 3)
        // Retorna lista de arrays [ID_INST, NOME_GESTOR]
        // Se houver mais de um gestor (erro de dados), pega o primeiro (mergeFunction)
        Map<Long, String> gestoresMap = userRepository.findGestoresAtivosMap().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (String) row[1],
                        (existing, replacement) -> existing
                ));

        // Monta a resposta em memória (Rápido)
        return instituicoes.stream().map(inst -> {
            Long totalUsers = contagemUsuarios.getOrDefault(inst.getId(), 0L);
            String gestorNome = gestoresMap.getOrDefault(inst.getId(), "—");
            int totalConflitos = 0; // Mantido 0 conforme original (exigiria repo de conflitos)

            return new InstituicaoResponseDTO(
                    inst.getId(),
                    inst.getNome(),
                    inst.getSigla(),
                    inst.getCnpj(),
                    inst.getTelefone(),
                    inst.getEmail(),
                    inst.getAreaAtuacao(),
                    inst.getDescricao(),
                    inst.getStatus(),
                    gestorNome,
                    totalUsers.intValue(),
                    totalConflitos
            );
        }).collect(Collectors.toList());
    }


}