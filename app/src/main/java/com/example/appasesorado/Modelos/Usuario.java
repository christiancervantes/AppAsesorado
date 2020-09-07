package com.example.appasesorado.Modelos;

public
class Usuario {
    private String uid,nombre,celular,spinner;
    private String fechadecumpleaños,fechadecreacion,fechaactualizacion;
    private Double ratingValue;
    private Long ratingCount;
    private String estado;

    public Usuario() {
    }

    public Usuario(String uid, String nombre, String celular, String spinner, String fechadecumpleaños, String fechadecreacion, String fechaactualizacion,String estado) {
        this.uid = uid;
        this.nombre = nombre;
        this.celular = celular;
        this.spinner = spinner;
        this.fechadecumpleaños = fechadecumpleaños;
        this.fechadecreacion = fechadecreacion;
        this.fechaactualizacion = fechaactualizacion;
        this.estado = estado;

    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
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

    public Double getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Double ratingValue) {
        this.ratingValue = ratingValue;
    }

    public Long getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Long ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getSpinner() {
        return spinner;
    }

    public void setSpinner(String spinner) {
        this.spinner = spinner;
    }
}
