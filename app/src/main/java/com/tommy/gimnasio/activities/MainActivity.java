package com.tommy.gimnasio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.tommy.gimnasio.R;
import com.tommy.gimnasio.fragments.AttendanceFragment;
import com.tommy.gimnasio.fragments.ClientsFragment;
import com.tommy.gimnasio.fragments.HomeFragment;
import com.tommy.gimnasio.fragments.MembershipsFragment;
import com.tommy.gimnasio.fragments.PaymentsFragment;
import com.tommy.gimnasio.fragments.RoutinesFragment;
import com.tommy.gimnasio.fragments.UsersFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private String userName, userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = getIntent().getStringExtra("USER_NAME");
        userRole = getIntent().getStringExtra("USER_ROLE");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        setupHeader();
        applyRolePermissions();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Inicio");
            }
        }
    }

    private void setupHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView tvNavName = headerView.findViewById(R.id.tvNavName);
        TextView tvNavRole = headerView.findViewById(R.id.tvNavRole);

        if (userName != null) tvNavName.setText(userName);
        if (userRole != null) tvNavRole.setText(userRole);
    }

    private void applyRolePermissions() {
        int roleId = getIntent().getIntExtra("ROLE_ID", -1);
        android.view.Menu menu = navigationView.getMenu();

        // Recepcionista
        if (roleId == 2) {
            menu.findItem(R.id.nav_users).setVisible(false);
            menu.findItem(R.id.nav_reports).setVisible(false);
        }

        // Entrenador
        if (roleId == 3) {
            menu.findItem(R.id.nav_clients).setVisible(false);
            menu.findItem(R.id.nav_memberships).setVisible(false);
            menu.findItem(R.id.nav_payments).setVisible(false);
            menu.findItem(R.id.nav_users).setVisible(false);
            menu.findItem(R.id.nav_reports).setVisible(false);
        }
        
        // Cliente
        if (roleId == 4) {
            menu.findItem(R.id.nav_clients).setVisible(false);
            menu.findItem(R.id.nav_memberships).setVisible(false);
            menu.findItem(R.id.nav_payments).setVisible(false);
            menu.findItem(R.id.nav_attendance).setVisible(false);
            menu.findItem(R.id.nav_users).setVisible(false);
            menu.findItem(R.id.nav_reports).setVisible(false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        String title = "Inicio";

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            selectedFragment = new HomeFragment();
            title = "Inicio";
        } else if (id == R.id.nav_clients) {
            selectedFragment = new ClientsFragment();
            title = "Clientes";
        } else if (id == R.id.nav_memberships) {
            selectedFragment = new MembershipsFragment();
            title = "Membresías";
        } else if (id == R.id.nav_attendance) {
            selectedFragment = new AttendanceFragment();
            title = "Asistencias";
        } else if (id == R.id.nav_routines) {
            selectedFragment = new RoutinesFragment();
            title = "Rutinas";
        } else if (id == R.id.nav_payments) {
            selectedFragment = new PaymentsFragment();
            title = "Pagos";
        } else if (id == R.id.nav_users) {
            selectedFragment = new UsersFragment();
            title = "Usuarios";
        } else if (id == R.id.nav_logout) {
            logout();
            return true;
        } else {
            Toast.makeText(this, "Sección en desarrollo", Toast.LENGTH_SHORT).show();
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
