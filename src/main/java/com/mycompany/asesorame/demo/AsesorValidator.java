/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.asesorame.demo;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AsesorValidator {

    private static final Logger logger = LoggerFactory.getLogger(AsesorValidator.class);
    private final EmailValidator emailValidator;

    public AsesorValidator() {
        this.emailValidator = EmailValidator.getInstance();
        logger.debug("AsesorValidator initialized");
    }

    public List<String> validate(Asesor asesor) {
        logger.debug("Iniciando Validación: {}",
                asesor != null ? asesor.getId() : "Asesor nulo");

        List<String> errors = new ArrayList<>();

        if (asesor == null) {
            errors.add("El asesor no puede ser nulo.");
            logger.warn("Validación fallida: Asesor nulo");
            return errors;
        }

        // Validación de campos obligatorios
        // Validación por Id
        if (asesor.getId() == null || asesor.getId() <= 0) {
            errors.add("El ID del asesor es obligatorio y debe ser un número positivo.");
            logger.warn("Validación fallida: ID inválido para asesor");
        }

        // Validacion de nombres
        if (StringUtils.isBlank(asesor.getNombre())) {
            errors.add("El nombre del asesor es obligatorio.");
        } else if (asesor.getNombre().length() < 2) {
            errors.add("El nombre del asesor debe tener al menos 2 caracteres.");
        } else if (asesor.getNombre().length() > 50) {
            errors.add("El nombre del asesor no puede exceder los 50 caracteres.");
        }

        // Validacion de apellidos
        if (StringUtils.isBlank(asesor.getApellido())) {
            errors.add("El apellido del asesor es obligatorio.");
        } else if (asesor.getApellido().length() < 2) {
            errors.add("El apellido del asesor debe tener al menos 2 caracteres.");
        } else if (asesor.getApellido().length() > 50) {
            errors.add("El apellido del asesor no puede exceder los 50 caracteres.");
        }

        // Validacion de email usando Apache Commons Validator
        if (StringUtils.isBlank(asesor.getEmail())) {
            errors.add("El email es obligatorio");
        } else if (!emailValidator.isValid(asesor.getEmail())) {
            errors.add("El formato del email es inválido: " + asesor.getEmail());
            logger.warn("Email inválido detectado: {}", asesor.getEmail());
        }

        // Validación de teléfono
        if (StringUtils.isBlank(asesor.getTelefono())) {
            errors.add("El teléfono del asesor es obligatorio.");
        } else if (!asesor.getTelefono().matches("^\\d{9}$")) {
            errors.add("El teléfono debe contener exactamente 9 dígitos.");
        }

        if (StringUtils.isBlank(asesor.getEspecialidad())) {
            errors.add("La especialidad del asesor es obligatoria.");
        }
        if (StringUtils.isBlank(asesor.getUbicacion())) {
            errors.add("La ubicación del asesor es obligatoria.");
        }

        if (errors.isEmpty()) {
            logger.debug("Validación exitosa para el asesor con ID: {}", asesor.getId());
        } else {
            logger.info("Asesor con ID {} tiene errores de validación: {}",
                    asesor.getId(), errors.size());
        }

        return errors;
    }

    // Valida si el asesor es válido sin devolver errores
    // Este método es útil para verificar rápidamente si un asesor cumple con las
    // reglas de validación
    public boolean isValid(Asesor asesor) {
        return validate(asesor).isEmpty();
    }

    public void normalizeAsesor(Asesor asesor) {
        if (asesor == null)
            return;

        logger.debug("Normalizando datos del asesor {}", asesor.getId());

        // Normalizar nombres (capitalizar primera letra)
        if (StringUtils.isNotBlank(asesor.getNombre())) {
            asesor.setNombre(StringUtils.capitalize(asesor.getNombre().toLowerCase().trim()));
        }

        if (StringUtils.isNotBlank(asesor.getApellido())) {
            asesor.setApellido(StringUtils.capitalize(asesor.getApellido().toLowerCase().trim()));
        }

        // Normalizar email (convertir a minúsculas)
        if (StringUtils.isNotBlank(asesor.getEmail())) {
            asesor.setEmail(asesor.getEmail().toLowerCase().trim());
        }

        // Normalizar departamento
        if (StringUtils.isNotBlank(asesor.getEspecialidad())) {
            asesor.setEspecialidad(StringUtils.capitalize(asesor.getEspecialidad().toLowerCase().trim()));
        }

        logger.debug("Asesor {} normalizado", asesor.getId());
    }

}

