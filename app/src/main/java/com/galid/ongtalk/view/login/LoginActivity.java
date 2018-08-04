package com.galid.ongtalk.view.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.galid.ongtalk.R;
import com.galid.ongtalk.view.join.JoinActivity;
import com.galid.ongtalk.view.main.MainActivity;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    public static final int SIGN_IN_REQUEST_CODE = 0;

    private FirebaseRemoteConfig mFireBaseRemoteConfig;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private SignInButton btnGoogleLogin;
    @BindView(R.id.button_loginactivity_login)
    public Button btnLogin;
    @BindView(R.id.button_loginactivity_join)
    public Button btnJoin;
    @BindView(R.id.edittext_loginactivity_email)
    public EditText editTextEmail;
    @BindView(R.id.edittext_loginactivity_pw)
    public EditText editTextPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        // 원격 설정
        mFireBaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        String splash_background = mFireBaseRemoteConfig.getString("splash_background");

        // FIrebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();

        // 상태바 색 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));
        }

        btnLogin.setBackgroundColor(Color.parseColor(splash_background));
        btnJoin.setBackgroundColor(Color.parseColor(splash_background));

        // 로그인 리스너 인터페이스
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if(user != null){
                    //TODO SUCCESS
                    Intent intent = new Intent(LoginActivity.this ,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    //TODO FAIL
                }
            }
        };

    }

    // 로그인
    @OnClick(R.id.button_loginactivity_login)
    public void login() {
        String email = editTextEmail.getText().toString();
        String pw = editTextPw.getText().toString();

        mFirebaseAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    //TODO FAIL
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    //TODO SUCCESS

                }
            }
        });

    }

    // 회원가입 화면으로
    @OnClick(R.id.button_loginactivity_join)
    public void goJoin() {
        Intent intent = new Intent(this, JoinActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }
}
