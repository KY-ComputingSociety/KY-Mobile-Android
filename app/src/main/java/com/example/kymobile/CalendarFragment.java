package com.example.kymobile;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.kymobile.adapters.AdapterEvents;
import com.example.kymobile.models.ModelEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FloatingActionButton Addpost;

    RecyclerView recyclerView;
    List<ModelEvent> postList;
    AdapterEvents adapterEvents;

    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //get user uid for admin verification
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();

        //init admin add button
        Addpost = view.findViewById(R.id.add_post);

        //recycler and properties
        recyclerView = view.findViewById(R.id.eventRecycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //shows newest post first
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set layout to recycler
        recyclerView.setLayoutManager(layoutManager);

        //init post list
        postList = new ArrayList<>();
        loadPosts();

        //condition to show button
        if (uid.equals("JrQ37bqxDzOXThzvDM2EOjS5Ltn2")){
            Addpost.setVisibility(View.VISIBLE);
        }
        else{
            Addpost.setVisibility(View.GONE);
        }

        Addpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CalendarFragment.this.getActivity(),AddEventActivity.class));
            }
        });
        return view;
    }

    private void loadPosts() {
        //path of posts
        final CollectionReference documentReference = FirebaseFirestore.getInstance().collection("Posts");
        //get data from document
        documentReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                postList.clear();
                for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots.getDocuments()){
                    ModelEvent modelEvent = documentSnapshot.toObject(ModelEvent.class);

                    postList.add(modelEvent);

                    //adapter
                    adapterEvents = new AdapterEvents(getActivity(),postList);
                    //setting adapter to recycle view
                    recyclerView.setAdapter(adapterEvents);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
