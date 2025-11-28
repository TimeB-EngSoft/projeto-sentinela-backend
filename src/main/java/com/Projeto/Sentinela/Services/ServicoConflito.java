package com.Projeto.Sentinela.Services;

import com.Projeto.Sentinela.Model.DTOs.ConflitoDTO;
import com.Projeto.Sentinela.Model.Entities.Denuncia;
import com.Projeto.Sentinela.Model.Enums.EnumPrioridade;
import com.Projeto.Sentinela.Model.Enums.EnumStatusConflito;
import com.Projeto.Sentinela.Model.Repositories.DenunciaRepository;
import com.Projeto.Sentinela.Model.Entities.Conflito;
import com.Projeto.Sentinela.Model.Entities.Localizacao;
import com.Projeto.Sentinela.Model.Repositories.ConflitoRepository;
import com.Projeto.Sentinela.Model.Repositories.LocalizacaoRepository;
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
    @Autowired
    private LocalizacaoRepository localizacaoRepository;

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

        if (dto.getCep() != null) {
            Localizacao loc = salvarOuAtualizarLocalizacao(dto);
            conflito.setLocalizacao(loc);
        }

        return conflitoRepository.save(conflito);
    }

    @Transactional
    public void gerarConflitoAutomatico(Denuncia denuncia) {
        // Evita duplicidade
        if (denuncia.getStatus().toString().equals("APROVADA")) {
            // Verifica se já existe conflito para esta denúncia (lógica simples)
            // O ideal seria ter um campo 'conflitoGerado' na denúncia ou buscar no repo
        }

        Conflito conflito = new Conflito();
        conflito.setTituloConflito(denuncia.getTituloDenuncia());
        conflito.setDescricaoConflito(denuncia.getDescricaoDenuncia());
        conflito.setTipoConflito(denuncia.getTipoDenuncia());
        conflito.setFonteDenuncia(denuncia.getFonteDenuncia());
        conflito.setGruposVulneraveis(denuncia.getDescricaoPartesEnvolvidas());
        conflito.setParteReclamante(denuncia.getNomeDenunciante()); // Suposição inicial
        conflito.setParteReclamada("A investigar");

        conflito.setDenunciaOrigem(denuncia);
        conflito.setInstituicao(denuncia.getInstituicao());

        conflito.setDataInicio(denuncia.getDataOcorrido());
        conflito.setStatus(EnumStatusConflito.ATIVO);
        conflito.setPrioridade(EnumPrioridade.MEDIA); // Prioridade padrão

        // Reutiliza a localização da denúncia (já gerenciada)
        if (denuncia.getLocalizacao() != null) {
            conflito.setLocalizacao(denuncia.getLocalizacao());
        }

        conflitoRepository.save(conflito);
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

        if (dto.getCep() != null) {
            Localizacao loc = salvarOuAtualizarLocalizacao(dto);
            conflito.setLocalizacao(loc);
        }

        return conflitoRepository.save(conflito);
    }

    // Métod auxiliar para evitar repetição e garantir salvamento correto
    private Localizacao salvarOuAtualizarLocalizacao(ConflitoDTO dto) {
        Localizacao loc = localizacaoRepository.findById(dto.getCep())
                .orElse(new Localizacao());

        loc.setCep(dto.getCep());
        loc.setEstado(dto.getEstado());
        loc.setMunicipio(dto.getMunicipio());

        // AQUI ESTAVA FALTANDO:
        if (dto.getLatitude() != null) loc.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) loc.setLongitude(dto.getLongitude());

        String compl = "Bairro: " + dto.getBairro() + ", Rua: " + dto.getRua();
        if (dto.getNumero() != null) compl += ", Nº " + dto.getNumero();
        if (dto.getReferencia() != null) compl += " (" + dto.getReferencia() + ")";
        loc.setComplemento(compl);

        return localizacaoRepository.save(loc); // Garante que salva/atualiza no banco
    }
}
