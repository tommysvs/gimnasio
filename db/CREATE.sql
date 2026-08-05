PRAGMA foreign_keys = ON;

-- =====================================
-- TABLA AUXILIAR: ROLES
-- =====================================
CREATE TABLE roles (
    id_rol INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE,
    descripcion TEXT,
    estado INTEGER DEFAULT 1
);

-- =====================================
-- TABLA AUXILIAR: METODOS DE PAGO
-- =====================================
CREATE TABLE metodos_pago (
    id_metodo_pago INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE,
    descripcion TEXT,
    estado INTEGER DEFAULT 1
);

-- =====================================
-- TABLA AUXILIAR: ESTADOS MEMBRESIA
-- =====================================
CREATE TABLE estados_membresia (
    id_estado_membresia INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE,
    descripcion TEXT
);

-- =====================================
-- TABLA AUXILIAR: TIPOS DE MEMBRESIA
-- =====================================
CREATE TABLE tipos_membresia (
    id_tipo_membresia INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    duracion_dias INTEGER NOT NULL,
    precio REAL NOT NULL,
    descripcion TEXT,
    estado INTEGER DEFAULT 1
);

-- =====================================
-- TABLA AUXILIAR: GRUPOS MUSCULARES
-- =====================================
CREATE TABLE grupos_musculares (
    id_grupo INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE,
    descripcion TEXT
);

-- =====================================
-- USUARIOS
-- =====================================
CREATE TABLE usuarios (
    id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
    id_rol INTEGER NOT NULL,
    usuario TEXT UNIQUE NOT NULL,
    nombre TEXT NOT NULL,
    correo TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    estado INTEGER DEFAULT 1,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY(id_rol)
        REFERENCES roles(id_rol)
);

-- =====================================
-- CLIENTES
-- =====================================
CREATE TABLE clientes (
    id_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    apellido TEXT,
    telefono TEXT,
    correo TEXT,
    fecha_nacimiento DATE,
    genero TEXT,
    estado INTEGER DEFAULT 1,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- =====================================
-- ENTRENADORES
-- =====================================
CREATE TABLE entrenadores (
    id_entrenador INTEGER PRIMARY KEY AUTOINCREMENT,
    id_usuario INTEGER NOT NULL,
    especialidad TEXT,
    experiencia TEXT,
    estado INTEGER DEFAULT 1,

    FOREIGN KEY(id_usuario)
        REFERENCES usuarios(id_usuario)
);

-- =====================================
-- MEMBRESIAS ASIGNADAS A CLIENTES
-- =====================================
CREATE TABLE cliente_membresias (
    id_cliente_membresia INTEGER PRIMARY KEY AUTOINCREMENT,
    id_cliente INTEGER NOT NULL,
    id_tipo_membresia INTEGER NOT NULL,
    id_estado_membresia INTEGER NOT NULL,

    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,

    FOREIGN KEY(id_cliente)
        REFERENCES clientes(id_cliente)
        ON DELETE CASCADE,


    FOREIGN KEY(id_tipo_membresia)
        REFERENCES tipos_membresia(id_tipo_membresia),


    FOREIGN KEY(id_estado_membresia)
        REFERENCES estados_membresia(id_estado_membresia)
);

-- =====================================
-- PAGOS
-- =====================================
CREATE TABLE pagos (
    id_pago INTEGER PRIMARY KEY AUTOINCREMENT,
    id_cliente INTEGER NOT NULL,
    id_cliente_membresia INTEGER,
    id_metodo_pago INTEGER NOT NULL,
    monto REAL NOT NULL,
    fecha_pago DATETIME DEFAULT CURRENT_TIMESTAMP,
    descripcion TEXT,

    FOREIGN KEY(id_cliente)
        REFERENCES clientes(id_cliente)
        ON DELETE CASCADE,

    FOREIGN KEY(id_cliente_membresia)
        REFERENCES cliente_membresias(id_cliente_membresia),

    FOREIGN KEY(id_metodo_pago)
        REFERENCES metodos_pago(id_metodo_pago)
);

-- =====================================
-- ASISTENCIAS
-- =====================================
CREATE TABLE asistencias (
    id_asistencia INTEGER PRIMARY KEY AUTOINCREMENT,
    id_cliente INTEGER NOT NULL,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY(id_cliente)
        REFERENCES clientes(id_cliente)
        ON DELETE CASCADE
);

-- =====================================
-- RUTINAS
-- =====================================
CREATE TABLE rutinas (
    id_rutina INTEGER PRIMARY KEY AUTOINCREMENT,
    id_entrenador INTEGER,

    nombre TEXT NOT NULL,
    descripcion TEXT,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,


    FOREIGN KEY(id_entrenador)
        REFERENCES entrenadores(id_entrenador)
);

-- =====================================
-- ASIGNACION DE RUTINAS A CLIENTES
-- =====================================
CREATE TABLE cliente_rutinas (
    id_cliente_rutina INTEGER PRIMARY KEY AUTOINCREMENT,
    id_cliente INTEGER NOT NULL,
    id_rutina INTEGER NOT NULL,
    fecha_asignacion DATE,
    estado INTEGER DEFAULT 1,

    FOREIGN KEY(id_cliente)
        REFERENCES clientes(id_cliente)
        ON DELETE CASCADE,


    FOREIGN KEY(id_rutina)
        REFERENCES rutinas(id_rutina)
);

-- =====================================
-- EJERCICIOS
-- =====================================
CREATE TABLE ejercicios (
    id_ejercicio INTEGER PRIMARY KEY AUTOINCREMENT,
	id_grupo INTEGER NOT NULL,
    nombre TEXT NOT NULL,
    descripcion TEXT,


    FOREIGN KEY(id_grupo)
        REFERENCES grupos_musculares(id_grupo)
);

-- =====================================
-- DETALLE RUTINA - EJERCICIOS
-- =====================================
CREATE TABLE rutina_ejercicios (
    id_detalle INTEGER PRIMARY KEY AUTOINCREMENT,
    id_rutina INTEGER NOT NULL,
    id_ejercicio INTEGER NOT NULL,
    series INTEGER,
    repeticiones INTEGER,
    peso REAL,
    descanso_segundos INTEGER,

    FOREIGN KEY(id_rutina)
        REFERENCES rutinas(id_rutina)
        ON DELETE CASCADE,


    FOREIGN KEY(id_ejercicio)
        REFERENCES ejercicios(id_ejercicio)
);

-- =====================================
-- INDICES
-- =====================================
CREATE INDEX idx_usuario_correo
ON usuarios(correo);

CREATE INDEX idx_usuario_rol
ON usuarios(id_rol);

CREATE INDEX idx_cliente_correo
ON clientes(correo);

CREATE INDEX idx_cliente_membresia_cliente
ON cliente_membresias(id_cliente);

CREATE INDEX idx_cliente_membresia_estado
ON cliente_membresias(id_estado_membresia);

CREATE INDEX idx_pago_cliente
ON pagos(id_cliente);

CREATE INDEX idx_pago_fecha
ON pagos(fecha_pago);

CREATE INDEX idx_asistencia_cliente
ON asistencias(id_cliente);

CREATE INDEX idx_asistencia_fecha
ON asistencias(fecha);

CREATE INDEX idx_rutina_entrenador
ON rutinas(id_entrenador);

CREATE INDEX idx_cliente_rutina_cliente
ON cliente_rutinas(id_cliente);

CREATE INDEX idx_ejercicio_grupo
ON ejercicios(id_grupo);