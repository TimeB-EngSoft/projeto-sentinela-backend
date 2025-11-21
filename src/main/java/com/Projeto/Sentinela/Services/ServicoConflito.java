package com.Projeto.Sentinela.Services;

import com.Projeto.Sentinela.Model.DTOs.ConflitoDTO;
import com.Projeto.Sentinela.Model.Entities.*;
import com.Projeto.Sentinela.Model.Enums.EnumFonte;
import com.Projeto.Sentinela.Model.Enums.EnumPrioridade;
import com.Projeto.Sentinela.Model.Enums.EnumStatusConflito;
import com.Projeto.Sentinela.Model.Enums.EnumTipoDeDenuncia;
import com.Projeto.Sentinela.Model.Repositories.ConflitoRepository;
import com.Projeto.Sentinela.Model.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class ServicoConflito {

    @Autowired
    private ConflitoRepository conflitoRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Cadastra um conflito diretamente (sem depender de denúncia).
     */
    public Conflito cadastarConflitoDiretamente(ConflitoDTO dto) {

        // (futuro) verificação de conflito duplicado
        Conflito conflito = new Conflito();

        // Dados gerais
        conflito.setTituloConflito(dto.getTituloConflito());
        conflito.setDescricaoConflito(dto.getDescricaoConflito());
        conflito.setParteReclamante(dto.getParteReclamante());
        conflito.setParteReclamada(dto.getParteReclamada());
        conflito.setGruposVulneraveis(dto.getGruposVulneraveis());
        conflito.setInstituicao(dto.getInstituicao());
        conflito.setDenunciaOrigem(dto.getDenunciaOrigem());

        // Datas
        conflito.setDataInicio(dto.getDataInicio() != null ? dto.getDataInicio() : LocalDateTime.now());
        conflito.setDataFim(dto.getDataFim());

        // Enums
        conflito.setStatus(dto.getStatus());
        conflito.setPrioridade(dto.getPrioridade());
        conflito.setFonteDenuncia(dto.getFonteDenuncia());
        conflito.setTipoConflito(dto.getTipoConflito());

        return conflitoRepository.save(conflito);
    }

    /**
     * Lista todos os conflitos registrados.
     */
    public List<Conflito> listarConflitos() {
        List<Conflito> conflitos = conflitoRepository.findAll();
        if (conflitos.isEmpty()) {
            throw new RuntimeException("Nenhum conflito encontrado.");
        }
        return conflitos;
    }

    /**
     * Busca um conflito pelo ID.
     */
    public Conflito buscarPorId(Long id) {
        return conflitoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conflito não encontrado com ID: " + id));
    }

    /**
     * Atualiza os dados de um conflito existente.
     */
    @Transactional
    public Conflito atualizarConflito(Long id, ConflitoDTO dto) {
        Conflito conflito = buscarPorId(id);

        if (StringUtils.hasText(dto.getTituloConflito())) {
            conflito.setTituloConflito(dto.getTituloConflito());
        }

        if (StringUtils.hasText(dto.getDescricaoConflito())) {
            conflito.setDescricaoConflito(dto.getDescricaoConflito());
        }

        if (StringUtils.hasText(dto.getParteReclamante())) {
            conflito.setParteReclamante(dto.getParteReclamante());
        }

        if (StringUtils.hasText(dto.getParteReclamada())) {
            conflito.setParteReclamada(dto.getParteReclamada());
        }

        if (StringUtils.hasText(dto.getGruposVulneraveis())) {
            conflito.setGruposVulneraveis(dto.getGruposVulneraveis());
        }

        if (dto.getDataInicio() != null && !dto.getDataInicio().equals(conflito.getDataInicio())) {
            conflito.setDataInicio(dto.getDataInicio());
        }

        if (dto.getDataFim() != null && !dto.getDataFim().equals(conflito.getDataFim())) {
            conflito.setDataFim(dto.getDataFim());
        }

        if (dto.getStatus() != null) {
            conflito.setStatus(dto.getStatus());
        }

        if (dto.getPrioridade() != null) {
            conflito.setPrioridade(dto.getPrioridade());
        }

        if (dto.getInstituicao() != null) {
            conflito.setInstituicao(dto.getInstituicao());
        }

        if (dto.getDenunciaOrigem() != null) {
            conflito.setDenunciaOrigem(dto.getDenunciaOrigem());
        }

        return conflitoRepository.save(conflito);
    }

    /**
     * Dada uma string recebida que seja compatível, case-insensitive, a qualquer um dos enums de conflito
     * é retornado o enum correspondente
     * */
    public Enum<?> conflitoEnumConverter(String tipo){
        try{
            EnumStatusConflito enumStatusConflito = EnumStatusConflito.valueOf(tipo);
            return enumStatusConflito;
        }catch(Exception e){
            try{
                EnumFonte fonte = EnumFonte.valueOf(tipo);
                return fonte;
            }catch(Exception ex){
                try{
                    EnumTipoDeDenuncia tipoDeDenuncia = EnumTipoDeDenuncia.valueOf(tipo);
                    return tipoDeDenuncia;
                }catch(Exception ex2){
                    try{
                        EnumPrioridade enumPrioridade = EnumPrioridade.valueOf(tipo);
                        return  enumPrioridade;
                    }catch(Exception ex3){
                        throw new RuntimeException("Parâmetro não corresponde a nenhum tipo de conflito.");
                    }
                }
            }
        }
    }

    /**
     * Filtra conflitos pelos seus enums
     * */
    public List<ConflitoDTO> filtroConflitos(long id, List<String> filtros){


        Optional<UserAbstract> u =userRepository.findById(id);

        boolean ok = u.isPresent();
        if(!ok){
            throw new RuntimeException("ID não corresponde a nenhum usuário");
        }
        UserAbstract user = u.get();

        boolean cargoNaSec = true;

        switch(user.getCargo()){
            case GESTOR_SECRETARIA -> {
                cargoNaSec = true; break;
            }
            case GESTOR_INSTITUICAO -> {
                cargoNaSec = false; break;
            }
            case USUARIO_INSTITUICAO -> {cargoNaSec = false; break;}
            case USUARIO_SECRETARIA -> { cargoNaSec = true; break; }
            case SECRETARIA -> {cargoNaSec = true; break;}
        }

        var enumFiltros = filtros.stream().map(this::conflitoEnumConverter)
                .map(e-> (Enum<?>) e).toList();

        List<Conflito> conflitos = listarConflitos();

        if(cargoNaSec){//garante que um user de secretaria possa filtrar por todos os conflitos do sistema

            List<Conflito> conflitosFiltrados = conflitos.stream()
                    .filter(conflito -> enumFiltros.stream().allMatch(enumFiltro ->
                            enumFiltro.equals(conflito.getStatus()) ||
                                    enumFiltro.equals(conflito.getFonteDenuncia()) ||
                                    enumFiltro.equals(conflito.getTipoConflito()) ||
                                    enumFiltro.equals(conflito.getPrioridade())))
                    .toList();
        }else{//garante que um user de instituição possa filtrar apenas os conflitos de sua instituição
            Instituicao inst = user.getInstituicao();
            List<Conflito> conflitosFiltrados = conflitos.stream()
                    .filter(conflito -> conflito.getInstituicao().equals(inst))
                    .filter(conflito -> enumFiltros.stream().allMatch(enumFiltro ->
                            enumFiltro.equals(conflito.getStatus()) ||
                                    enumFiltro.equals(conflito.getFonteDenuncia()) ||
                                    enumFiltro.equals(conflito.getTipoConflito()) ||
                                    enumFiltro.equals(conflito.getPrioridade())))
                    .toList();
        }

        if(cargoNaSec && conflitos.isEmpty()){
            throw new RuntimeException("Nenhum conflito registrado");
        }

        if(!cargoNaSec && conflitos.isEmpty()){
            throw new RuntimeException("Nenhum conflito registrado para a instituição: " + user.getInstituicao().getNome());
        }


        //converte para DTO a list de conflitos
        return conflitos.stream().map(conflito ->{
            ConflitoDTO dto = new ConflitoDTO();
            dto.setId(conflito.getId());
            dto.setFonteDenuncia(conflito.getFonteDenuncia());
            dto.setTituloConflito(conflito.getTituloConflito());
            dto.setTipoConflito(conflito.getTipoConflito());
            dto.setDataInicio(conflito.getDataInicio());
            dto.setDataFim(conflito.getDataFim());
            dto.setDescricaoConflito(conflito.getDescricaoConflito());
            dto.setParteReclamante(conflito.getParteReclamante());
            dto.setParteReclamada(conflito.getParteReclamada());
            dto.setGruposVulneraveis(conflito.getGruposVulneraveis());
            dto.setStatus(conflito.getStatus());
            dto.setPrioridade(conflito.getPrioridade());
            dto.setInstituicao(conflito.getInstituicao());
            dto.setDenunciaOrigem(conflito.getDenunciaOrigem());
            return  dto;
        })
                .toList();
    }

}
