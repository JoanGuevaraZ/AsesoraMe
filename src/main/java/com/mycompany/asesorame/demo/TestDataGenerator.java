/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.mycompany.asesorame.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestDataGenerator {

    private static final Logger logger = LoggerFactory.getLogger(TestDataGenerator.class);
    private static final Random random = new Random();

    private static final String[] NOMBRES = {
            "Ana", "Luis", "Carlos", "María", "Anwi", "Laura", "Pedro", "Sofía", "Javier", "Isabel"
    };

    private static final String[] APELLIDOS = {
            "Gómez", "Pérez", "López", "Martínez", "Fernández", "Sánchez", "Ramírez", "Torres", "Gutiérrez", "Morales"
    };

    private static final String[] ESPECIALIDADES = {
            "Finanzas", "Marketing", "Recursos Humanos", "Tecnología", "Ventas", "Logística", "Atención al Cliente"
    };

    private static final String[] UBICACIONES = {
            "Madrid", "Barcelona", "Valencia", "Sevilla", "Bilbao", "Zaragoza", "Málaga", "Granada", "Alicante",
            "Córdoba"
    };

    public List<Asesor> generateTestAsesors(int count) {
        System.out.println("🔄 Generando " + count + " aserores de prueba...");

        List<Asesor> asesors = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            Asesor asesor = generateRandomAsesor((long) i);

            // VALIDAR que el empleado esté completo antes de agregarlo
            if (isAsesorComplete(asesor)) {
                asesors.add(asesor);

                if (i % 10 == 0) {
                    System.out.println("📊 Generados: " + i + " asesor");
                }
            } else {
                System.out.println("⚠ Asesor " + i + " incompleto, regenerando...");
                i--; // Reintentar este empleado
            }
        }

        System.out.println("✅ Generación completada: " + asesors.size() + " asesores generados.");
        return asesors;
    }

    private boolean isAsesorComplete(Asesor asesor) {
        return asesor != null &&
                asesor.getId() != null &&
                asesor.getNombre() != null && !asesor.getNombre().isEmpty() &&
                asesor.getApellido() != null && !asesor.getApellido().isEmpty() &&
                asesor.getTelefono() != null && !asesor.getTelefono().isEmpty() &&
                asesor.getEspecialidad() != null && !asesor.getEspecialidad().isEmpty() &&
                asesor.getUbicacion() != null && !asesor.getUbicacion().isEmpty();
    }

    private Asesor generateRandomAsesor(Long id) {
        Asesor asesor = new Asesor();

        try {
            // Asignar ID
            asesor.setId(id);

            // Generar nombre y apellido aleatorios
            String nombre = NOMBRES[random.nextInt(NOMBRES.length)];
            String apellido = APELLIDOS[random.nextInt(APELLIDOS.length)];
            asesor.setNombre(nombre);
            asesor.setApellido(apellido);

            // Generar teléfono aleatorio (simulado, ejemplo: "600-XXXX")
            String telefono = "600-" + (1000 + random.nextInt(9000));
            asesor.setTelefono(telefono);

            // Generar especialidad aleatoria
            String especialidad = ESPECIALIDADES[random.nextInt(ESPECIALIDADES.length)];
            asesor.setEspecialidad(especialidad);

            // Generar ubicación aleatoria
            String ubicacion = UBICACIONES[random.nextInt(UBICACIONES.length)];
            asesor.setUbicacion(ubicacion);

            return asesor;

        } catch (Exception e) {
            System.err.println("❌ Error generando asesor " + id + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Genera un archivo Excel con los datos de los asesores generados.
     */
    public void generateTestFile(String filePath, int normalAsesors) throws IOException {
        System.out.println("🔄 Generando archivo de prueba: " + filePath);
        System.out.println("📊 Asesores a generar: " + normalAsesors);

        List<Asesor> allAsesors = new ArrayList<>();

        // Generar asesores
        if (normalAsesors > 0) {
            List<Asesor> generatedAsesors = generateTestAsesors(normalAsesors);
            allAsesors.addAll(generatedAsesors);
        }

        // Escribir en archivo Excel
        ExcelService excelService = new ExcelService();
        excelService.writeAsesoresToExcel(allAsesors, filePath);

        System.out.println("✅ Archivo generado exitosamente: " + filePath + " (" + allAsesors.size() + " asesores)");
    }

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║     GENERADOR DE DATOS DE PRUEBA   ║");
        System.out.println("╚════════════════════════════════════╝");

        TestDataGenerator generator = new TestDataGenerator();

        try {
            // Crear directorio 'data' si no existe
            java.io.File dataDir = new java.io.File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
                System.out.println("📁 Directorio 'data' creado");
            }

            // Generar archivo con asesores de prueba
            System.out.println("\n--- Generando archivo de asesores ---");
            generator.generateTestFile("data/asesores_prueba.xlsx", 50);

            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║        GENERACIÓN COMPLETADA       ║");
            System.out.println("╚════════════════════════════════════╝");

        } catch (IOException e) {
            System.err.println("❌ Error al generar archivos: " + e.getMessage());
            e.printStackTrace();
        }
    }

}