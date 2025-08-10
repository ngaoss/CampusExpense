package com.example.manager.activity;

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

import com.example.manager.R;
import com.example.manager.data.UserModel;
import com.example.manager.data.UserRepository;

public class LoginActivity extends AppCompatActivity {

    TextView tvRegister;
    EditText edtUsername, edtPassword;
    Button btnLogin;
    UserRepository respository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        respository = new UserRepository(LoginActivity.this);
        tvRegister = findViewById(R.id.tvRegister);
        edtPassword = findViewById(R.id.edtPassword);
        edtUsername = findViewById(R.id.edtUsername);
        btnLogin = findViewById(R.id.btnLogin);
        checkloginUser();
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkloginUser() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(username)) {
                    edtUsername.setError("Please enter the username");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    edtPassword.setError("Please enter the password");
                    return;
                }

                UserModel infoAcccount = respository.getInfoAccountByUsername(username, password);

                if (infoAcccount != null && infoAcccount.getUsername() != null && infoAcccount.getId() > 0) {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("ID_ACCOUNT", infoAcccount.getId());
                    bundle.putString("USERNAME_ACCOUNT", infoAcccount.getUsername());
                    bundle.putString("EMAIL_ACCOUNT", infoAcccount.getEmail());
                    bundle.putInt("ROLE_ACCOUNT", infoAcccount.getRole());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}