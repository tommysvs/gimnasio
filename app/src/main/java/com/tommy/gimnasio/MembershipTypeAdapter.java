package com.tommy.gimnasio;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class MembershipTypeAdapter extends RecyclerView.Adapter<MembershipTypeAdapter.MemViewHolder> {

    private Cursor cursor;
    private final OnMemClickListener listener;

    public interface OnMemClickListener {
        void onEditClick(int id, String nombre, int dias, double precio, String desc, int estado);
    }

    public MembershipTypeAdapter(Cursor cursor, OnMemClickListener listener) {
        this.cursor = cursor;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_membership_type, parent, false);
        return new MemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_tipo_membresia"));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
            int dias = cursor.getInt(cursor.getColumnIndexOrThrow("duracion_dias"));
            double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
            int estado = cursor.getInt(cursor.getColumnIndexOrThrow("estado"));

            holder.tvName.setText(nombre);
            holder.tvDesc.setText(desc);
            holder.tvDuration.setText(String.format(Locale.getDefault(), "%d días", dias));
            holder.tvPrice.setText(String.format(Locale.getDefault(), "L %.2f", precio));

            if (estado == 0) {
                holder.itemView.setAlpha(0.6f);
            } else {
                holder.itemView.setAlpha(1.0f);
            }

            holder.btnEdit.setOnClickListener(v -> listener.onEditClick(id, nombre, dias, precio, desc, estado));
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

    static class MemViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvDuration, tvPrice;
        ImageButton btnEdit;

        public MemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMemName);
            tvDesc = itemView.findViewById(R.id.tvMemDesc);
            tvDuration = itemView.findViewById(R.id.tvMemDuration);
            tvPrice = itemView.findViewById(R.id.tvMemPrice);
            btnEdit = itemView.findViewById(R.id.btnEditMem);
        }
    }
}
