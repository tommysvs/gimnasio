package com.tommy.gimnasio.adapters;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tommy.gimnasio.R;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {

    private Cursor cursor;
    private final OnClientClickListener listener;

    public interface OnClientClickListener {
        void onEditClick(int id, String nombre, String apellido, String telefono, String correo, String fechaNac, String genero, int estado, int idTipoMembresia);
    }

    public ClientAdapter(Cursor cursor, OnClientClickListener listener) {
        this.cursor = cursor;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_client, parent, false);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente"));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
            String apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"));
            String telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono"));
            String correo = cursor.getString(cursor.getColumnIndexOrThrow("correo"));
            String fechaNac = cursor.getString(cursor.getColumnIndexOrThrow("fecha_nacimiento"));
            String genero = cursor.getString(cursor.getColumnIndexOrThrow("genero"));
            int estado = cursor.getInt(cursor.getColumnIndexOrThrow("estado"));
            int idTipoMembresia = cursor.isNull(cursor.getColumnIndexOrThrow("id_tipo_membresia")) ? -1 : cursor.getInt(cursor.getColumnIndexOrThrow("id_tipo_membresia"));
            String membresia = cursor.getString(cursor.getColumnIndexOrThrow("membresia_nombre"));
            if (membresia == null || membresia.trim().isEmpty()) {
                membresia = "Sin membresía";
            }

            holder.tvFullName.setText(nombre + " " + apellido);
            holder.tvPhone.setText(telefono);
            holder.tvEmail.setText(correo);
            holder.tvMembership.setText("Membresía: " + membresia);
            
            if (estado == 1) {
                holder.tvStatus.setText("Activo");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_active);
            } else {
                holder.tvStatus.setText("Inactivo");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_inactive);
            }

            holder.btnEdit.setOnClickListener(v -> listener.onEditClick(id, nombre, apellido, telefono, correo, fechaNac, genero, estado, idTipoMembresia));
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

    public static class ClientViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullName, tvPhone, tvEmail, tvMembership, tvStatus;
        ImageButton btnEdit;

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvClientFullName);
            tvPhone = itemView.findViewById(R.id.tvClientPhone);
            tvEmail = itemView.findViewById(R.id.tvClientEmail);
            tvMembership = itemView.findViewById(R.id.tvClientMembership);
            tvStatus = itemView.findViewById(R.id.tvClientStatus);
            btnEdit = itemView.findViewById(R.id.btnEditClient);
        }
    }
}
