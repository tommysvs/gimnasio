package com.tommy.gimnasio;

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

    // Login
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

    // Usuarios
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

    // Membresías
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

    // Clientes
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
}
