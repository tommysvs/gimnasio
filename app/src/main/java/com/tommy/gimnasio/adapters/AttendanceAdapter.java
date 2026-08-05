package com.tommy.gimnasio.adapters;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tommy.gimnasio.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttViewHolder> {

    private Cursor cursor;

    public AttendanceAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public AttViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance, parent, false);
        return new AttViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            String clientName = cursor.getString(cursor.getColumnIndexOrThrow("cliente_nombre"));
            String fullDate = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));

            holder.tvClientName.setText(clientName);
            
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(fullDate);
                if (date != null) {
                    String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date);
                    holder.tvTime.setText(time);
                }
            } catch (Exception e) {
                holder.tvTime.setText(fullDate);
            }
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

    static class AttViewHolder extends RecyclerView.ViewHolder {
        TextView tvClientName, tvTime;

        public AttViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClientName = itemView.findViewById(R.id.tvAttClientName);
            tvTime = itemView.findViewById(R.id.tvAttTime);
        }
    }
}
