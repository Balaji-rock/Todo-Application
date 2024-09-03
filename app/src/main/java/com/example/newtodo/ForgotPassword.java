package com.example.newtodo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    Button resetbtn, backbtn;
    EditText emailtxt;
    String strEmail;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        resetbtn = findViewById(R.id.resetpassword);
        backbtn = findViewById(R.id.back);
        emailtxt = findViewById(R.id.email);

        backbtn.setOnClickListener(v -> startActivity(new Intent(ForgotPassword.this, LoginActivity.class)));

        resetbtn.setOnClickListener(view -> {
            strEmail = emailtxt.getText().toString().trim(); // Correctly get text from emailtxt
            if (!TextUtils.isEmpty(strEmail)) {
                ResetPassword();
            } else {
                emailtxt.setError("Email field can't be empty");
            }
        });
    }

    private void ResetPassword() {
        firebaseAuth.sendPasswordResetEmail(strEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPassword.this, "Reset Password Link has been sent to your registered email", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ForgotPassword.this, "Failed to send reset email. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
