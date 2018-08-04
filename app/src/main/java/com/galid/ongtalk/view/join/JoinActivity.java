package com.galid.ongtalk.view.join;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.galid.ongtalk.R;
import com.galid.ongtalk.model.UserModel;
import com.galid.ongtalk.view.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JoinActivity extends AppCompatActivity {

    @BindView(R.id.edittext_joinactivity_email)
    public EditText editTextEmail;
    @BindView(R.id.edittext_joinactivity_pw)
    public EditText editTextNickName;

    public Button buttonJoin;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        ButterKnife.bind(this);

        buttonJoin = findViewById(R.id.button_joinactivity_join);
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doJoin();
            }
        });

    }

    public void doJoin(){
        String email = editTextEmail.getText().toString();
        if(email == null || email.length() <= 1){
            Toast.makeText(this , "이메일을 입력하세요.", Toast.LENGTH_LONG).show();
            return;
        }

        final String nickName = editTextNickName.getText().toString();
        if(nickName == null || nickName.length() <= 1){
            Toast.makeText(this , "닉네임을 입력하세요.", Toast.LENGTH_LONG).show();
            return;
        }

        if(!checkEmailForm(email)){
            Toast.makeText(this , "올바른 이메일을 입력하세요.", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email , nickName)
                .addOnCompleteListener(JoinActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UserModel userModel = new UserModel();
                            userModel.userName = nickName;

                            String uid = task.getResult().getUser().getUid();
                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);

                            afterJoinSuccess();
                        }
                        else{
                            Toast.makeText(JoinActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // 이메일형태 확인
    private boolean checkEmailForm(String email){
        if(StringUtils.countMatches(email, "@") == 1
                && StringUtils.countMatches(email , ".") == 1)
            return true;

        return false;
    }

    // 회원가입 성공시
    private void afterJoinSuccess(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
