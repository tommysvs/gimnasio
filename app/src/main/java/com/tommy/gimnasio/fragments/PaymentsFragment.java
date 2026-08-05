package com.tommy.gimnasio.fragments;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.tommy.gimnasio.R;
import com.tommy.gimnasio.adapters.PaymentAdapter;
import com.tommy.gimnasio.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentsFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private PaymentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payments, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        RecyclerView rvPayments = view.findViewById(R.id.rvPayments);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddPayment);

        rvPayments.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PaymentAdapter(dbHelper.getPagos());
        rvPayments.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showPaymentForm());

        return view;
    }

    private void showPaymentForm() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_payment_form, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogView).create();

        Spinner spnClient = dialogView.findViewById(R.id.spnPayClient);
        TextInputEditText etAmount = dialogView.findViewById(R.id.etPayAmount);
        Spinner spnMethod = dialogView.findViewById(R.id.spnPayMethod);
        Button btnSave = dialogView.findViewById(R.id.btnSavePay);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelPay);

        Cursor clientsCursor = dbHelper.getClientes();
        List<String> clientsList = new ArrayList<>();
        List<Integer> clientsIds = new ArrayList<>();
        while (clientsCursor.moveToNext()) {
            clientsIds.add(clientsCursor.getInt(clientsCursor.getColumnIndexOrThrow("id_cliente")));
            clientsList.add(clientsCursor.getString(clientsCursor.getColumnIndexOrThrow("nombre")) + " " + 
                           clientsCursor.getString(clientsCursor.getColumnIndexOrThrow("apellido")));
        }
        clientsCursor.close();
        ArrayAdapter<String> clientAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, clientsList);
        clientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnClient.setAdapter(clientAdapter);

        btnSave.setOnClickListener(v -> {
            if (spnClient.getSelectedItemPosition() == -1) return;
            
            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(getContext(), "Ingresa el monto", Toast.LENGTH_SHORT).show();
                return;
            }

            int clientId = clientsIds.get(spnClient.getSelectedItemPosition());
            double amount = Double.parseDouble(amountStr);
            int methodId = spnMethod.getSelectedItemPosition() + 1; // 1-based index
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            long result = dbHelper.registrarPago(clientId, null, methodId, amount, date);

            if (result != -1) {
                Toast.makeText(getContext(), "Pago registrado", Toast.LENGTH_SHORT).show();
                adapter.swapCursor(dbHelper.getPagos());
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Error al registrar pago", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
