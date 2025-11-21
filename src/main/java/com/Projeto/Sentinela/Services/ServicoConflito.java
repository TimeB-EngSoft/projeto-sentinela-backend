package com.Projeto.Sentinela.Services;

import com.Projeto.Sentinela.Model.DTOs.ConflitoDTO;
import com.Projeto.Sentinela.Model.Entities.Conflito;
import com.Projeto.Sentinela.Model.Entities.GestorInstituicao;
import com.Projeto.Sentinela.Model.Entities.GestorSecretaria;
import com.Projeto.Sentinela.Model.Entities.UserAbstract;
import com.Projeto.Sentinela.Model.Enums.EnumFonte;
import com.Projeto.Sentinela.Model.Enums.EnumPrioridade;
import com.Projeto.Sentinela.Model.Enums.EnumStatusConflito;
import com.Projeto.Sentinela.Model.Enums.EnumTipoDeDenuncia;
import com.Projeto.Sentinela.Model.Repositories.ConflitoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServicoConflito {

    @Autowired
    private ConflitoRepository conflitoRepository;

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
    public Enum<?> ConflitoEnumConverter(String tipo){
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


}
