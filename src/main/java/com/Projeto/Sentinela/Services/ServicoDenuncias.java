package com.Projeto.Sentinela.Services;

import com.Projeto.Sentinela.DTOs.DenunciaDTO;
import com.Projeto.Sentinela.Entities.Denuncia;
import com.Projeto.Sentinela.Enums.EnumFonte;
import com.Projeto.Sentinela.Enums.EnumStatusDenuncia;
import com.Projeto.Sentinela.Enums.EnumTipoDeDenuncia;
import com.Projeto.Sentinela.Repositories.DenunciaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ServicoDenuncias {

    @Autowired
    private DenunciaRepository denunciaRepository;

    /**
     * Registra uma denúncia vinda de um formulário público (sem login).
     */
    public Denuncia registrarDenunciaExterna(DenunciaDTO dto) {
        Denuncia denuncia = new Denuncia();

        // --- Dados do denunciante ---
        denuncia.setNomeDenunciante(dto.getNomeDenunciante());
        denuncia.setEmailDenunciante(dto.getEmailDenunciante());
        denuncia.setTelefoneDenunciante(dto.getTelefoneDenunciante());
        denuncia.setCpfDenunciante(dto.getCpfDenunciante());
        denuncia.setFonteDenuncia(EnumFonte.FORMULARIO_PUBLICO);

        // --- Tipo da denúncia (conversão segura de texto para Enum) ---
        if (dto.getTipoDenuncia() != null) {
            denuncia.setTipoDenuncia(dto.getTipoDenuncia());
        } else if (dto.getTipoDenunciaTexto() != null) {
            // Permite que o front envie uma string (ex: "Desmatamento")
            String tipoNormalizado = dto.getTipoDenunciaTexto()
                    .trim()
                    .toUpperCase()
                    .replace(" ", "_")
                    .replace("-", "_");

            try {
                denuncia.setTipoDenuncia(EnumTipoDeDenuncia.valueOf(tipoNormalizado));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Tipo de denúncia inválido: " + dto.getTipoDenunciaTexto());
            }
        } else {
            throw new RuntimeException("O tipo de denúncia é obrigatório.");
        }

        // --- Informações gerais ---
        denuncia.setTituloDenuncia(dto.getTituloDenuncia());
        denuncia.setDescricaoDenuncia(dto.getDescricaoDenuncia());
        denuncia.setDescricaoPartesEnvolvidas(dto.getDescricaoPartesEnvolvidas());

        // --- Datas ---
        if (dto.getDataOcorrido() != null) {
            denuncia.setDataOcorrido(dto.getDataOcorrido());
        } else {
            denuncia.setDataOcorrido(LocalDateTime.now());
        }

        // --- Status inicial ---
        denuncia.setStatus(EnumStatusDenuncia.PENDENTE);

        // --- Persistência ---
        return denunciaRepository.save(denuncia);
    }

    /**
     * Busca uma denúncia pelo ID.
     */
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

    @Transactional
    public Denuncia atualizarDenuncia(long id, DenunciaDTO dto){

        Denuncia denuncia = denunciaRepository.findById(id).orElseThrow(()-> new RuntimeException("Denúncia não presente no sistema"));


        if(!dto.getDescricaoDenuncia().isEmpty()){
            denuncia.setDescricaoDenuncia(dto.getDescricaoDenuncia());
        }

        if(!dto.getDescricaoPartesEnvolvidas().isEmpty()){
            denuncia.setDescricaoPartesEnvolvidas(dto.getDescricaoPartesEnvolvidas());
        }

        if(!dto.getTipoDenuncia().equals(denuncia.getTipoDenuncia())){
            denuncia.setTipoDenuncia(dto.getTipoDenuncia());
        }

        if(!dto.getDataOcorrido().equals(denuncia.getDataOcorrido())){
            denuncia.setDataOcorrido(dto.getDataOcorrido());
        }

        if(!dto.getEmailDenunciante().isEmpty()){
            denuncia.setEmailDenunciante(dto.getEmailDenunciante());
        }

        if(!dto.getTelefoneDenunciante().isEmpty()){
            denuncia.setTelefoneDenunciante(dto.getTelefoneDenunciante());
        }

        if(dto.getCpfDenunciante().isEmpty()){
            denuncia.setCpfDenunciante(dto.getCpfDenunciante());
        }

        if(!dto.getTituloDenuncia().isEmpty()){
            denuncia.setTituloDenuncia(dto.getTituloDenuncia());
        }

        return denunciaRepository.save(denuncia);
    }
}
