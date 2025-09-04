-- V6__fix_credentials_pk.sql
-- Estandariza credentials con PK id y columnas necesarias.
-- Seguro e idempotente.

-- 1) Quitar PK actual si existe (por ejemplo en usuario_id)
DO $$
DECLARE
  pk_name text;
BEGIN
  SELECT conname INTO pk_name
  FROM pg_constraint
  WHERE conrelid = 'public.credentials'::regclass
    AND contype = 'p';

  IF pk_name IS NOT NULL THEN
    EXECUTE format('ALTER TABLE public.credentials DROP CONSTRAINT %I', pk_name);
  END IF;
END$$;

-- 2) Asegurar columna id BIGSERIAL y PK(id)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='credentials' AND column_name='id'
  ) THEN
    ALTER TABLE public.credentials ADD COLUMN id BIGSERIAL;
  END IF;
END$$;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM pg_constraint
    WHERE conrelid='public.credentials'::regclass AND contype='p'
  ) THEN
    ALTER TABLE public.credentials
      ADD CONSTRAINT credentials_pkey PRIMARY KEY (id);
  END IF;
END$$;

-- 3) Asegurar usuario_id (FK a usuarios)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='credentials' AND column_name='usuario_id'
  ) THEN
    ALTER TABLE public.credentials ADD COLUMN usuario_id BIGINT;
  END IF;
END$$;

-- 4) Alinear password_hash (si existía 'contrasena', renombrar)
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='credentials' AND column_name='contrasena'
  ) AND NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='credentials' AND column_name='password_hash'
  ) THEN
    ALTER TABLE public.credentials RENAME COLUMN contrasena TO password_hash;
  END IF;
END$$;

-- 5) Asegurar password_hash
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='credentials' AND column_name='password_hash'
  ) THEN
    ALTER TABLE public.credentials ADD COLUMN password_hash VARCHAR(100);
  END IF;
END$$;

-- 6) Asegurar enabled
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='credentials' AND column_name='enabled'
  ) THEN
    ALTER TABLE public.credentials ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT TRUE;
  END IF;
END$$;

-- 7) Asegurar email (AQUÍ ESTABA EL FALLO)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='credentials' AND column_name='email'
  ) THEN
    ALTER TABLE public.credentials ADD COLUMN email VARCHAR(320);
  END IF;
END$$;

-- 8) Índice único email (solo si existe la columna y el índice no existe)
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='credentials' AND column_name='email'
  ) AND NOT EXISTS (
    SELECT 1 FROM pg_indexes
    WHERE schemaname='public' AND tablename='credentials' AND indexname='ux_credentials_email'
  ) THEN
    CREATE UNIQUE INDEX ux_credentials_email ON public.credentials(email);
  END IF;
END$$;

-- 9) FK usuario_id -> usuarios(id) (ajusta el nombre de tabla si no es 'usuarios')
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM pg_constraint c
    WHERE c.conrelid='public.credentials'::regclass
      AND c.contype='f'
      AND c.conname='fk_credentials_usuario'
  ) THEN
    ALTER TABLE public.credentials
      ADD CONSTRAINT fk_credentials_usuario
      FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);
  END IF;
END$$;
