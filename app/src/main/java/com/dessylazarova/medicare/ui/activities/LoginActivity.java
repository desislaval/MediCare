package com.dessylazarova.medicare.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.dessylazarova.medicare.AuthUtils;
import com.dessylazarova.medicare.R;
import com.dessylazarova.medicare.data.User;
import com.dessylazarova.medicare.databinding.ActivityLoginBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            redirectToMainScreen();
        }

        binding.btnLogin.setOnClickListener(v -> startLogInAuthentication());

        binding.btnSignin.setOnClickListener(v -> startSignInAuthentication());

        db = FirebaseFirestore.getInstance();
    }

    private void startSignInAuthentication() {
        email = binding.edtEmail.getText().toString();
        password = binding.edtPassword.getText().toString();
        if (isEmailValid(email) && isPasswordValid(password)) {
            AuthUtils.getInstance().loginWithEmail(email, password, new AuthUtils.LoginListener() {
                @Override
                public void onLoginSuccessful(User user) {
                    Map<String, Object> doctor = new HashMap<>();
                    db.collection("users").document(user.getId()).set(doctor, SetOptions.merge());
                    redirectToMainScreen();
                }

                @Override
                public void onLoginFailed(String error) {
                    Toast.makeText(LoginActivity.this, error,
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else if (!isEmailValid(email)) {
            binding.edtEmail.setError(getString(R.string.email_check));
        }
        if (!isPasswordValid(password)) {
            Snackbar snackbar = Snackbar.make(binding.coordinatorLayout, getString(R.string.password_check), Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void startLogInAuthentication() {
        email = binding.edtEmail.getText().toString();
        password = binding.edtPassword.getText().toString();
        if (isEmailValid(email) && isPasswordValid(password)) {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            redirectToMainScreen();
                        } else {
                            Snackbar snackbar = Snackbar.make(binding.coordinatorLayout, getString(R.string.authentication_failed), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    });
        } else {
            Snackbar snackbar = Snackbar.make(binding.coordinatorLayout, getString(R.string.authentication_failed), Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    public void redirectToMainScreen() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    boolean isPasswordValid(CharSequence password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^[a-zA-Z0-9]{6,20}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
