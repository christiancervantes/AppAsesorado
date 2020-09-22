package com.example.appasesorado.Modelos;

public
class TokenModel {
    private String phone,nombre,token;

    public TokenModel(String phone, String nombre, String token) {
        this.phone = phone;
        this.nombre = nombre;
        this.token = token;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
