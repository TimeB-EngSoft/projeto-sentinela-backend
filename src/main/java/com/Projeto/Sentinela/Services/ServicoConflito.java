package com.Projeto.Sentinela.Services;

import com.Projeto.Sentinela.Model.DTOs.ConflitoDTO;
import com.Projeto.Sentinela.Model.Entities.Denuncia;
import com.Projeto.Sentinela.Model.Repositories.DenunciaRepository;
import com.Projeto.Sentinela.Model.Entities.Conflito;
import com.Projeto.Sentinela.Model.Entities.Localizacao;
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
    @Autowired
    private DenunciaRepository denunciaRepository;

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

        // LÓGICA DE HERANÇA OU CRIAÇÃO DE LOCALIZAÇÃO
        if (dto.getCep() != null) {
            // 1. Prioridade: Endereço novo vindo no DTO
            Localizacao loc = new Localizacao();
            loc.setCep(dto.getCep());
            loc.setEstado(dto.getEstado());
            loc.setMunicipio(dto.getMunicipio());

            // Montar complemento se necessário (igual ao ServicoDenuncias)
            String compl = "Bairro: " + dto.getBairro() + ", Rua: " + dto.getRua();
            if (dto.getNumero() != null) compl += ", Nº " + dto.getNumero();
            if (dto.getReferencia() != null) compl += " (" + dto.getReferencia() + ")";
            loc.setComplemento(compl);

            conflito.setLocalizacao(loc);

        } else if (dto.getDenunciaOrigem() != null && dto.getDenunciaOrigem().getId() != null) {
            // 2. Fallback: Herdar endereço da denúncia de origem

            // BUSCAR A DENÚNCIA COMPLETA NO BANCO
            Denuncia denunciaFull = denunciaRepository.findById(dto.getDenunciaOrigem().getId())
                    .orElse(null);

            if (denunciaFull != null && denunciaFull.getLocalizacao() != null) {
                Localizacao locOrigem = denunciaFull.getLocalizacao();

                // Copiar dados para uma NOVA localização (para não compartilhar a mesma referência de objeto)
                Localizacao novaLoc = new Localizacao();
                novaLoc.setCep(locOrigem.getCep());
                novaLoc.setEstado(locOrigem.getEstado());
                novaLoc.setMunicipio(locOrigem.getMunicipio());
                novaLoc.setLatitude(locOrigem.getLatitude());
                novaLoc.setLongitude(locOrigem.getLongitude());
                novaLoc.setComplemento(locOrigem.getComplemento());

                conflito.setLocalizacao(novaLoc);
            }
        }

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
}
