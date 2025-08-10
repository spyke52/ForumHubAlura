package com.alura.forum.forumHub.domain.topico;

import com.alura.forum.forumHub.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "topicos")
@Entity(name = "Topico")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Topico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String mensaje;

    @Column(updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private String status = "NO_RESPONDIDO";
    private String curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Topico(DatosRegistroTopico datosRegistroTopico) {
        this.titulo = datosRegistroTopico.titulo();
        this.mensaje = datosRegistroTopico.mensaje();
        this.curso = datosRegistroTopico.curso();
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void actualizarDatos(DatosActualizarTopico datosActualizar) {
        if (datosActualizar.titulo() != null) {
            this.titulo = datosActualizar.titulo();
        }
        if (datosActualizar.mensaje() != null) {
            this.mensaje = datosActualizar.mensaje();
        }
        if (datosActualizar.status() != null) {
            this.status = datosActualizar.status();
        }
    }
}