
package com.mycompany.asesorame.demo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

public class AsesoraMeDemo{

	private static final Logger logger = LoggerFactory.getLogger(AsesoraMeDemo.class);

	public static void main(String[] args) {
		logger.info("Iniciando la aplicación Demo...");

		logger.error("Este es un mensaje de error de prueba");
		logger.warn("Este es un mensaje de advertencia de prueba");
		logger.info("La aplicación se está iniciando correctamente");

		var lista = Lists.newArrayList("Java", "Spring", "Guava", "Commons Lang");
		logger.debug("Lista de tecnologías: {}", StringUtils.join(lista, ", "));

		String nombre = StringUtils.capitalize("demo");
		logger.info("Nombre capitalizado: {}", nombre);

		logger.info("Aplicación Demo iniciada correctamente");
	}
}