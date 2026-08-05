package com.tommy.gimnasio.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "gimnasio.db";
    private final Context myContext;
    private final String DATABASE_PATH;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.myContext = context;
        this.DATABASE_PATH = context.getDatabasePath(DATABASE_NAME).getPath();
        
        if (!new File(DATABASE_PATH).exists()) {
            copyDatabaseFromAssets();
        }
    }

    private void copyDatabaseFromAssets() {
        try {
            File dbFile = new File(DATABASE_PATH);
            dbFile.getParentFile().mkdirs();

            try (InputStream is = myContext.getAssets().open(DATABASE_NAME);
                 OutputStream os = new FileOutputStream(DATABASE_PATH)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) os.write(buffer, 0, length);
                os.flush();
            }
        } catch (IOException e) {
            Log.e("DB_HELPER", "Error al inicializar base de datos", e);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public Cursor validarUsuario(String usuario, String password) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
            String query = "SELECT u.id_usuario, u.nombre, u.usuario, u.id_rol, r.nombre as rol_nombre " +
                    "FROM usuarios u " +
                    "INNER JOIN roles r ON u.id_rol = r.id_rol " +
                    "WHERE u.usuario = ? AND u.password_hash = ? AND u.estado = 1";
            return db.rawQuery(query, new String[]{usuario, password});
        } catch (Exception e) {
            return null;
        }
    }

    public boolean verificarCorreo(String correo) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
            Cursor cursor = db.rawQuery("SELECT id_usuario FROM usuarios WHERE correo = ? AND estado = 1", new String[]{correo});
            boolean existe = cursor.getCount() > 0;
            cursor.close();
            return existe;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean actualizarPassword(String correo, String nuevaPassword) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
            ContentValues values = new ContentValues();
            values.put("password_hash", nuevaPassword);
            int resultado = db.update("usuarios", values, "correo = ?", new String[]{correo});
            return resultado > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public Cursor getUsuarios() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT u.id_usuario, u.nombre, u.usuario, u.correo, u.estado, u.id_rol, r.nombre as rol_nombre " +
                "FROM usuarios u " +
                "INNER JOIN roles r ON u.id_rol = r.id_rol " +
                "ORDER BY u.id_usuario DESC";
        return db.rawQuery(query, null);
    }

    public Cursor getRoles() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
        return db.rawQuery("SELECT id_rol, nombre FROM roles", null);
    }

    public long insertarUsuario(int idRol, String nombre, String correo, String usuario, String password, int estado) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("id_rol", idRol);
        values.put("nombre", nombre);
        values.put("correo", correo);
        values.put("usuario", usuario);
        values.put("password_hash", password);
        values.put("estado", estado);
        return db.insert("usuarios", null, values);
    }

    public int actualizarUsuario(int idUsuario, int idRol, String nombre, String correo, String usuario, String password, int estado) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("id_rol", idRol);
        values.put("nombre", nombre);
        values.put("correo", correo);
        values.put("usuario", usuario);
        if (password != null && !password.isEmpty()) {
            values.put("password_hash", password);
        }
        values.put("estado", estado);
        return db.update("usuarios", values, "id_usuario = ?", new String[]{String.valueOf(idUsuario)});
    }

    public int cambiarEstadoUsuario(int idUsuario, int nuevoEstado) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("estado", nuevoEstado);
        return db.update("usuarios", values, "id_usuario = ?", new String[]{String.valueOf(idUsuario)});
    }

    public Cursor getTiposMembresia() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
        return db.rawQuery("SELECT * FROM tipos_membresia ORDER BY precio ASC", null);
    }

    public long insertarTipoMembresia(String nombre, int dias, double precio, String desc, int estado) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("duracion_dias", dias);
        values.put("precio", precio);
        values.put("descripcion", desc);
        values.put("estado", estado);
        return db.insert("tipos_membresia", null, values);
    }

    public int actualizarTipoMembresia(int id, String nombre, int dias, double precio, String desc, int estado) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("duracion_dias", dias);
        values.put("precio", precio);
        values.put("descripcion", desc);
        values.put("estado", estado);
        return db.update("tipos_membresia", values, "id_tipo_membresia = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getClientes() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
        return db.rawQuery("SELECT * FROM clientes ORDER BY nombre ASC", null);
    }

    public long insertarCliente(String nombre, String apellido, String telefono, String correo, String fechaNac, String genero, int estado) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("apellido", apellido);
        values.put("telefono", telefono);
        values.put("correo", correo);
        values.put("fecha_nacimiento", fechaNac);
        values.put("genero", genero);
        values.put("estado", estado);
        return db.insert("clientes", null, values);
    }

    public int actualizarCliente(int id, String nombre, String apellido, String telefono, String correo, String fechaNac, String genero, int estado) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("apellido", apellido);
        values.put("telefono", telefono);
        values.put("correo", correo);
        values.put("fecha_nacimiento", fechaNac);
        values.put("genero", genero);
        values.put("estado", estado);
        return db.update("clientes", values, "id_cliente = ?", new String[]{String.valueOf(id)});
    }

    // Pagos
    public Cursor getPagos() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT p.*, c.nombre || ' ' || c.apellido as cliente_nombre " +
                "FROM pagos p " +
                "INNER JOIN clientes c ON p.id_cliente = c.id_cliente " +
                "ORDER BY p.fecha_pago DESC";
        return db.rawQuery(query, null);
    }

    public long registrarPago(int idCliente, Integer idClienteMembresia, int idMetodo, double monto, String fecha) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("id_cliente", idCliente);
        if (idClienteMembresia != null) values.put("id_cliente_membresia", idClienteMembresia);
        values.put("id_metodo_pago", idMetodo);
        values.put("monto", monto);
        values.put("fecha_pago", fecha);
        return db.insert("pagos", null, values);
    }

    public Cursor getClienteMembresiasActivas(int idCliente) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT cm.id_cliente_membresia, tm.nombre " +
                "FROM cliente_membresias cm " +
                "INNER JOIN tipos_membresia tm ON cm.id_tipo_membresia = tm.id_tipo_membresia " +
                "WHERE cm.id_cliente = ? AND cm.id_estado_membresia = 1"; // 1 = Activa
        return db.rawQuery(query, new String[]{String.valueOf(idCliente)});
    }

    // Asistencias
    public Cursor getAsistenciasHoy() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT a.id_asistencia, a.fecha, c.nombre || ' ' || c.apellido as cliente_nombre " +
                "FROM asistencias a " +
                "INNER JOIN clientes c ON a.id_cliente = c.id_cliente " +
                "WHERE date(a.fecha) = date('now', 'localtime') " +
                "ORDER BY a.fecha DESC";
        return db.rawQuery(query, null);
    }

    public boolean tieneMembresiaActiva(int idCliente) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT COUNT(*) FROM cliente_membresias " +
                "WHERE id_cliente = ? AND id_estado_membresia = 1 " +
                "AND date('now', 'localtime') BETWEEN date(fecha_inicio) AND date(fecha_fin)";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idCliente)});
        boolean activa = false;
        if (cursor.moveToFirst()) {
            activa = cursor.getInt(0) > 0;
        }
        cursor.close();
        return activa;
    }

    public long registrarAsistencia(int idCliente) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("id_cliente", idCliente);
        values.put("fecha", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date()));
        return db.insert("asistencias", null, values);
    }

    // Rutinas
    public Cursor getRutinas() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT r.*, u.nombre as entrenador_nombre " +
                "FROM rutinas r " +
                "LEFT JOIN entrenadores e ON r.id_entrenador = e.id_entrenador " +
                "LEFT JOIN usuarios u ON e.id_usuario = u.id_usuario " +
                "ORDER BY r.nombre ASC";
        return db.rawQuery(query, null);
    }

    public long insertarRutina(int idEntrenador, String nombre, String descripcion) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("id_entrenador", idEntrenador);
        values.put("nombre", nombre);
        values.put("descripcion", descripcion);
        return db.insert("rutinas", null, values);
    }

    public int actualizarRutina(int idRutina, int idEntrenador, String nombre, String descripcion) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("id_entrenador", idEntrenador);
        values.put("nombre", nombre);
        values.put("descripcion", descripcion);
        return db.update("rutinas", values, "id_rutina = ?", new String[]{String.valueOf(idRutina)});
    }

    public Cursor getEjercicios() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
        return db.rawQuery("SELECT * FROM ejercicios ORDER BY nombre ASC", null);
    }

    public Cursor getDetalleRutina(int idRutina) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT re.*, e.nombre as ejercicio_nombre " +
                "FROM rutina_ejercicios re " +
                "INNER JOIN ejercicios e ON re.id_ejercicio = e.id_ejercicio " +
                "WHERE re.id_rutina = ?";
        return db.rawQuery(query, new String[]{String.valueOf(idRutina)});
    }

    public long agregarEjercicioARutina(int idRutina, int idEjercicio, int series, int reps, double peso, int descansoSegundos) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("id_rutina", idRutina);
        values.put("id_ejercicio", idEjercicio);
        values.put("series", series);
        values.put("repeticiones", reps);
        values.put("peso", peso);
        values.put("descanso_segundos", descansoSegundos);
        return db.insert("rutina_ejercicios", null, values);
    }

    // Reportes
    public int getCountMembresiasActivas() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT COUNT(*) FROM cliente_membresias WHERE id_estado_membresia = 1 " +
                "AND date('now', 'localtime') BETWEEN date(fecha_inicio) AND date(fecha_fin)";
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public double getIngresosMesActual() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
        String currentMonth = new java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.getDefault()).format(new java.util.Date());
        String query = "SELECT SUM(monto) FROM pagos WHERE strftime('%Y-%m', fecha_pago) = ?";
        Cursor cursor = db.rawQuery(query, new String[]{currentMonth});
        double sum = 0;
        if (cursor.moveToFirst()) sum = cursor.getDouble(0);
        cursor.close();
        return sum;
    }

    public Cursor getReporteAsistencia() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT c.nombre || ' ' || c.apellido as cliente, COUNT(a.id_asistencia) as total " +
                "FROM clientes c " +
                "LEFT JOIN asistencias a ON c.id_cliente = a.id_cliente " +
                "GROUP BY c.id_cliente " +
                "ORDER BY total DESC";
        return db.rawQuery(query, null);
    }
}
