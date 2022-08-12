package com.lesscode.pinokio;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link frag_buat#newInstance} factory method to
 * create an instance of this fragment.
 */

@SuppressWarnings("SpellCheckingInspection")
public class frag_buat extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button btn_post;
    EditText txt_edt_stat;
    RadioGroup rg_second;
    RadioButton checked;
    FirebaseAuth mAuth;
    DatabaseReference pino_db;
    boolean is_trusted = true;
    int idx;

    public frag_buat() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment frag_buat.
     */
    // TODO: Rename and change types and number of parameters
    public static frag_buat newInstance(String param1, String param2) {
        frag_buat fragment = new frag_buat();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mAuth = FirebaseAuth.getInstance();
        pino_db = FirebaseDatabase.getInstance().getReference("pino_db");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frag_buat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_post = getView().findViewById(R.id.btn_post);
        txt_edt_stat = getView().findViewById(R.id.txt_edt_stat);
        rg_second = getView().findViewById(R.id.rg_second);
        RadioButton rb_trust = getView().findViewById(R.id.rb_4);
        RadioButton rb_not_trust = getView().findViewById(R.id.rb_5);
        Button btn_reset = getView().findViewById(R.id.btn_reset);

        // mengecek perubahan pada radio group (trust atau not trust)
        // jika salah satu dipilih maka akan mengubah nilai is_trusted dan textStyle
        rg_second.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_4:
                        is_trusted = true;
                        rb_trust.setTypeface(null, Typeface.BOLD);
                        rb_not_trust.setTypeface(null, Typeface. NORMAL);
                        break;
                    case R.id.rb_5:
                        is_trusted = false;
                        rb_trust.setTypeface(null, Typeface.NORMAL);
                        rb_not_trust.setTypeface(null, Typeface.BOLD);
                        break;
                }
            }
        });

        pino_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // mengambil data index dari database
                idx = Integer.parseInt(snapshot.child("all_statements").child("detail").child("total").getValue().toString()) + 1;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // menambahkan fungsi pada btn_post
        // jika button ditekan maka akan menampung data statement dan mengirimkan pada database
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_edt_stat.getText().toString().equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Belum ada statement!", Toast.LENGTH_SHORT).show();
                } else {
                    String stat = txt_edt_stat.getText().toString();
                    String user = mAuth.getCurrentUser().getEmail().replace("@gmail.com", "");

                    pino_db.child("all_statements").child(String.valueOf(idx)).child("content").setValue(stat);
                    pino_db.child("all_statements").child(String.valueOf(idx)).child("id").setValue(String.valueOf(idx));
                    pino_db.child("all_statements").child(String.valueOf(idx)).child("is_hoax").setValue("");
                    pino_db.child("all_statements").child(String.valueOf(idx)).child("posted_by").setValue(user);
                    if (is_trusted) {
                        pino_db.child("all_statements").child(String.valueOf(idx)).child("trusted_by").setValue(1);
                        pino_db.child("all_statements").child(String.valueOf(idx)).child("not_trusted_by").setValue(0);
                    } else {
                        pino_db.child("all_statements").child(String.valueOf(idx)).child("trusted_by").setValue(0);
                        pino_db.child("all_statements").child(String.valueOf(idx)).child("not_trusted_by").setValue(1);
                    }
                    pino_db.child("all_statements").child(String.valueOf(idx)).child("verified_by").setValue("");

                    pino_db.child("all_statements").child("detail").child("total").setValue(idx);

                    Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(0,0);
                }
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_edt_stat.setText("");
                rb_trust.setChecked(true);
            }
        });
    }
}