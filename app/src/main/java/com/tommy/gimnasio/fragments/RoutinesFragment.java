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
import com.google.android.material.textfield.TextInputEditText;
import com.tommy.gimnasio.R;
import com.tommy.gimnasio.adapters.RoutineAdapter;
import com.tommy.gimnasio.database.DatabaseHelper;

public class RoutinesFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private RoutineAdapter adapter;
    private int currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routines, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        if (getActivity() != null && getActivity().getIntent() != null) {
            currentUserId = getActivity().getIntent().getIntExtra("USER_ID", 1);
        }
        
        RecyclerView rvRoutines = view.findViewById(R.id.rvRoutines);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddRoutine);

        rvRoutines.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RoutineAdapter(dbHelper.getRutinas(), new RoutineAdapter.OnRoutineClickListener() {
            @Override
            public void onEditClick(int id, String nombre, String descripcion, int idEntrenador) {
                showRoutineForm(id, nombre, descripcion, idEntrenador);
            }

            @Override
            public void onItemClick(int id, String nombre, String descripcion) {
                // Navegar al detalle de la rutina
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, RoutineDetailFragment.newInstance(id, nombre, descripcion))
                        .addToBackStack(null)
                        .commit();
            }
        });
        rvRoutines.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showRoutineForm(-1, "", "", currentUserId));

        return view;
    }

    private void showRoutineForm(int id, String nombre, String descripcion, int idEntrenador) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_routine_form, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogView).create();

        TextInputEditText etName = dialogView.findViewById(R.id.etRoutineName);
        TextInputEditText etDesc = dialogView.findViewById(R.id.etRoutineDesc);
        Button btnSave = dialogView.findViewById(R.id.btnSaveRoutine);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelRoutine);

        if (id != -1) {
            etName.setText(nombre);
            etDesc.setText(descripcion);
        }

        btnSave.setOnClickListener(v -> {
            if (etName.getText() == null) return;
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText() != null ? etDesc.getText().toString().trim() : "";

            if (name.isEmpty()) {
                Toast.makeText(getContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            if (id == -1) {
                dbHelper.insertarRutina(idEntrenador, name, desc);
            } else {
                dbHelper.actualizarRutina(id, idEntrenador, name, desc);
            }

            adapter.swapCursor(dbHelper.getRutinas());
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
