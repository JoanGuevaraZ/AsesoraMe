
package com.mycompany.asesorame.demo;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AsesorProcessorApp {
    
private static final Logger logger = LoggerFactory.getLogger(AsesorProcessorApp.class);
    
    // Servicios
    private final ExcelService excelService;
    private final AsesorValidator validator;
    private final StatisticsService statisticsService;
    
    public AsesorProcessorApp() {
        this.excelService = new ExcelService();
        this.validator = new AsesorValidator();
        this.statisticsService = new StatisticsService();
        
        logger.info("AsesorProcessorApp inicializada");
        logger.info("Timestamp: {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
    

    public void processAsesorFile(String inputFilePath, String outputFilePath) {
        
        // Usar Guava Stopwatch para medir tiempo de ejecución
        Stopwatch totalStopwatch = Stopwatch.createStarted();
        
        logger.info("=== INICIANDO PROCESAMIENTO DE ASESORES ===");
        logger.info("Archivo de entrada: {}", inputFilePath);
        logger.info("Archivo de salida: {}", outputFilePath);
        
        try {
            // VERIFICAR Y CREAR ARCHIVO DE ENTRADA SI NO EXISTE
            if (!ensureInputFileExists(inputFilePath)) {
                logger.error("No se pudo crear o encontrar el archivo de entrada");
                return;
            }
            
            // PASO 1: Leer asesores del Excel
            logger.info("--- PASO 1: Leyendo asesores del archivo Excel ---");
            Stopwatch readStopwatch = Stopwatch.createStarted();
            
            List<Asesor> asesores = excelService.readAsesoresFromExcel(inputFilePath);
            
            readStopwatch.stop();
            logger.info("Lectura completada en {} ms. {} asesores leídos", 
                       readStopwatch.elapsed(TimeUnit.MILLISECONDS), asesores.size());
            
            if (asesores.isEmpty()) {
                logger.warn("No se encontraron asesores en el archivo");
                return;
            }
            
            // PASO 2: Validar y normalizar asesores
            logger.info("--- PASO 2: Validando y normalizando asesores ---");
            Stopwatch validationStopwatch = Stopwatch.createStarted();
            
            List<Asesor> validAsesores = validateAndNormalizeAsesores(asesores);
            
            validationStopwatch.stop();
            logger.info("Validación completada en {} ms. {} asesores válidos de {} totales", 
                       validationStopwatch.elapsed(TimeUnit.MILLISECONDS), 
                       validAsesores.size(), asesores.size());
            
            // PASO 3: Generar estadísticas
            logger.info("--- PASO 3: Generando estadísticas ---");
            Stopwatch statsStopwatch = Stopwatch.createStarted();
            
            generateStatistics(validAsesores);
            
            statsStopwatch.stop();
            logger.info("Estadísticas generadas en {} ms", 
                       statsStopwatch.elapsed(TimeUnit.MILLISECONDS));
            
            // PASO 4: Escribir archivo de salida
            logger.info("--- PASO 4: Escribiendo archivo de salida ---");
            Stopwatch writeStopwatch = Stopwatch.createStarted();
            
            excelService.writeAsesoresToExcel(validAsesores, outputFilePath);
            
            writeStopwatch.stop();
            logger.info("Escritura completada en {} ms", 
                       writeStopwatch.elapsed(TimeUnit.MILLISECONDS));
            
            // PASO 5: Demostrar filtros avanzados
            logger.info("--- PASO 5: Demostrando filtros avanzados ---");
            demonstrateAdvancedFiltering(validAsesores);
            
        } catch (IOException e) {
            logger.error("Error de E/S durante el procesamiento: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado durante el procesamiento: {}", e.getMessage(), e);
        } finally {
            totalStopwatch.stop();
            logger.info("=== PROCESAMIENTO COMPLETADO EN {} ms ===", 
                       totalStopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }
    
    /**
     * NUEVO MÉTODO: Asegura que el archivo de entrada existe
     */
    private boolean ensureInputFileExists(String inputFilePath) {
        File inputFile = new File(inputFilePath);
        
        // Si el archivo existe, todo bien
        if (inputFile.exists()) {
            logger.info("✅ Archivo de entrada encontrado: {}", inputFilePath);
            return true;
        }
        
        // Si no existe, intentar generarlo
        logger.warn("⚠️ Archivo de entrada no encontrado: {}", inputFilePath);
        logger.info("🔄 Generando archivo de datos de prueba automáticamente...");
        
        try {
            // Crear directorio si no existe
            File parentDir = inputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
                logger.info("📁 Directorio creado: {}", parentDir.getPath());
            }
            
            // Generar archivo con datos de prueba
            TestDataGenerator generator = new TestDataGenerator();
            generator.generateTestFile(inputFilePath, 50); // 50 asesores de prueba
            
            logger.info("✅ Archivo de entrada generado exitosamente");
            return true;
            
        } catch (Exception e) {
            logger.error("❌ Error al generar archivo de entrada: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Valida y normaliza la lista de asesores
     */
    private List<Asesor> validateAndNormalizeAsesores(List<Asesor> asesores) {
        logger.debug("Iniciando validación de {} asesores", asesores.size());
        
        List<Asesor> validAsesores = asesores.stream()
            .peek(validator::normalizeAsesor) // Normalizar datos
            .filter(asesor -> {
                List<String> errors = validator.validate(asesor);
                if (errors.isEmpty()) {
                    return true;
                } else {
                    logger.warn("Asesor {} inválido. Errores: {}", 
                               asesor.getId(), String.join(", ", errors));
                    return false;
                }
            })
            .collect(Collectors.toList());
        
        int validCount = validAsesores.size();
        int invalidCount = asesores.size() - validCount;
        
        logger.info("Validación completada: {} válidos, {} inválidos", validCount, invalidCount);
        
        if (invalidCount > 0) {
            logger.warn("Se encontraron {} asesores con datos inválidos que fueron excluidos", invalidCount);
        }
        
        return validAsesores;
    }
    
    /**
     * Genera y muestra estadísticas detalladas
     */
    private void generateStatistics(List<Asesor> asesores) {
        logger.debug("Generando estadísticas para {} asesores", asesores.size());
        
        // Resumen general
        StatisticsService.EmployeeSummary summary = statisticsService.createSummary(asesores);
        logger.info("RESUMEN GENERAL:");
        logger.info("  Total asesores: {}", summary.getTotalAsesores());
        logger.info("  Especialidades: {}", summary.getEspecialidades());

        // Estadísticas por departamento
        Map<String, StatisticsService.DepartmentStats> deptStats = 
            statisticsService.calculateDepartmentStatistics(asesores);

        logger.info("ESTADÍSTICAS POR DEPARTAMENTO:");
        deptStats.forEach((dept, stats) -> {
            logger.info("  {} ({} asesores):", dept, stats.getAsesorCount());
            // Si quieres mostrar la distribución de especialidades:
            logger.info("    Distribución: {}", stats.getSpecializationDistribution());
        });
    }
    
    /**
     * Demuestra el uso de filtros avanzados con Guava
     */
    private void demonstrateAdvancedFiltering(List<Asesor> asesores) {
        logger.info("Demostrando filtros avanzados:");
        
        // Filtro 1: Asesores de un departamento específico
        List<Asesor> itAsesores = statisticsService.filterAsesores(asesores, "Tecnología", null);
        logger.info("  Asesores del departamento Tecnología: {}", itAsesores.size());
        
        // Agrupamiento avanzado
        Multimap<String, Asesor> groupedByDept = statisticsService.groupByEspecialidad(asesores);
        logger.info("  Agrupamiento por especialidad completado: {} grupos", groupedByDept.keySet().size());
    }
    
    /**
     * Método main CORREGIDO
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║   ASESORES PROCESSOR APPLICATION   ║");
        System.out.println("╚════════════════════════════════════╝");
        
        logger.info("=== INICIANDO ASESORES PROCESSOR APPLICATION ===");
        logger.info("Java Version: {}", System.getProperty("java.version"));
        logger.info("User: {}", System.getProperty("user.name"));
        logger.info("Working Directory: {}", System.getProperty("user.dir"));
        
        try {
            AsesorProcessorApp app = new AsesorProcessorApp();
            
            // Archivos con nombres más simples
            String inputFile = "data/asesores_input.xlsx";
            String outputFile = "data/asesores_processed_" + 
                              LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + 
                              ".xlsx";
            
            // Ejecutar procesamiento
            app.processAsesorFile(inputFile, outputFile);
            
            System.out.println("╔════════════════════════════════════╗");
            System.out.println("║       PROCESAMIENTO EXITOSO!      ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.println("Archivos generados:");
            System.out.println("  • " + inputFile + " (datos de entrada)");
            System.out.println("  • " + outputFile + " (datos procesados)");
            
            logger.info("=== APLICACIÓN FINALIZADA EXITOSAMENTE ===");
            
        } catch (Exception e) {
            logger.error("Error fatal en la aplicación: {}", e.getMessage(), e);
            System.err.println("Error fatal: " + e.getMessage());
            System.exit(1);
        }
    }

}
