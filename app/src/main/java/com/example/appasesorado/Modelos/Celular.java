package com.example.appasesorado.Modelos;

public
class Celular {
    private String fechahoraregis,imei,modeloCelular,nroRegistro;

    public Celular(String fechahoraregis, String imei, String modeloCelular, String nroRegistro) {
        this.fechahoraregis = fechahoraregis;
        this.imei = imei;
        this.modeloCelular = modeloCelular;
        this.nroRegistro = nroRegistro;
    }

    public String getFechahoraregis() {
        return fechahoraregis;
    }

    public void setFechahoraregis(String fechahoraregis) {
        this.fechahoraregis = fechahoraregis;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getModeloCelular() {
        return modeloCelular;
    }

    public void setModeloCelular(String modeloCelular) {
        this.modeloCelular = modeloCelular;
    }

    public String getNroRegistro() {
        return nroRegistro;
    }

    public void setNroRegistro(String nroRegistro) {
        this.nroRegistro = nroRegistro;
    }
}
