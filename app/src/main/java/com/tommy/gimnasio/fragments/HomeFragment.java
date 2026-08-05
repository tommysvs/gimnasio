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
        setupShortcuts();

        return view;
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
        cardAttendance.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Ir a Asistencia", Toast.LENGTH_SHORT).show());
        
        cardNewClient.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Ir a Nuevo Cliente", Toast.LENGTH_SHORT).show());
            
        cardRoutines.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Ir a Rutinas", Toast.LENGTH_SHORT).show());
            
        cardPayments.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Ir a Pagos", Toast.LENGTH_SHORT).show());
    }
}
