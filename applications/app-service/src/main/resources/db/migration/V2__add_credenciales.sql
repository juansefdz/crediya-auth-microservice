
-- 1) Tabla credenciales
CREATE TABLE IF NOT EXISTS credenciales (
    usuario_id   BIGINT PRIMARY KEY,
    password_hash VARCHAR(120) NOT NULL,
    last_changed  TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_credenciales_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE CASCADE
);

-- 2) (Opcional pero recomendado) normalizar email en usuarios a minúsculas
--    Para inserts futuros:
--    *Si quieres forzarlo*: crea un índice único por LOWER(email)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE schemaname = 'public'
          AND indexname = 'ux_usuarios_email_lower'
    ) THEN
        CREATE UNIQUE INDEX ux_usuarios_email_lower ON usuarios (LOWER(email));
    END IF;
END$$;

-- 3) (Semilla opcional para desarrollo) admin@crediya.com / 123456
--    Cambia el hash en cuanto puedas.
INSERT INTO credenciales (usuario_id, password_hash)
SELECT u.id, '$2a$10$6a4sJ6lVn8aQvYb4J9v4E.H9A7Cz0r0m8rm1VvGk8jNEc1O4Q2iG6'
FROM usuarios u
WHERE LOWER(u.email) = LOWER('admin@crediya.com')
ON CONFLICT (usuario_id) DO NOTHING;