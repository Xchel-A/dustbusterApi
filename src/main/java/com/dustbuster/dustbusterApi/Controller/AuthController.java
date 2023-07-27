package com.dustbuster.dustbusterApi.Controller;


import com.dustbuster.dustbusterApi.Entity.Usuario;
import com.dustbuster.dustbusterApi.Jwt.JwtUtils;
import com.dustbuster.dustbusterApi.Model.Bean.AuthRequestBean;
import com.dustbuster.dustbusterApi.Model.Bean.JwtResponseBean;
import com.dustbuster.dustbusterApi.Model.Bean.MessageResponseBean;
import com.dustbuster.dustbusterApi.Repository.IUsuarioRepository;
import com.dustbuster.dustbusterApi.Service.impl.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    IUsuarioRepository usuarioRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequestBean authRequestBean) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestBean.getCorreo(), authRequestBean.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Actualizar la fecha de última sesión
        Optional<Usuario> usuarioOptional = usuarioRepository.findByCorreo(authRequestBean.getCorreo());
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setUltimaSesion(new Timestamp(System.currentTimeMillis()));

            usuarioRepository.save(usuario);

        } else {
            // Manejar el caso si el usuario no existe
            // Puedes lanzar una excepción, crear un nuevo usuario, etc.
            return ResponseEntity.badRequest().body(new MessageResponseBean("Usuario no encontrado."));
        }
        Usuario data = usuarioOptional.get();
        data.setPassword(null);

        return ResponseEntity.ok(new JwtResponseBean(jwt, "Bearer",
               data));
    }


    @PostMapping("/signup")
    public ResponseEntity<MessageResponseBean> registerUser(@Valid @RequestBody Usuario usuario) {
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseBean("Error: Username is already taken!"));
        }
        String pass = encoder.encode(usuario.getPassword());
        usuario.setPassword(pass);
        usuario.setUltimaSesion(new Timestamp(System.currentTimeMillis()));
        usuario.setFechaRegistro(new Timestamp(System.currentTimeMillis()));// Setear la fecha de última sesión al registrarse
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(new MessageResponseBean("User registered successfully!"));
    }
}
