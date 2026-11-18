package com.tienda.tienda_online.controller;

import com.tienda.tienda_online.dto.AuthResponse;
import com.tienda.tienda_online.dto.LoginRequest;
import com.tienda.tienda_online.dto.RefreshRequest;
import com.tienda.tienda_online.dto.RegistroRequest;
import com.tienda.tienda_online.entity.Usuario;
import com.tienda.tienda_online.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroRequest request) {

        Usuario usuario = usuarioService.registrarUsuario(request.getUsername(),request.getPassword());

        AuthResponse response = new AuthResponse(
                "Usuario registrado correctamente",
                usuario.getUsername(),
                usuario.getRol(),
                null,
                null
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> iniciarSesion(@Valid @RequestBody LoginRequest request) {

        UsuarioService.TokenSesion sesion = usuarioService.iniciarSesion(request.getUsername(), request.getPassword());

        AuthResponse response = new AuthResponse(
                "Inicio de sesión exitoso",
                sesion.username(),
                sesion.rol(),
                sesion.token(),
                sesion.refreshToken()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> renovarToken(@Valid @RequestBody RefreshRequest request) {

        UsuarioService.TokenSesion sesion = usuarioService.renovarTokens(request.refreshToken());

        AuthResponse response = new AuthResponse(
                "Tokens renovados correctamente",
                sesion.username(),
                sesion.rol(),
                sesion.token(),
                sesion.refreshToken()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> cerrarSesion(@Valid @RequestBody RefreshRequest request) {
        usuarioService.cerrarSesion(request.refreshToken());
        
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada correctamente"));
    }
}