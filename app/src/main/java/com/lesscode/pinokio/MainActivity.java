package com.lesscode.pinokio;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

@SuppressWarnings("SpellCheckingInspection")
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // mengubah isi layout frag_frame dengan layout frag_beranda
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_frame, new frag_beranda()).commit();

        // deklarasi dan inisialisasi bar navigasi nav_btm
        BottomNavigationView nav_btm = findViewById(R.id.nav_btm);

        // mendeteksi menu yang dipilih pada nav_btm
        nav_btm.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selected_frag = null;
                switch (item.getItemId()) {
                    case R.id.btn_home:
                        selected_frag = new frag_beranda();
                        break;
                    case R.id.btn_add:
                        selected_frag = new frag_buat();
                        break;
                    case R.id.btn_account:
                        selected_frag = new frag_akun();
                        break;
                }

                // mengubah isi layout frag_frame dengan layout fragment yang dipilih
                getSupportFragmentManager().beginTransaction().replace(R.id.frag_frame, selected_frag).commit();
                return true;
            }
        });
    }
}