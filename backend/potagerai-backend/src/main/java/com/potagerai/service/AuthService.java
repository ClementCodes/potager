package com.potagerai.service;

import com.potagerai.domain.user.User;
import com.potagerai.domain.user.UserRepository;
import com.potagerai.dto.auth.AuthResponse;
import com.potagerai.dto.auth.LoginRequest;
import com.potagerai.dto.auth.RegisterRequest;
import com.potagerai.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Gestion de l'authentification : inscription et connexion.
 * Génère un JWT HS256 à chaque succès.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * Crée un compte utilisateur et retourne un JWT.
     *
     * @throws IllegalArgumentException si l'email est déjà utilisé
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email déjà utilisé : " + request.email());
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        User saved = userRepository.save(user);
        String token = jwtTokenProvider.generateToken(saved.getEmail());

        return AuthResponse.of(token, saved.getId(), saved.getEmail());
    }

    /**
     * Authentifie un utilisateur existant et retourne un JWT.
     *
     * @throws org.springframework.security.core.AuthenticationException si les identifiants sont incorrects
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable après authentification"));

        String token = jwtTokenProvider.generateToken(email);
        return AuthResponse.of(token, user.getId(), email);
    }
}
