package com.example.kymobile;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final String FIRE_LOG = "Fire_Log";
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore firebaseDatabase;
    StorageReference storageReference;
    DocumentReference documentReference;
    //path for image
    String storagePath = "Users_ProfilePic/";

    //view from xml file
    ImageView ProfilePic;
    TextView Name,Email,Batch;
    CardView Edit,Logout;

    //progress dialog
    ProgressDialog pd;

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


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //initiate firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseFirestore.getInstance();
        storageReference = getInstance().getReference();



        //init views
        ProfilePic = view.findViewById(R.id.profile_pic);
        Name = view.findViewById(R.id.user_name);
        Email = view.findViewById(R.id.user_email);
        Batch = view.findViewById(R.id.user_batch);
        Edit = view.findViewById(R.id.edit_prof);
        Logout = view.findViewById(R.id.logout);

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(ProfileFragment.this.getActivity(),MainActivity.class));

            }
        });

        //initialise progress dialog
        pd = new ProgressDialog(getActivity());

        //init permission arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};



        String uid = user.getUid();


        //finding info based on user email
        firebaseDatabase.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    String email =documentSnapshot.getString("Email");
                    String batch =documentSnapshot.getString("Batch");
                    String name =documentSnapshot.getString("Name");
                    String image = documentSnapshot.getString("Image");

                    Name.setText(name);
                    Batch.setText(batch);
                    Email.setText(email);

                    try{
                        Picasso.get().load(image).into(ProfilePic);
                    }
                    catch (Exception e){
                        //Load default picture
                    }
                }
                else{
                    Log.d(FIRE_LOG,"Error");
                }
            }
        });

        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();

            }
        });


        return view;
    }


    private boolean checkStoragePermission(){
        //check if permitted to access storage and evaluates true or false
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        //request runtime storage permission
        requestPermissions(storagePermissions,STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermission(){
        //check if access to camera and evaluates true or false
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        //check if permitted to access storage and evaluates true or false
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result && result1;

    }
    private void requestCameraPermission(){
        //request runtime storage permission
        requestPermissions(cameraPermissions,CAMERA_REQUEST_CODE);
    }

    private void showEditProfileDialog() {
        //options for the dialog
        String[] options = {"Edit Profile Picture","Edit Name","Edit Batch Name"};
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //setting the dialog title
        builder.setTitle("Edit Profile");
        //putting in items in dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //events when item clicked
                if (which == 0){
                    //Edit Profile Pic
                    pd.setMessage("Updating Profile Picture");
                    showImagePicDialog();
                }
                else if (which == 1){
                    //Edit the User Name
                    pd.setMessage("Updating Name");
                    showNameUpdate("Name");

                }
                else{
                    //Edit User Batch
                    pd.setMessage("Updating Batch");
                    showBatchUpdate("Batch");


                }
            }
        });
        //showing dialog
        builder.create().show();

    }

    private void showNameUpdate(final String key) {
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit Name");
        //dialog layout
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        //adding an edit text
        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter New Name");
        linearLayout.addView(editText);

        //comment

        builder.setView(linearLayout);
        //button to update new name/batch
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input text from the edit text
                final String value = editText.getText().toString().trim();
                //see if user has entered anything or not
                if(!TextUtils.isEmpty(value)){
                    pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key , value);
                    documentReference = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
                    documentReference.update(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Name.setText(value);
                            Toast.makeText(getActivity(), "Successfully Updated!", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
                else{
                    Toast.makeText(getActivity(), "Please Enter New Name", Toast.LENGTH_SHORT).show();

                }

            }
        });
        //cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //create and show dialog
        builder.create().show();


    }
    private void showBatchUpdate(final String key) {
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit Batch");
        //dialog layout
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        //adding an edit text
        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter New Batch");
        linearLayout.addView(editText);

        builder.setView(linearLayout);
        //button to update new name/batch
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input text from the edit text
                final String value = editText.getText().toString().trim();
                //see if user has entered anything or not
                if(!TextUtils.isEmpty(value) && (value.equals("23.0") || value.equals("23.5") || value.equals("22.5") || value.equals("22.0"))){
                    pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key , value);

                    documentReference.update(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Batch.setText(value);
                            Toast.makeText(getActivity(), "Successfully Updated!", Toast.LENGTH_SHORT).show();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                else{
                    Toast.makeText(getActivity(), "Please Enter Appropriate Batch", Toast.LENGTH_SHORT).show();

                }

            }
        });
        //cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //create and show dialog
        builder.create().show();


    }

    private void showImagePicDialog() {
        //shows option to upload photo from gallery or taking pic
        //options for the dialog
        String[] options = {"Camera","Gallery"};
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //setting the dialog title
        builder.setTitle("Select Image From");
        //putting in items in dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //events when item clicked
                if (which == 0){
                    //From Camera
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else{
                        pickFromCamera();
                    }
                }
                else{
                    //From Gallery
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else{
                        pickFromGallery();
                    }
                }

            }
        });
        //showing dialog
        builder.create().show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                //when picking from camera, check if permission is allowed
                if (grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        //when permissions enabled
                        pickFromCamera();
                    }
                    else{
                        //when denied
                        Toast.makeText(getActivity(), "Please Enable Permissions",Toast.LENGTH_SHORT).show();
                    }

                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length > 0){
                    //picking from gallery after checking writing to storage permission
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        //when permissions enabled
                        pickFromGallery();
                    }
                    else{
                        //when denied
                        Toast.makeText(getActivity(), "Please Enable Permissions",Toast.LENGTH_SHORT).show();
                    }

                }

            }
            break;

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //method called after selecting image
        if(resultCode == RESULT_OK){

            if(requestCode == IMAGE_PICK_GALERY_CODE){
                //if image from gallery, uri
                image_uri = data.getData();
                uploadProfilePicture(image_uri);
            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                //if image from camera, get uri
                uploadProfilePicture(image_uri);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilePicture(final Uri uri) {
        //show progress dialog
        pd.show();
        //path and image name to store in firebase database

        String filePathAndName = storagePath+ user.getUid();

        StorageReference storageReference2 = storageReference.child(filePathAndName);
        storageReference2.putFile(uri)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while(!uriTask.isSuccessful());
                    final Uri downloadUri = uriTask.getResult();

                    //check if image is uploaded and url received
                    if (uriTask.isSuccessful()){
                        //update url in user database
                        HashMap<String,Object> results = new HashMap<>();
                        results.put("Image", downloadUri.toString());
                        documentReference = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
                        documentReference.update(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pd.dismiss();
                                Toast.makeText(getActivity(), "Successfully Updated!", Toast.LENGTH_SHORT).show();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                    else{
                        //error when uploading
                        pd.dismiss();
                        Toast.makeText(getActivity(), "Something Went Wrong..", Toast.LENGTH_SHORT).show();
                    }

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     pd.dismiss();
                     Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void pickFromGallery() {
        //pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALERY_CODE);
    }

    private void pickFromCamera() {
        //picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        //putting image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //starting the camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);

    }

}

