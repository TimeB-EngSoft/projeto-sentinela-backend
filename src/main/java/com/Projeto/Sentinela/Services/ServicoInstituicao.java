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
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
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
        // 1. Validação: Verifica se já existe uma instituição com o mesmo nome.
        if (instituicaoRepository.findByNomeContainingIgnoreCase(dto.getNome()) != null) {
            throw new RuntimeException("Já existe uma instituição cadastrada com o nome: " + dto.getNome());
        }

        // 2. Mapeamento: Converte os dados do DTO para a entidade Instituicao.
        Instituicao novaInstituicao = new Instituicao();
        novaInstituicao.setNome(dto.getNome());
        novaInstituicao.setSigla(dto.getSigla());
        novaInstituicao.setCnpj(dto.getCnpj());
        novaInstituicao.setTelefone(dto.getTelefone());
        novaInstituicao.setEmail(dto.getEmail());
        novaInstituicao.setAreaAtuacao(dto.getAreaAtuacao());
        novaInstituicao.setDescricao(dto.getDescricao());

        // 3. Define valores padrão antes de salvar.
        novaInstituicao.setDataCadastro(LocalDateTime.now());
        novaInstituicao.setStatus(EnumStatusInstituicao.PENDENTE); // Toda nova instituição começa como pendente.

        // 4. Persistência: Chama o repositório para salvar a entidade no banco de dados.
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
        // 1. Busca a instituição no banco de dados. Se não encontrar, lança uma exceção.
        Instituicao instituicao = instituicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada com o ID: " + id));

        // 2. Atualiza cada campo apenas se um novo valor foi fornecido no DTO.
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
        if (dto.getStatus() != null) {
            instituicao.setStatus(dto.getStatus());
        }

        // 3. Salva a entidade atualizada no banco de dados.
        return instituicaoRepository.save(instituicao);
    }



    public List<UpUserDTO> listarUsuarios(long id,  String tipo){

        Instituicao i = instituicaoRepository.findById(id).orElseThrow(()-> new RuntimeException("Intituição não presente"));

        try {
            if (servicoUser.enumConverter(tipo) instanceof EnumCargo) {
                EnumCargo c = (EnumCargo) servicoUser.enumConverter(tipo);

                //Instanciação de usuários para servir como exemplo para querry no BD
                UserAbstract g = new GestorInstituicao();
                g.setInstituicao(i);
                g.setCargo(c);

                Example<UserAbstract> e1 = Example.of(g);

                List<UserAbstract> lista =userRepository.findAll(e1);

                if(lista.isEmpty()){
                    throw new RuntimeException("Nenhum usuario encontrado");
                }

                List<UpUserDTO> listDTO = lista.stream().map(user -> new UpUserDTO(
                        user.getNome(),
                        user.getEmail(),
                        user.getTelefone(),
                        user.getDataNascimento().toString(),
                        user.getCpf(),
                        user.getCargo(),
                        user.getStatus(),
                        user.getInstituicao().getNome()
                )).toList();

                return listDTO;
            }else{
                throw new RuntimeException("Parametro não corresponde a um cargo");
            }
        }catch(IllegalArgumentException ex){
            throw new RuntimeException(ex.getMessage());
        }
    }

    public List<InstituicaoResponseDTO> listarTodasInstituicoes() {
        return instituicaoRepository.findAll().stream()
                .map(inst -> new InstituicaoResponseDTO(
                        inst.getId(),
                        inst.getNome(),
                        inst.getSigla(),
                        inst.getCnpj(),
                        inst.getTelefone(),
                        inst.getEmail(),
                        inst.getAreaAtuacao(),
                        inst.getDescricao(),
                        inst.getStatus()
                ))
                .collect(toList());
    }


}