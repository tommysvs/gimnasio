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

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PayViewHolder> {

    private Cursor cursor;

    public PaymentAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public PayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
        return new PayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PayViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            String clientName = cursor.getString(cursor.getColumnIndexOrThrow("cliente_nombre"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("fecha_pago"));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("monto"));

            holder.tvClientName.setText(clientName);
            holder.tvDate.setText(date);
            holder.tvAmount.setText(String.format(Locale.getDefault(), "L %.2f", amount));
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

    static class PayViewHolder extends RecyclerView.ViewHolder {
        TextView tvClientName, tvDate, tvAmount;

        public PayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClientName = itemView.findViewById(R.id.tvPayClientName);
            tvDate = itemView.findViewById(R.id.tvPayDate);
            tvAmount = itemView.findViewById(R.id.tvPayAmount);
        }
    }
}
