package com.example.kymobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class UserDashboard extends AppCompatActivity {

    //authenticating firebase
    FirebaseAuth firebaseAuth;

    FragmentManager fragmentManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        //intialising
        firebaseAuth = FirebaseAuth.getInstance();

        //for bottom navigaiton
        ChipNavigationBar navigationView = findViewById(R.id.navigation);

        if(savedInstanceState == null){
            navigationView.setItemSelected(R.id.nav_student,true);
            fragmentManager = getSupportFragmentManager();
            StudentFragment studentFragment = new StudentFragment();
            fragmentManager.beginTransaction().replace(R.id.content,studentFragment).commit();
        }


        navigationView.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Fragment fragment = null;
                switch (id){
                    case R.id.nav_notification:
                        fragment = new NotificationFragment();
                        break;

                    case R.id.nav_student:
                        fragment = new StudentFragment();
                        break;

                    case R.id.nav_cal:
                        fragment = new CalendarFragment();
                        break;

                    case R.id.nav_profile:
                        fragment = new ProfileFragment();
                        break;
                }
                if (fragment != null){
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content,fragment).commit();
                }
            }
        });






    }


    private void checkUserStatus() {
        //check status of user if signed in or not
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (!(user == null)) {
            //user redirected to registration page


        } else {
            //set email of logged in user to welcome
            startActivity(new Intent(UserDashboard.this, MainActivity.class));
            finish();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart(){
        //checks when the app starts
        checkUserStatus();
        super.onStart();
    }



}
