package com.lesscode.pinokio;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link frag_akun#newInstance} factory method to
 * create an instance of this fragment.
 */
public class frag_akun extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseAuth mAuth;

    public frag_akun() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment frag_akun.
     */
    // TODO: Rename and change types and number of parameters
    public static frag_akun newInstance(String param1, String param2) {
        frag_akun fragment = new frag_akun();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frag_akun, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btn_logout = getView().findViewById(R.id.btn_logout);
        TextView txt_username = getView().findViewById(R.id.txt_username);
        TextView txt_email = getView().findViewById(R.id.txt_email);
        ImageView img_profile = getView().findViewById(R.id.img_profile);

        txt_username.setText(mAuth.getCurrentUser().getDisplayName());
        txt_email.setText(mAuth.getCurrentUser().getEmail());

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity().getApplicationContext(), login_screen.class);
                intent.putExtra("frm_logout", true);
                startActivity(intent);
                getActivity().overridePendingTransition(0,0);
                getActivity().finishAffinity();
                Toast.makeText(getActivity().getApplicationContext(), "Logout Berhasil", Toast.LENGTH_SHORT).show();
            }
        });
    }
}