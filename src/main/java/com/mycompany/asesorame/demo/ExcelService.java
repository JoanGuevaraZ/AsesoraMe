/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.asesorame.demo;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import static org.apache.poi.ss.usermodel.CellType.BOOLEAN;
import static org.apache.poi.ss.usermodel.CellType.FORMULA;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

public class ExcelService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);

    private static final int COL_ID = 0;
    private static final int COL_NOMBRE = 1;
    private static final int COL_APELLIDO = 2;
    private static final int COL_EMAIL = 3;
    private static final int COL_TELEFONO = 4;
    private static final int COL_ESPECIALIDAD = 5;
    private static final int COL_UBICACION = 6;

    public List<Asesor> readAsesoresFromExcel(String filePath) throws IOException {
        logger.info("Iniciando lectura de archivo Excel: {}", filePath);

        List<Asesor> asesores = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
                Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            logger.debug("Leyendo hoja: {} con {} filas", sheet.getSheetName(), sheet.getLastRowNum());

            // Saltar la fila de encabezados (fila 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                try {
                    Asesor asesor = extractAsesorFromRow(row);
                    if (asesor != null) {
                        asesores.add(asesor);
                        logger.debug("Asesor leído: {}", asesor.getId());
                    }
                } catch (Exception e) {
                    logger.error("Error al procesar fila {}: {}", i, e.getMessage());
                }
            }

            logger.info("Se leyeron {} asesores del archivo {}", asesores.size(), filePath);
        }

        return asesores;
    }

    private Asesor extractAsesorFromRow(Row row) {
        try {
            Asesor asesor = new Asesor();

            Cell idCell = row.getCell(COL_ID);
            if (idCell != null && idCell.getCellType() == CellType.NUMERIC) {
                asesor.setId((long) idCell.getNumericCellValue());
            }

            Cell nombreCell = row.getCell(COL_NOMBRE);
            if (nombreCell != null && nombreCell.getCellType() == CellType.STRING) {
                asesor.setNombre(nombreCell.getStringCellValue().trim());
            }

            Cell apellidoCell = row.getCell(COL_APELLIDO);
            if (apellidoCell != null && apellidoCell.getCellType() == CellType.STRING) {
                asesor.setApellido(apellidoCell.getStringCellValue().trim());
            }

            Cell emailCell = row.getCell(COL_EMAIL);
            if (emailCell != null && emailCell.getCellType() == CellType.STRING) {
                asesor.setEmail(emailCell.getStringCellValue().trim());
            }

            Cell telefonoCell = row.getCell(COL_TELEFONO);
            if (telefonoCell != null && telefonoCell.getCellType() == CellType.STRING) {
                asesor.setTelefono(telefonoCell.getStringCellValue().trim());
            }

            Cell especialidadCell = row.getCell(COL_ESPECIALIDAD);
            if (especialidadCell != null && especialidadCell.getCellType() == CellType.STRING) {
                asesor.setEspecialidad(especialidadCell.getStringCellValue().trim());
            }

            Cell ubicacionCell = row.getCell(COL_UBICACION);
            if (ubicacionCell != null && ubicacionCell.getCellType() == CellType.STRING) {
                asesor.setUbicacion(ubicacionCell.getStringCellValue().trim());
            }

            return asesor;

        } catch (Exception e) {
            logger.error("Error al extraer asesor de la fila: {}", e.getMessage());
            return null;
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue()).trim();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue()).trim();
            case FORMULA:
                return cell.getCellFormula().trim();
            default:
                return "";
        }
    }

    public void writeAsesoresToExcel(List<Asesor> asesores, String filePath) throws IOException {
        logger.info("Iniciando escritura de {} asesores a archivo: {}", asesores.size(), filePath);

        try (Workbook workbook = new XSSFWorkbook()) {

            // Crear hoja de asesores
            Sheet asesorSheet = workbook.createSheet("Asesores");
            createAsesorSheet(workbook, asesorSheet, asesores);

            // Escribir archivo
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
                logger.info("Archivo Excel creado exitosamente: {}", filePath);
            }
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();

        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 12);

        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    private void createAsesorSheet(Workbook workbook, Sheet sheet, List<Asesor> asesores) {
        // Crear estilo para encabezados
        CellStyle headerStyle = createHeaderStyle(workbook);

        // Crear fila de encabezados
        Row headerRow = sheet.createRow(0);
        String[] headers = { "ID", "Nombre", "Apellido", "Email", "Teléfono", "Especialidad", "Ubicación" };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (Asesor asesor : asesores) {
            Row row = sheet.createRow(rowNum++);

            Cell idCell = row.createCell(0);
            idCell.setCellValue(asesor.getId() != null ? asesor.getId() : 0);
            idCell.setCellStyle(headerStyle);

            Cell nombreCell = row.createCell(1);
            nombreCell.setCellValue(asesor.getNombre() != null ? asesor.getNombre() : "");
            nombreCell.setCellStyle(headerStyle);

            Cell apellidoCell = row.createCell(2);
            apellidoCell.setCellValue(asesor.getApellido() != null ? asesor.getApellido() : "");
            apellidoCell.setCellStyle(headerStyle);

            Cell emailCell = row.createCell(3);
            emailCell.setCellValue(asesor.getEmail() != null ? asesor.getEmail() : "");
            emailCell.setCellStyle(headerStyle);

            Cell telefonoCell = row.createCell(4);
            telefonoCell.setCellValue(asesor.getTelefono() != null ? asesor.getTelefono() : "");
            telefonoCell.setCellStyle(headerStyle);

            Cell especialidadCell = row.createCell(5);
            especialidadCell.setCellValue(asesor.getEspecialidad() != null ? asesor.getEspecialidad() : "");
            especialidadCell.setCellStyle(headerStyle);

            Cell ubicacionCell = row.createCell(6);
            ubicacionCell.setCellValue(asesor.getUbicacion() != null ? asesor.getUbicacion() : "");
            ubicacionCell.setCellStyle(headerStyle);

        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        System.out.println(" 📊 Hoja de Asesores creada con " + asesores.size() + " registros.");
    }
}
