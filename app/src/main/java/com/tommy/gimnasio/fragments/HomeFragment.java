package com.tommy.gimnasio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.tommy.gimnasio.R;
import com.tommy.gimnasio.activities.MainActivity;
import com.tommy.gimnasio.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView tvWelcome, tvDate, tvAttendance, tvIncome, tvNewClients;
    private MaterialCardView cardAttendance, cardNewClient, cardRoutines, cardPayments;
    private View summaryTitle, summaryLayout, summaryDivider;
    private MaterialCardView cardSumAttendance, cardSumIncome, cardSumNewClients;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvDate = view.findViewById(R.id.tvDate);
        
        summaryTitle = view.findViewById(R.id.tvSummaryTitle);
        summaryLayout = view.findViewById(R.id.layoutSummaryCards);
        summaryDivider = view.findViewById(R.id.homeDivider);
        cardSumAttendance = view.findViewById(R.id.cardSummaryAttendance);
        cardSumIncome = view.findViewById(R.id.cardSummaryIncome);
        cardSumNewClients = view.findViewById(R.id.cardSummaryNewClients);
        
        tvAttendance = view.findViewById(R.id.tvHomeAttendance);
        tvIncome = view.findViewById(R.id.tvHomeIncome);
        tvNewClients = view.findViewById(R.id.tvHomeNewClients);

        cardAttendance = view.findViewById(R.id.cardAttendance);
        cardNewClient = view.findViewById(R.id.cardNewClient);
        cardRoutines = view.findViewById(R.id.cardRoutines);
        cardPayments = view.findViewById(R.id.cardPayments);

        setupHeader();
        loadTodaySummary();
        applyRolePermissions();
        setupShortcuts();

        return view;
    }

    private void loadTodaySummary() {
        int attendance = dbHelper.getCountAsistenciasHoy();
        double income = dbHelper.getIngresosHoy();
        int newClients = dbHelper.getCountNuevosClientesHoy();

        tvAttendance.setText(String.valueOf(attendance));
        tvIncome.setText(String.format(Locale.getDefault(), "L %.0f", income));
        tvNewClients.setText(String.valueOf(newClients));
    }

    private void applyRolePermissions() {
        if (getActivity() == null || getActivity().getIntent() == null) return;
        
        int roleId = getActivity().getIntent().getIntExtra("ROLE_ID", -1);

        if (roleId == 3) { // Entrenador
            if (cardSumIncome != null) cardSumIncome.setVisibility(View.GONE);
            if (cardSumNewClients != null) cardSumNewClients.setVisibility(View.GONE);
        } else if (roleId == 4) { // Cliente
            if (summaryTitle != null) summaryTitle.setVisibility(View.GONE);
            if (summaryLayout != null) summaryLayout.setVisibility(View.GONE);
            if (summaryDivider != null) summaryDivider.setVisibility(View.GONE);
        }

        if (roleId == 3) { // Entrenador
            cardNewClient.setVisibility(View.GONE);
            cardPayments.setVisibility(View.GONE);
        }

        if (roleId == 4) { // Cliente
            cardAttendance.setVisibility(View.GONE);
            cardNewClient.setVisibility(View.GONE);
            cardPayments.setVisibility(View.GONE);
        }
    }

    private void setupHeader() {
        String currentDate = new SimpleDateFormat("EEEE, d 'de' MMMM", new Locale("es", "ES")).format(new Date());
        tvDate.setText(currentDate);

        if (getActivity() != null && getActivity().getIntent() != null) {
            String userName = getActivity().getIntent().getStringExtra("USER_NAME");
            if (userName != null) {
                tvWelcome.setText("¡Hola, " + userName + "!");
            }
        }
    }

    private void setupShortcuts() {
        cardAttendance.setOnClickListener(v -> navigateTo(R.id.nav_attendance));
        cardNewClient.setOnClickListener(v -> navigateTo(R.id.nav_clients));
        cardRoutines.setOnClickListener(v -> navigateTo(R.id.nav_routines));
        cardPayments.setOnClickListener(v -> navigateTo(R.id.nav_payments));
    }

    private void navigateTo(int menuId) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).handleNavigation(menuId);
        }
    }
}
