package com.Projeto.Sentinela.Services;

import com.Projeto.Sentinela.Model.Entities.LogAuditoria;
import com.Projeto.Sentinela.Model.Enums.EnumNivelAuditoria;
import com.Projeto.Sentinela.Model.Repositories.LogAuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServicoAuditoria {

    @Autowired
    private LogAuditoriaRepository logRepository;

    public void registrarLog(String usuario, String acao, String modulo, String detalhes, EnumNivelAuditoria nivel, String ip) {
        LogAuditoria log = new LogAuditoria(usuario, acao, modulo, detalhes, nivel, ip);
        logRepository.save(log);
    }

    public List<LogAuditoria> listarLogs() {
        return logRepository.findAllByOrderByDataHoraDesc();
    }

    public Map<String, Object> obterEstatisticas() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalLogs", logRepository.count());
        stats.put("acoesHoje", logRepository.countByDataHoraAfter(LocalDate.now().atStartOfDay()));
        stats.put("avisos", logRepository.countByNivel(EnumNivelAuditoria.AVISO));
        stats.put("erros", logRepository.countByNivel(EnumNivelAuditoria.ERRO));

        stats.put("topUsuarios", logRepository.findTopActiveUsers());
        stats.put("topAcoes", logRepository.findTopActions());

        return stats;
    }
}

