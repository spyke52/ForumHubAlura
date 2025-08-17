CREATE INDEX idx_topicos_status ON topicos(status);
CREATE INDEX idx_topicos_curso ON topicos(curso);
CREATE INDEX idx_topicos_fecha_creacion ON topicos(fecha_creacion);
CREATE INDEX idx_topicos_usuario_fecha ON topicos(usuario_id, fecha_creacion);
CREATE INDEX idx_topicos_search ON topicos(curso, status, fecha_creacion);
CREATE INDEX idx_respuestas_topico ON respuestas(topico_id);
CREATE INDEX idx_respuestas_usuario ON respuestas(usuario_id);
CREATE INDEX idx_respuestas_fecha ON respuestas(fecha_creacion);
ALTER TABLE topicos ADD FULLTEXT(titulo, mensaje);