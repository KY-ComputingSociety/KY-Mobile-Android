package com.example.kymobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class AddEventActivity extends AppCompatActivity {
    //views setup
    EditText mTitle,mShortDesc,mFullDesc,mDate,mTime,mVenue,mEndDate,mEndTime;
    Button Submit;
    ImageView mEventImage;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseDatabase;

    Uri image_rui = null;

    //progress bar
    ProgressDialog pd;


    //PERMISSION CONSTANT
    private static final int STORAGE_REQUEST_CODE = 100;
    //IMAGE PICK CONSTANT
    private static  final int IMAGE_PICK_GALLERY_CODE = 200;
    //PERMISSION
    String[] storagePermission;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        //init progress
        pd = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseFirestore.getInstance();
        checkUserStatus();

        //init permission
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //init views
        mTitle = findViewById(R.id.event_title);
        mShortDesc = findViewById(R.id.event_desc_short);
        mFullDesc = findViewById(R.id.event_desc_full);
        mDate = findViewById(R.id.event_date);
        mTime = findViewById(R.id.event_time);
        mVenue = findViewById(R.id.event_venue);
        mEventImage = findViewById(R.id.event_image);
        Submit = findViewById(R.id.event_submit);
        mEndDate = findViewById(R.id.event_date_end);
        mEndTime = findViewById(R.id.event_time_end);

        //image click listener
        mEventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show request permission
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                }
                else{
                    pickFromGallery();
                }

            }
        });
        //submit click listener
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Title = mTitle.getText().toString().trim();
                String ShortDesc = mShortDesc.getText().toString().trim();
                String FullDesc = mFullDesc.getText().toString().trim();
                String Date = mDate.getText().toString().trim();
                String EndDate = mEndDate.getText().toString().trim();
                String Time = mTime.getText().toString().trim();
                String EndTime = mEndTime.getText().toString().trim();
                String Venue = mVenue.getText().toString().trim();

                if(TextUtils.isEmpty(Title)){
                    Toast.makeText(AddEventActivity.this, "Please Add Title!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(ShortDesc)){
                    Toast.makeText(AddEventActivity.this, "Please Add A Short Description!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(FullDesc)){
                    Toast.makeText(AddEventActivity.this, "Please Add A Description!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(image_rui == null){
                    //event without image
                    uploadData(Title,ShortDesc,FullDesc,Date,EndDate,EndTime,Time,Venue,"NoCover");
                }
                else{
                    //event with image
                    uploadData(Title,ShortDesc,FullDesc,Date,EndDate,EndTime,Time,Venue,String.valueOf(image_rui));


                }


            }
        });

    }

    private void uploadData(final String title, final String shortDesc, final String fullDesc, final String date, final String enddate,final String endtime,final String time, final String venue, String uri) {
        pd.setMessage("Publishing Event..");
        pd.show();
        //time stamp
        final String timeStamp = String.valueOf(System.currentTimeMillis());
        //event path\
        UUID uuid = UUID.randomUUID();
        String path = "Posts/"+uuid;

        if (!uri.equals("NoCover")){
            //posting with image
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(path);
            ref.putFile(Uri.parse(uri)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //image uploaded and get url
                    pd.dismiss();
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());

                    String downloadedUri = uriTask.getResult().toString();
                    if (uriTask.isSuccessful()){
                        //url received and uploads to database
                        HashMap<String,Object> hashMap = new HashMap<>();
                        //put in post info
                        hashMap.put("Title",title);
                        hashMap.put("ShortDesc",shortDesc);
                        hashMap.put("FullDesc",fullDesc);
                        hashMap.put("StartDate",date);
                        hashMap.put("EndDate",enddate);
                        hashMap.put("StartTime",time);
                        hashMap.put("EndTime",endtime);
                        hashMap.put("Venue",venue);
                        hashMap.put("TimeStamp",timeStamp);
                        hashMap.put("Cover", downloadedUri);
                        UUID uuid = UUID.randomUUID();

                        String Document = uuid.toString();



                        //path to post data
                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                        //posting data
                        firebaseFirestore.collection("Posts").document(Document).set(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //added successfully
                                        pd.dismiss();
                                        Toast.makeText(AddEventActivity.this, "Event Published!", Toast.LENGTH_SHORT).show();
                                        //resetting the views
                                        mTitle.setText("");
                                        mShortDesc.setText("");
                                        mFullDesc.setText("");
                                        mDate.setText("");
                                        mEndDate.setText("");
                                        mEndTime.setText("");
                                        mTime.setText("");
                                        mVenue.setText("");
                                        mEventImage.setImageURI(null);
                                        image_rui = null;
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(AddEventActivity.this, "Error Uploading Event", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //failed to upload image
                    pd.dismiss();
                    Toast.makeText(AddEventActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
        else{
            //posting without image
            HashMap<String,Object> hashMap = new HashMap<>();
            //put in post info
            hashMap.put("Title",title);
            hashMap.put("ShortDesc",shortDesc);
            hashMap.put("FullDesc",fullDesc);
            hashMap.put("Date",date);
            hashMap.put("Time",time);
            hashMap.put("Venue",venue);
            hashMap.put("timestamp",timeStamp);
            hashMap.put("Cover", "No Image");

            //path to post data
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            //posting data
            firebaseFirestore.collection("Posts").document(timeStamp).set(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //added successfully
                    pd.dismiss();
                    Toast.makeText(AddEventActivity.this, "Event Published!", Toast.LENGTH_SHORT).show();
                    //resetting the views
                    mTitle.setText("");
                    mShortDesc.setText("");
                    mFullDesc.setText("");
                    mDate.setText("");
                    mTime.setText("");
                    mVenue.setText("");
                    mEventImage.setImageURI(null);
                    image_rui = null;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(AddEventActivity.this, "Error Uploading Event", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }



    private void requestStoragePermission(){
        //request for storage permission
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }



    private void checkUserStatus() {
        //get user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            //stay signed in
        }
        else{
            startActivity(new Intent(AddEventActivity.this,MainActivity.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    //handling permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        //when storage access allowed
                        pickFromGallery();
                    }
                    else{
                        Toast.makeText(this, "Please Press Allow", Toast.LENGTH_SHORT).show();
                    }
                }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                image_rui = data.getData();
                //setting the image
                mEventImage.setImageURI(image_rui);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

