package com.tommy.gimnasio.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.tommy.gimnasio.adapters.MembershipTypeAdapter;
import com.tommy.gimnasio.database.DatabaseHelper;

public class MembershipsFragment extends Fragment {

    private RecyclerView rvMemberships;
    private DatabaseHelper dbHelper;
    private MembershipTypeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memberships, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        rvMemberships = view.findViewById(R.id.rvMemberships);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddMembership);

        rvMemberships.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MembershipTypeAdapter(dbHelper.getTiposMembresia(), this::showMembershipForm);
        rvMemberships.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showMembershipForm(-1, "", 30, 0.0, "", 1));

        return view;
    }

    private void showMembershipForm(int id, String nombre, int dias, double precio, String desc, int estado) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_membership_type_form, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogView).create();

        TextInputEditText etName = dialogView.findViewById(R.id.etMemName);
        TextInputEditText etDuration = dialogView.findViewById(R.id.etMemDuration);
        TextInputEditText etPrice = dialogView.findViewById(R.id.etMemPrice);
        TextInputEditText etDesc = dialogView.findViewById(R.id.etMemDesc);
        MaterialSwitch swStatus = dialogView.findViewById(R.id.swMemStatus);
        Button btnSave = dialogView.findViewById(R.id.btnSaveMem);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelMem);

        if (id != -1) {
            etName.setText(nombre);
            etDuration.setText(String.valueOf(dias));
            etPrice.setText(String.valueOf(precio));
            etDesc.setText(desc);
            swStatus.setChecked(estado == 1);
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String durationStr = etDuration.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String description = etDesc.getText().toString().trim();
            int status = swStatus.isChecked() ? 1 : 0;

            if (name.isEmpty() || durationStr.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(getContext(), "Completa los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            int duration = Integer.parseInt(durationStr);
            double price = Double.parseDouble(priceStr);

            if (id == -1) {
                dbHelper.insertarTipoMembresia(name, duration, price, description, status);
            } else {
                dbHelper.actualizarTipoMembresia(id, name, duration, price, description, status);
            }

            adapter.swapCursor(dbHelper.getTiposMembresia());
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
