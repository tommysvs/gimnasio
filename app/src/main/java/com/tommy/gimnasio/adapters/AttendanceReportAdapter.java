package com.tommy.gimnasio.adapters;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tommy.gimnasio.R;

public class AttendanceReportAdapter extends RecyclerView.Adapter<AttendanceReportAdapter.RepViewHolder> {

    private Cursor cursor;

    public AttendanceReportAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public RepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance_report, parent, false);
        return new RepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RepViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("cliente"));
            int total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));

            holder.tvName.setText(name);
            holder.tvCount.setText(total + " visitas");
        }
    }

    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    static class RepViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCount;

        public RepViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRepClientName);
            tvCount = itemView.findViewById(R.id.tvRepCount);
        }
    }
}
