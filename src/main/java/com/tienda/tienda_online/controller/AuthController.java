package com.tienda.tienda_online.controller;

import com.tienda.tienda_online.dto.AuthResponse;
import com.tienda.tienda_online.dto.LoginRequest;
import com.tienda.tienda_online.dto.RegistroRequest;
import com.tienda.tienda_online.entity.Usuario;
import com.tienda.tienda_online.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroRequest request) {

        Usuario usuario = usuarioService.registrarUsuario(request.getUsername(), request.getPassword()
        );
        AuthResponse response = new AuthResponse(
                "Usuario registrado correctamente",
                usuario.getUsername(),
                usuario.getRol(),
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> iniciarSesion(@Valid @RequestBody LoginRequest request) {
        String token = usuarioService.iniciarSesion(request.getUsername(), request.getPassword());
        Usuario usuario = usuarioService.buscarPorUsername(request.getUsername());

        AuthResponse response = new AuthResponse(
                "Inicio de sesión exitoso",
                usuario.getUsername(),
                usuario.getRol(),
                token
        );
        return ResponseEntity.ok(response);
    }
}
