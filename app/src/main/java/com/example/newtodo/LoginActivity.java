package com.example.newtodo;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    EditText emailEdit,passwordTxt;
    Button loginbtn;
    ProgressBar progressBar;
    TextView createaccbtn;
    ImageButton eyebtn;
    boolean isPasswordVisible = false;
    TextView forgotpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        emailEdit=findViewById(R.id.email_edittxt);
        passwordTxt=findViewById(R.id.passwordtxt);
        loginbtn=findViewById(R.id.login);
        createaccbtn=findViewById(R.id.create);
        progressBar=findViewById(R.id.progressBar);
        eyebtn=findViewById(R.id.eyebtn);
        forgotpassword=findViewById(R.id.forgotbtn);

        forgotpassword.setOnClickListener(v->startActivity(new Intent(LoginActivity.this,ForgotPassword.class)));

        eyebtn.setOnClickListener(v->togglePasswordVisibility());

        loginbtn.setOnClickListener(v->loginUser());
        createaccbtn.setOnClickListener(v->startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class)));
    }



    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            passwordTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eyebtn.setImageResource(R.drawable.baseline_visibility_off_24); // Replace with your icon resource
        } else {
            // Show password
            passwordTxt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eyebtn.setImageResource(R.drawable.baseline_remove_red_eye_24); // Replace with your icon resource
        }
        passwordTxt.setSelection(passwordTxt.getText().length()); // Move cursor to end
        isPasswordVisible = !isPasswordVisible;
    }

    void loginUser(){
        String em=emailEdit.getText().toString();
        String ps=passwordTxt.getText().toString();

        boolean isValidate=validateData(em,ps);
        if(!isValidate){
            return;
        }
        loginAccInFirebase(em,ps);
    }
    void loginAccInFirebase(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            changeInProgress(false);
                            if (task.isSuccessful()) {
                                if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified()) {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish(); // To prevent returning to the login activity
                                } else {
                                    Toast.makeText(LoginActivity.this, "Email not verified", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                String errorMessage = task.getException() != null ? task.getException().getLocalizedMessage() : "Login failed";
                                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception e) {
            changeInProgress(false);
            Toast.makeText(LoginActivity.this, "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            loginbtn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            loginbtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email,String pass){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEdit.setError("Email is invalid");
            return false;
        }
        if(pass.length()<6){
            passwordTxt.setError("Password Length is invalid");
            return false;
        }

        return true;
    }
}