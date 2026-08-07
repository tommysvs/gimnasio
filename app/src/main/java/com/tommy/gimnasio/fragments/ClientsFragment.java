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
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.tommy.gimnasio.R;
import com.tommy.gimnasio.adapters.ClientAdapter;
import com.tommy.gimnasio.database.DatabaseHelper;

public class ClientsFragment extends Fragment {

    private RecyclerView rvClients;
    private DatabaseHelper dbHelper;
    private ClientAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clients, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        rvClients = view.findViewById(R.id.rvClients);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddClient);

        rvClients.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClientAdapter(dbHelper.getClientes(), this::showClientForm);
        rvClients.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showClientForm(-1, "", "", "", "", "2000-01-01", "Masculino", 1, -1));

        return view;
    }

    private void showClientForm(int id, String nombre, String apellido, String telefono, String correo, String fechaNac, String genero, int estado, int idTipoMembresia) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_client_form, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogView).create();

        TextInputEditText etName = dialogView.findViewById(R.id.etClientName);
        TextInputEditText etLastName = dialogView.findViewById(R.id.etClientLastName);
        TextInputEditText etPhone = dialogView.findViewById(R.id.etClientPhone);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etClientEmail);
        TextInputEditText etBirth = dialogView.findViewById(R.id.etClientBirthDate);
        Spinner spnGender = dialogView.findViewById(R.id.spnClientGender);
        Spinner spnMembership = dialogView.findViewById(R.id.spnClientMembership);
        MaterialSwitch swStatus = dialogView.findViewById(R.id.swClientStatus);
        Button btnSave = dialogView.findViewById(R.id.btnSaveClient);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelClient);

        Cursor cursorMem = dbHelper.getTiposMembresia();
        java.util.List<String> memNames = new java.util.ArrayList<>();
        final java.util.List<Integer> memIds = new java.util.ArrayList<>();
        final java.util.List<Integer> memDurations = new java.util.ArrayList<>();

        memNames.add(getString(R.string.no_membership));
        memIds.add(-1);
        memDurations.add(0);

        if (cursorMem != null && cursorMem.moveToFirst()) {
            do {
                memNames.add(cursorMem.getString(cursorMem.getColumnIndexOrThrow("nombre")));
                memIds.add(cursorMem.getInt(cursorMem.getColumnIndexOrThrow("id_tipo_membresia")));
                memDurations.add(cursorMem.getInt(cursorMem.getColumnIndexOrThrow("duracion_dias")));
            } while (cursorMem.moveToNext());
            cursorMem.close();
        }

        ArrayAdapter<String> memAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, memNames);
        memAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMembership.setAdapter(memAdapter);

        if (id != -1) {
            etName.setText(nombre);
            etLastName.setText(apellido);
            etPhone.setText(telefono);
            etEmail.setText(correo);
            etBirth.setText(fechaNac);
            swStatus.setChecked(estado == 1);
            
            ArrayAdapter<CharSequence> genderAdapter = (ArrayAdapter<CharSequence>) spnGender.getAdapter();
            if (genderAdapter != null) {
                int pos = genderAdapter.getPosition(genero);
                if (pos != -1) spnGender.setSelection(pos);
            }

            if (idTipoMembresia != -1) {
                int pos = memIds.indexOf(idTipoMembresia);
                if (pos != -1) spnMembership.setSelection(pos);
            }
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String mail = etEmail.getText().toString().trim();
            String birth = etBirth.getText().toString().trim();
            String gender = spnGender.getSelectedItem().toString();
            int status = swStatus.isChecked() ? 1 : 0;
            
            int selectedMemIndex = spnMembership.getSelectedItemPosition();
            int memId = memIds.get(selectedMemIndex);
            int memDuration = memDurations.get(selectedMemIndex);

            if (name.isEmpty() || lastName.isEmpty() || phone.isEmpty()) {
                Toast.makeText(getContext(), "Nombres, apellidos y teléfono son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (id == -1) {
                long newId = dbHelper.insertarCliente(name, lastName, phone, mail, birth, gender, status);
                if (newId != -1 && memId != -1) {
                    dbHelper.asignarMembresia((int) newId, memId, memDuration);
                }
            } else {
                dbHelper.actualizarCliente(id, name, lastName, phone, mail, birth, gender, status);
                if (memId != idTipoMembresia) {
                    if (memId != -1) {
                        dbHelper.asignarMembresia(id, memId, memDuration);
                    } else {
                        dbHelper.desactivarMembresiasActivas(id);
                    }
                }
            }

            adapter.swapCursor(dbHelper.getClientes());
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
