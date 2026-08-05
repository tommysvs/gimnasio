package com.tommy.gimnasio;

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
}
