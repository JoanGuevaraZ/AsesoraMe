/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.asesorame.demo;


import com.google.common.base.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Asesor {
    
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String especialidad;
    private String ubicacion;


    //Constructor vacio
    public Asesor() {
    }

    //Constructor con todos los campos
    public Asesor(Long id, String nombre, String apellido, String email, String telefono, String especialidad, String ubicacion) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.especialidad = especialidad;
        this.ubicacion = ubicacion;
    }

    /*  Obetiene el nombre completo de los Asesores esta utilizando
     * el Apache Commons Lang3 para unir el nombre y apellido
     * y manejar los casos donde alguno de los dos sea nulo o vacío.
     */
    public String getFullName() {
        return StringUtils.join(
            StringUtils.defaultString(nombre),
            " ",
            StringUtils.defaultString(apellido)
        ).trim();
    }

    public boolean isValid() {
        return 
        StringUtils.isNotBlank(nombre) && 
        StringUtils.isNotBlank(apellido) && 
        StringUtils.isNotBlank(email) && 
        StringUtils.isNotBlank(telefono) &&
        StringUtils.isNotBlank(especialidad) &&
        StringUtils.isNotBlank(ubicacion);
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    /* Implementacion de equals Google Guava Objects */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Asesor)) return false;
        Asesor asesor = (Asesor) o;
        return Objects.equal(id, asesor.id) &&
               Objects.equal(nombre, asesor.nombre) &&
               Objects.equal(apellido, asesor.apellido) &&
               Objects.equal(email, asesor.email) &&
               Objects.equal(telefono, asesor.telefono) &&
               Objects.equal(especialidad, asesor.especialidad) &&
               Objects.equal(ubicacion, asesor.ubicacion);
    }

    /* Implementacion de hashCode Google Guava Objects */
    @Override
    public int hashCode() {
        return Objects.hashCode(id, nombre, apellido, email, telefono, especialidad, ubicacion);
    }

    /* Implementacion de toString Apache Commons Lang3 */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
            .append("id", id)
            .append("fullName", getFullName())
            .append("email", email)
            .append("telefono", telefono)
            .append("especialidad", especialidad)
            .append("ubicacion", ubicacion)    
            .toString();
    }
    
}

