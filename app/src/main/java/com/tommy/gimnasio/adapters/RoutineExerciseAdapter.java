package com.tommy.gimnasio.adapters;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tommy.gimnasio.R;

import java.util.Locale;

public class RoutineExerciseAdapter extends RecyclerView.Adapter<RoutineExerciseAdapter.ExerciseViewHolder> {

    private Cursor cursor;

    public RoutineExerciseAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_routine_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("ejercicio_nombre"));
            int series = cursor.getInt(cursor.getColumnIndexOrThrow("series"));
            int reps = cursor.getInt(cursor.getColumnIndexOrThrow("repeticiones"));
            double weight = cursor.getDouble(cursor.getColumnIndexOrThrow("peso"));
            int rest = cursor.getInt(cursor.getColumnIndexOrThrow("descanso_segundos"));

            holder.tvName.setText(name);
            holder.tvDetails.setText(String.format(Locale.getDefault(), "%d series x %d reps | %.1f kg", series, reps, weight));
            holder.tvRest.setText(String.format(Locale.getDefault(), "Descanso: %ds", rest));
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

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails, tvRest;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvExerciseName);
            tvDetails = itemView.findViewById(R.id.tvExerciseDetails);
            tvRest = itemView.findViewById(R.id.tvExerciseRest);
        }
    }
}
