package com.example.kymobile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kymobile.adapters.AdapterEvents;
import com.example.kymobile.adapters.AdapterNotices;
import com.example.kymobile.models.ModelEvent;
import com.example.kymobile.models.ModelNotice;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FloatingActionButton AddNotice;

    RecyclerView recyclerView;
    List<ModelNotice> postList;
    AdapterNotices adapterNotices;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();

        recyclerView = view.findViewById(R.id.noticeRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //shows newest post first
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set layout to recycler
        recyclerView.setLayoutManager(layoutManager);

        //init post list
        postList = new ArrayList<>();
        loadPosts();

        AddNotice = view.findViewById(R.id.add_notice);



        //condition to show button
        if (uid.equals("JrQ37bqxDzOXThzvDM2EOjS5Ltn2")){
            AddNotice.setVisibility(View.VISIBLE);
        }
        else{
            AddNotice.setVisibility(View.GONE);
        }

        AddNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotificationFragment.this.getActivity(),AddNoticeActivity.class));
            }
        });
        return view;
    }

    private void loadPosts() {
        //path of posts
        final CollectionReference documentReference = FirebaseFirestore.getInstance().collection("Notices");
        //get data from document
        documentReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                postList.clear();
                for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots.getDocuments()){
                    ModelNotice modelNotice = documentSnapshot.toObject(ModelNotice.class);

                    postList.add(modelNotice);

                    //adapter
                    adapterNotices = new AdapterNotices(getActivity(),postList);
                    //setting adapter to recycle view
                    recyclerView.setAdapter(adapterNotices);

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
