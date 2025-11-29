package com.Projeto.Sentinela.Services;

import com.Projeto.Sentinela.Model.DTOs.FiltroRelatorioDTO;
import com.Projeto.Sentinela.Model.Entities.Conflito;
import com.Projeto.Sentinela.Model.Entities.Denuncia;
import com.Projeto.Sentinela.Model.Enums.EnumFormato;
import com.Projeto.Sentinela.Model.Enums.EnumNivelAuditoria;
import com.Projeto.Sentinela.Model.Repositories.ConflitoRepository;
import com.Projeto.Sentinela.Model.Repositories.DenunciaRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ServicoRelatorios {

    @Autowired
    private ConflitoRepository conflitoRepository;

    @Autowired
    private DenunciaRepository denunciaRepository;

    @Autowired
    private ServicoAuditoria servicoAuditoria;

    public byte[] gerarRelatorio(FiltroRelatorioDTO filtro, String usuarioSolicitante) {
        byte[] arquivo = null;
        String formatoStr = filtro.getFormato().toUpperCase();

        // Lógica para definir qual gerador chamar
        try {
            EnumFormato formato = EnumFormato.valueOf(formatoStr);
            switch (formato) {
                case PDF:
                    arquivo = gerarPdf(filtro);
                    break;
                case XLSX:
                    arquivo = gerarExcel(filtro);
                    break;
                case CSV:
                default:
                    arquivo = gerarCsv(filtro);
                    break;
            }

            // --- LOG DE AUDITORIA ---
            servicoAuditoria.registrarLog(
                    usuarioSolicitante != null ? usuarioSolicitante : "Sistema",
                    "GERAR_RELATORIO",
                    "Relatórios",
                    "Gerou relatório do tipo " + filtro.getTipoRelatorio() + " em formato " + formato,
                    EnumNivelAuditoria.INFO,
                    "127.0.0.1"
            );
            // ------------------------

        } catch (Exception e) {
            servicoAuditoria.registrarLog(
                    usuarioSolicitante, "ERRO_RELATORIO", "Relatórios",
                    "Erro ao gerar: " + e.getMessage(), EnumNivelAuditoria.ERRO, "127.0.0.1"
            );
            throw new RuntimeException("Erro na geração do relatório: " + e.getMessage());
        }
        return arquivo;
    }

    private byte[] gerarCsv(FiltroRelatorioDTO filtro) {
        StringBuilder csv = new StringBuilder();
        csv.append("Tipo;ID;Título;Data;Status;Prioridade/Tipo;Local\n");

        // Buscar e filtrar Conflitos
        List<Conflito> conflitos = conflitoRepository.findAll();
        // (Idealmente usar Specification ou Query personalizada com os filtros do DTO)

        for (Conflito c : conflitos) {
            // Aplica filtro simples de data se fornecido
            if (filtro.getDataInicial() != null && c.getDataInicio().toLocalDate().isBefore(filtro.getDataInicial()))
                continue;
            if (filtro.getDataFinal() != null && c.getDataInicio().toLocalDate().isAfter(filtro.getDataFinal()))
                continue;

            csv.append(String.format("CONFLITO;%d;%s;%s;%s;%s;%s\n",
                    c.getId(), escapeCsv(c.getTituloConflito()),
                    c.getDataInicio() != null ? c.getDataInicio().format(DateTimeFormatter.ISO_DATE) : "-",
                    c.getStatus(), c.getPrioridade(),
                    escapeCsv(c.getLocalizacao() != null ? c.getLocalizacao().getMunicipio() : "-")
            ));
        }

        // Buscar e filtrar Denúncias
        List<Denuncia> denuncias = denunciaRepository.findAll();

        for (Denuncia d : denuncias) {
            if (filtro.getDataInicial() != null && d.getDataOcorrido().toLocalDate().isBefore(filtro.getDataInicial()))
                continue;

            csv.append(String.format("DENUNCIA;%d;%s;%s;%s;%s;%s\n",
                    d.getId(), escapeCsv(d.getTituloDenuncia()),
                    d.getDataOcorrido() != null ? d.getDataOcorrido().format(DateTimeFormatter.ISO_DATE) : "-",
                    d.getStatus(), d.getTipoDenuncia(),
                    escapeCsv(d.getLocalizacao() != null ? d.getLocalizacao().getMunicipio() : "-")
            ));
        }

        return csv.toString().getBytes();
    }

    private byte[] gerarExcel(FiltroRelatorioDTO filtro) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Relatório Sentinela");

            // Cabeçalho
            Row headerRow = sheet.createRow(0);
            String[] colunas = {"Tipo", "ID", "Título", "Data", "Status", "Prioridade/Tipo", "Local"};
            for (int i = 0; i < colunas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(colunas[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            int rowIdx = 1;
            List<Conflito> conflitos = conflitoRepository.findAll();
            for (Conflito c : conflitos) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue("CONFLITO");
                row.createCell(1).setCellValue(c.getId());
                row.createCell(2).setCellValue(c.getTituloConflito());
                row.createCell(3).setCellValue(c.getDataInicio() != null ? c.getDataInicio().format(DateTimeFormatter.ISO_DATE) : "");
                row.createCell(4).setCellValue(c.getStatus().toString());
                row.createCell(5).setCellValue(c.getPrioridade().toString());
                row.createCell(6).setCellValue(c.getLocalizacao() != null ? c.getLocalizacao().getMunicipio() : "-");
            }

            List<Denuncia> denuncias = denunciaRepository.findAll();
            for (Denuncia d : denuncias) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue("DENÚNCIA");
                row.createCell(1).setCellValue(d.getId());
                row.createCell(2).setCellValue(d.getTituloDenuncia());
                row.createCell(3).setCellValue(d.getDataOcorrido() != null ? d.getDataOcorrido().format(DateTimeFormatter.ISO_DATE) : "");
                row.createCell(4).setCellValue(d.getStatus().toString());
                row.createCell(5).setCellValue(d.getTipoDenuncia().toString());
                row.createCell(6).setCellValue(d.getLocalizacao() != null ? d.getLocalizacao().getMunicipio() : "-");
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private byte[] gerarPdf(FiltroRelatorioDTO filtro) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            // Título
            com.lowagie.text.Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph titulo = new Paragraph("Relatório Projeto Sentinela", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(new Paragraph("\n"));

            // Tabela
            PdfPTable table = new PdfPTable(5); // 5 colunas
            table.setWidthPercentage(100);

            // Cabeçalhos
            String[] headers = {"Tipo", "Título", "Data", "Status", "Local"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                table.addCell(cell);
            }

            List<Conflito> conflitos = conflitoRepository.findAll();
            for (Conflito c : conflitos) {
                table.addCell("Conflito");
                table.addCell(c.getTituloConflito());
                table.addCell(c.getDataInicio() != null ? c.getDataInicio().format(DateTimeFormatter.ISO_DATE) : "-");
                table.addCell(c.getStatus().toString());
                table.addCell(c.getLocalizacao() != null ? c.getLocalizacao().getMunicipio() : "-");
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro PDF: " + e.getMessage());
        }
    }

    private String escapeCsv(String data) {
        if (data == null) return "";
        return "\"" + data.replace("\"", "\"\"") + "\"";
    }
}
