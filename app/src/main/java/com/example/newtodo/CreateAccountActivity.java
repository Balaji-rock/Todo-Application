

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import androidx.annotation.NonNull;
public class CreateAccountActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    EditText emailEdit, passwordTxt, confirmpasswordTxt;
    Button createAcc;
    ProgressBar progressBar;
    TextView login;
    ImageButton eyebtn;
    SignInButton googleSignIn;
    boolean isPasswordVisible = false;
    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEdit = findViewById(R.id.email_edittxt);
        passwordTxt = findViewById(R.id.passwordtxt);
        confirmpasswordTxt = findViewById(R.id.confirmpasswordtxt);
        createAcc = findViewById(R.id.createAccBtn);
        login = findViewById(R.id.logintxt);
        progressBar = findViewById(R.id.progressBar);
        eyebtn = findViewById(R.id.eyebtn);
        googleSignIn = findViewById(R.id.google_sign_in_button);

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        eyebtn.setOnClickListener(v -> togglePasswordVisibility());
        createAcc.setOnClickListener(v -> createAcc());
        login.setOnClickListener(v -> startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class)));
        googleSignIn.setOnClickListener(v -> signInWithGoogle());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eyebtn.setImageResource(R.drawable.baseline_visibility_off_24);
        } else {
            passwordTxt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eyebtn.setImageResource(R.drawable.baseline_remove_red_eye_24);
        }
        isPasswordVisible = !isPasswordVisible;
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                Toast.makeText(CreateAccountActivity.this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        changeInProgress(true);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    Toast.makeText(CreateAccountActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(CreateAccountActivity.this, MainActivity.class));
            finish();
        }
    }

    void createAcc() {
        String em = emailEdit.getText().toString();
        String ps = passwordTxt.getText().toString();
        String cps = confirmpasswordTxt.getText().toString();
        boolean isValidate = validateData(em, ps, cps);
        if (!isValidate) {
            return;
        }
        createAccountInFirebase(em, ps);
    }

    void createAccountInFirebase(String email, String password) {
        changeInProgress(true);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(CreateAccountActivity.this,
                        task -> {
                            changeInProgress(false);
                            if (task.isSuccessful()) {
                                Toast.makeText(CreateAccountActivity.this, "Account successfully created", Toast.LENGTH_SHORT).show();
                                firebaseAuth.getCurrentUser().sendEmailVerification();
                                firebaseAuth.signOut();
                                startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(CreateAccountActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            createAcc.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            createAcc.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String pass, String conpass) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdit.setError("Email is invalid");
            return false;
        }
        if (pass.length() < 6) {
            passwordTxt.setError("Password Length is invalid");
            return false;
        }
        if (!pass.equals(conpass)) {
            confirmpasswordTxt.setError("Passwords do not match");
            return false;
        }
        return true;
    }
}

//package com.example.newtodo;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.InputType;
//import android.util.Patterns;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.google.firebase.auth.FirebaseAuth;
//
//public class CreateAccountActivity extends AppCompatActivity {
//    EditText emailEdit,passwordTxt,confirmpasswordTxt;
//    Button createAcc;
//    ProgressBar progressBar;
//    TextView login;
//    ImageButton eyebtn;
//    boolean isPasswordVisible = false;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_create_account);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//        emailEdit=findViewById(R.id.email_edittxt);
//        passwordTxt=findViewById(R.id.passwordtxt);
//        confirmpasswordTxt=findViewById(R.id.confirmpasswordtxt);
//        createAcc=findViewById(R.id.createAccBtn);
//        login=findViewById(R.id.logintxt);
//        progressBar=findViewById(R.id.progressBar);
//        eyebtn=findViewById(R.id.eyebtn);
//        eyebtn.setOnClickListener(v->togglePasswordVisibility());
//        createAcc.setOnClickListener(v->
//                createAcc());
//        login.setOnClickListener(v->startActivity(new Intent(CreateAccountActivity.this,LoginActivity.class)));
//
//    }
//    private void togglePasswordVisibility() {
//        if (isPasswordVisible) {
//            // Hide password
//            passwordTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//            eyebtn.setImageResource(R.drawable.baseline_visibility_off_24); // Replace with your icon resource
//        } else {
//            // Show password
//            passwordTxt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//            eyebtn.setImageResource(R.drawable.baseline_remove_red_eye_24); // Replace with your icon resource
//        }
//        //passwordTxt.setSelection(passwordTxt.getText().length()); // Move cursor to end
//        isPasswordVisible = !isPasswordVisible;
//    }
//    void createAcc(){
//        String em=emailEdit.getText().toString();
//        String ps=passwordTxt.getText().toString();
//        String cps=confirmpasswordTxt.getText().toString();
//        boolean isValidate=validateData(em,ps,cps);
//        if(!isValidate){
//            return;
//        }
//        createAccountInFirebase(em,ps);
//    }
//    void   createAccountInFirebase(String email,String password){
//        changeInProgress(true);
//
//        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
//        firebaseAuth.createUserWithEmailAndPassword(email,password)
//                .addOnCompleteListener(CreateAccountActivity.this,
//                        task -> {
//                            changeInProgress(false);
//                            if(task.isSuccessful()){
//                                utility.showToast(CreateAccountActivity.this,"Successfull created");
//                                firebaseAuth.getCurrentUser().sendEmailVerification();
//                                firebaseAuth.signOut();
//                            }else {
//                                Toast.makeText(CreateAccountActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//
//    }
//
//    void changeInProgress(boolean inProgress){
//        if(inProgress){
//            progressBar.setVisibility(View.VISIBLE);
//            createAcc.setVisibility(View.GONE);
//        }
//        else {
//            progressBar.setVisibility(View.GONE);
//            createAcc.setVisibility(View.VISIBLE);
//        }
//    }
//
//    boolean validateData(String email,String pass,String conpass){
//        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
//            emailEdit.setError("Email is invalid");
//            return false;
//        }
//        if(pass.length()<6){
//            passwordTxt.setError("Password Length is invalid");
//            return false;
//        }
//        if(!pass.equals(conpass)){
//            confirmpasswordTxt.setError("Password Not Matched");
//            return false;
//        }
//        return true;
//    }
//}