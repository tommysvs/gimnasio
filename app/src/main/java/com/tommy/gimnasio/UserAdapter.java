package com.tommy.gimnasio;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Cursor cursor;
    private final OnUserClickListener listener;

    public interface OnUserClickListener {
        void onEditClick(int idUsuario, String nombre, String usuario, String correo, int idRol, int estado);
    }

    public UserAdapter(Cursor cursor, OnUserClickListener listener) {
        this.cursor = cursor;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario"));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
            String usuario = cursor.getString(cursor.getColumnIndexOrThrow("usuario"));
            String correo = cursor.getString(cursor.getColumnIndexOrThrow("correo"));
            String rol = cursor.getString(cursor.getColumnIndexOrThrow("rol_nombre"));
            int idRol = cursor.getInt(cursor.getColumnIndexOrThrow("id_rol"));
            int estado = cursor.getInt(cursor.getColumnIndexOrThrow("estado"));

            holder.tvUserName.setText(nombre);
            holder.tvUserLogin.setText(usuario + " | " + correo);
            holder.tvUserRole.setText(rol);
            
            if (estado == 1) {
                holder.tvStatus.setText("Activo");
                holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
            } else {
                holder.tvStatus.setText("Inactivo");
                holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
            }

            holder.btnEdit.setOnClickListener(v -> listener.onEditClick(id, nombre, usuario, correo, idRol, estado));
        }
    }

    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) cursor.close();
        cursor = newCursor;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserLogin, tvUserRole, tvStatus;
        ImageButton btnEdit;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserLogin = itemView.findViewById(R.id.tvUserLogin);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
