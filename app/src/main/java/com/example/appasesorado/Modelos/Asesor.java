package com.example.appasesorado.Modelos;

public class Asesor {
    String uid;
    String nombre;
    String curso;
    String skill;
    String celular;
    String valoracion;
    String comentario;
    String estado;
    String fechadecumpleaños, fechadecreacion, fechaactualizacion;
    boolean verificacion;

    public Asesor() {
    }

    public Asesor(String uid, String nombre, String curso, String skill, String celular, String valoracion, String comentario, String estado, String fechadecumpleaños, String fechadecreacion, String fechaactualizacion, boolean verificacion) {
        this.uid = uid;
        this.nombre = nombre;
        this.curso = curso;
        this.skill = skill;
        this.celular = celular;
        this.valoracion = valoracion;
        this.comentario = comentario;
        this.estado = estado;
        this.fechadecumpleaños = fechadecumpleaños;
        this.fechadecreacion = fechadecreacion;
        this.fechaactualizacion = fechaactualizacion;
        this.verificacion = verificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getValoracion() {
        return valoracion;
    }

    public void setValoracion(String valoracion) {
        this.valoracion = valoracion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFechadecumpleaños() {
        return fechadecumpleaños;
    }

    public void setFechadecumpleaños(String fechadecumpleaños) {
        this.fechadecumpleaños = fechadecumpleaños;
    }

    public String getFechadecreacion() {
        return fechadecreacion;
    }

    public void setFechadecreacion(String fechadecreacion) {
        this.fechadecreacion = fechadecreacion;
    }

    public String getFechaactualizacion() {
        return fechaactualizacion;
    }

    public void setFechaactualizacion(String fechaactualizacion) {
        this.fechaactualizacion = fechaactualizacion;
    }

    public boolean isVerificacion() {
        return verificacion;
    }

    public void setVerificacion(boolean verificacion) {
        this.verificacion = verificacion;
    }
}
