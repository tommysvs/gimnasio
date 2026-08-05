package com.tommy.gimnasio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.tommy.gimnasio.R;
import com.tommy.gimnasio.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView tvWelcome, tvDate;
    private MaterialCardView cardAttendance, cardNewClient, cardRoutines, cardPayments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvDate = view.findViewById(R.id.tvDate);
        cardAttendance = view.findViewById(R.id.cardAttendance);
        cardNewClient = view.findViewById(R.id.cardNewClient);
        cardRoutines = view.findViewById(R.id.cardRoutines);
        cardPayments = view.findViewById(R.id.cardPayments);

        setupHeader();
        applyRolePermissions();
        setupShortcuts();

        return view;
    }

    private void applyRolePermissions() {
        if (getActivity() == null || getActivity().getIntent() == null) return;
        
        int roleId = getActivity().getIntent().getIntExtra("ROLE_ID", -1);

        // Entrenador
        if (roleId == 3) {
            cardNewClient.setVisibility(View.GONE);
            cardPayments.setVisibility(View.GONE);
        }

        // Cliente
        if (roleId == 4) {
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
