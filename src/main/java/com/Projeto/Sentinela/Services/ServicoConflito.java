package com.Projeto.Sentinela.Services;

import com.Projeto.Sentinela.Model.DTOs.ConflitoDTO;
import com.Projeto.Sentinela.Model.Entities.Conflito;
import com.Projeto.Sentinela.Model.Repositories.ConflitoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ServicoConflito {

    @Autowired
    private ConflitoRepository conflitoRepository;

    public Conflito cadastarConflitoDiretamente(ConflitoDTO dto){



        if(true) {//placeholder para um método futuro que verifica a existência de um conflito no BD

            Conflito conflito = new  Conflito();

            //Dados Gerais

            conflito.setTituloConflito(dto.getTituloConflito());

            if (dto.getDataInicio() != null) {
                conflito.setDataInicio(dto.getDataInicio());
            } else {
                conflito.setDataInicio(LocalDateTime.now());
            }

            if (dto.getDataFim() != null) {
                conflito.setDataFim(dto.getDataFim());
            }

            conflito.setDescricaoConflito(dto.getDescricaoConflito());
            conflito.setParteReclamante(dto.getParteReclamante());
            conflito.setParteReclamada(dto.getParteReclamada());
            conflito.setGruposVulneraveis(dto.getGruposVulneraveis());
            conflito.setInstituicao(dto.getInstituicao());

            //Enuns
            conflito.setStatus(dto.getStatus());
            conflito.setPrioridade(dto.getPrioridade());

            return conflitoRepository.save(conflito);
        }else{
            throw new RuntimeException("Conflito já cadastrado");
        }
    }

}
