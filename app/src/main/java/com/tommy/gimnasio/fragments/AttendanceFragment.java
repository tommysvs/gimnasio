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
import com.tommy.gimnasio.R;
import com.tommy.gimnasio.adapters.AttendanceAdapter;
import com.tommy.gimnasio.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class AttendanceFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private AttendanceAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        RecyclerView rvAttendance = view.findViewById(R.id.rvAttendance);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddAttendance);

        rvAttendance.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AttendanceAdapter(dbHelper.getAsistenciasHoy());
        rvAttendance.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showAttendanceDialog());

        return view;
    }

    private void showAttendanceDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_payment_form, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogView).create();

        ((android.widget.TextView)dialogView.findViewById(R.id.tvDialogTitle)).setText("Marcar asistencia");
        
        dialogView.findViewById(R.id.tilPayAmount).setVisibility(View.GONE);
        dialogView.findViewById(R.id.tvPayMethodLabel).setVisibility(View.GONE);
        dialogView.findViewById(R.id.spnPayMethod).setVisibility(View.GONE);

        Spinner spnClient = dialogView.findViewById(R.id.spnPayClient);
        Button btnSave = dialogView.findViewById(R.id.btnSavePay);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelPay);

        btnSave.setText("REGISTRAR ENTRADA");

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
            
            int clientId = clientsIds.get(spnClient.getSelectedItemPosition());

            if (dbHelper.tieneMembresiaActiva(clientId)) {
                dbHelper.registrarAsistencia(clientId);
                Toast.makeText(getContext(), "Asistencia registrada correctamente", Toast.LENGTH_SHORT).show();
                adapter.swapCursor(dbHelper.getAsistenciasHoy());
                dialog.dismiss();
            } else {
                new AlertDialog.Builder(getContext())
                    .setTitle("Atención")
                    .setMessage("El cliente NO tiene una membresía activa vigente.")
                    .setPositiveButton("Registrar de todos modos", (d, w) -> {
                        dbHelper.registrarAsistencia(clientId);
                        adapter.swapCursor(dbHelper.getAsistenciasHoy());
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
