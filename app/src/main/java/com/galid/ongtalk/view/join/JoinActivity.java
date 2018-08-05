package com.galid.ongtalk.view.join;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.galid.ongtalk.R;
import com.galid.ongtalk.constant.FirebaseDatabaseConstant;
import com.galid.ongtalk.model.UserModel;
import com.galid.ongtalk.view.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JoinActivity extends AppCompatActivity{
    public static final int PICK_FROM_ALBUM = 10;

    @BindView(R.id.edittext_joinactivity_email)
    public EditText editTextEmail;
    @BindView(R.id.edittext_joinactivity_name)
    public EditText editTextNickName;
    @BindView(R.id.edittext_joinactivity_pw)
    public EditText editTextPw;

    private ImageView imageViewAddProfile;
    private Uri profileImageUri;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        ButterKnife.bind(this);

        imageViewAddProfile = findViewById(R.id.imageview_joinactivity_addprofileimage);
        imageViewAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent , PICK_FROM_ALBUM);
            }
        });

        Button buttonJoin = findViewById(R.id.button_joinactivity_join);
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doJoin();
            }
        });

    }

    public void doJoin(){
        String email = editTextEmail.getText().toString();
        final String name = editTextNickName.getText().toString();
        String pw = editTextPw.getText().toString();

        if(StringUtils.isEmpty(email) || email.length() <= 1){
            Toast.makeText(this , "이메일을 입력하세요.", Toast.LENGTH_LONG).show();
            return;
        }

        if(StringUtils.isEmpty(name) || name.length() <= 1){
            Toast.makeText(this , "이름을 입력하세요.", Toast.LENGTH_LONG).show();
            return;
        }

        if(StringUtils.isEmpty((pw)) || pw.length() <= 1){
            Toast.makeText(this , "비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
            return;
        }

        if(!checkEmailForm(email)){
            Toast.makeText(this , "올바른 이메일을 입력하세요.", Toast.LENGTH_LONG).show();
            return;
        }

        if(profileImageUri == null){
            Toast.makeText(this , "이미지를 추가 하세요.", Toast.LENGTH_LONG).show();
            return;
        }


        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email , pw)
                .addOnCompleteListener(JoinActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final String uid = task.getResult().getUser().getUid(); //계정생성 완료 후 생성된 uid 불러옴

                            FirebaseStorage.getInstance().getReference().child(FirebaseDatabaseConstant.FIREBASE_DATABASE_USERIMAGE).child(uid)
                                    .putFile(profileImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                        String imageUrl = task.getResult().getDownloadUrl().toString();
                                        UserModel userModel = new UserModel();
                                        userModel.userName = name;
                                        userModel.profileImageUrl = imageUrl;

                                        JoinActivity.this.finish();
                                        afterJoinSuccess();

                                        FirebaseDatabase.getInstance()
                                                .getReference().child(FirebaseDatabaseConstant.FIREBASE_DATABASE_USERLIST).child(uid).setValue(userModel);
                                    }
                                }
                            });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK){
            // 이미지 가져온 후의 처리
            imageViewAddProfile.setImageURI(data.getData()); // 이미지 추가버튼 이미지를 내가 선택한 이미지로 바꿈
            profileImageUri = data.getData();  // 사진을 찍던 , 갤러리에서 받아오던 그 Uri를 멤버변수에 저장
        }
    }

}
