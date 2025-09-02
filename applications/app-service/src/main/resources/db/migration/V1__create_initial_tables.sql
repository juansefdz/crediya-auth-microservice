-- Creación de la tabla para Roles
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);
-- Creación de la tabla para Usuarios
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    documento_identidad VARCHAR(20) NOT NULL UNIQUE,
    fecha_nacimiento DATE,
    direccion VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    salario_base NUMERIC(12, 2) DEFAULT 0.00,
    rol_id BIGINT NOT NULL,

    CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES roles(id)
);

CREATE INDEX idx_usuario_email ON usuarios (email);

INSERT INTO roles (nombre, descripcion) VALUES
('ADMIN', 'Rol con acceso total al sistema.'),
('USER', 'Rol para usuarios estándar con permisos limitados.');