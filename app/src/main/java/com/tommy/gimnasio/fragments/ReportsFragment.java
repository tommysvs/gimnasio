package com.tommy.gimnasio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tommy.gimnasio.R;
import com.tommy.gimnasio.adapters.AttendanceReportAdapter;
import com.tommy.gimnasio.database.DatabaseHelper;

import java.util.Locale;

public class ReportsFragment extends Fragment {

    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        TextView tvActiveMembers = view.findViewById(R.id.tvReportActiveMembers);
        TextView tvMonthlyIncome = view.findViewById(R.id.tvReportMonthlyIncome);
        RecyclerView rvAttendance = view.findViewById(R.id.rvAttendanceReport);

        tvActiveMembers.setText(String.valueOf(dbHelper.getCountMembresiasActivas()));
        tvMonthlyIncome.setText(String.format(Locale.getDefault(), "L %.2f", dbHelper.getIngresosMesActual()));

        rvAttendance.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAttendance.setAdapter(new AttendanceReportAdapter(dbHelper.getReporteAsistencia()));
        rvAttendance.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        return view;
    }
}
