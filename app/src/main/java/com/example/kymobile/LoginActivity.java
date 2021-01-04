package com.example.kymobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText mEmailEt, mPassword;
    TextView mNoAccount,mRecoverPassword;
    Button mAcLogin;

    //Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;

    //progress dialog for login
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //initialising variables
        mEmailEt = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mRecoverPassword = findViewById(R.id.recover_password);
        mAcLogin = findViewById(R.id.login_to_account);
        mNoAccount = findViewById(R.id.no_account);


        //click on login button
        mAcLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                //types in email, password and batch
                String email = mEmailEt.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //show error when email doesnt match
                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);
                }
                else{
                    //valid email
                    loginUser(email,password);
                }

            }

            private void loginUser(String email, final String password) {
                //show progress dialog
                progressDialog.show();
                progressDialog.setMessage("Logging In...");
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    progressDialog.dismiss();


                                    //when user logged in send to profile
                                    startActivity(new Intent(LoginActivity.this, UserDashboard.class));
                                    finish();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Failed To Login", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();

                                }


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });



        //no account from text view
        mNoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,Registration.class));
                finish();
            }
        });

        //recovering password
        mRecoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });

        //initialise progress dialog
        progressDialog = new ProgressDialog(this);


    }

    private void showRecoverPasswordDialog() {
        //AlertDialog show
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        //setting up linear layout for dialog
        LinearLayout linearLayout = new LinearLayout(this);

        //text in dialog
        final EditText emailET = new EditText(this);
        emailET.setHint("Email Address");
        emailET.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        //setting minimum width of the edit text to contents
        emailET.setMinEms(16);

        linearLayout.addView(emailET);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        //submit button
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //enter email
                String email = emailET.getText().toString().trim();
                beginRecovery(email);
            }
        });

        //cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //exit dialog
                dialog.dismiss();

            }
        });

        //show dialog
        builder.create().show();

    }

    private void beginRecovery(String email){
        progressDialog.show();
        progressDialog.setMessage("Sending Email...");
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"Email Sent",Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(LoginActivity.this,"Invalid Email",Toast.LENGTH_SHORT).show();


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //show error message properly
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });
    }

}
