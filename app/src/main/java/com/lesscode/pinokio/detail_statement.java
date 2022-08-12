package com.lesscode.pinokio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class detail_statement extends AppCompatActivity {
    String user, trusted_by, not_trusted_by, total;
    boolean user_op;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_statement);

        DatabaseReference pino_db = FirebaseDatabase.getInstance().getReference("pino_db");
        FirebaseAuth mAuth;

        mAuth = FirebaseAuth.getInstance();

        if (getIntent() != null) {
            String id = getIntent().getStringExtra("id");
            TextView txt_statement = findViewById(R.id.txt_statement);
            TextView txt_verified_by = findViewById(R.id.txt_verified_by);
            TextView txt_trusted_by = findViewById(R.id.txt_trusted_by);
            TextView txt_not_trusted_by = findViewById(R.id.txt_not_trusted_by);

            LinearLayout form_opinion = findViewById(R.id.form_opinion);
            LinearLayout ll_options = findViewById(R.id.ll_options);
            TextView txt_stat = findViewById(R.id.txt_stat);

            Button btn_trust = findViewById(R.id.btn_trust);
            Button btn_not_trust = findViewById(R.id.btn_not_trust);
            Button btn_cancel = findViewById(R.id.btn_cancel);
            Button btn_delete = findViewById(R.id.btn_delete);

            btn_trust.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int old_val = Integer.parseInt(trusted_by);
                    pino_db.child("all_statements").child(id).child("trusted_by").setValue(old_val + 1);
                    pino_db.child("users").child(user).child("trusted_list").child(id).setValue(true);
                }
            });

            btn_not_trust.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int old_val = Integer.parseInt(not_trusted_by);
                    pino_db.child("all_statements").child(id).child("not_trusted_by").setValue(old_val + 1);
                    pino_db.child("users").child(user).child("trusted_list").child(id).setValue(false);
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pino_db.child("users").child(user).child("trusted_list").child(id).removeValue();
                    if (user_op) {
                        int old_val = Integer.parseInt(trusted_by);
                        pino_db.child("all_statements").child(id).child("trusted_by").setValue(old_val - 1);
                    } else {
                        int old_val = Integer.parseInt(not_trusted_by);
                        pino_db.child("all_statements").child(id).child("not_trusted_by").setValue(old_val - 1);
                    }
                    finish();
                    startActivity(getIntent());
                    overridePendingTransition(0,0);
                }
            });

            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pino_db.child("all_statements").child(id).removeValue();
                    pino_db.child("all_statements").child("detail").child("total").setValue(Integer.parseInt(total) - 1);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0,0);
                }
            });

            pino_db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    txt_statement.setText(snapshot.child("all_statements").child(id).child("content").getValue().toString());
                    String verified_by = snapshot.child("all_statements").child(id).child("verified_by").getValue().toString();
                    boolean is_hoax = Boolean.getBoolean(snapshot.child("all_statements").child(id).child("is_hoax").getValue().toString());
                    String hoaxOrfact = (is_hoax) ? "HOAX" : "FACT";

                    trusted_by = snapshot.child("all_statements").child(id).child("trusted_by").getValue().toString();
                    not_trusted_by = snapshot.child("all_statements").child(id).child("not_trusted_by").getValue().toString();
                    String posted_by = snapshot.child("all_statements").child(id).child("posted_by").getValue().toString();

                    user = mAuth.getCurrentUser().getEmail().replace("@gmail.com", "");

                    total = snapshot.child("all_statements").child("detail").child("total").getValue().toString();

                    if (snapshot.child("users").child(user).child("trusted_list").child(id).exists()) {
                        user_op = Boolean.getBoolean(snapshot.child("users").child(user).child("trusted_list").child(id).getValue().toString());
                    }

                    if (verified_by.equals("")) {
                        txt_verified_by.setText(String.valueOf("Not verified yet"));
                        if (snapshot.child("users").child(user).child("trusted_list").child(id).exists()) {
                            form_opinion.setVisibility(View.GONE);
                            txt_stat.setVisibility(View.VISIBLE);
                            btn_cancel.setVisibility(View.VISIBLE);
                            txt_stat.setText("Anda sudah memilih, \ntidak dapat mengubah apapun.");
                        }
                    } else {
                        txt_verified_by.setText(String.format("Verified by %s as a %s", verified_by, hoaxOrfact));
                        form_opinion.setVisibility(View.GONE);
                        txt_stat.setVisibility(View.VISIBLE);
                        if (snapshot.child(user).child("trusted_list").child(id).exists()) {
                            txt_stat.setText("Opini sudah diverifikasi, \ndan Anda memilih untuk...");
                        } else {
                            txt_stat.setText("Opini sudah diverifikasi,\ntidak dapat mengubah apapun.");
                        }
                    }

                    if (posted_by.equals(user)) {
                        ll_options.setVisibility(View.VISIBLE);
                    }

                    txt_trusted_by.setText(String.format("Trusted by %s people", trusted_by));
                    txt_not_trusted_by.setText(String.format("Not trusted by %s people", not_trusted_by));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishActivity(1);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }
}