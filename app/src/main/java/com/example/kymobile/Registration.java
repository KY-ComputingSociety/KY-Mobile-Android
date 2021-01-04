package com.example.kymobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class Registration extends AppCompatActivity {

    EditText mEmailET, mPasswordET, mBatch, mID, mName, mConPass;
    Button mRegisterBtn;
    TextView mHaveAccount;
    ImageView mProfilePic;

    //progress dialog when user registers
    ProgressDialog progressDialog;
    //Declare an instance of FirebaseAuth
    FirebaseAuth mAuth;
    private static final String FIRE_LOG = "Fire_Log";
    FirebaseUser user;
    FirebaseFirestore firebaseDatabase;
    StorageReference storageReference;
    DocumentReference documentReference;

    //permission constant
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;


    //ASKING PERMISSION ARRAYS
    String[] cameraPermissions;
    String[] storagePermissions;

    //uri of the image
    Uri image_uri;

    //path for image
    String storagePath = "Users_ProfilePic/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);



        //initialise registration request
        mEmailET = findViewById(R.id.email);
        mPasswordET = findViewById(R.id.password);
        mBatch = findViewById(R.id.batch);
        mRegisterBtn = findViewById(R.id.sign);
        mHaveAccount = findViewById(R.id.have_account);
        mID = findViewById(R.id.reg_id);
        mName = findViewById(R.id.reg_name);
        mConPass = findViewById(R.id.reg_confirm_pass);
        mProfilePic = findViewById(R.id.reg_profile_pic);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        progressDialog = new ProgressDialog(this);


        progressDialog.setMessage("Registering Your Account...");


        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input email,password and batch
                String email = mEmailET.getText().toString().trim();
                String password = mPasswordET.getText().toString().trim();
                String batch = mBatch.getText().toString().trim();
                String confirm = mConPass.getText().toString().trim();
                String ID = mID.getText().toString().trim();
                String name = mName.getText().toString().trim();

                //validating inputs
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    //highlights error in the email
                    mEmailET.setError("Invalid Email!");
                    mEmailET.setFocusable(true);

                }
                else if  (password.length()<6){
                    //show error for the password
                    mPasswordET.setError("Password Too Short!");
                    mPasswordET.setFocusable(true);
                }
                else if (!batch.equals("23.0") && !batch.equals("23.5")&& !batch.equals("22.0") && !batch.equals("22.5")){

                        mBatch.setError("Invalid Batch");
                        mBatch.setFocusable(true);
                    }

                else if(!password.equals(confirm)){
                    mConPass.setError("Passwords Do Not Match");
                    mConPass.setFocusable(true);
                }

                else if(ID.equals("")){
                    mID.setError("Fill In Your ID");
                    mID.setFocusable(true);
                }

                else if(ID.length()!=7){
                    mID.setError("Include Your Admission Year");
                    mID.setFocusable(true);
                }


                else if(name.equals("")) {
                    mName.setError("Fill In Your Name");
                    mName.setFocusable(true);
                }

                else if(name.length()>15) {
                    mName.setError("Use A Shorter Nickname");
                    mName.setFocusable(true);
                }

                else{
                    //register user
                    registerUser(email , password);
                }


            }
        });

        //handles login text from the textview
        mHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registration.this,LoginActivity.class));
                finish();
            }
        });


    }




    private void registerUser(String email, String password){
        // method to show the progress dialog and start registering the user
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, continue with registration
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();

                            //get user uid and email
                            String email = user.getEmail();
                            String uid = user.getUid();


                            //when user is registered, data is stored in database
                            HashMap<Object,String> hashMap = new HashMap<>();
                            //append info into hash map
                            hashMap.put("Email",email);
                            hashMap.put("UID",uid);
                            hashMap.put("Batch",mBatch.getText().toString().trim());
                            hashMap.put("Name",mName.getText().toString().trim());
                            hashMap.put("Image","");
                            hashMap.put("StudentID",mID.getText().toString().trim());
                            //Firebase instance
                            FirebaseFirestore database = FirebaseFirestore.getInstance();
                            //path to store to store the users
                            database.collection("Users").document(uid).set(hashMap).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Registration.this, "Failed To Register", Toast.LENGTH_SHORT).show();
                                }
                            });


                            Toast.makeText(Registration.this, "Successfully Registered! \n"+user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Registration.this, UserDashboard.class));

                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(Registration.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //shows error message when failed
                progressDialog.dismiss();
                Toast.makeText(Registration.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });

    }



}
