package com.tienda.tienda_online.service;

import com.tienda.tienda_online.entity.Usuario;
import com.tienda.tienda_online.entity.RefreshToken;
import com.tienda.tienda_online.repository.UsuarioRepository;
import com.tienda.tienda_online.repository.RefreshTokenRepository;
import com.tienda.tienda_online.security.JwtService;
import com.tienda.tienda_online.security.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@Transactional
public class UsuarioService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public UsuarioService(UsuarioRepository usuarioRepository, RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder, JwtService jwtService, CustomUserDetailsService userDetailsService) {
        
        this.usuarioRepository = usuarioRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    public Usuario registrarUsuario(String username, String password) {

        if (usuarioRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol("ROLE_CLIENTE");

        return usuarioRepository.save(usuario);
    }

    public TokenSesion iniciarSesion(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Usuario o contraseña incorrectos"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Usuario o contraseña incorrectos");
        }

        return generarTokens(usuario);
    }

    public TokenSesion renovarTokens(String refreshToken) {

        String hash = calcularHash(refreshToken);

        RefreshToken tokenGuardado = refreshTokenRepository.findByTokenHash(hash).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido"));

        if (tokenGuardado.isRevocado() || !tokenGuardado.getFechaExpiracion().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Refresh token expirado o revocado");
        }

        Usuario usuario = tokenGuardado.getUsuario();

        tokenGuardado.setRevocado(true);
        refreshTokenRepository.save(tokenGuardado);

        return generarTokens(usuario);
    }

    public void cerrarSesion(String refreshToken) {

        String hash = calcularHash(refreshToken);

        refreshTokenRepository.findByTokenHash(hash).ifPresent(token -> {token.setRevocado(true);
                    refreshTokenRepository.save(token);
                });
        }

    private TokenSesion generarTokens(Usuario usuario) {

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(usuario.getUsername());

        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = generarRefreshToken(usuario);

        return new TokenSesion(
                usuario.getUsername(),
                usuario.getRol(),
                accessToken,
                refreshToken
        );
    }

    private String generarRefreshToken(Usuario usuario) {

        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(usuario);
        refreshToken.setTokenHash(calcularHash(token));
        refreshToken.setFechaExpiracion(LocalDateTime.now().plusNanos(refreshExpiration * 1_000_000));

        refreshTokenRepository.save(refreshToken);

        return token;
    }

    private String calcularHash(String token) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return java.util.HexFormat.of().formatHex(hash);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "No se pudo calcular el hash del token",
                    e
            );
        }
    }

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }


    public record TokenSesion(
            String username,
            String rol,
            String token,
            String refreshToken
    ) {}
}
