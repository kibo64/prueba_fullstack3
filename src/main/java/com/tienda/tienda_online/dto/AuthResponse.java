package com.tienda.tienda_online.dto;

public class AuthResponse {

    private String mensaje;
    private String username;
    private String rol;
    private String token;
    private String refreshToken;

    public AuthResponse(
            String mensaje,
            String username,
            String rol,
            String token,
            String refreshToken) {

        this.mensaje = mensaje;
        this.username = username;
        this.rol = rol;
        this.token = token;
        this.refreshToken = refreshToken;
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

    public String getRefreshToken() {
        return refreshToken;
    }
}
