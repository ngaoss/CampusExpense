package com.example.manager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.manager.activity.LoginActivity;
import com.example.manager.R;

public class AccountFragment extends Fragment {

    private TextView tvUsername, tvEmail;
    private Button btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        tvUsername = view.findViewById(R.id.tv_account_username);
        tvEmail = view.findViewById(R.id.tv_account_email);
        btnLogout = view.findViewById(R.id.btn_logout);

        Bundle userData = getActivity().getIntent().getExtras();
        if (userData != null) {
            String username = userData.getString("USERNAME_ACCOUNT", "N/A");
            String email = userData.getString("EMAIL_ACCOUNT", "N/A");
            tvUsername.setText(username);
            tvEmail.setText(email);
        }

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }
}