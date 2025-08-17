package com.alura.forum.forumHub.domain.topico;

import com.alura.forum.forumHub.domain.respuesta.Respuesta;
import com.alura.forum.forumHub.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "topicos")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Topico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private StatusTopico status = StatusTopico.NO_RESPONDIDO;

    @Column(nullable = false, length = 100)
    private String curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "topico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Respuesta> respuestas = new ArrayList<>();

    public Topico(String titulo, String mensaje, String curso) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.curso = curso;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void actualizarDatos(String titulo, String mensaje, StatusTopico status) {
        if (titulo != null && !titulo.isBlank()) {
            this.titulo = titulo;
        }
        if (mensaje != null && !mensaje.isBlank()) {
            this.mensaje = mensaje;
        }
        if (status != null) {
            this.status = status;
        }
    }

    public void agregarRespuesta(Respuesta respuesta) {
        respuestas.add(respuesta);
        respuesta.setTopico(this);
    }

    public void marcarSolucionada(Respuesta respuesta) {
        this.status = StatusTopico.SOLUCIONADO;
        respuesta.marcarComoSolucion();
    }
}