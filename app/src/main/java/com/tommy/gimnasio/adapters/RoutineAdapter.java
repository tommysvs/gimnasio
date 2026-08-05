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

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder> {

    private Cursor cursor;
    private final OnRoutineClickListener listener;

    public interface OnRoutineClickListener {
        void onEditClick(int id, String nombre, String descripcion, int idEntrenador);
        void onItemClick(int id, String nombre, String descripcion);
    }

    public RoutineAdapter(Cursor cursor, OnRoutineClickListener listener) {
        this.cursor = cursor;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_routine, parent, false);
        return new RoutineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_rutina"));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
            String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
            String entrenador = cursor.getString(cursor.getColumnIndexOrThrow("entrenador_nombre"));
            int idEntrenador = cursor.getInt(cursor.getColumnIndexOrThrow("id_entrenador"));

            holder.tvName.setText(nombre);
            holder.tvDesc.setText(descripcion);
            holder.tvTrainer.setText("Entrenador: " + (entrenador != null ? entrenador : "N/A"));

            holder.btnEdit.setOnClickListener(v -> listener.onEditClick(id, nombre, descripcion, idEntrenador));
            holder.itemView.setOnClickListener(v -> listener.onItemClick(id, nombre, descripcion));
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

    static class RoutineViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvTrainer;
        ImageButton btnEdit;

        public RoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRoutineName);
            tvDesc = itemView.findViewById(R.id.tvRoutineDesc);
            tvTrainer = itemView.findViewById(R.id.tvTrainerName);
            btnEdit = itemView.findViewById(R.id.btnEditRoutine);
        }
    }
}
