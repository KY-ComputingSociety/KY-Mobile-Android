package com.example.kymobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.UUID;

public class AddNoticeActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseDatabase;
    //init progress dialog
    ProgressDialog pd;
    //init views
    EditText nTitle,nBody;
    Button Submit;
    //radio group
    RadioGroup radioGroup;
    RadioButton radioButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseFirestore.getInstance();
        checkUserStatus();

        pd = new ProgressDialog(this);


        //edit text init
        nTitle = findViewById(R.id.notice_title);
        nBody = findViewById(R.id.notice_body);
        //submit init
        Submit = findViewById(R.id.notice_submit);

        //radio group
        radioGroup = findViewById(R.id.radio);

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //init string values for hash maps
                String Title = nTitle.getText().toString().trim();
                String Body = nBody.getText().toString().trim();

                int RadioId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(RadioId);

                String Exco = radioButton.getText().toString().trim();

                if(TextUtils.isEmpty(Title)){
                    Toast.makeText(AddNoticeActivity.this, "Please Add Title!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(Body)){
                    Toast.makeText(AddNoticeActivity.this, "Please Add Body!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    uploadData(Title,Body,Exco);
                }


            }
        });

    }

    public void checkButton(View v){
        int RadioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(RadioId);
    }

    private void checkUserStatus() {
        //get user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            //stay signed in
        }
        else{
            startActivity(new Intent(AddNoticeActivity.this,MainActivity.class));
        }
    }

    private void uploadData(String title, String body, String exco) {
        pd.setMessage("Publishing Notice..");
        pd.show();
        //time stamp
        final String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap<String,Object> hashMap = new HashMap<>();
        //put in notice info
        hashMap.put("Title",title);
        hashMap.put("Body",body);
        hashMap.put("Exco",exco);
        hashMap.put("TimeStamp",timeStamp);

        UUID uuid = UUID.randomUUID();
        String Document = uuid.toString();

        //path to post data
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Notices") .document(Document).set(hashMap)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //added successfully
                pd.dismiss();
                Toast.makeText(AddNoticeActivity.this, "Notice Published!", Toast.LENGTH_SHORT).show();
                //resetting views
                nTitle.setText("");
                nBody.setText("");
                radioGroup.check(0);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddNoticeActivity.this, "Error Uploading Notice", Toast.LENGTH_SHORT).show();

            }
        });
   }


}
