-- Eliminar índices innecesarios
DROP INDEX idx_topicos_search ON topicos;

-- Optimizar índice fulltext
ALTER TABLE topicos
    ADD FULLTEXT idx_titulo_mensaje (titulo, mensaje)
    WITH PARSER ngram;

-- Optimizar índice para trending
CREATE INDEX idx_topicos_fecha_status ON topicos(fecha_creacion, status);
CREATE INDEX idx_respuestas_topico_fecha ON respuestas(topico_id, fecha_creacion);