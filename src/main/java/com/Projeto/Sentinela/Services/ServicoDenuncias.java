package com.Projeto.Sentinela.Services;

import com.Projeto.Sentinela.Model.DTOs.DenunciaDTO;
import com.Projeto.Sentinela.Model.Entities.Denuncia;
import com.Projeto.Sentinela.Model.Entities.Instituicao;
import com.Projeto.Sentinela.Model.Entities.Localizacao;
import com.Projeto.Sentinela.Model.Enums.EnumFonte;
import com.Projeto.Sentinela.Model.Enums.EnumStatusDenuncia;
import com.Projeto.Sentinela.Model.Enums.EnumTipoDeDenuncia;
import com.Projeto.Sentinela.Model.Repositories.DenunciaRepository;
import com.Projeto.Sentinela.Model.Repositories.InstituicaoRepository;
import com.Projeto.Sentinela.Model.Repositories.LocalizacaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServicoDenuncias {

    @Autowired
    private DenunciaRepository denunciaRepository;
    @Autowired
    private InstituicaoRepository instituicaoRepository;
    @Autowired
    private LocalizacaoRepository localizacaoRepository;
    @Autowired
    private ServicoConflito servicoConflito;

    /**
     * Registra uma denúncia vinda de um formulário público (sem login).
     */
    public Denuncia registrarDenunciaExterna(DenunciaDTO dto) {
        Denuncia denuncia = new Denuncia();

        // Dados Pessoais
        denuncia.setNomeDenunciante(dto.getNomeDenunciante());
        denuncia.setEmailDenunciante(dto.getEmailDenunciante());
        denuncia.setTelefoneDenunciante(dto.getTelefoneDenunciante());
        denuncia.setCpfDenunciante(dto.getCpfDenunciante());

        // Lógica de Vínculo e Fonte
        if (dto.getInstituicaoId() != null) {
            // Caso 1: Usuário de Instituição (Gestor ou Operador)
            Instituicao inst = instituicaoRepository.findById(dto.getInstituicaoId())
                    .orElseThrow(() -> new RuntimeException("Instituição informada não encontrada."));
            denuncia.setInstituicao(inst);
            denuncia.setFonteDenuncia(EnumFonte.USUARIO_INTERNO);
        } else {
            // Caso 2: Secretaria (Interno sem vínculo) OU Público (Externo)
            // Se o front mandou a fonte (ex: USUARIO_INTERNO), respeitamos. Se não, é Público.
            if (dto.getFonteDenuncia() != null) {
                denuncia.setFonteDenuncia(dto.getFonteDenuncia());
            } else {
                denuncia.setFonteDenuncia(EnumFonte.FORMULARIO_PUBLICO);
            }
            // Deixa instituicao como null
        }

        // Tipo de Denúncia
        if (dto.getTipoDenuncia() != null) {
            denuncia.setTipoDenuncia(dto.getTipoDenuncia());
        } else if (dto.getTipoDenunciaTexto() != null) {
            try {
                String normalizado = dto.getTipoDenunciaTexto().trim().toUpperCase().replace(" ", "_").replace("-", "_");
                denuncia.setTipoDenuncia(EnumTipoDeDenuncia.valueOf(normalizado));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Tipo inválido.");
            }
        } else {
            throw new RuntimeException("Tipo é obrigatório.");
        }

        // Detalhes
        denuncia.setTituloDenuncia(dto.getTituloDenuncia());
        denuncia.setDescricaoDenuncia(dto.getDescricaoDenuncia());
        denuncia.setDescricaoPartesEnvolvidas(dto.getDescricaoPartesEnvolvidas());
        denuncia.setDataOcorrido(dto.getDataOcorrido() != null ? dto.getDataOcorrido() : LocalDateTime.now());
        denuncia.setStatus(EnumStatusDenuncia.PENDENTE);

        // Localização (Persistência)
        if (dto.getCep() != null) {
            // Busca se já existe para não duplicar ou dar erro se for PK
            Localizacao loc = localizacaoRepository.findById(dto.getCep())
                    .orElse(new Localizacao());

            loc.setCep(dto.getCep());
            loc.setEstado(dto.getEstado());
            loc.setMunicipio(dto.getMunicipio());

            // SALVAR AS COORDENADAS
            if (dto.getLatitude() != null) loc.setLatitude(dto.getLatitude());
            if (dto.getLongitude() != null) loc.setLongitude(dto.getLongitude());

            String compl = "Bairro: " + dto.getBairro() + ", Rua: " + dto.getRua();
            if (dto.getNumero() != null) compl += ", Nº " + dto.getNumero();
            if (dto.getReferencia() != null) compl += " (" + dto.getReferencia() + ")";
            loc.setComplemento(compl);

            // Salva explicitamente a localização antes de associar
            localizacaoRepository.save(loc);
            denuncia.setLocalizacao(loc);
        }

        return denunciaRepository.save(denuncia);

    }


    public Denuncia buscarPorId(Long id) {
        return denunciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Denúncia não encontrada com ID: " + id));
    }

    /**
     * Aprova ou arquiva uma denúncia existente.
     */
    public Denuncia aprovarDenuncia(Long id, boolean aprovada) {
        Denuncia denuncia = buscarPorId(id);
        denuncia.serAprovada(aprovada);
        return denunciaRepository.save(denuncia);
    }

    /**
    * verifica se uma string é null ou está em branco
    */
    public boolean vs(String s) {
        return StringUtils.hasText(s);
    }

    @Transactional
    public Denuncia atualizarDenuncia(long id, DenunciaDTO dto){

        Denuncia denuncia = buscarPorId(id);


        if(vs(dto.getDescricaoDenuncia())){
            denuncia.setDescricaoDenuncia(dto.getDescricaoDenuncia());
        }

        if(vs(dto.getDescricaoPartesEnvolvidas())){
            denuncia.setDescricaoPartesEnvolvidas(dto.getDescricaoPartesEnvolvidas());
        }

        if(dto.getTipoDenuncia() != null){
            denuncia.setTipoDenuncia(dto.getTipoDenuncia());
        }

        if(dto.getDataOcorrido() != null && !dto.getDataOcorrido().equals(denuncia.getDataOcorrido())  ){
            denuncia.setDataOcorrido(dto.getDataOcorrido());
        }

        if(dto.getStatusDenuncia() != null){
            denuncia.setStatus(dto.getStatusDenuncia());
        }

        if(vs(dto.getEmailDenunciante())){
            denuncia.setEmailDenunciante(dto.getEmailDenunciante());
        }

        if(vs(dto.getTelefoneDenunciante())){
            denuncia.setTelefoneDenunciante(dto.getTelefoneDenunciante());
        }

        if(vs(dto.getCpfDenunciante())){
            denuncia.setCpfDenunciante(dto.getCpfDenunciante());
        }

        if(vs(dto.getTituloDenuncia())){
            denuncia.setTituloDenuncia(dto.getTituloDenuncia());
        }

        return denunciaRepository.save(denuncia);
    }

    public List<Denuncia> visualizarDenuncias(){
        List<Denuncia> d = denunciaRepository.findAll();
        if(d.isEmpty()){
            throw new RuntimeException("Nenhum denuncia encontrada");
        }
        return d;
    }

}
