package com.lesscode.pinokio;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

@SuppressWarnings("SpellCheckingInspection")
public class login_screen extends AppCompatActivity {
    TextView txt_login;
    LinearLayout ll_divider;
    Button btn_signin, btn_create;

    FirebaseAuth mAuth;

    protected final static String TAG = "Err";

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    protected void onStart() {
        super.onStart();

        // inisialisasi FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // mengecek jika terdapat intent maka akan melakukan logout
        if (getIntent() != null) {
            if (getIntent().getBooleanExtra("frm_logout", false)){
                gsc.signOut();
            }
        }

        // mengambil user yang sedang aktif
        FirebaseUser user = mAuth.getCurrentUser();

        // Jika terdapat user yang sedang aktif maka akan meneruskan ke MainActivity
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0,0);
            finish();
            Toast.makeText(getApplicationContext(), "Masuk sebagai " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        btn_signin = findViewById(R.id.btn_signin);
        txt_login = findViewById(R.id.txt_login);

        ll_divider = findViewById(R.id.divider);
        btn_create = findViewById(R.id.btn_create);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_anim);
        txt_login.startAnimation(animation);

        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_in_anim);
        btn_signin.startAnimation(animation1);

        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_anim_sec);
        ll_divider.startAnimation(animation2);

        Animation animation3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_anim_th);
        btn_create.startAnimation(animation3);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                String idToken = account.getIdToken();

                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                            finish();
                            Toast.makeText(login_screen.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(login_screen.this, "Login Gagal, silakan coba lagi", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (ApiException e) {
                e.printStackTrace();
                Log.d(TAG, String.valueOf(e));
            }
        }
    }
}
