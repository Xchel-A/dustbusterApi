package com.dustbuster.dustbusterApi.Entity;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@Table(name="Usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long IdUser;


    @Column(unique = true, length = 50)
    @NotEmpty
    private String correo;

    @Column(nullable = false)
    @NotEmpty
    private String password;

    private Boolean enabled;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "IdUser", referencedColumnName = "IdUser")
    private List<Rol> authorities;


    public Usuario() {

    }
}
