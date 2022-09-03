package com.lesscode.pinokio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
 * Use the {@link frag_beranda#newInstance} factory method to
 * create an instance of this fragment.
 */

@SuppressWarnings("SpellCheckingInspection")
public class frag_beranda extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    LinearLayout list_verified, list_not_verified, list_my_posts;
    DatabaseReference db_pino;
    RadioGroup rg_main;
    RadioButton rb_1, rb_2, rb_3;
    LinearLayout tab_verified, tab_not_verified, tab_my_posts;
    FirebaseAuth mAuth;

    public frag_beranda() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment frg_beranda.
     */
    // TODO: Rename and change types and number of parameters
    public static frag_beranda newInstance(String param1, String param2) {
        frag_beranda fragment = new frag_beranda();
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

        db_pino = FirebaseDatabase.getInstance().getReference("pino_db");

        db_pino.addValueEventListener(new ValueEventListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // iterasi semua statement di dalam all_statements
                for (DataSnapshot snapshot1 : snapshot.child("all_statements").getChildren()) {
                    if (!snapshot1.getKey().equals("detail")) {
                        boolean is_verified = !snapshot1.child("verified_by").getValue().toString().equals("");
                        String verificator = "null", content, num_trusted, num_not_trusted, hoaxORfact = "null";
                        final TextView pstContent = new TextView(getActivity().getApplicationContext());
                        final TextView pstVerifiedBy = new TextView(getActivity().getApplicationContext());
                        final TextView pstTrustedBy = new TextView(getActivity().getApplicationContext());
                        final TextView pstNotTrustedBy = new TextView(getActivity().getApplicationContext());

                        if (is_verified) {
                            Toast.makeText(getActivity().getApplicationContext(), "Ada yang terverifikasi", Toast.LENGTH_SHORT).show();
                            verificator = snapshot1.child("verified_by").getValue().toString();

                            boolean is_hoax = Boolean.parseBoolean(Objects.requireNonNull(snapshot1.child("is_hoax").getValue()).toString());

                            if (is_hoax) {
                                hoaxORfact = "HOAX";
                            } else {
                                hoaxORfact = "FACT";
                            }
                        }

                        content = snapshot1.child("content").getValue().toString();
                        num_trusted = snapshot1.child("trusted_by").getValue().toString();
                        num_not_trusted = snapshot1.child("not_trusted_by").getValue().toString();

                        // inisialisasi
                        int dens = (int) getActivity().getApplicationContext().getResources().getDisplayMetrics().density;
                        pstContent.setText(content);
                        pstContent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity().getApplicationContext(), detail_statement.class);
                                intent.putExtra("id", snapshot1.child("id").getValue().toString());
                                startActivity(intent);
                                getActivity().overridePendingTransition(0, 0);
                            }
                        });
                        pstContent.setBackground(getActivity().getApplicationContext().getDrawable(R.drawable.rounded_corner));
                        pstContent.setPadding(10 * dens, 5 * dens, 7 * dens, 5 * dens);
                        pstContent.setTextSize((float) (12 / 0.75));

                        if (is_verified) {
                            pstVerifiedBy.setText(String.format("Verified by %s as a %s", verificator, hoaxORfact));
                            pstVerifiedBy.setTextSize((float) (12 / 0.75));
                            pstVerifiedBy.setPadding(10 * dens, 5 * dens, 7 * dens, 5 * dens);
                        }

                        pstTrustedBy.setText(String.format("Trusted by %s", num_trusted));
                        pstTrustedBy.setTextSize((float) (12 / 0.75));
                        pstTrustedBy.setPadding(10 * dens, 5 * dens, 7 * dens, 5 * dens);

                        pstNotTrustedBy.setText(String.format("Not trusted by %s", num_not_trusted));
                        pstNotTrustedBy.setTextSize((float) (12 / 0.75));
                        pstNotTrustedBy.setPadding(10 * dens, 5 * dens, 7 * dens, 5 * dens);

                        if (is_verified) {
                            list_verified.addView(pstContent);
                            list_verified.addView(pstVerifiedBy);
                            list_verified.addView(pstTrustedBy);
                            list_verified.addView(pstNotTrustedBy);
                        } else {
                            list_not_verified.addView(pstContent);
                            list_not_verified.addView(pstTrustedBy);
                            list_not_verified.addView(pstNotTrustedBy);
                        }

//                        if (snapshot1.child("posted_by").getValue().toString()
//                                .equals(mAuth.getCurrentUser().getEmail().toString().replace("@gmail.com",""))) {
//                            list_my_posts.addView(teks);
//                            if (is_verified) {
//                                list_my_posts.addView(teks1);
//                            }
//                            list_my_posts.addView(teks3);
//                            list_my_posts.addView(teks4);
//                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frag_beranda, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list_verified = getView().findViewById(R.id.list_verified);
        list_not_verified = getView().findViewById(R.id.list_not_verified);
        list_my_posts = getView().findViewById(R.id.list_my_posts);

        rg_main = getView().findViewById(R.id.rg_main);
        rb_1 = getView().findViewById(R.id.rb_1);
        rb_2 = getView().findViewById(R.id.rb_2);
        rb_3 = getView().findViewById(R.id.rb_3);
        tab_verified = getView().findViewById(R.id.tab_verified);
        tab_not_verified = getView().findViewById(R.id.tab_not_verified);
        tab_my_posts = getView().findViewById(R.id.tab_my_posts);

        rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_1) {
                    rb_1.setTextColor(Color.BLACK);
                    rb_2.setTextColor(Color.WHITE);
                    rb_3.setTextColor(Color.WHITE);
                    tab_verified.setVisibility(View.VISIBLE);
                    tab_not_verified.setVisibility(View.GONE);
                    tab_my_posts.setVisibility(View.GONE);
                } else if (checkedId == R.id.rb_2) {
                    rb_1.setTextColor(Color.WHITE);
                    rb_2.setTextColor(Color.BLACK);
                    rb_3.setTextColor(Color.WHITE);
                    tab_verified.setVisibility(View.GONE);
                    tab_not_verified.setVisibility(View.VISIBLE);
                    tab_my_posts.setVisibility(View.GONE);
                } else if (checkedId == R.id.rb_3) {
                    rb_1.setTextColor(Color.WHITE);
                    rb_2.setTextColor(Color.WHITE);
                    rb_3.setTextColor(Color.BLACK);
                    tab_verified.setVisibility(View.GONE);
                    tab_not_verified.setVisibility(View.GONE);
                    tab_my_posts.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}