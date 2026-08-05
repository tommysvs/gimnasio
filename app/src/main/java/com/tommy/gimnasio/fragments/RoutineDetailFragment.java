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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.tommy.gimnasio.R;
import com.tommy.gimnasio.adapters.RoutineExerciseAdapter;
import com.tommy.gimnasio.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class RoutineDetailFragment extends Fragment {

    private int routineId;
    private String routineName, routineDesc;
    private DatabaseHelper dbHelper;
    private RoutineExerciseAdapter adapter;

    public static RoutineDetailFragment newInstance(int id, String name, String desc) {
        RoutineDetailFragment fragment = new RoutineDetailFragment();
        Bundle args = new Bundle();
        args.putInt("ROUTINE_ID", id);
        args.putString("ROUTINE_NAME", name);
        args.putString("ROUTINE_DESC", desc);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            routineId = getArguments().getInt("ROUTINE_ID");
            routineName = getArguments().getString("ROUTINE_NAME");
            routineDesc = getArguments().getString("ROUTINE_DESC");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routine_detail, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        
        TextView tvName = view.findViewById(R.id.tvDetailRoutineName);
        TextView tvDesc = view.findViewById(R.id.tvDetailRoutineDesc);
        RecyclerView rvExercises = view.findViewById(R.id.rvRoutineExercises);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddExercise);

        tvName.setText(routineName);
        tvDesc.setText(routineDesc);

        rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RoutineExerciseAdapter(dbHelper.getDetalleRutina(routineId));
        rvExercises.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showAddExerciseDialog());

        return view;
    }

    private void showAddExerciseDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_exercise, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogView).create();

        Spinner spnExercise = dialogView.findViewById(R.id.spnExercise);
        TextInputEditText etSeries = dialogView.findViewById(R.id.etSeries);
        TextInputEditText etReps = dialogView.findViewById(R.id.etReps);
        TextInputEditText etWeight = dialogView.findViewById(R.id.etWeight);
        TextInputEditText etRest = dialogView.findViewById(R.id.etRest);
        Button btnSave = dialogView.findViewById(R.id.btnSaveExercise);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelExercise);

        Cursor exerciseCursor = dbHelper.getEjercicios();
        List<String> names = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        while (exerciseCursor.moveToNext()) {
            ids.add(exerciseCursor.getInt(exerciseCursor.getColumnIndexOrThrow("id_ejercicio")));
            names.add(exerciseCursor.getString(exerciseCursor.getColumnIndexOrThrow("nombre")));
        }
        exerciseCursor.close();
        ArrayAdapter<String> exAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, names);
        exAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnExercise.setAdapter(exAdapter);

        btnSave.setOnClickListener(v -> {
            if (spnExercise.getSelectedItemPosition() == -1) return;

            String seriesStr = etSeries.getText().toString().trim();
            String repsStr = etReps.getText().toString().trim();
            String weightStr = etWeight.getText().toString().trim();
            String restStr = etRest.getText().toString().trim();

            if (seriesStr.isEmpty() || repsStr.isEmpty()) {
                Toast.makeText(getContext(), "Series y repeticiones son obligatorias", Toast.LENGTH_SHORT).show();
                return;
            }

            int exerciseId = ids.get(spnExercise.getSelectedItemPosition());
            int series = Integer.parseInt(seriesStr);
            int reps = Integer.parseInt(repsStr);
            double weight = weightStr.isEmpty() ? 0 : Double.parseDouble(weightStr);
            int rest = 0;
            try {
                rest = restStr.isEmpty() ? 0 : Integer.parseInt(restStr.replaceAll("[^0-9]", ""));
            } catch (Exception e) {
                rest = 0;
            }

            long res = dbHelper.agregarEjercicioARutina(routineId, exerciseId, series, reps, weight, rest);
            if (res != -1) {
                adapter.swapCursor(dbHelper.getDetalleRutina(routineId));
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Error al agregar", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
