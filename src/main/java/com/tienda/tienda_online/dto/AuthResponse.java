package com.tienda.tienda_online.dto;

public class AuthResponse {

    private String mensaje;
    private String username;
    private String rol;
    private String token;

    public AuthResponse(String mensaje, String username, String rol, String token) {
        this.mensaje = mensaje;
        this.username = username;
        this.rol = rol;
        this.token = token;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getUsername() {
        return username;
    }

    public String getRol() {
        return rol;
    }

    public String getToken() {
        return token;
    }
}
