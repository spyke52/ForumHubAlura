package com.alura.forum.forumHub.infra.exceptions;

import com.auth0.jwt.exceptions.JWTVerificationException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class TratadorDeErrores {

    private record ErrorResponse(
            String mensaje,
            String detalle,
            int status,
            String codigo
    ) {}

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> tratarError404(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        "Recurso no encontrado",
                        e.getMessage(),
                        HttpStatus.NOT_FOUND.value(),
                        "RECURSO_NO_ENCONTRADO"
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DatosErrorValidacion>> tratarError400(MethodArgumentNotValidException e) {
        var errores = e.getFieldErrors().stream()
                .map(DatosErrorValidacion::new)
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<ErrorResponse> tratarErrorDeNegocio(ValidacionException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "Error de validación de negocio",
                        e.getMessage(),
                        HttpStatus.BAD_REQUEST.value(),
                        "ERROR_NEGOCIO"
                ));
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ErrorResponse> manejarErrorJWT(JWTVerificationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        "Token inválido",
                        ex.getMessage(),
                        HttpStatus.UNAUTHORIZED.value(),
                        "TOKEN_INVALIDO"
                ));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> manejarTokenExpirado(ExpiredJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        "Token expirado",
                        "Debe renovar su token de acceso",
                        HttpStatus.UNAUTHORIZED.value(),
                        "TOKEN_EXPIRADO"
                ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> manejarErrorIntegridadDatos(DataIntegrityViolationException ex) {
        String mensaje = "Error de integridad de datos";
        if (ex.getRootCause() != null) {
            mensaje = ex.getRootCause().getMessage();
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        "Conflicto de datos",
                        mensaje,
                        HttpStatus.CONFLICT.value(),
                        "CONFLICTO_DATOS"
                ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> manejarBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        "Credenciales inválidas",
                        "Usuario o contraseña incorrectos",
                        HttpStatus.UNAUTHORIZED.value(),
                        "CREDENCIALES_INVALIDAS"
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> manejarAccesoDenegado(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                        "Acceso denegado",
                        "No tiene permisos para esta operación",
                        HttpStatus.FORBIDDEN.value(),
                        "ACCESO_DENEGADO"
                ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> manejarTipoArgumentoInvalido(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "Tipo de parámetro inválido",
                        String.format("El parámetro '%s' debe ser de tipo %s",
                                ex.getName(),
                                ex.getRequiredType().getSimpleName()),
                        HttpStatus.BAD_REQUEST.value(),
                        "PARAMETRO_INVALIDO"
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarCuerpoInvalido(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "Cuerpo de solicitud inválido",
                        "El formato JSON es incorrecto o hay campos mal formados",
                        HttpStatus.BAD_REQUEST.value(),
                        "CUERPO_INVALIDO"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarErrorGenerico(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "Error interno del servidor",
                        "Contacte al administrador del sistema",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "ERROR_INTERNO"
                ));
    }

    private record DatosErrorValidacion(String campo, String error) {
        public DatosErrorValidacion(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }
}