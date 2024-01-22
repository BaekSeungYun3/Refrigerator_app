package com.example.refrigerator_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    EditText username, email,password;
    Button Signup_btn;
    TextView LoginRedirectText;
   FirebaseAuth auth;
   DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = findViewById(R.id.signup_username);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        Signup_btn = findViewById(R.id.signup_button);
        LoginRedirectText = findViewById(R.id.loginRedirectText);

        auth = FirebaseAuth.getInstance();

        LoginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

        Signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str_username = username.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if(TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                    Toast.makeText(SignupActivity.this, "모든 칸을 입력해주세요!", Toast.LENGTH_SHORT).show();
                }else if(str_password.length() < 6){
                    Toast.makeText(SignupActivity.this, "비밀번호는 6자리 이상 입력해주세요", Toast.LENGTH_SHORT).show();
                }else{
                        // SignUp 메서드 호출
                        SignUp(str_username, str_email, str_password);
                }
            }
        });
    }

    private void SignUp(String name, String email, String password){
        auth.createUserWithEmailAndPassword(email , password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String userid = firebaseUser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("id",userid);
                    hashMap.put("name",name);
                    hashMap.put("email", email);
                    hashMap.put("password", password);
                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                Toast.makeText(SignupActivity.this, "회원가입에 성공하였습니다!", Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                            }
                        }
                    });

                } else {
                    Toast.makeText(SignupActivity.this, "회원가입에 실패하였습니다." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}