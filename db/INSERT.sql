PRAGMA foreign_keys = ON;

-- =====================================
-- ROLES
-- =====================================
INSERT INTO roles (nombre, descripcion)
VALUES
('Administrador', 'Control total del sistema'),
('Recepcionista', 'Gestiona clientes, pagos y asistencias'),
('Entrenador', 'Gestiona rutinas y clientes asignados'),
('Cliente', 'Consulta información personal');

-- =====================================
-- METODOS DE PAGO
-- =====================================
INSERT INTO metodos_pago (nombre, descripcion)
VALUES
('Efectivo', 'Pago en efectivo'),
('Tarjeta', 'Pago con tarjeta bancaria'),
('Transferencia', 'Transferencia bancaria');

-- =====================================
-- ESTADOS DE MEMBRESIA
-- =====================================
INSERT INTO estados_membresia (nombre, descripcion)
VALUES
('Activa', 'Membresía vigente'),
('Vencida', 'Membresía fuera de fecha'),
('Cancelada', 'Membresía cancelada');

-- =====================================
-- TIPOS DE MEMBRESIA
-- =====================================
INSERT INTO tipos_membresia
(nombre, duracion_dias, precio, descripcion)
VALUES
('Mensual', 30, 500, 'Acceso al gimnasio durante 30 días'),
('Trimestral', 90, 1300, 'Acceso durante 3 meses'),
('Anual', 365, 4500, 'Acceso durante todo el año');

-- =====================================
-- GRUPOS MUSCULARES
-- =====================================
INSERT INTO grupos_musculares
(nombre, descripcion)
VALUES
('Pecho', 'Ejercicios para pectorales'),
('Espalda', 'Ejercicios para dorsales'),
('Pierna', 'Ejercicios inferiores'),
('Hombro', 'Ejercicios deltoides'),
('Brazos', 'Biceps y triceps'),
('Abdomen', 'Zona abdominal');

-- =====================================
-- USUARIOS
-- =====================================
INSERT INTO usuarios
(id_rol, usuario, nombre, correo, password_hash)
VALUES
(1, 'admin', 'Administrador General', 'admin@gym.com', '12345'),
(2, 'mgonzales', 'Maria Gónzales', 'recepcion@gym.com', '12345'),
(3, 'cmejia', 'Carlos Mejía', 'trainer@gym.com', '12345');

-- =====================================
-- CLIENTES
-- =====================================
INSERT INTO clientes
(nombre, apellido, telefono, correo, fecha_nacimiento, genero)
VALUES
('Juan', 'Perez', '99990001', 'juan@gmail.com', '1995-05-10', 'Masculino'),
('Ana', 'Martinez', '99990002', 'ana@gmail.com', '1998-08-15', 'Femenino'),
('Luis', 'Rodriguez', '99990003', 'luis@gmail.com', '1992-02-20', 'Masculino');

-- =====================================
-- ENTRENADORES
-- =====================================
INSERT INTO entrenadores
(id_usuario, especialidad, experiencia)
VALUES
(3, 'Musculación', '5 años de experiencia');

-- =====================================
-- MEMBRESIAS DE CLIENTES
-- =====================================
INSERT INTO cliente_membresias
(id_cliente, id_tipo_membresia, id_estado_membresia, fecha_inicio, fecha_fin)
VALUES
(1, 1, 1, '2026-08-01', '2026-08-31'),
(2, 2, 1, '2026-07-01', '2026-09-30'),
(3, 1, 2, '2026-06-01', '2026-06-30');

-- =====================================
-- PAGOS
-- =====================================
INSERT INTO pagos
(id_cliente, id_cliente_membresia, id_metodo_pago, monto, descripcion)
VALUES
(1, 1, 1, 500, 'Pago mensual'),
(2, 2, 2, 1300, 'Pago trimestral'),
(3, 3, 3, 500, 'Renovacion');

-- =====================================
-- ASISTENCIAS
-- =====================================
INSERT INTO asistencias
(id_cliente, fecha)
VALUES
(1, '2026-08-01 08:00:00'),
(1, '2026-08-02 09:15:00'),
(2, '2026-08-02 17:00:00');

-- =====================================
-- RUTINAS
-- =====================================
INSERT INTO rutinas
(id_entrenador, nombre, descripcion)
VALUES
(1, 'Hipertrofia inicial', 'Rutina para aumento muscular'),
(1, 'Pérdida de grasa', 'Rutina cardiovascular y fuerza');

-- =====================================
-- CLIENTE_RUTINAS
-- =====================================
INSERT INTO cliente_rutinas
(id_cliente, id_rutina, fecha_asignacion)
VALUES
(1, 1, '2026-08-01'),
(2, 2, '2026-08-01');

-- =====================================
-- EJERCICIOS
-- =====================================
INSERT INTO ejercicios
(id_grupo, nombre, descripcion)
VALUES
(1, 'Press banca', 'Ejercicio con barra para pecho'),
(1, 'Aperturas con mancuernas', 'Trabajo de pectoral'),
(2, 'Remo con barra', 'Ejercicio para espalda'),
(3, 'Sentadilla', 'Ejercicio compuesto de pierna'),
(5, 'Curl de biceps', 'Ejercicio de brazo');

-- =====================================
-- RUTINA_EJERCICIOS
-- =====================================
INSERT INTO rutina_ejercicios
(id_rutina, id_ejercicio, series, repeticiones, peso, descanso_segundos)
VALUES
(1, 1, 4, 10, 40, 90),
(1, 3, 4, 12, 35, 90),
(1, 4, 3, 10, 60, 120),
(2, 2, 3, 15, 15, 60),
(2, 5, 3, 12, 20, 60);