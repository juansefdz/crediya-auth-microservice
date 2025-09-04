-- Permite guardar usuarios en estado borrador sin rol
ALTER TABLE usuarios
  ALTER COLUMN rol_id DROP NOT NULL;
