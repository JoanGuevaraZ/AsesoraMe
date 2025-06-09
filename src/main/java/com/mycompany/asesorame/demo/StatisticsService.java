
package com.mycompany.asesorame.demo;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class StatisticsService {
    
    private static final Logger logger = LoggerFactory.getLogger(StatisticsService.class);

    /**
     * Agrupa los asesores por especialidad.
     */
    public Multimap<String, Asesor> groupByEspecialidad(List<Asesor> asesores) {
        logger.info("Agrupando {} asesores por especialidad", asesores.size());

        // Crear un Multimap inmutable usando Guava
        ImmutableListMultimap.Builder<String, Asesor> builder = ImmutableListMultimap.builder();

        asesores.forEach(asesor -> {
            if (asesor.getEspecialidad() != null) {
                builder.put(asesor.getEspecialidad(), asesor);
            }
        });

        Multimap<String, Asesor> groupedAsesores = builder.build();

        logger.debug("Asesores agrupados en {} especialidades", groupedAsesores.keySet().size());
        groupedAsesores.keySet().forEach(especialidad ->
                logger.debug("Especialidad '{}': {} asesores", especialidad, groupedAsesores.get(especialidad).size())
        );

        return groupedAsesores;
    }

    /**
     * Calcula las estadísticas por especialidad.
     */
    public Map<String, DepartmentStats> calculateDepartmentStatistics(List<Asesor> asesores) {
        logger.info("Calculando estadísticas por especialidad");

        Multimap<String, Asesor> groupedAsesores = groupByEspecialidad(asesores);
        Map<String, DepartmentStats> statistics = Maps.newHashMap();

        for (String especialidad : groupedAsesores.keySet()) {
            Collection<Asesor> deptAsesores = groupedAsesores.get(especialidad);
            DepartmentStats stats = calculateStatsForDepartment(especialidad, deptAsesores);
            statistics.put(especialidad, stats);
        }

        logger.info("Estadísticas calculadas para {} especialidades", statistics.size());
        return statistics;
    }

    /**
     * Calcula las estadísticas para un departamento (especialidad) específico.
     */
    private DepartmentStats calculateStatsForDepartment(String especialidad, Collection<Asesor> asesores) {
        List<Asesor> asesorList = Lists.newArrayList(asesores);

        int count = asesorList.size();

        // Calcular distribución de especialidades
        Map<String, Long> specializationCounts = calculateSpecializationCounts(asesorList);

        return new DepartmentStats(
            especialidad,
            count,
            specializationCounts
        );
    }

    /**
     * Calcula la distribución de especialidades.
     */
    private Map<String, Long> calculateSpecializationCounts(List<Asesor> asesores) {
        return asesores.stream()
            .filter(asesor -> asesor.getEspecialidad() != null)
            .collect(Collectors.groupingBy(
                Asesor::getEspecialidad,
                Collectors.counting()
            ));
    }

    /**
     * Filtra asesores usando los criterios de especialidad, ubicación, etc.
     */
    public List<Asesor> filterAsesores(List<Asesor> asesores,
                                       String especialidad,
                                       String ubicacion) {
        logger.info("Filtrando asesores con criterios: especialidad = {}, ubicacion = {}",
                especialidad, ubicacion);

        List<Predicate<Asesor>> predicates = Lists.newArrayList();

        if (especialidad != null && !especialidad.trim().isEmpty()) {
            predicates.add(asesor -> especialidad.equalsIgnoreCase(asesor.getEspecialidad()));
        }

        if (ubicacion != null && !ubicacion.trim().isEmpty()) {
            predicates.add(asesor -> ubicacion.equalsIgnoreCase(asesor.getUbicacion()));
        }

        List<Asesor> filtered = asesores.stream()
            .filter(asesor -> predicates.stream().allMatch(predicate -> predicate.apply(asesor)))
            .collect(Collectors.toList());

        logger.info("Filtrado completado: {} asesores cumplen los criterios", filtered.size());
        return filtered;
    }

    /**
     * Obtiene los top N asesores por alguna métrica. En este caso, se omite el cálculo por edad.
     */
    public List<Asesor> getTopAsesoresByEspecialidad(List<Asesor> asesores, int topN) {
        logger.info("Obteniendo top {} asesores por especialidad", topN);

        // Ordenar por especialidad
        List<Asesor> topAsesores = asesores.stream()
            .sorted(Comparator.comparing(Asesor::getEspecialidad))  // Orden por especialidad
            .limit(topN)
            .collect(Collectors.toList());

        logger.debug("Top {} asesores obtenidos", topAsesores.size());
        return topAsesores;
    }

    /**
     * Crea un resumen general de los asesores.
     */
    public EmployeeSummary createSummary(List<Asesor> asesores) {
        int totalAsesores = asesores.size();
        ImmutableSet<String> especialidades = ImmutableSet.copyOf(
            asesores.stream()
                .map(Asesor::getEspecialidad)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet())
        );
        ImmutableMap<String, Long> specializationCounts = ImmutableMap.copyOf(
            asesores.stream()
                .filter(a -> a.getEspecialidad() != null)
                .collect(Collectors.groupingBy(
                    Asesor::getEspecialidad,
                    Collectors.counting()
                ))
        );
        return new EmployeeSummary(totalAsesores, especialidades, specializationCounts);
    }

    // Clases internas para estadísticas y resumen pueden quedarse igual

    /**
     * Clase interna para estadísticas por especialidad.
     */
    public static class DepartmentStats {
        private final String especialidad;
        private final int asesorCount;
        private final Map<String, Long> specializationDistribution;

        public DepartmentStats(String especialidad, int asesorCount, Map<String, Long> specializationDistribution) {
            this.especialidad = especialidad;
            this.asesorCount = asesorCount;
            this.specializationDistribution = specializationDistribution;
        }

        // Getters
        public String getEspecialidad() { return especialidad; }
        public int getAsesorCount() { return asesorCount; }
        public Map<String, Long> getSpecializationDistribution() { return specializationDistribution; }

        @Override
        public String toString() {
            return String.format("DepartmentStats{especialidad='%s', count=%d}", 
                               especialidad, asesorCount);
        }
    }

    /**
     * Clase interna para resumen general.
     */
    public static class EmployeeSummary {
        private final int totalAsesores;
        private final ImmutableSet<String> especialidades;
        private final ImmutableMap<String, Long> specializationCounts;

        public EmployeeSummary(int totalAsesores, ImmutableSet<String> especialidades, 
                               ImmutableMap<String, Long> specializationCounts) {
            this.totalAsesores = totalAsesores;
            this.especialidades = especialidades;
            this.specializationCounts = specializationCounts;
        }

        // Getters
        public int getTotalAsesores() { return totalAsesores; }
        public ImmutableSet<String> getEspecialidades() { return especialidades; }
        public ImmutableMap<String, Long> getSpecializationCounts() { return specializationCounts; }

        @Override
        public String toString() {
            return String.format("EmployeeSummary{total=%d, especialidades=%d}", 
                               totalAsesores, especialidades.size());
        }
    }
}
