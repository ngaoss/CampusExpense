package com.example.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.manager.data.UserRepository;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class SignUpActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword, edtEmail;
    Button btnRegister;
    TextView tvlogin;
    UserRepository respository;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        respository = new UserRepository(SignUpActivity.this);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        btnRegister = findViewById(R.id.btnRegister);
        tvlogin = findViewById(R.id.tvlogin);

        SignUpAccount();

        tvlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void SignUpAccount(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                if (TextUtils.isEmpty(username)){
                    edtUsername.setError("Please enter the username");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    edtPassword.setError("Please enter password");
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    edtEmail.setError("Please enter the email");
                    return;
                }
                long insert = respository.saveUserAccount(username, password, email);
                if (insert == -1){
                    Toast.makeText(SignUpActivity.this,"Sign Up Fail", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(SignUpActivity.this,"Sign Up Success", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
    private void signUpV1(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(username)){
                    edtUsername.setError("Please enter the Username");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    edtUsername.setError("Please enter Password");
                    return;
                }
                FileOutputStream fileOutput = null;
                try {
                    username = username + "|";
                    fileOutput = openFileOutput("user.txt", Context.MODE_APPEND);
                    fileOutput.write(username.getBytes(StandardCharsets.UTF_8));
                    fileOutput.write(password.getBytes(StandardCharsets.UTF_8));
                    fileOutput.write('\n');
                    edtUsername.setText("");
                    edtPassword.setText("");
                    Toast.makeText(SignUpActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                }catch (Exception e){
                    throw new RuntimeException(e);

                }
            }
        });
    }
}
