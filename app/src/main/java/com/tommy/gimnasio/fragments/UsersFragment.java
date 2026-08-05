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
import com.tommy.gimnasio.adapters.UserAdapter;
import com.tommy.gimnasio.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private RecyclerView rvUsers;
    private FloatingActionButton fabAddUser;
    private DatabaseHelper dbHelper;
    private UserAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        rvUsers = view.findViewById(R.id.rvUsers);
        fabAddUser = view.findViewById(R.id.fabAddUser);

        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserAdapter(dbHelper.getUsuarios(), (id, nombre, usuario, correo, idRol, estado) -> 
            showUserForm(id, nombre, usuario, correo, idRol, estado));
        rvUsers.setAdapter(adapter);

        fabAddUser.setOnClickListener(v -> showUserForm(-1, "", "", "", 1, 1));

        return view;
    }

    private void showUserForm(int idUsuario, String nombre, String usuario, String correo, int idRol, int estado) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_user_form, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogView).create();

        TextInputEditText etName = dialogView.findViewById(R.id.etName);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etEmail);
        TextInputEditText etUsername = dialogView.findViewById(R.id.etUsername);
        TextInputEditText etPassword = dialogView.findViewById(R.id.etPassword);
        Spinner spnRole = dialogView.findViewById(R.id.spnRole);
        MaterialSwitch swStatus = dialogView.findViewById(R.id.swStatus);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Llenar roles
        Cursor rolesCursor = dbHelper.getRoles();
        List<String> rolesList = new ArrayList<>();
        List<Integer> rolesIds = new ArrayList<>();
        while (rolesCursor.moveToNext()) {
            rolesIds.add(rolesCursor.getInt(0));
            rolesList.add(rolesCursor.getString(1));
        }
        rolesCursor.close();
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, rolesList);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRole.setAdapter(roleAdapter);

        // Si es edición, cargar datos
        if (idUsuario != -1) {
            etName.setText(nombre);
            etEmail.setText(correo);
            etUsername.setText(usuario);
            swStatus.setChecked(estado == 1);
            // Seleccionar rol en spinner
            int rolePos = rolesIds.indexOf(idRol);
            if (rolePos != -1) spnRole.setSelection(rolePos);
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String mail = etEmail.getText().toString().trim();
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            int roleId = rolesIds.get(spnRole.getSelectedItemPosition());
            int status = swStatus.isChecked() ? 1 : 0;

            if (name.isEmpty() || mail.isEmpty() || user.isEmpty()) {
                Toast.makeText(getContext(), "Campos obligatorios vacíos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (idUsuario == -1) {
                if (pass.isEmpty()) {
                    Toast.makeText(getContext(), "La contraseña es obligatoria para nuevos usuarios", Toast.LENGTH_SHORT).show();
                    return;
                }
                dbHelper.insertarUsuario(roleId, name, mail, user, pass, status);
            } else {
                dbHelper.actualizarUsuario(idUsuario, roleId, name, mail, user, pass, status);
            }

            adapter.swapCursor(dbHelper.getUsuarios());
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
