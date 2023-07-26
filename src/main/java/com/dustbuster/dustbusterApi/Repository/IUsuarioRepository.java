package com.dustbuster.dustbusterApi.Repository;

import com.dustbuster.dustbusterApi.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IUsuarioRepository extends JpaRepository<Usuario , Long> {
    Optional<Usuario> findByCorreo(String correo);
    @Modifying
    @Query("DELETE FROM Usuario u WHERE u.correo = ?1")
    void deleteByCorreo(String correo);
    Boolean existsByCorreo(String correo);
}
